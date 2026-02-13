package no.nav.dokdistadmin.config.jms;

import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.mq.jakarta.jms.MQQueue;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import no.nav.dokdistadmin.config.ServiceuserProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;

import javax.net.ssl.SSLSocketFactory;

@Configuration
@EnableConfigurationProperties(JmsProperties.class)
@Profile("nais")
public class JmsConfig {

    private static final int UTF_8_WITH_PUA = 1208;
    private static final String ANY_TLS13_OR_HIGHER = "*TLS13ORHIGHER";

    @Bean
    public Queue printQueue(JmsProperties properties) throws JMSException {
        return new MQQueue(properties.getQueues().getQdist009Print());
    }

    @Bean
    public Queue dittnavQueue(JmsProperties properties) throws JMSException {
        return new MQQueue(properties.getQueues().getQdist010Dittnav());
    }

    @Bean
    public Queue sdpQueue(JmsProperties properties) throws JMSException {
        return new MQQueue(properties.getQueues().getQdist011Sdp());
    }

    @Bean
    public Queue dpvtQueue(JmsProperties properties) throws JMSException {
        return new MQQueue(properties.getQueues().getQdist016Dpvt());
    }

    @Bean
    public ConnectionFactory connectionFactory(JmsProperties properties, ServiceuserProperties serviceuserProperties) throws JMSException {
        MQConnectionFactory factory = new MQConnectionFactory();

        factory.setHostName(properties.getBroker().getHostname());
        factory.setPort(properties.getBroker().getPort());
        factory.setQueueManager(properties.getBroker().getName());
        factory.setChannel(properties.getBroker().getChannel());
        factory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
        factory.setCCSID(UTF_8_WITH_PUA);
        factory.setIntProperty(WMQConstants.JMS_IBM_ENCODING, MQConstants.MQENC_NATIVE);
        factory.setIntProperty(WMQConstants.JMS_IBM_CHARACTER_SET, UTF_8_WITH_PUA);
        factory.setSSLCipherSuite(ANY_TLS13_OR_HIGHER);
        factory.setSSLSocketFactory(SSLSocketFactory.getDefault());

        UserCredentialsConnectionFactoryAdapter adapter = new UserCredentialsConnectionFactoryAdapter();
        adapter.setTargetConnectionFactory(factory);
        adapter.setUsername(serviceuserProperties.getUsername());
        adapter.setPassword(serviceuserProperties.getPassword());

        return adapter;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setSessionTransacted(true); //Send melding til kø først når transaksjonen committes
        return jmsTemplate;
    }

}

