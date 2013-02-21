package vanity.crawler.spider.result

import grails.validation.Validateable
import vanity.ContentSource

@Validateable
class PageMeta implements Serializable {

    private static final long serialVersionUID = - 5425084408155645285L ;

    final ContentSource source

    final String url

    final Set<String> tags

    final Date date

    PageMeta(ContentSource source, String url, Set<String> tags, Date date) {
        this.source = source
        this.url = url
        this.tags = tags ?: Collections.emptySet()
        this.date = date
    }

    static constraints = {
        source(nullable: false)
        url(nullable: false, blank:false, url: true)
        tags(minSize: 1)
        date(nullable: false)
    }

    @Override
    public String toString() {
        return "PageMeta{" +
                "url='" + url + '\'' +
                ", tags=" + tags + '\'' +
                ", date=" + date +
                '}';
    }

}