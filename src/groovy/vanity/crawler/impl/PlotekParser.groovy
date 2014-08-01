package vanity.crawler.impl

import org.apache.commons.lang.StringUtils
import org.jsoup.nodes.Document
import vanity.article.ContentSource
import vanity.crawler.parser.AbstractParser

class PlotekParser extends AbstractParser {

    @Override
    ContentSource.Target parses() {
        return ContentSource.Target.PLOTEK
    }

    @Override
    protected boolean shouldParse(final String url) {
        String externalId = getExternalId(url)

        if (!externalId) {
            return false
        }
        // list video plotek zaczyna sie z 0
        return externalId.split(',').first() != '0'
    }

    @Override
    protected Date getDate(final String url, final Document doc) {
        String date = doc.select('#gazeta_article_date')?.first()?.text()
        return date ? Date.parse('dd.MM.yyyy HH:mm', date) : null
    }

    @Override
    protected String getTitle(final String url, final Document doc) {
        return doc.select('#gazeta_article h1')?.first()?.text()
    }

    @Override
    protected String getBody(final String url, final Document doc) {
        return doc.select('div.cmsArtykulElem')?.first()?.text()
    }

    @Override
    protected Set<String> getTags(final String url, final Document doc) {
        return doc.select('#gazeta_article_tags li a').collect { it.text() } as Set<String>
    }

    @Override
    protected String getExternalId(final String url) {
        def matcher = (url =~ '(\\d+),(\\d+),(\\d+)')

        if (!matcher) {
            return StringUtils.EMPTY
        }

        return (matcher[0] && matcher[0][0] ? matcher[0][0] : StringUtils.EMPTY)
    }
}
