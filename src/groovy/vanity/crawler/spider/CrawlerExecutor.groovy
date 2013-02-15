package vanity.crawler.spider

import org.codehaus.groovy.grails.commons.GrailsApplication
import vanity.ContentSource

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

    public boolean isCurrentlyCrawling(final ContentSource source){
        return cache.isRegistered(source)
    }

    public boolean startFor(final ContentSource source){
        // check without locking if we process current source
        if (isCurrentlyCrawling(source)){
            return false
        }
        // prepare controller
        CrawlerControllerWrapper controllerWrapper = prepareController(source)
        // try to add it to cache
        if (!cache.register(source, controllerWrapper)){
            return false
        }
        // execute crawling, make sure that whatever will happen controller will be unregistered
        try {
            Class<? extends Crawler> crawler = crawlerFactory.produce(source)
            controllerWrapper.start(crawler, numberOfCrawlers)
        } finally {
            cache.unregister(source, controllerWrapper)
        }
        // crawling finished, indicate success
        return true
    }

    private CrawlerControllerWrapper prepareController(final ContentSource source){
        return new CrawlerControllerWrapper("${crawlStorageBaseFolder}/${source}", source.address)
    }

    private class ExecutorCache {

        private static final ConcurrentMap<ContentSource, CrawlerControllerWrapper> RUN_INDICATOR = new ConcurrentHashMap<ContentSource, CrawlerControllerWrapper>()

        public boolean isRegistered(final ContentSource source){
            return RUN_INDICATOR.containsKey(source)
        }

        public boolean register(final ContentSource source, final CrawlerControllerWrapper controllerWrapper){
            return RUN_INDICATOR.putIfAbsent(source, controllerWrapper) == null
        }

        public boolean unregister(final ContentSource source, final CrawlerControllerWrapper controllerWrapper){
            return RUN_INDICATOR.remove(source, controllerWrapper)
        }

    }

}
