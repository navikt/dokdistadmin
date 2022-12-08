package no.nav.dokdistadmin.config;

import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.FilInfoRepository;
import no.nav.dokdistadmin.repository.PingRepository;
import no.nav.dokdistadmin.repository.VarselInfoRepository;
import no.nav.dokdistadmin.repository.support.Jpa2DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.support.Jpa2FilInfoRepository;
import no.nav.dokdistadmin.repository.support.Jpa2PingRepository;
import no.nav.dokdistadmin.repository.support.Jpa2VarselInfoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Spring repository configuration.
 *
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
@Configuration
@EnableTransactionManagement
public class RepositoryConfig {

	@Bean
	public DokumentDistribusjonRepository dokumentDistribusjonRepository() {
		return new Jpa2DokumentDistribusjonRepository();
	}

	@Bean
	public VarselInfoRepository varselInfoRepository() {
		return new Jpa2VarselInfoRepository();
	}

	@Bean
	public FilInfoRepository filInfoRepository() {
		return new Jpa2FilInfoRepository();
	}

	@Bean
	public PingRepository pingRepository() {
		return new Jpa2PingRepository();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JtaTransactionManager();
	}

	@Bean
	public DataSource dataSource() throws NamingException {
		return getJndiObject("java:/jboss/datasources/DokdistDS", DataSource.class);
	}

	@Bean
	public EntityManagerFactory entityManagerFactory() {
		return getJndiObject("java:comp/env/dokdist/em", EntityManager.class)
				.getEntityManagerFactory();
	}

	@SuppressWarnings("unchecked")
	private static <T> T getJndiObject(final String jndiName, final Class<T> expectedType) {
		JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
		factory.setJndiName(jndiName);
		factory.setExpectedType(expectedType);
		try {
			factory.afterPropertiesSet();
		} catch (IllegalArgumentException | NamingException e) {
			throw new RuntimeException(e);
		}
		return (T) factory.getObject();
	}

}
