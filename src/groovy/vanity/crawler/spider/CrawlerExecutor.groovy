package vanity.crawler.spider

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import vanity.article.ContentSource

@Slf4j
class CrawlerExecutor {

    @Autowired
    public CrawlerExecutionSynchronizer synchronizer

    public CrawlerExecutionSynchronizer.Status getStatus(final ContentSource.Target contentSourceTarget) {
        return synchronizer.getStatus(contentSourceTarget)
    }

    public boolean stop(final ContentSource.Target contentSourceTarget) {
        return synchronizer.stop(contentSourceTarget)
    }

    public boolean start(final ContentSource.Target contentSourceTarget) {
        return synchronizer.start(contentSourceTarget)
    }

}
