package vanity.crawler.processor.post.indexer

import org.springframework.beans.factory.annotation.Autowired
import vanity.article.Article
import vanity.crawler.processor.post.PostProcessor
import vanity.search.ArticleDocument
import vanity.search.SearchEngineIndexer

class IndexerPostProcessor implements PostProcessor {

    private static final int ORDER = 1

    @Autowired
    SearchEngineIndexer searchEngineIndexer

    void process(final Article article) {
        ArticleDocument document = new ArticleDocument(
            article.hash,
            article.title,
            article.body,
            article.source.toString(),
            article.publicationDate,
            article.tags.collect {it.name} as Set
        )
        searchEngineIndexer.indexArticle(document)
    }

    int getOrder() {
        return ORDER
    }
}
