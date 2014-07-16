package vanity.crawler.parser

import org.springframework.beans.factory.annotation.Value
import vanity.article.ContentSource

class ParserConfigFactory {

    @Value('${crawler.maxDepthOfCrawling}')
    public int maxDepthOfCrawling

    @Value('${crawler.politenessDelay}')
    public int politenessDelay

    @Value('${crawler.userAgentName}')
    public String userAgentName

    public ParserConfig produce(final ContentSource.Target contentSourceTarget) {
        return new ParserConfig(
            maxDepthOfCrawling: maxDepthOfCrawling,
            politenessDelay: politenessDelay,
            userAgentName: userAgentName,
            timeout: 50000
        )
    }

}
