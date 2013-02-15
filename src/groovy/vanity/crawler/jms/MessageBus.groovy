package vanity.crawler.jms

import org.apache.commons.lang.Validate
import vanity.crawler.spider.result.CrawledPage

class MessageBus {

    private final def jmsService

    MessageBus(jmsService) {
        this.jmsService = jmsService
    }

    public void sendToProcessor(final CrawledPage crawledPage){
        Validate.notNull(crawledPage)
        jmsService.send(queue:Constants.TO_BE_PROCESSED_QUEUE, crawledPage)
    }

    public static final class Constants {

        public static final String TO_BE_PROCESSED_QUEUE = 'toBeProcessed'

    }
}
