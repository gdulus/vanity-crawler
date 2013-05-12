package vanity.crawler.spider

import groovy.util.logging.Log4j
import org.quartz.JobExecutionContext
import vanity.article.ContentSource

@Log4j
class CrawlerJob {

    static triggers = {
        ContentSource.Target.values().each {
            simple CrawlerJobConfiguration.buildConfigFor(it)
        }
    }

    CrawlerExecutor crawlerExecutor

    def execute(final JobExecutionContext context) {
        // get source for which job was executed
        ContentSource.Target source = CrawlerJobConfiguration.getContentSource(context)
        // execute crawling
        if (crawlerExecutor.startFor(source)){
            log.info("Crawling for ${source} finished succesfully")
        } else {
            log.warn("Seams that crawler for ${source} is running now - skip execution")
        }
    }

}