package vanity.crawler.result

import grails.validation.Validateable

@Validateable
class PageMeta {

    final String url

    final Set<String> tags

    static constraints = {
        url(nullable: false, blank:false, url: true)
    }

    PageMeta(String url, Set<String> tags) {
        this.url = url
        this.tags = tags ?: Collections.emptySet()
    }

    @Override
    public String toString() {
        return "PageMeta{" +
                "url='" + url + '\'' +
                ", tags=" + tags +
                '}';
    }

}
