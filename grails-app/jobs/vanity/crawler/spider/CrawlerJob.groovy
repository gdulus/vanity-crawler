package vanity.crawler.spider

import groovy.util.logging.Slf4j
import org.quartz.JobExecutionContext
import vanity.article.ContentSource
import vanity.article.ContentSourceService

@Slf4j
class CrawlerJob {

    static triggers = {
        ContentSource.Target.values().each {
            simple CrawlerJobConfiguration.buildConfigFor(it)
        }
    }

    CrawlerExecutor crawlerExecutor

    ContentSourceService contentSourceService

    def execute(final JobExecutionContext context) {
        ContentSource.Target target = CrawlerJobConfiguration.getContentSource(context)
        ContentSource source = contentSourceService.get(target)

        if (source.disabled) {
            log.warn("Crawler for {} is disabled - skipping execution", target)
            return
        }

        if (crawlerExecutor.startFor(target)) {
            log.info("Crawling for {} finished successfully", target)
        } else {
            log.warn("Seams that crawler for {} is running now - skip execution", target)
        }
    }

}