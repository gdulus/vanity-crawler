package vanity.crawler.spider

import org.apache.commons.lang.StringUtils
import org.jsoup.nodes.Document
import vanity.article.ContentSource

class KozaczekCrawler extends Crawler {

    KozaczekCrawler() {
        super(ContentSource.Target.KOZACZEK)
    }

    @Override
    protected boolean shouldParse(final String url) {
        return (getExternalId(url) != StringUtils.EMPTY)
    }

    @Override
    protected Date getDate(final Document doc) {
        String date = doc.select('.plotki_header > span')?.first()?.text()?.tokenize()?.last()
        return date ? Date.parse('dd.MM.yyyy', date) : null
    }

    @Override
    protected String getTitle(final Document doc) {
        return doc.select('h1.header')?.first()?.text()
    }

    @Override
    protected String getBody(final Document doc) {
        return doc.select('.plotka_content')?.first()?.text()
    }

    @Override
    protected Set<String> getTags(final Document doc) {
        return doc.select('.tags a').collect { it.text() } as Set<String>
    }

    @Override
    protected String getExternalId(final String url) {
        def matcher = (url =~ 'id=(\\d+)')

        if (!matcher) {
            return StringUtils.EMPTY
        }

        return (matcher[0] && matcher[0].last()?.isNumber() ? matcher[0].last() : StringUtils.EMPTY)
    }
}
