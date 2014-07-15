package vanity.crawler.spider

import groovy.util.logging.Slf4j
import vanity.article.ContentSource

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger

@Slf4j
class CrawlerExecutionSynchronizer {

    private final ExecutorCache cache = new ExecutorCache()

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
        return cache.register(contentSourceTarget)
    }

    public void execute(final ContentSource.Target contentSourceTarget, final Closure<Integer> executor) {
        ExecutorContext context = cache.get(contentSourceTarget)

        if (!context) {
            log.error('For {} context was already unregistered', contentSourceTarget)
            return
        }

        int index = context.countWorkers.incrementAndGet()

        try {
            if (!context.stopRequested) {
                log.info('Start {} parser for {}', index, contentSourceTarget)
                int newMessages = executor.call(index)
                int messagesCount = context.countMessages.addAndGet(newMessages)
                log.info('Parser {} - new messages = {}, messagesCount = {}', index, newMessages, messagesCount)
            } else {
                log.info('Stop {} requested', contentSourceTarget)
            }
        } finally {
            int workersCount = context.countWorkers.decrementAndGet()
            int messagesCount = context.countMessages.decrementAndGet()
            log.info('Stopped {} parsed for {} [ workersCount = {}, messagesCount = {} ]', index, contentSourceTarget, workersCount, messagesCount)

            if (messagesCount <= 0 && workersCount <= 0) {
                log.info('Unregister {} crawler', contentSourceTarget)
                cache.unRegister(contentSourceTarget)
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

    }

    public enum Status {
        STOPPED,
        STOPPING,
        WORKING,
    }

}
