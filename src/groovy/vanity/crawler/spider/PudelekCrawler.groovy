package vanity.crawler.spider

import groovy.transform.PackageScope
import org.jsoup.nodes.Document
import vanity.article.ContentSource

@PackageScope
class PudelekCrawler extends Crawler {

    PudelekCrawler() {
        super(ContentSource.Target.PUDELEK)
    }

    @Override
    protected boolean shouldVisit(String url) {
        // to prevent /miko_ma_juz_nowego_faceta_przystojny/5/
        return url.contains('/artykul/') && !url.tokenize('/').last().isNumber()
    }

    @Override
    protected Date getDate(final Document doc) {
        String date = doc.select('span.time')?.first()?.text()?.tokenize()?.last()
        return date ? Date.parse('dd.MM.yyyy', date) : null
    }

    @Override
    protected String getTitle(final Document doc) {
        return doc.select('.header h1')?.first()?.text()
    }

    @Override
    protected String getBody(final Document doc) {
        return doc.select('div.single-entry-text.bbtext')?.first()?.text()
    }

    @Override
    protected Set<String> getTags(final Document doc) {
        return doc.select('.inline-tags a')?.collect({ it.text() }) as Set<String>
    }

    @Override
    protected String getExternalId(final String url) {
        def matcher = (url =~ 'artykul/(\\d+)/')
        return (matcher[0] && matcher[0].last()?.isNumber() ? matcher[0].last() : null)
    }
}
