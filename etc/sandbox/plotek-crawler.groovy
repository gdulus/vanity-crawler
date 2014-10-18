@Grapes(
    @Grab(group = 'org.jsoup', module = 'jsoup', version = '1.7.3')
)

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

def address = "http://www.plotek.pl/plotek/1,78649,15832144,Kinga_Rusin_nie_spedza_swiat_w_Polsce__W_Lany_Poniedzialek.html"
def html = address.toURL().text
Document doc = Jsoup.parse(html)

println "Title: " + doc.select('#gazeta_article h1')?.first()?.text()
println "Date: " + Date.parse('dd.MM.yyyy HH:mm', doc.select('#gazeta_article_date')?.first()?.text())
println "Body: " + doc.select('div.cmsArtykulElem')?.first()?.text()
println "Tags: " + doc.select('#gazeta_article_tags li').collect { it.text() }

def matcher = (address =~ 'plotek/(\\d+)/')
def externalId = address.split('/plotek/').last().split(',')[0..2].join(',')

println "matcher: " + matcher

def first = externalId.split(',').first()
def videoList = first == '1'
println "video plotka: " + videoList
