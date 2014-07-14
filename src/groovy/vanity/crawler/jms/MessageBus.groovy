package vanity.crawler.jms

import org.apache.commons.lang.Validate
import vanity.crawler.parser.result.CrawledPage
import vanity.crawler.parser.source.CrawlSourceCommand

class MessageBus {

    private final def jmsService

    MessageBus(jmsService) {
        this.jmsService = jmsService
    }

    public void send(final CrawlSourceCommand crawlCommand) {
        Validate.notNull(crawlCommand)
        jmsService.send(queue: Constants.PARSING_QUEUE, crawlCommand)
    }

    public void send(final CrawledPage crawledPage) {
        Validate.notNull(crawledPage)
        jmsService.send(queue: Constants.PROCESSING_QUEUE, crawledPage)
    }

    public static final class Constants {

        public static final String PARSING_QUEUE = 'vanity.crawler.parser'

        public static final String PROCESSING_QUEUE = 'vanity.crawler.processor'

        public static final String CONTAINER = 'crawler'

    }
}
