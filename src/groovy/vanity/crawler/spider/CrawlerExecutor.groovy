package vanity.crawler.spider

import edu.uci.ics.crawler4j.crawler.CrawlConfig
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import vanity.article.ContentSource

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Slf4j
class CrawlerExecutor {

    @Autowired
    public CrawlerFactory crawlerFactory

    @Value('${crawler.crawlStorageBaseFolder}')
    public String crawlStorageBaseFolder

    @Value('${crawler.numberOfCrawlers}')
    public int numberOfCrawlers

    @Value('${crawler.maxDepthOfCrawling}')
    public int maxDepthOfCrawling

    @Value('${crawler.politenessDelay}')
    public int politenessDelay

    @Value('${crawler.userAgentName}')
    public String userAgentName

    private final ExecutorCache cache = new ExecutorCache()

    public Status getStatus(final ContentSource.Target contentSourceTarget) {
        if (!cache.isRegistered(contentSourceTarget)) {
            return Status.STOPPED
        }

        CrawlerControllerWrapper controllerWrapper = cache.get(contentSourceTarget)

        if (!controllerWrapper) {
            return Status.STOPPED
        }

        if (controllerWrapper.isStopping()) {
            return Status.STOPPING
        }

        return Status.WORKING
    }

    public boolean isCurrentlyCrawling(final ContentSource.Target contentSourceTarget) {
        return getStatus(contentSourceTarget) == Status.WORKING

    }

    public boolean stop(final ContentSource.Target contentSourceTarget) {
        if (!cache.isRegistered(contentSourceTarget)) {
            return true
        }

        CrawlerControllerWrapper controllerWrapper = cache.get(contentSourceTarget)

        if (!controllerWrapper) {
            return true
        }

        controllerWrapper.stop()
    }

    public boolean startFor(final ContentSource.Target contentSourceTarget) {
        // check without locking if we process current source
        if (isCurrentlyCrawling(contentSourceTarget)) {
            return false
        }
        // prepare controller
        CrawlerControllerWrapper controllerWrapper = prepareController(contentSourceTarget)
        // try to add it to cache
        if (!cache.register(contentSourceTarget, controllerWrapper)) {
            return false
        }
        // execute crawling, make sure that whatever will happen controller will be unregistered
        try {
            Class<? extends Crawler> crawler = crawlerFactory.produce(contentSourceTarget)
            CrawlConfig crawlConfig = getCrawlConfig(contentSourceTarget)
            RobotstxtConfig robotstxtConfig = getRobotstxtConfig()
            // clean up working dir, for some reason crawling doesn't work when cashed
            File storageFolder = new File(crawlConfig.crawlStorageFolder)
            if (storageFolder.exists()){
                log.info('Deleting cash folder {}', storageFolder)
                storageFolder.deleteDir()
            }
            // trigger execution
            controllerWrapper.start(crawler, numberOfCrawlers, crawlConfig, robotstxtConfig)
        } finally {
            cache.unRegister(contentSourceTarget, controllerWrapper)
        }
        // crawling finished, indicate success
        return true
    }

    private CrawlConfig getCrawlConfig(final ContentSource.Target contentSourceTarget) {
        CrawlConfig config = new CrawlConfig()
        config.crawlStorageFolder = "${crawlStorageBaseFolder}/${contentSourceTarget}"
        config.resumableCrawling = true

        if (maxDepthOfCrawling) {
            config.maxDepthOfCrawling = maxDepthOfCrawling
        }

        if (politenessDelay) {
            config.politenessDelay = politenessDelay
        }

        return config
    }

    private RobotstxtConfig getRobotstxtConfig() {
        RobotstxtConfig config = new RobotstxtConfig()

        if (userAgentName) {
            config.userAgentName = userAgentName
        }

        return config
    }

    private CrawlerControllerWrapper prepareController(final ContentSource.Target contentSourceTarget) {
        return new CrawlerControllerWrapper("${crawlStorageBaseFolder}/${contentSourceTarget}", contentSourceTarget.address)
    }

    private class ExecutorCache {

        private static final ConcurrentMap<ContentSource.Target, CrawlerControllerWrapper> RUN_INDICATOR = new ConcurrentHashMap<ContentSource.Target, CrawlerControllerWrapper>()

        public boolean isRegistered(final ContentSource.Target contentSourceTarget) {
            return RUN_INDICATOR.containsKey(contentSourceTarget)
        }

        public boolean register(final ContentSource.Target contentSourceTarget, final CrawlerControllerWrapper controllerWrapper) {
            return RUN_INDICATOR.putIfAbsent(contentSourceTarget, controllerWrapper) == null
        }

        public boolean unRegister(final ContentSource.Target contentSourceTarget, final CrawlerControllerWrapper controllerWrapper) {
            return RUN_INDICATOR.remove(contentSourceTarget, controllerWrapper)
        }

        public CrawlerControllerWrapper get(final ContentSource.Target contentSourceTarget) {
            return RUN_INDICATOR.get(contentSourceTarget)
        }

    }

    public enum Status {
        STOPPED,
        STOPPING,
        WORKING,
    }

}
