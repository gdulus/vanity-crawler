package vanity.crawler.spider

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import vanity.article.ContentSource
import vanity.crawler.jms.MessageBus
import vanity.crawler.parser.ParserFactory
import vanity.crawler.parser.source.CrawlSourceCommand

@Slf4j
class CrawlerExecutor {

    @Autowired
    public ParserFactory crawlerFactory

    @Autowired
    public MessageBus messageBus

    @Autowired
    public CrawlerMonitor monitor

    public CrawlerMonitor.Status getStatus(final ContentSource.Target contentSourceTarget) {
        monitor.getStatus(contentSourceTarget)
    }

    public boolean stop(final ContentSource.Target contentSourceTarget) {
        monitor.stop(contentSourceTarget)
    }

    public boolean start(final ContentSource.Target contentSourceTarget) {
        // check without locking if we process current source
        if (monitor.getStatus(contentSourceTarget) == CrawlerMonitor.Status.WORKING || !monitor.start(contentSourceTarget)) {
            log.info('Crawler {} currently running, skipping', contentSourceTarget)
            return false
        }
        // send to message bus
        log.info('Starting execution of {} crawler', contentSourceTarget)
        messageBus.send(new CrawlSourceCommand(contentSourceTarget, contentSourceTarget.address, 0))
        return true
    }

}
