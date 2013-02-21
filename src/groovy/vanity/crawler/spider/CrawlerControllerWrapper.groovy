package vanity.crawler.spider

import edu.uci.ics.crawler4j.crawler.CrawlConfig
import edu.uci.ics.crawler4j.crawler.CrawlController
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer
import groovy.transform.PackageScope
import org.apache.commons.lang.Validate

@PackageScope
final class CrawlerControllerWrapper {

    private final String crawlStorageFolder

    private final String sourceAddress

    private CrawlController controller

    CrawlerControllerWrapper(final String crawlStorageFolder, final String sourceAddress) {
        Validate.notEmpty(crawlStorageFolder, 'Provide not null and not blank crawlStorageFolder')
        Validate.notEmpty(sourceAddress, 'Provide not null and not blank sourceAddress')
        this.crawlStorageFolder = crawlStorageFolder
        this.sourceAddress = sourceAddress
    }

    public void start(final Class<? extends Crawler> crawler, final int numberOfCrawlers) {
        // initialize base object necessary to run crawler
        CrawlConfig config = new CrawlConfig()
        config.setCrawlStorageFolder(crawlStorageFolder)
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher)
        // execute crawler
        controller = new CrawlController(config, pageFetcher, robotstxtServer)
        controller.addSeed(sourceAddress)
        controller.start(crawler, numberOfCrawlers)
    }

    public void stop(){
        controller.Shutdown()
    }

    @Override
    boolean equals(o) {
        if (this.is(o)){
            return true
        }

        if (getClass() != o.class){
            return false
        }

        CrawlerControllerWrapper that = (CrawlerControllerWrapper) o
        return sourceAddress == that.sourceAddress
    }

    @Override
    int hashCode() {
        return sourceAddress.hashCode()
    }
}
