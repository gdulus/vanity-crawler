package vanity.crawler.spider

import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.crawler.WebCrawler
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.url.WebURL
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import vanity.article.ArticleService
import vanity.article.ContentSource
import vanity.crawler.jms.MessageBus
import vanity.crawler.spider.result.CrawledPage
import vanity.crawler.spider.result.PageContent
import vanity.crawler.spider.result.PageMeta

@Slf4j
abstract class Crawler extends WebCrawler {

    protected final ContentSource.Target contentSourceTarget

    private final MessageBus messageBus

    private final ArticleService articleService

    Crawler(ContentSource.Target contentSourceTarget) {
        this.contentSourceTarget = contentSourceTarget
        this.messageBus = Holders.applicationContext.getBean(MessageBus)
        this.articleService = Holders.applicationContext.getBean(ArticleService)
    }

    @Override
    public final boolean shouldVisit(final WebURL url) {
        String href = url.getURL().toLowerCase();
        return href.startsWith(contentSourceTarget.address)
    }

    @Override
    public final void visit(final Page page) {
        String url = page.getWebURL().getURL();

        if (!isValidForParsing(url)) {
            log.info('Skipping parsing for {}', url)
            return
        }

        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        Document doc = Jsoup.parse(htmlParseData.html)
        CrawledPage crawledPage

        try {
            PageMeta meta = new PageMeta(getExternalId(url), contentSourceTarget, url, getTags(doc), getDate(doc))
            PageContent content = new PageContent(getTitle(doc), getBody(doc))
            crawledPage = new CrawledPage(meta, content)
        } catch (Exception e) {
            log.error("Exception during parsing ${url}", e)
            return
        }

        if (!crawledPage.validate()) {
            log.error("For [${url}] we have an errors ${crawledPage.errors}")
            return
        }

        log.info("Page [${url}] parsed successfuly")
        messageBus.sendToProcessor(crawledPage)
    }

    private boolean isValidForParsing(final String url) {
        if (!shouldParse(url)){
            log.info('Parsing of {} skipped due to custom validation issue', url)
            return false
        }

        if (articleService.findByExternalId(getExternalId(url))){
            log.info('Parsing of {} skipped due external id validation issue', url)
            return false
        }

        return true
    }

    protected abstract boolean shouldParse(String url)

    protected abstract Date getDate(Document doc)

    protected abstract String getTitle(Document doc)

    protected abstract String getBody(Document doc)

    protected abstract Set<String> getTags(Document doc)

    protected abstract String getExternalId(String url)

}
