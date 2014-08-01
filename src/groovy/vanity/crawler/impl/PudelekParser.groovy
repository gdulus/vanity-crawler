package vanity.crawler.impl

import org.apache.commons.lang.StringUtils
import org.jsoup.nodes.Document
import vanity.article.ContentSource
import vanity.crawler.parser.AbstractParser

class PudelekParser extends AbstractParser {

    @Override
    ContentSource.Target parses() {
        return ContentSource.Target.PUDELEK
    }

    @Override
    protected boolean shouldParse(final String url) {
        // to prevent /miko_ma_juz_nowego_faceta_przystojny/5/
        return url.contains('/artykul/') && !url.tokenize('/').last().isNumber()
    }

    @Override
    protected Date getDate(final String url, final Document doc) {
        String date = doc.select('span.time')?.first()?.text()?.tokenize()?.last()
        return date ? Date.parse('dd.MM.yyyy', date) : null
    }

    @Override
    protected String getTitle(final String url, final Document doc) {
        return doc.select('.single-entry__header h1')?.first()?.text()
    }

    @Override
    protected String getBody(final String url, final Document doc) {
        return doc.select('div.single-entry-text.bbtext')?.first()?.text()
    }

    @Override
    protected Set<String> getTags(final String url, final Document doc) {
        return doc.select('.inline-tags a')?.collect({ it.text() }) as Set<String>
    }

    @Override
    protected String getExternalId(final String url) {
        def matcher = (url =~ 'artykul/(\\d+)/')

        if (!matcher) {
            return StringUtils.EMPTY
        }

        return (matcher[0] && matcher[0].last()?.isNumber() ? matcher[0].last() : null)
    }
}
