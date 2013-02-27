package vanity.crawler.processor

import org.codehaus.groovy.grails.commons.GrailsApplication

class WkhtmlToImageWebPageImageProvider implements WebPageImageProvider {

    private static final String FILE_FORMAT = 'jpg'

    GrailsApplication grailsApplication

    InputStream getImage(final String url) {
        CutyCapt cutyCut = new CutyCapt(url, getFileName(url))
        cutyCut.setNoJavaScript()
        return new FileInputStream(cutyCut.execute())
    }

    private String getFileName(final String url){
        String fileName = url.encodeAsMD5()
        return "${grailsApplication.config.crawler.screenshotsFolder}/${fileName}.${FILE_FORMAT}"
    }
}
