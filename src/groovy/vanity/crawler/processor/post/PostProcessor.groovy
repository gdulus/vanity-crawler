package vanity.crawler.processor.post

import org.springframework.core.Ordered
import vanity.article.Article

public interface PostProcessor extends Ordered {

    public void process(Article article)

}