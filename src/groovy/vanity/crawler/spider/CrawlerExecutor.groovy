package vanity.crawler.spider

import org.codehaus.groovy.grails.commons.GrailsApplication
import vanity.article.ContentSource

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class CrawlerExecutor {

    CrawlerFactory crawlerFactory

    GrailsApplication grailsApplication

    private final ExecutorCache cache = new ExecutorCache()

    @Lazy
    private String crawlStorageBaseFolder = grailsApplication.config.crawler.crawlStorageBaseFolder

    @Lazy
    private int numberOfCrawlers = grailsApplication.config.crawler.numberOfCrawlers

    public Status getStatus(final ContentSource.Target contentSourceTarget){
        if (!cache.isRegistered(contentSourceTarget)){
            return Status.STOPPED
        }

        CrawlerControllerWrapper controllerWrapper = cache.get(contentSourceTarget)

        if (!controllerWrapper){
            return Status.STOPPED
        }

        if (controllerWrapper.isStopping()){
            return Status.STOPPING
        }

        return Status.WORKING
    }

    public boolean isCurrentlyCrawling(final ContentSource.Target contentSourceTarget){
        return getStatus(contentSourceTarget) == Status.WORKING

    }

    public boolean stop(final ContentSource.Target contentSourceTarget){
        if (!cache.isRegistered(contentSourceTarget)){
            return true
        }

        CrawlerControllerWrapper controllerWrapper = cache.get(contentSourceTarget)

        if (!controllerWrapper){
            return true
        }

        controllerWrapper.stop()
    }

    public boolean startFor(final ContentSource.Target contentSourceTarget){
        // check without locking if we process current source
        if (isCurrentlyCrawling(contentSourceTarget)){
            return false
        }
        // prepare controller
        CrawlerControllerWrapper controllerWrapper = prepareController(contentSourceTarget)
        // try to add it to cache
        if (!cache.register(contentSourceTarget, controllerWrapper)){
            return false
        }
        // execute crawling, make sure that whatever will happen controller will be unregistered
        try {
            Class<? extends Crawler> crawler = crawlerFactory.produce(contentSourceTarget)
            controllerWrapper.start(crawler, numberOfCrawlers)
        } finally {
            cache.unRegister(contentSourceTarget, controllerWrapper)
        }
        // crawling finished, indicate success
        return true
    }

    private CrawlerControllerWrapper prepareController(final ContentSource.Target contentSourceTarget){
        return new CrawlerControllerWrapper("${crawlStorageBaseFolder}/${contentSourceTarget}", contentSourceTarget.address)
    }

    private class ExecutorCache {

        private static final ConcurrentMap<ContentSource.Target, CrawlerControllerWrapper> RUN_INDICATOR = new ConcurrentHashMap<ContentSource.Target, CrawlerControllerWrapper>()

        public boolean isRegistered(final ContentSource.Target contentSourceTarget){
            return RUN_INDICATOR.containsKey(contentSourceTarget)
        }

        public boolean register(final ContentSource.Target contentSourceTarget, final CrawlerControllerWrapper controllerWrapper){
            return RUN_INDICATOR.putIfAbsent(contentSourceTarget, controllerWrapper) == null
        }

        public boolean unRegister(final ContentSource.Target contentSourceTarget, final CrawlerControllerWrapper controllerWrapper){
            return RUN_INDICATOR.remove(contentSourceTarget, controllerWrapper)
        }

        public CrawlerControllerWrapper get(final ContentSource.Target contentSourceTarget){
            return RUN_INDICATOR.get(contentSourceTarget)
        }

    }

    public enum Status {
        STOPPED,
        STOPPING,
        WORKING,
    }

}
