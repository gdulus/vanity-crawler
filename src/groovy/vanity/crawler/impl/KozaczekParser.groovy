package vanity.crawler.impl

import org.apache.commons.lang.StringUtils
import org.jsoup.nodes.Document
import vanity.article.ContentSource
import vanity.crawler.parser.AbstractParser

class KozaczekParser extends AbstractParser {

    @Override
    ContentSource.Target parses() {
        return ContentSource.Target.KOZACZEK
    }

    @Override
    protected boolean shouldParse(final String url) {
        return (getExternalId(url) != StringUtils.EMPTY)
    }

    @Override
    protected Date getDate(final String url, final Document doc) {
        String date = doc.select('.plotki_header > span')?.first()?.text()?.tokenize()?.last()
        return date ? Date.parse('dd-MM-yy', date) : null
    }

    @Override
    protected String getTitle(final String url, final Document doc) {
        return removeNullCharacters(doc.select('h1.header')?.first()?.text())
    }

    @Override
    protected String getBody(final String url, final Document doc) {
        return removeNullCharacters(doc.select('.plotka_content')?.first()?.text())
    }

    @Override
    protected Set<String> getTags(final String url, final Document doc) {
        return doc.select('.tags a').collect { removeNullCharacters(it.text()) } as Set<String>
    }

    @Override
    protected String getExternalId(final String url) {
        def matcher = (url =~ 'id=(\\d+)')

        if (!matcher) {
            return StringUtils.EMPTY
        }

        return (matcher[0] && matcher[0].last()?.isNumber() ? matcher[0].last() : StringUtils.EMPTY)
    }

    private String removeNullCharacters(final String string) {
        string ? string.replaceAll("\u0000", '') : string
    }
}
