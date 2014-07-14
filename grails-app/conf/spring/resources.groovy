import org.apache.activemq.broker.region.policy.PolicyEntry
import org.apache.activemq.broker.region.policy.PolicyMap
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter
import vanity.crawler.impl.FaktParser
import vanity.crawler.impl.KozaczekParser
import vanity.crawler.impl.PlotekParser
import vanity.crawler.impl.PudelekParser
import vanity.crawler.jms.MessageBus
import vanity.crawler.parser.ParserConfigFactory
import vanity.crawler.parser.ParserFactory
import vanity.crawler.processor.post.PostProcessorChain
import vanity.crawler.processor.post.indexer.IndexerPostProcessor
import vanity.crawler.processor.post.webImage.CutyCaptWebPageImageProvider
import vanity.crawler.spider.CrawlerExecutor
import vanity.crawler.parser.ParserFactory
import vanity.crawler.spider.CrawlerMonitor

// Place your Spring DSL code here
beans = {

    /**
     * Processing wiring
     */
    postProcessorChain(PostProcessorChain)

    /**
     * Indexing
     */
    indexerPostProcessor(IndexerPostProcessor)

    /**
     * Processing wiring
     */
    webPageImageProvider(CutyCaptWebPageImageProvider)
    // webImagePostProcessor(WebImagePostProcessor) For its switched off

    /**
     * Crawler wiring
     */
    crawlerMonitor(CrawlerMonitor)
    crawlerFactory(ParserFactory)
    parserConfigFactory(ParserConfigFactory)
    crawlerExecutor(CrawlerExecutor)
    faktParser(FaktParser)
    kozaczekParser(KozaczekParser)
    plotekParser(PlotekParser)
    pudelekParser(PudelekParser)

    /**
     * JMS wiring
     */
    mesageBus(MessageBus){bean ->
        bean.constructorArgs = [ref('jmsService')]
    }

    crawlerBroker(org.apache.activemq.xbean.XBeanBrokerService) {bean ->
        // broker name
        brokerName = 'crawler'
        // jmx yes, lets see what happen inside
        useJmx = true
        // persistence config - Kahadb
        persistent = true
        def kahaDBPersistenceAdapter =  new KahaDBPersistenceAdapter()
        kahaDBPersistenceAdapter.directory = new File(application.config.crawler.jms.storage.location)
        persistenceAdapter = kahaDBPersistenceAdapter
        // enable producer flow control
        def policyMap = new PolicyMap();
        def defaultPolicy = new PolicyEntry();
        defaultPolicy.producerFlowControl = true
        policyMap.setDefaultEntry(defaultPolicy)
        destinationPolicy = policyMap
    }

    jmsConnectionFactory(org.apache.activemq.ActiveMQConnectionFactory) {
    	brokerURL = 'vm://crawler?create=false'
    }

    pooledConnectionFactory(org.apache.activemq.pool.PooledConnectionFactory) { bean ->
        bean.initMethod = 'start'
        bean.destroyMethod = 'stop'
        maxConnections = 50
        connectionFactory = ref('jmsConnectionFactory')
    }

}
