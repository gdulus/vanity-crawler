package vanity.crawler

import vanity.ContentSource

import javax.annotation.PostConstruct

final class CrawlerFactory {

    private final Map<ContentSource, Class<? extends Crawler>> CACHE = [:]

    public Class<? extends Crawler> produce(final ContentSource contentSource){
        if (!CACHE.containsKey(contentSource)){
            throw new IllegalArgumentException("No crawler for source ${contentSource}")
        }

        return CACHE[contentSource]
    }

    @PostConstruct
    public void init(){
        CACHE[ContentSource.PUDELEK] = PudelekCrawler
    }
}
