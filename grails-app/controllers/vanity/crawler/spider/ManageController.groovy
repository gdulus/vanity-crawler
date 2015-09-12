package vanity.crawler.spider

import grails.plugin.springsecurity.annotation.Secured
import vanity.article.ContentSource
import vanity.article.ContentSourceService
import vanity.crawler.stats.StatsService
import vanity.user.Authority

@Secured([Authority.ROLE_ADMIN])
class ManageController {

    CrawlerExecutor crawlerExecutor

    ContentSourceService contentSourceService

    StatsService statsService

    def index() {
        def crawlers = ContentSource.list(sort: 'target').collect {
            [source: it, status: crawlerExecutor.getStatus(it.target)]
        }
        [crawlers: crawlers, stats: statsService.getCountPerDay()]
    }

    def startJob(final Long id) {
        ContentSource contentSource = contentSourceService.read(id)
        CrawlerJob.triggerNow(CrawlerJobConfiguration.getTriggerNowParam(contentSource.target))
        flash.message = 'crawler.executed'
        redirect(action: 'index')
    }

    def stopJob(final Long id) {
        ContentSource contentSource = contentSourceService.read(id)
        crawlerExecutor.stop(contentSource.target)
        flash.message = 'crawler.stopped'
        redirect(action: 'index')
    }

    def enableCrawler(final Long id) {
        contentSourceService.enable(id)
        flash.message = 'crawler.enabled'
        redirect(action: 'index')
    }

    def disableCrawler(final Long id) {
        contentSourceService.disable(id)
        flash.message = 'crawler.disabled'
        redirect(action: 'index')
    }

}
