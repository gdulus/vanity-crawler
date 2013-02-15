package vanity.crawler.spider.result

import grails.validation.Validateable

@Validateable
class PageMeta implements Serializable {

    private static final long serialVersionUID = - 5425084408155645285L ;

    final String url

    final Set<String> tags

    final Date date

    static constraints = {
        url(nullable: false, blank:false, url: true)
        tags(minSize: 1)
        date(nullable: false)
    }

    PageMeta(String url, Set<String> tags, Date date) {
        this.url = url
        this.tags = tags ?: Collections.emptySet()
        this.date = date
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
