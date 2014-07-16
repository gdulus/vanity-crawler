package vanity.crawler.parser

import groovy.util.logging.Slf4j
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Autowired
import vanity.article.ArticleService
import vanity.crawler.parser.result.CrawledPage
import vanity.crawler.parser.result.PageContent
import vanity.crawler.parser.result.PageMeta

@Slf4j
abstract class AbstractParser implements Parser {

    @Autowired
    ArticleService articleService

    @Override
    public final boolean shouldVisit(final String url) {
        if (!url.toLowerCase().startsWith(url.toLowerCase())) {
            log.info('Parsing of {} skipped - page external', url)
            return false
        }

        if (url == parses().address) {
            log.info('Requested url {} is a main page - parse', url)
            return true
        }

        if (!shouldParse(url)) {
            log.info('Parsing of {} skipped - invalid url', url)
            return false
        }

        if (articleService.findByExternalIdAndContentSource(getExternalId(url), parses())) {
            log.info('Parsing of {} skipped - already parsed', url)
            return false
        }

        return true
    }

    @Override
    public final CrawledPage visit(final String url, final Document document) {
        PageMeta meta = new PageMeta(getExternalId(url), parses(), url, getTags(document), getDate(document))
        PageContent content = new PageContent(getTitle(document), getBody(document))
        return new CrawledPage(meta, content)
    }

    protected abstract boolean shouldParse(String url)

    protected abstract Date getDate(Document doc)

    protected abstract String getTitle(Document doc)

    protected abstract String getBody(Document doc)

    protected abstract Set<String> getTags(Document doc)

    protected abstract String getExternalId(String url)

}
