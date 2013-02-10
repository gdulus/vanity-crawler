package vanity.crawler

import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.crawler.WebCrawler
import edu.uci.ics.crawler4j.url.WebURL
import vanity.ContentSource

abstract class Crawler extends WebCrawler {

    protected final ContentSource contentSource

    Crawler(ContentSource contentSource) {
        this.contentSource = contentSource
    }

    @Override
    public final boolean shouldVisit(final WebURL url) {
        String href = url.getURL().toLowerCase();
        return href.startsWith(contentSource.address) && shouldVisit(href)
    }

    protected abstract boolean shouldVisit(String url)

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public final void visit(Page page) {
        parse(page)
    }

    protected abstract void parse(final Page page)

}
