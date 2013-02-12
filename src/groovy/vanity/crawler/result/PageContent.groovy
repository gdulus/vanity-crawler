package vanity.crawler.result

import grails.validation.Validateable

@Validateable
class PageContent {

    final String title

    final String body

    static constraints = {
        title(nullable: false, blank:false)
        body(nullable: false, blank:false)
    }

    PageContent(String title, String body) {
        this.title = title
        this.body = body
    }

    @Override
    public String toString() {
        return "PageContent{" +
                "title='" + title + '\'' +
                '}';
    }
}
