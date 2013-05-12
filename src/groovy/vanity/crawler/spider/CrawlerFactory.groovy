package vanity.crawler.spider

import vanity.article.ContentSource

import javax.annotation.PostConstruct

class CrawlerFactory {

    private final Map<ContentSource.Target, Class<? extends Crawler>> CACHE = [:]

    public Class<? extends Crawler> produce(final ContentSource.Target contentSourceTarget){
        if (!CACHE.containsKey(contentSourceTarget)){
            throw new IllegalArgumentException("No crawler for source ${contentSourceTarget}")
        }

        return CACHE[contentSourceTarget]
    }

    @PostConstruct
    public void init(){
        CACHE[ContentSource.Target.PUDELEK] = PudelekCrawler
    }
}
