package vanity.crawler.spider

import groovy.transform.PackageScope
import org.apache.commons.lang.StringUtils
import org.jsoup.nodes.Document
import vanity.article.ContentSource

@PackageScope
class PlotekCrawler extends Crawler {

    PlotekCrawler() {
        super(ContentSource.Target.PLOTEK)
    }

    @Override
    protected boolean shouldParse(final String url) {
        return (getExternalId(url) != StringUtils.EMPTY)
    }

    @Override
    protected Date getDate(final Document doc) {
        String date = doc.select('#gazeta_article_date')?.first()?.text()
        return date ? Date.parse('dd.MM.yyyy HH:mm', date) : null
    }

    @Override
    protected String getTitle(final Document doc) {
        return doc.select('#gazeta_article h1')?.first()?.text()
    }

    @Override
    protected String getBody(final Document doc) {
        return doc.select('div.cmsArtykulElem')?.first()?.text()
    }

    @Override
    protected Set<String> getTags(final Document doc) {
        return doc.select('#gazeta_article_tags li').collect { it.text() } as Set<String>
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
