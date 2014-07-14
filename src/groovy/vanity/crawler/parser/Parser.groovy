package vanity.crawler.parser

import org.jsoup.nodes.Document
import vanity.article.ContentSource
import vanity.crawler.parser.result.CrawledPage

interface Parser {

    public ContentSource.Target parses()

    public boolean shouldVisit(final String url)

    public CrawledPage visit(final String url, final Document document)

}
