package vanity.crawler.parser

import grails.plugin.jms.Queue
import groovy.util.logging.Slf4j
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Autowired
import vanity.crawler.jms.MessageBus
import vanity.crawler.parser.result.CrawledPage
import vanity.crawler.parser.source.CrawlSourceCommand
import vanity.crawler.spider.CrawlerExecutionSynchronizer

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
    CrawlerExecutionSynchronizer synchronizer

    @Queue(name = MessageBus.Constants.PARSING_QUEUE, container = MessageBus.Constants.CONTAINER)
    void processData(final CrawlSourceCommand crawlCommand) {
        synchronizer.execute(crawlCommand.source) { Integer executionIndex ->
            log.info('Executing parsing for {}', crawlCommand)

            ParserConfig config = parserConfigFactory.produce(crawlCommand.source)
            Parser parser = parserFactory.produce(crawlCommand.source)
            ParseContext parseContext = new ParseContext(config, parser, crawlCommand)

            if (parseContext.isMaxDepthExceeded()) {
                log.info('Parsing {} stop - max depth policy', crawlCommand)
                return 0
            }

            if (parseContext.isVisitProhibited()) {
                log.info('Parsing {} stop - should visit policy', crawlCommand)
                return 0
            }

            if (parseContext.isNumberOfCrawlersExceeded(executionIndex)) {
                log.info('Parsing {} stop - max number of crawlers policy', crawlCommand)
                messageBus.send(crawlCommand)
                return 1
            }

            if (parseContext.isPauseRequested()) {
                log.info('Parsing {} pause', crawlCommand)
                parseContext.executePause()
            }

            executeParsingCurrentPage(parseContext)
            return executeParsingRelatedPages(parseContext)
        }
    }

    private void executeParsingCurrentPage(final ParseContext parseContext) {
        try {
            CrawledPage crawledPage = parseContext.executeParse()

            if (crawledPage.validate()) {
                log.info('Parsing {} successful')
                messageBus.send(crawledPage)
            } else {
                log.warn('Parsing {} failed due validation issues {}', crawledPage.errors)
            }
        } catch (final Throwable exp) {
            log.error("There was an error during parssing of ${parseContext}", exp)
        }
    }

    private int executeParsingRelatedPages(final ParseContext parseContext) {
        Set<String> links = parseContext.executeRelatedPagesScan()

        if (links) {
            log.info('Found {} external links for crawler {}', links.size(), parseContext)
            links.each { messageBus.send(new CrawlSourceCommand(it, parseContext.crawlCommand)) }
            return links.size()
        } else {
            log.info('No external links found for {}', parseContext)
            return 0
        }
    }

    private static final class ParseContext {

        final ParserConfig config

        final Parser parser

        final CrawlSourceCommand crawlCommand

        @Lazy
        private Document document = Jsoup.connect(crawlCommand.url).timeout(5000).get()

        ParseContext(ParserConfig config, Parser parser, CrawlSourceCommand crawlCommand) {
            this.config = config
            this.parser = parser
            this.crawlCommand = crawlCommand
        }

        public boolean isMaxDepthExceeded() {
            return config.maxDepthOfCrawling == crawlCommand.depth
        }

        public boolean isNumberOfCrawlersExceeded(final Integer executionIndex) {
            return config.numberOfCrawlers < executionIndex
        }

        public boolean isVisitProhibited() {
            return !parser.shouldVisit(crawlCommand.url)
        }

        public boolean isPauseRequested() {
            return config.politenessDelay != null
        }

        public void executePause() {
            Thread.sleep(config.politenessDelay)
        }

        public CrawledPage executeParse() {
            parser.visit(crawlCommand.url, document)
        }

        public Set<String> executeRelatedPagesScan() {
            return document.select('a')?.collect({ it.attr('href') }) as Set<String>
        }

        @Override
        public String toString() {
            return crawlCommand.toString()
        }
    }
}
