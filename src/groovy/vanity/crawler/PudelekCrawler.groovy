package vanity.crawler

import groovy.transform.PackageScope
import org.jsoup.nodes.Document
import vanity.ContentSource

@PackageScope
class PudelekCrawler extends Crawler {

    PudelekCrawler() {
        super(ContentSource.PUDELEK)
    }

    @Override
    protected boolean shouldVisit(String url) {
        return url.contains('artykul')
    }

    @Override
    protected String getTitle(final Document doc) {
        return doc.select(".header h1")?.first()?.text()
    }

    @Override
    protected String getBody(final Document doc) {
        return doc.select("div.single-entry-text.bbtext")?.first()?.text()
    }

    @Override
    protected Set<String> getTags(final Document doc) {
        return doc.select(".inline-tags a")?.collect({it.text()}) as Set<String>
    }
}
