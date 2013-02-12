package vanity.crawler.result

final class CrawledPage {

    final PageMeta meta

    final PageContent content

    CrawledPage(PageMeta meta, PageContent content) {
        this.meta = meta
        this.content = content
    }

    @Override
    public String toString() {
        return "CrawledPage{" +
                "meta=" + meta +
                ", content=" + content +
                '}';
    }

    public boolean validate(){
        return (meta && content && meta.validate() && content.validate())
    }
}
