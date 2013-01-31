package vanity.crawler

import edu.uci.ics.crawler4j.crawler.WebCrawler
import vanity.ContentSource

abstract class Crawler extends WebCrawler {

    protected final ContentSource contentSource

    Crawler(ContentSource contentSource) {
        this.contentSource = contentSource
    }
}
