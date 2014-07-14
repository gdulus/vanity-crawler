package vanity.crawler.parser.source

import groovy.transform.ToString
import vanity.article.ContentSource

@ToString(includes = ['source', 'url', 'depth'])
class CrawlSourceCommand implements Serializable {

    private static final long serialVersionUID = -9108491426544698274L;

    final ContentSource.Target source

    final String url

    final int depth

    CrawlSourceCommand(ContentSource.Target source, String url, int depth) {
        this.source = source
        this.url = url
        this.depth = depth
    }

    CrawlSourceCommand(String url, CrawlSourceCommand parentCommand) {
        this.url = url
        this.source = parentCommand.source
        this.depth = parentCommand.depth + 1
    }

}
