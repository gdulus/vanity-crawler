package vanity.crawler

import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.crawler.WebCrawler
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.url.WebURL
import groovy.util.logging.Log4j
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import vanity.ContentSource
import vanity.crawler.result.CrawledPage
import vanity.crawler.result.PageContent
import vanity.crawler.result.PageMeta

@Log4j
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

    @Override
    public final void visit(Page page) {
        String url = page.getWebURL().getURL();
        log.info("Start parsing [${url}]")

        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        Document doc = Jsoup.parse(htmlParseData.html)
        PageMeta meta = new PageMeta(url, getTags(doc))
        PageContent content = new PageContent(getTitle(doc), getBody(doc))
        CrawledPage crawledPage = new CrawledPage(meta, content)

        if (!crawledPage.validate()){
            log.warn("For [${url}] we have an errors")
            return
        }

        log.info("Page [${url}] parsed successfuly")
    }

    protected abstract String getTitle(Document doc)

    protected abstract String getBody(Document doc)

    protected abstract Set<String> getTags(Document doc)

}
