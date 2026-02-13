package no.nav.dokdistadmin.config;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;
import no.nav.dokdistadmin.config.jms.JmsProperties;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties(JmsProperties.class)
@Profile("itest")
public class JmsTestConfig {
	@Bean
	public Queue printQueue(JmsProperties properties) {
		return new ActiveMQQueue(properties.getQueues().getQdist009Print());
	}

	@Bean
	public Queue dittnavQueue(JmsProperties properties) {
		return new ActiveMQQueue(properties.getQueues().getQdist010Dittnav());
	}

	@Bean
	public Queue sdpQueue(JmsProperties properties) {
		return new ActiveMQQueue(properties.getQueues().getQdist011Sdp());
	}

	@Bean
	public Queue dpvtQueue(JmsProperties properties) {
		return new ActiveMQQueue(properties.getQueues().getQdist016Dpvt());
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	public EmbeddedActiveMQ activeMQServer() {
		EmbeddedActiveMQ embeddedActiveMQ = new EmbeddedActiveMQ();
		embeddedActiveMQ.setConfigResourcePath("artemis-server.xml");
		return embeddedActiveMQ;
	}

	// avhengig av EmbeddedActiveMQ slik at server er startet før klient forsøker lage koblinger
	@Bean
	public ConnectionFactory activemqConnectionFactory(EmbeddedActiveMQ embeddedActiveMQ) {
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("vm://0");
		JmsPoolConnectionFactory pooledFactory = new JmsPoolConnectionFactory();
		pooledFactory.setConnectionFactory(activeMQConnectionFactory);
		pooledFactory.setMaxConnections(1);
		return pooledFactory;
	}
}
