package vanity.crawler

import vanity.ContentSource
import vanity.crawler.spider.CrawlerJob
import vanity.crawler.spider.CrawlerJobConfiguratior

class ManageController {

    def index() {
        [sources:ContentSource.values()]
    }

    def startJob(){
        def source = ContentSource.valueOf(ContentSource, params.source)
        CrawlerJob.triggerNow(CrawlerJobConfiguratior.getTriggerNowParam(source))
        flash.message = 'crawler.executed'
        redirect(action: 'index')
    }
}
