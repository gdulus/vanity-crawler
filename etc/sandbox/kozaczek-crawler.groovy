@Grapes(
    @Grab(group = 'org.jsoup', module = 'jsoup', version = '1.7.3')
)

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

def address = "http://www.kozaczek.pl/plotka.php?id=67666"
def html = address.toURL().text
Document doc = Jsoup.parse(html)

println "Title: " + doc.select('h1.header')?.first()?.text()?.replaceAll("\u0000", '')
println "Date: " + Date.parse('dd-MM-yy', doc.select('.plotki_header > span')?.first()?.text()?.tokenize()?.last())
println "Body: " + doc.select('.plotka_content')?.first()?.text()?.replaceAll("\u0000", '')
println "Tags: " + doc.select('.tags a').collect { it?.text()?.replaceAll("\u0000", '') }
def matcher = (address =~ 'id=(\\d+)')
println "External id: " + matcher[0].first()