package vanity.crawler.spider

import vanity.ContentSource

class ManageController {

    CrawlerExecutor crawlerExecutor

    def index() {
        // prepare model data - collect all crawlers and it states
        def crawlers = ContentSource.values().collect {
            [source:it, isRunning:crawlerExecutor.isCurrentlyCrawling(it)]
        }
        // return model
        [crawlers:crawlers]
    }

    def startJob(){
        def source = ContentSource.valueOf(ContentSource, params.source)
        CrawlerJob.triggerNow(CrawlerJobConfiguratior.getTriggerNowParam(source))
        flash.message = 'crawler.executed'
        redirect(action: 'index')
    }

    def stopJob(){
        def source = ContentSource.valueOf(ContentSource, params.source)
        crawlerExecutor.stop(source)
        flash.message = 'crawler.stopped'
        redirect(action: 'index')
    }

}
