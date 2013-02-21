package vanity.crawler.processor

import grails.plugin.jms.Queue
import vanity.article.Article
import vanity.article.ArticleService
import vanity.crawler.jms.MessageBus
import vanity.crawler.spider.result.CrawledPage

class PageDataProcessorService {

    static exposes = ['jms']

    static transactional = false

    ArticleService articleService

    @Queue(name=MessageBus.Constants.TO_BE_PROCESSED_QUEUE)
    void processData(final CrawledPage crawledPage) {
        // try to create article based on crawled page data
        Article article = articleService.create(crawledPage.meta.tags) { Article article ->
            article.source = crawledPage.meta.source
            article.publicationDate = crawledPage.meta.date
            article.url = crawledPage.meta.url
            article.title = crawledPage.content.title
            article.body = crawledPage.content.body
        }
    }
}