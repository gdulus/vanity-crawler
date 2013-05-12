package vanity.crawler.processor

import grails.plugin.jms.Queue
import vanity.article.Article
import vanity.article.ArticleService
import vanity.crawler.jms.MessageBus
import vanity.crawler.processor.post.PostProcessorChain
import vanity.crawler.spider.result.CrawledPage

class PageDataProcessorService {

    static exposes = ['jms']

    static transactional = false

    ArticleService articleService

    PostProcessorChain postProcessorChain

    @Queue(name=MessageBus.Constants.TO_BE_PROCESSED_QUEUE)
    void processData(final CrawledPage crawledPage) {
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
        if (!article){
            log.error("Error while saving article for crawled page ${crawledPage}")
            return
        }
        // execute all other actions on top of crawled article
        postProcessorChain.execute(article)
    }
}
