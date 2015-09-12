package vanity.crawler

import vanity.article.ContentSource

class StatsTagLib {

    static defaultEncodeAs = 'raw'

    static namespace = 'v'

    def contentSourceColor = { attrs ->
        ContentSource.Target target = attrs.remove('target')
        out << getColor(target)
    }

    def contentSourceColorLegent = { attrs ->
        out << '<ul class="list-unstyled legend">'
        ContentSource.Target.each {
            out << "<li class='pull-left' style='color:${getColor(it)}'>"
            out << it.name()
            out << '</li>'
        }
        out << '</ul>'
    }

    private String getColor(final ContentSource.Target target) {
        switch (target) {
            case (ContentSource.Target.FAKT):
                return '#C5AAF5'
            case (ContentSource.Target.KOZACZEK):
                return '#A3CBF1'
            case (ContentSource.Target.NOCOTY):
                return '#79BFA1'
            case (ContentSource.Target.PLOTEK):
                return '#F5A352'
            case (ContentSource.Target.PUDELEK):
                return '#FB7374'
            default:
                return '#423C40'
        }
    }
}
