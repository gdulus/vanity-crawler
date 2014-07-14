package vanity.crawler.spider

import groovy.util.logging.Slf4j
import vanity.article.ContentSource

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger

@Slf4j
class CrawlerMonitor {

    private final ExecutorCache cache = new ExecutorCache()

    public Status getStatus(final ContentSource.Target contentSourceTarget) {
        if (!cache.isRegistered(contentSourceTarget)) {
            return Status.STOPPED
        }

        CrawlMeta meta = cache.get(contentSourceTarget)

        if (!meta) {
            return Status.STOPPED
        }

        if (meta.countWorkers.get() > 0 && meta.stopRequested) {
            return Status.STOPPING
        }

        return Status.WORKING
    }

    public boolean stop(final ContentSource.Target contentSourceTarget) {
        CrawlMeta meta = cache.get(contentSourceTarget)

        if (!meta) {
            return true
        }

        meta.stopRequested = true
        return true
    }

    public boolean start(final ContentSource.Target contentSourceTarget) {
        return cache.register(contentSourceTarget)
    }

    public void invalidateMessage(final ContentSource.Target contentSourceTarget) {
        cache.get(contentSourceTarget).countMessages.decrementAndGet()
    }

    public void registerMessages(final ContentSource.Target contentSourceTarget, final Integer count) {
        cache.get(contentSourceTarget).countMessages.addAndGet(count)
    }

    public void execute(final ContentSource.Target contentSourceTarget, final Closure executor) {
        CrawlMeta meta = cache.get(contentSourceTarget)

        if (!meta || meta.stopRequested) {
            log.info('For content source {} stop was requested or crawler was unregistered', contentSourceTarget)
            return
        }

        int index = meta.countWorkers.incrementAndGet()

        try {
            log.info('Executing {} parser for content source {}', index, contentSourceTarget)
            executor.call()
        } finally {
            log.info('Execution of {} parser for content source {} is over', index, contentSourceTarget)
            meta.countWorkers.decrementAndGet()

            if (meta.countMessages.get() <= 0) {
                cache.unRegister(contentSourceTarget)
            }
        }
    }

    private static final class ExecutorCache {

        private static final ConcurrentMap<ContentSource.Target, CrawlMeta> RUN_INDICATOR = new ConcurrentHashMap<ContentSource.Target, CrawlMeta>()

        public boolean isRegistered(final ContentSource.Target contentSourceTarget) {
            return RUN_INDICATOR.containsKey(contentSourceTarget)
        }

        public boolean register(final ContentSource.Target contentSourceTarget) {
            return RUN_INDICATOR.putIfAbsent(contentSourceTarget, new CrawlMeta()) == null
        }

        public boolean unRegister(final ContentSource.Target contentSourceTarget) {
            return RUN_INDICATOR.remove(contentSourceTarget, get(contentSourceTarget))
        }

        public CrawlMeta get(final ContentSource.Target contentSourceTarget) {
            return RUN_INDICATOR.get(contentSourceTarget)
        }

    }

    private static final class CrawlMeta {

        private volatile boolean stopRequested = false

        private AtomicInteger countWorkers = new AtomicInteger(0)

        private AtomicInteger countMessages = new AtomicInteger(0)

    }

    public enum Status {
        STOPPED,
        STOPPING,
        WORKING,
    }

}
