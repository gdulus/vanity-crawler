package vanity.crawler.parser.result

final class CrawledPage implements Serializable {

    private static final long serialVersionUID = - 7712634726924094391L

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

    public String getErrors(){
        return "[${meta.errors}] AND [${content.errors}]"
    }
}
