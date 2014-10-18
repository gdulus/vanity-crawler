package vanity.crawler.impl

import org.jsoup.nodes.Document
import vanity.article.ContentSource
import vanity.crawler.parser.AbstractParser

class FaktParser extends AbstractParser {

    @Override
    ContentSource.Target parses() {
        return ContentSource.Target.FAKT
    }

    @Override
    protected boolean shouldParse(final String url) {
        return url.contains(',artykuly,')
    }

    @Override
    protected Date getDate(final String url, final Document doc) {
        return doGetDate('span.datetime time', doc) ?: doGetDate('span.datetime', doc)
    }

    private Date doGetDate(final String selector, final Document doc) {
        String date = doc.select(selector)?.first()?.text()
        return date ? Date.parse('dd.MM.yyyy HH:mm', date) : null
    }

    @Override
    protected String getTitle(final String url, final Document doc) {
        return doc.select('header h1')?.first()?.text()
    }

    @Override
    protected String getBody(final String url, final Document doc) {
        String lead = doc.select('p.lead')?.first()?.text()
        return lead ?: getTitle(url, doc)
    }

    @Override
    protected Set<String> getTags(final String url, final Document doc) {
        return doc.select('.tags a')?.collect({ it.text() }) as Set<String>
    }

    @Override
    protected String getExternalId(final String url) {
        def matcher = (url =~ ',artykuly,(\\d+),')
        return (matcher[0] && matcher[0].last()?.isNumber() ? matcher[0].last() : null)
    }
}
