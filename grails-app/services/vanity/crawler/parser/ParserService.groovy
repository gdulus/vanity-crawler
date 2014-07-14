package vanity.crawler.parser

import grails.plugin.jms.Queue
import groovy.util.logging.Slf4j
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Autowired
import vanity.crawler.jms.MessageBus
import vanity.crawler.parser.result.CrawledPage
import vanity.crawler.parser.source.CrawlSourceCommand
import vanity.crawler.spider.CrawlerMonitor

@Slf4j
class ParserService {

    static exposes = ['jms']

    static transactional = false

    @Autowired
    MessageBus messageBus

    @Autowired
    ParserFactory parserFactory

    @Autowired
    ParserConfigFactory parserConfigFactory

    @Autowired
    CrawlerMonitor monitor

    @Queue(name = MessageBus.Constants.PARSING_QUEUE, container = MessageBus.Constants.CONTAINER)
    void processData(final CrawlSourceCommand crawlCommand) {
        monitor.execute(crawlCommand.source) {
            log.info('Executing parsing for {}', crawlCommand)
            monitor.invalidateMessage(crawlCommand.source)

            ParserConfig config = parserConfigFactory.produce(crawlCommand.source)
            if (config.maxDepthOfCrawling == crawlCommand.depth) {
                log.info('Executing of parsing for {} stopping due to max depth policy', crawlCommand)
                return
            }

            Parser parser = parserFactory.produce(crawlCommand.source)
            if (!parser.shouldVisit(crawlCommand.url)) {
                log.debug('Crawler for {} will stop execution due to url validation policy', crawlCommand)
                return
            }

            if (config.politenessDelay) {
                log.debug('Crawler for {} will pause due to politeness policy', crawlCommand)
                Thread.sleep(config.politenessDelay)
            }

            Document document = Jsoup.connect(crawlCommand.url).get()

            try {
                CrawledPage crawledPage = parser.visit(crawlCommand.url, document)

                if (crawledPage.validate()) {
                    log.info('Parsing for {} successful')
                    messageBus.send(crawledPage)
                } else {
                    log.warn('Crawling for {} failed due to validation issues')
                }
            } catch (final Throwable exp) {
                log.error("There was an error during parssing of ${crawlCommand}", exp)
            }

            Set<String> links = document.select('a')?.collect({ it.attr('href') }) as Set<String>

            if (links) {
                int size = links.size()
                log.info('Found {} external links for crawler {}', size, crawlCommand)
                monitor.registerMessages(crawlCommand.source, size)
                links.each { messageBus.send(new CrawlSourceCommand(it, crawlCommand)) }
            } else {
                log.info('No external links found for {}', crawlCommand)
            }
        }
    }
}
