package vanity.crawler.jms

import grails.plugin.jms.JmsService
import org.apache.commons.lang.Validate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import vanity.crawler.parser.result.CrawledPage
import vanity.crawler.parser.source.CrawlSourceCommand

class MessageBus {

    @Autowired
    JmsService jmsService

    public void send(final CrawlSourceCommand crawlCommand) {
        Validate.notNull(crawlCommand)
        jmsService.send(queue: MessageBusConstants.PARSING_QUEUE, crawlCommand)
    }

    @Async
    public void send(final Set<CrawlSourceCommand> crawlCommands) {
        crawlCommands.each { send(it) }
    }

    public void send(final CrawledPage crawledPage) {
        Validate.notNull(crawledPage)
        jmsService.send(queue: MessageBusConstants.PROCESSING_QUEUE, crawledPage)
    }

}
