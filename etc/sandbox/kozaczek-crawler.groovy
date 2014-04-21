@Grapes(
    @Grab(group = 'org.jsoup', module = 'jsoup', version = '1.7.3')
)

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

def address = "http://www.kozaczek.pl/plotka.php?id=53085"
def html = address.toURL().text
Document doc = Jsoup.parse(html)

println "Title: " + doc.select('#gazeta_article h1')?.first()?.text()
println "Date: " + Date.parse('dd.MM.yyyy HH:mm', doc.select('#gazeta_article_date')?.first()?.text())
println "Body: " + doc.select('div.cmsArtykulElem')?.first()?.text()
println "Tags: " + doc.select('#gazeta_article_tags li').collect { it.text() }
def matcher = (address =~ '(\\d+),(\\d+),(\\d+)')
println "External id: " + matcher[0].first()