package vanity.crawler.spider

import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.crawler.WebCrawler
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.url.WebURL
import grails.util.Holders
import groovy.util.logging.Log4j
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import vanity.ContentSource
import vanity.crawler.jms.MessageBus
import vanity.crawler.spider.result.CrawledPage
import vanity.crawler.spider.result.PageContent
import vanity.crawler.spider.result.PageMeta

@Log4j
abstract class Crawler extends WebCrawler {

    protected final ContentSource contentSource

    private final MessageBus messageBus

    Crawler(ContentSource contentSource) {
        this.contentSource = contentSource
        this.messageBus = Holders.applicationContext.getBean(MessageBus)
    }

    @Override
    public final boolean shouldVisit(final WebURL url) {
        String href = url.getURL().toLowerCase();
        return href.startsWith(contentSource.address) && shouldVisit(href)
    }

    protected abstract boolean shouldVisit(String url)

    @Override
    public final void visit(final Page page) {
        String url = page.getWebURL().getURL();
        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        Document doc = Jsoup.parse(htmlParseData.html)
        PageMeta meta = new PageMeta(url, getTags(doc), getDate(doc))
        PageContent content = new PageContent(getTitle(doc), getBody(doc))
        CrawledPage crawledPage = new CrawledPage(meta, content)

        if (!crawledPage.validate()){
            log.error("For [${url}] we have an errors ${crawledPage.errors}")
            return
        }


        log.info("Page [${url}] parsed successfuly")
        messageBus.sendToProcessor(crawledPage)
    }

    protected abstract Date getDate(Document doc)

    protected abstract String getTitle(Document doc)

    protected abstract String getBody(Document doc)

    protected abstract Set<String> getTags(Document doc)

}
