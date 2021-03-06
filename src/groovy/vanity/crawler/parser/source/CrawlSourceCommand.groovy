package vanity.crawler.parser.source

import groovy.transform.ToString
import vanity.article.ContentSource

@ToString(includes = ['source', 'url', 'depth'])
class CrawlSourceCommand implements Serializable {

    private static final long serialVersionUID = 470094655704172768L;

    final ContentSource.Target source

    final String url

    final int depth

    CrawlSourceCommand(ContentSource.Target source, String url, int depth) {
        this.source = source
        this.url = prepareURL(source, url)
        this.depth = depth
    }

    CrawlSourceCommand(String url, CrawlSourceCommand parentCommand) {
        this.url = prepareURL(parentCommand.source, url)
        this.source = parentCommand.source
        this.depth = parentCommand.depth + 1
    }

    private String prepareURL(ContentSource.Target source, String url) {
        if (url.startsWith('http')){
            return url
        }

        if (url.startsWith(source.address)) {
            return url
        }

        if (!url || url == '/') {
            return source.address
        }

        if (url.startsWith('/')) {
            url = url[1..-1]
        }

        return "${source.address}${url}"
    }

}
