import org.apache.activemq.broker.region.policy.PolicyEntry
import org.apache.activemq.broker.region.policy.PolicyMap
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter
import vanity.crawler.jms.MessageBus
import vanity.crawler.spider.CrawlerExecutor
import vanity.crawler.spider.CrawlerFactory

// Place your Spring DSL code here
beans = {

    /**
     * Crawler wiring
     */
    crawlerFactory(CrawlerFactory){bean -> bean.autowire = 'byName'}
    crawlerExecutor(CrawlerExecutor){bean -> bean.autowire = 'byName'}

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
        kahaDBPersistenceAdapter.directory = new File(application.config.jms.storage.location)
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
        maxConnections = 8
        connectionFactory = ref('jmsConnectionFactory')
    }

}
