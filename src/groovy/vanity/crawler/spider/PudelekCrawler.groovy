package vanity.crawler.spider

import groovy.transform.PackageScope
import org.jsoup.nodes.Document
import vanity.ContentSource

@PackageScope
class PudelekCrawler extends Crawler {

    private static final String URL_REQUIRED_ELEMENT = '/artykul/'

    private static final String DATE_SELECTOR = 'span.time'

    private static final String DATE_FORMAT= 'dd.MM.yyyy'

    private static final String TITLE_SELECTOR = '.header h1'

    private static final String BODY_SELECTOR = 'div.single-entry-text.bbtext'

    private static final String TAGS_SELECTOR = '.inline-tags a'

    PudelekCrawler() {
        super(ContentSource.PUDELEK)
    }

    @Override
    protected boolean shouldVisit(String url) {
        // to prevent /miko_ma_juz_nowego_faceta_przystojny/5/
        return url.contains(URL_REQUIRED_ELEMENT) && !url.tokenize('/').last().isNumber()
    }

    @Override
    protected Date getDate(final Document doc) {
        String date =  doc.select(DATE_SELECTOR)?.first()?.text()?.tokenize()?.last()
        return date ? Date.parse(DATE_FORMAT, date) : null
    }

    @Override
    protected String getTitle(final Document doc) {
        return doc.select(TITLE_SELECTOR)?.first()?.text()
    }

    @Override
    protected String getBody(final Document doc) {
        return doc.select(BODY_SELECTOR)?.first()?.text()
    }

    @Override
    protected Set<String> getTags(final Document doc) {
        return doc.select(TAGS_SELECTOR)?.collect({it.text()}) as Set<String>
    }
}
