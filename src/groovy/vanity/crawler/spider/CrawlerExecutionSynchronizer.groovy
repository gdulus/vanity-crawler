package vanity.crawler.spider

import groovy.transform.ToString
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import vanity.article.ContentSource
import vanity.crawler.jms.MessageBus
import vanity.crawler.parser.source.CrawlSourceCommand

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger

@Slf4j
class CrawlerExecutionSynchronizer {

    private final ExecutorCache cache = new ExecutorCache()

    @Autowired
    public MessageBus messageBus

    public Status getStatus(final ContentSource.Target contentSourceTarget) {
        ExecutorContext context = cache.get(contentSourceTarget)

        if (!context) {
            return Status.STOPPED
        }

        if (context.countWorkers.get() > 0 && context.stopRequested) {
            return Status.STOPPING
        }

        return Status.WORKING
    }

    public boolean stop(final ContentSource.Target contentSourceTarget) {
        ExecutorContext context = cache.get(contentSourceTarget)

        if (!context) {
            return true
        }

        context.stopRequested = true
        return true
    }

    public boolean start(final ContentSource.Target contentSourceTarget) {
        if (getStatus(contentSourceTarget) == Status.WORKING) {
            log.info('Crawler {} currently running, skipping', contentSourceTarget)
            return false
        }

        if (!cache.register(contentSourceTarget)) {
            log.info('Crawler {} is registered but not running, skip execution', contentSourceTarget)
            return false
        }

        log.info('Starting execution of {} crawler', contentSourceTarget)
        messageBus.send(new CrawlSourceCommand(contentSourceTarget, contentSourceTarget.address, 0))
        return true
    }

    public void execute(final CrawlSourceCommand crawlCommand, final Closure<Set<String>> executor) {
        ExecutorContext context = cache.get(crawlCommand.source)

        if (!context) {
            log.error('For {} context was already unregistered', crawlCommand.source)
            return
        }

        int index = context.countWorkers.incrementAndGet()

        try {
            if (!context.stopRequested) {
                if (context.visitedCache.putIfAbsent(crawlCommand.url, Boolean.TRUE) == null) {
                    log.info('Start {} parser for {}', index, crawlCommand.source)
                    Set<String> notVisitedTargetPages = executor.call(index).findAll { !context.visitedCache.containsKey(it) }
                    context.countMessages.addAndGet(notVisitedTargetPages.size())
                    messageBus.send(notVisitedTargetPages.collect { new CrawlSourceCommand(it, crawlCommand) } as Set)
                } else {
                    log.info('Page {} already visited', crawlCommand.url)
                }
            } else {
                log.info('Stop requested for {}', crawlCommand.source)
            }
        } finally {
            int workersCount = context.countWorkers.decrementAndGet()
            int messagesCount = context.countMessages.decrementAndGet()
            log.info('Stopped {} parsed for {} [ workersCount = {}, messagesCount = {} ]', index, crawlCommand.source, workersCount, messagesCount)

            if (messagesCount <= 0 && workersCount <= 0) {
                log.info('Unregister {} crawler.Visited pages: {}', crawlCommand.source, context.visitedCache.keySet())
                cache.unRegister(crawlCommand.source)
            }
        }
    }

    private static final class ExecutorCache {

        private static final ConcurrentMap<ContentSource.Target, ExecutorContext> RUN_INDICATOR = new ConcurrentHashMap<ContentSource.Target, ExecutorContext>()

        public boolean register(final ContentSource.Target contentSourceTarget) {
            return RUN_INDICATOR.putIfAbsent(contentSourceTarget, new ExecutorContext()) == null
        }

        public boolean unRegister(final ContentSource.Target contentSourceTarget) {
            return RUN_INDICATOR.remove(contentSourceTarget, get(contentSourceTarget))
        }

        public ExecutorContext get(final ContentSource.Target contentSourceTarget) {
            return RUN_INDICATOR.get(contentSourceTarget)
        }

    }

    private static final class ExecutorContext {

        private volatile boolean stopRequested = false

        private AtomicInteger countWorkers = new AtomicInteger(0)

        private AtomicInteger countMessages = new AtomicInteger(1) // initial seed page

        private ConcurrentMap<String, Boolean> visitedCache = new ConcurrentHashMap<>()

    }

    public enum Status {
        STOPPED,
        STOPPING,
        WORKING,
    }

}
