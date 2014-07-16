package vanity.crawler.processor

import grails.plugin.jms.Queue
import groovy.util.logging.Slf4j
import vanity.article.Article
import vanity.article.ArticleService
import vanity.crawler.jms.MessageBusConstants
import vanity.crawler.parser.result.CrawledPage
import vanity.crawler.processor.post.PostProcessorChain

@Slf4j
class ProcessorService {

    static exposes = ['jms']

    static transactional = false

    ArticleService articleService

    PostProcessorChain postProcessorChain

    @Queue(name = MessageBusConstants.PROCESSING_QUEUE, container = MessageBusConstants.CONTAINER)
    void processData(final CrawledPage crawledPage) {
        try {
            log.info("Processing data for page ${crawledPage}")
            // try to create article based on crawled page data
            Article article = articleService.create(crawledPage.meta.tags, crawledPage.meta.contentSourceTarget) { Article article ->
                article.externalId = crawledPage.meta.externalId
                article.publicationDate = crawledPage.meta.date
                article.url = crawledPage.meta.url
                article.title = crawledPage.content.title
                article.body = crawledPage.content.body
            }
            // check if save was successful
            if (!article) {
                log.error("Error while saving article for crawled page {}", crawledPage)
                return
            }
            // execute all other actions on top of crawled article
            log.info("Article saved correctly {}, executing postprocessing", article)
            postProcessorChain.execute(article)
        } catch (final Throwable exp) {
            log.error("Exception during saving page ${crawledPage}", exp)
        }
    }
}
