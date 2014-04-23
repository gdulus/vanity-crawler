@Grapes(
    @Grab(group = 'org.jsoup', module = 'jsoup', version = '1.7.3')
)

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

def address = "http://www.fakt.pl/dlaczego-obawiamy-sie-google-,artykuly,456664,1.html"
def html = address.toURL().text
Document doc = Jsoup.parse(html)

println "Title: " + doc.select('h1')?.first()?.text()
println "Date 1: " + doc.select('span.sourcedate')
println "Date 2: " + Date.parse('dd.MM.yyyy HH:mm', doc.select('span.sourcedate')?.first()?.text())
println "Body: " + doc.select('p.lead')?.first()?.text()
println "Tags: " + doc.select('.tags a').collect { it.text() }
def matcher = (address =~ '(\\d+),(\\d+),(\\d+)')
println "External id: " + matcher[0].first()