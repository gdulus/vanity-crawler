package vanity.crawler.processor.post.indexer

import org.springframework.beans.factory.annotation.Autowired
import vanity.article.Article
import vanity.crawler.processor.post.PostProcessor
import vanity.search.SearchEngineIndexer

class IndexerPostProcessor implements PostProcessor {

    private static final int ORDER = 1

    @Autowired
    SearchEngineIndexer searchEngineIndexer

    void process(final Article article) {
        //searchEngineIndexer.indexArticle(Document.asArticleDocument(article))
    }

    int getOrder() {
        return ORDER
    }
}
