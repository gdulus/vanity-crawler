package vanity.crawler.processor.post.indexer

import org.springframework.beans.factory.annotation.Autowired
import vanity.article.Article
import vanity.article.Tag
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
            article.source.target,
            article.publicationDate,
            flatTagSet(article.tags as Set, [] as Set<String>)
        )
        searchEngineIndexer.indexArticle(document)
    }

    private Set<String> flatTagSet(final Set<Tag> tags, final Set<String> tagsNames){
        tags.each {final Tag tag ->
            if (tag.hasChildren()){
                flatTagSet(tag.childTags, tagsNames)
            }

            tagsNames << tag.name
        }

        return tagsNames
    }

    int getOrder() {
        return ORDER
    }
}
