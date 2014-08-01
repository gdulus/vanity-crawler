package vanity.crawler.impl

import org.jsoup.nodes.Document
import vanity.article.ContentSource
import vanity.crawler.parser.AbstractParser

class NocotyParser extends AbstractParser {

    private ParseManager parseManager = new ParseManager()

    @Override
    ContentSource.Target parses() {
        return ContentSource.Target.NOCOTY
    }

    @Override
    protected boolean shouldParse(final String url) {
        return parseManager.shouldParse(url)
    }

    @Override
    protected Date getDate(final String url, final Document doc) {
        return parseManager.produce(url)?.getDate(doc)
    }

    @Override
    protected String getTitle(final String url, final Document doc) {
        return parseManager.produce(url)?.getTitle(doc)
    }

    @Override
    protected String getBody(final String url, final Document doc) {
        return parseManager.produce(url)?.getBody(doc)
    }

    @Override
    protected Set<String> getTags(final String url, final Document doc) {
        return parseManager.produce(url)?.getTags(doc)
    }

    @Override
    protected String getExternalId(final String url) {
        return parseManager.produce(url)?.getExternalId(url)
    }

    public class ParseManager {

        private final Parser galleryParser = new GalleryParser()

        private final Parser newsParser = new NewsParser()

        public Parser produce(final String url) {
            switch (true) {
                case (galleryParser.getExternalId(url) != null):
                    return galleryParser
                case (newsParser.getExternalId(url) != null):
                    return newsParser
                default:
                    return null
            }
        }

        public boolean shouldParse(final String url) {
            return produce(url) != null
        }
    }

    private static class NewsParser implements Parser {

        @Override
        Date getDate(final Document doc) {
            return Date.parse('yyyy-MM-dd (HH:mm)', doc.select('.zajawka_sub_data')?.first()?.text())
        }

        @Override
        String getTitle(final Document doc) {
            return doc.select('h1.mainHd')?.first()?.text()
        }

        @Override
        String getBody(final Document doc) {
            Document docCopy = doc.clone()
            docCopy.select('.galleryTags').remove()
            docCopy.select('.nius div').remove()
            docCopy.select('.nius table').remove()
            return docCopy.select('.nius')?.first()?.text()
        }

        @Override
        Set<String> getTags(final Document doc) {
            return doc.select('.galleryTags a')?.collect({ it.text() }) as Set
        }

        @Override
        String getExternalId(final String url) {
            def matcher = (url =~ 'wid,(\\d+)')
            return matcher && matcher[0] && matcher[0][0] ? matcher[0][0] : null
        }
    }

    private static class GalleryParser implements Parser {

        @Override
        Date getDate(final Document doc) {
            return Date.parse('yyyy-MM-dd', doc.select('.fotkaBx p')?.first()?.text())
        }

        @Override
        String getTitle(final Document doc) {
            return doc.select('h1.galeria')?.first()?.text()
        }

        @Override
        String getBody(final Document doc) {
            return doc.select('div.galeriaPrawyBx div.body p')?.first()?.text()
        }

        @Override
        Set<String> getTags(final Document doc) {
            return doc.select('.galleryTags a')?.collect({ it.text() }) as Set
        }

        @Override
        String getExternalId(final String url) {
            def matcher = (url =~ 'gid,(\\d+)')
            return matcher && matcher[0] && matcher[0][0] ? matcher[0][0] : null
        }
    }

    private static interface Parser {

        public Date getDate(final Document doc)

        public String getTitle(final Document doc)

        public String getBody(final Document doc)

        public Set<String> getTags(final Document doc)

        public String getExternalId(final String url)
    }
}
