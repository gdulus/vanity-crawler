package vanity.crawler

import edu.uci.ics.crawler4j.crawler.CrawlConfig
import edu.uci.ics.crawler4j.crawler.CrawlController
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer
import org.quartz.JobExecutionContext
import vanity.ContentSource

class CrawlerJob {

    static triggers = {
        ContentSource.values().each {
            simple CrawlerJobConfiguratior.buildConfigFor(it)
        }
    }

    CrawlerFactory crawlerFactory

    def execute(final JobExecutionContext context) {
        ContentSource source = CrawlerJobConfiguratior.getContentSource(context)
        Class<? extends Crawler> crawlerClass = crawlerFactory.produce(source)

        String crawlStorageFolder = "/home/gdulus/Downloads/crawl/${source.name()}";
        int numberOfCrawlers = 7;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);

        /*
        * Instantiate the controller for this crawl.
        */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed(source.address);

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(crawlerClass, numberOfCrawlers);
    }

}
