package vanity.crawler.processor.post

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.AnnotationAwareOrderComparator
import vanity.article.Article

import javax.annotation.PostConstruct

class PostProcessorChain {

    @Autowired
    List<PostProcessor> processors;

    @PostConstruct
    public void init() {
        Collections.sort(processors, AnnotationAwareOrderComparator.INSTANCE);
    }

    public void execute(final Article article) {
        for (PostProcessor processor : processors) {
            processor.process(article)
        }
    }
}
