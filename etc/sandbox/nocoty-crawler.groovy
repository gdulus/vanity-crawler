@Grapes(
    @Grab(group = 'org.jsoup', module = 'jsoup', version = '1.7.3')
)

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

def address = "http://nocoty.pl/gid,16790171,kat,1013547,opage,10,title,Rafal-Brzozowski-bedzie-ojcem,galeria.html#opinie"
def html = address.toURL().text
Document doc = Jsoup.parse(html)

println "Title: " + doc.select('h1.galeria')?.first()?.text()
println "Date: " + Date.parse('yyyy-MM-dd', doc.select('.fotkaBx p')?.first()?.text())
println "Body: " + doc.select('div.galeriaPrawyBx div.body p')?.first()?.text()
println "Tags: " + doc.select('.galleryTags a')?.collect({ it.text() })

def matcher = (address =~ 'gid,(\\d+)')
println "External id: " + matcher[0].first()

println '-' * 50

address = "http://nocoty.pl/title,Donatan-Ch-dupa-i-kamieni-kupa,wid,16783207,wiadomosc.html"
html = address.toURL().text
doc = Jsoup.parse(html)

println "Title: " + doc.select('h1.mainHd')?.first()?.text()
println "Date: " + Date.parse('yyyy-MM-dd (HH:mm)', doc.select('.zajawka_sub_data')?.first()?.text())
println "Tags: " + doc.select('.galleryTags a')?.collect({ it.text() })

doc = doc.clone()
doc.select('.galleryTags').remove()
doc.select('.nius div').remove()
doc.select('.nius table').remove()

println "Body: " + doc.select('.nius')?.first()?.text()


matcher = (address =~ 'wid,(\\d+)')
println "External id: " + matcher[0].first()