package vanity.crawler

import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.parser.HtmlParseData
import groovy.transform.PackageScope
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
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
    protected void parse(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println('-------------------------')
        System.out.println("URL: " + url);

        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        Document doc = Jsoup.parse(htmlParseData.html)
        Element title = doc.select(".header h1").first()
        System.out.println(title.text())
    }
}
