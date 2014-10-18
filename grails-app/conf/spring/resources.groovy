import org.apache.activemq.broker.region.policy.PolicyEntry
import org.apache.activemq.broker.region.policy.PolicyMap
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter
import vanity.crawler.impl.*
import vanity.crawler.jms.MessageBus
import vanity.crawler.parser.ParserConfigFactory
import vanity.crawler.parser.ParserFactory
import vanity.crawler.processor.post.PostProcessorChain
import vanity.crawler.processor.post.indexer.IndexerPostProcessor
import vanity.crawler.processor.post.webImage.CutyCaptWebPageImageProvider
import vanity.crawler.spider.CrawlerExecutionSynchronizer
import vanity.crawler.spider.CrawlerExecutor

// Place your Spring DSL code here
beans = {

    /**
     * @Async
     */
    xmlns task: "http://www.springframework.org/schema/task"
    task.'annotation-driven'('proxy-target-class': true, 'mode': 'proxy')

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
    crawlerExecutionSynchronizer(CrawlerExecutionSynchronizer)
    crawlerFactory(ParserFactory)
    parserConfigFactory(ParserConfigFactory)
    crawlerExecutor(CrawlerExecutor)
    faktParser(FaktParser)
    kozaczekParser(KozaczekParser)
    plotekParser(PlotekParser)
    pudelekParser(PudelekParser)
    nocotyParser(NocotyParser)

    /**
     * JMS wiring
     */
    mesageBus(MessageBus)

    crawlerBroker(org.apache.activemq.xbean.XBeanBrokerService) { bean ->
        // broker name
        brokerName = 'crawler'
        // jmx yes, lets see what happen inside
        useJmx = true
        // persistence config
        persistent = false
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
