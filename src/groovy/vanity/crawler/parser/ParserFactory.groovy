package vanity.crawler.parser

import org.springframework.beans.factory.annotation.Autowired
import vanity.article.ContentSource

class ParserFactory {

    @Autowired
    List<Parser> parsers

    public Parser produce(final ContentSource.Target contentSourceTarget) {
        Parser parser = parsers.find { it.parses() == contentSourceTarget }

        if (!parser) {
            throw new IllegalArgumentException("Cant find parser for ${contentSourceTarget}")
        }

        return parser
    }

}
