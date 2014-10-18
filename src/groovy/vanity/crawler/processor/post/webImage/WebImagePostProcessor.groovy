package vanity.crawler.processor.post.webImage

import vanity.article.Article
import vanity.crawler.processor.post.PostProcessor

class WebImagePostProcessor implements PostProcessor {

    private static final int ORDER = 2

    WebPageImageProvider webPageImageProvider

    void process(Article article) {
        // TODO implement
    }

    int getOrder() {
        return ORDER
    }
}
