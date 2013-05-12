package vanity.crawler.spider

import vanity.ContentSource
import vanity.article.ContentSource

class ManageController {

    CrawlerExecutor crawlerExecutor

    def index() {
        // prepare model data - collect all crawlers and it states
        def crawlers = ContentSource.Target.values().collect {
            [source:it, status:crawlerExecutor.getStatus(it)]
        }
        // return model
        [crawlers:crawlers]
    }

    def startJob(){
        def contentSourceTarget = ContentSource.Target.valueOf(ContentSource.Target, params.source)
        CrawlerJob.triggerNow(CrawlerJobConfiguration.getTriggerNowParam(contentSourceTarget))
        flash.message = 'crawler.executed'
        redirect(action: 'index')
    }

    def stopJob(){
        def contentSourceTarget = ContentSource.Target.valueOf(ContentSource.Target, params.source)
        crawlerExecutor.stop(contentSourceTarget)
        flash.message = 'crawler.stopped'
        redirect(action: 'index')
    }

}
