grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
//grails.project.fork = [
//   run: [maxMemory:1024, minMemory:64, debug:false, maxPerm:256]
//]

grails.project.dependency.resolution = {
    inherits("global") {}
    log "error"          // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true       // Whether to verify checksums on resolve
    legacyResolve false  // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true    // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        compile('edu.uci.ics:crawler4j:3.4')
        compile('org.jsoup:jsoup:1.7.2')
        compile('org.apache.activemq:activemq-pool:5.7.0')
        compile('org.apache.activemq:activemq-core:5.7.0'){ excludes 'slf4j-log4j12' }
        compile('postgresql:postgresql:9.1-901.jdbc4')
    }

    plugins {
        runtime ":jquery:1.8.3"
        runtime ":resources:1.1.6"
        runtime ":database-migration:1.2.1"
        runtime ":hibernate:$grailsVersion"
        build ":tomcat:$grailsVersion"
        compile ':cache:1.0.1'
        compile ":quartz:1.0-RC5"
        compile ":jms:1.2"
    }
}

grails.plugin.location.'vanity-core' = '../vanity-core'

