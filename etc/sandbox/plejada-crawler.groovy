@Grapes(
    @Grab(group = 'org.jsoup', module = 'jsoup', version = '1.7.3')
)

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

def dateMapp = ['sty': '01', 'lut': '02', 'mar': '03', 'kwi': '04', 'maj': '05', 'cze': '06', 'lip': '07', 'sie': '08', 'wrz': '09', 'paź': '10', 'paz': '10', 'lis': '11', 'gru': '12']

def address = "http://plejada.onet.pl/przetakiewicz-ubrala-kolejna-zagraniczna-gwiazde/wx1hr"
def html = address.toURL().text
Document doc = Jsoup.parse(html)

println "Title: " + doc.select('.contentHeader h1')?.first()?.text()
println "Date1: " + doc.select('.contentHeader .meta')?.first()?.text()?.tokenize('|')?.first()
//println "Date2: " + Date.parse('dd m, HH:mm', doc.select('.contentHeader .meta')?.first()?.text()?.tokenize('|')?.first())
println "Body: " + doc.select('div.contentBody p')?.collect({ it.text() }).join(' ')
println "Tags: " + doc.select('#gazeta_article_tags li').collect { it.text() }

def matcher = (address =~ '\\w{5}$')
println "External id: " + matcher[0]