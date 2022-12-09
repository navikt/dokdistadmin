package no.nav.dokdistadmin.config;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.FilInfoRepository;
import no.nav.dokdistadmin.repository.VarselInfoRepository;
import no.nav.dokdistadmin.repository.support.Jpa2VarselInfoRepository;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hsqldb.jdbc.pool.JDBCXADataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.util.Properties;

/**
 * Repository spring configuration for tests.
 *
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
@Configuration
@EnableTransactionManagement
public class RepositoryTestConfig  {

	@Bean
	public DokumentDistribusjonRepository dokumentDistribusjonRepository() {
		return new RepositoryConfig().dokumentDistribusjonRepository();
	}

	@Bean
	public VarselInfoRepository varselInfoRepository() {
		return new Jpa2VarselInfoRepository();
	}

	@Bean
	public FilInfoRepository filInfoRepository() {
		return new RepositoryConfig().filInfoRepository();
	}

	@Bean
	public JtaTransactionManager transactionManager() {
		try {
			return new JtaTransactionManager(userTransaction(), atomikosTransactionManager());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(JtaTransactionManager tm) throws Exception {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setPackagesToScan("no.nav.dokdistadmin.domain");
		factoryBean.setJtaDataSource(dataSource());
		factoryBean.setPersistenceProvider(new HibernatePersistenceProvider());
		factoryBean.getJpaPropertyMap().put("hibernate.transaction.jta.platform",
				new SpringJtaPlatform(tm.getTransactionManager(), tm.getUserTransaction()));
		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		jpaProperties.setProperty("hibernate.hbm2ddl.auto", "create");
		jpaProperties.setProperty("hibernate.show_sql", "false");
		jpaProperties.setProperty("hibernate.format_sql", "true");
		jpaProperties.setProperty("hibernate.jdbc.fetch_size", "100");
		jpaProperties.setProperty("hibernate.id.new_generator_mappings", "true");
		jpaProperties.setProperty("hibernate.session_factory.interceptor", "no.nav.dokdistadmin.domain.util.ChangeStampInterceptor");
		factoryBean.setJpaProperties(jpaProperties);
		return factoryBean;
	}

	@Bean
	public DataSource dataSource() throws Exception {
		JDBCXADataSource dataSource = new JDBCXADataSource();
		dataSource.setUrl("jdbc:hsqldb:mem:dokdist");
		dataSource.setUser("sa");
		dataSource.setPassword("");
		AtomikosDataSourceBean bean = new AtomikosDataSourceBean();
		bean.setXaDataSource(dataSource);
		bean.setUniqueResourceName("dokdistxads");
		bean.setMinPoolSize(10);
		bean.setMaxPoolSize(50);
		return bean;
	}

	@Bean(initMethod = "init", destroyMethod = "shutdownForce")
	public UserTransactionService userTransactionService() {
		Properties properties = new Properties();
		properties.setProperty("com.atomikos.icatch.enable_logging", "false");
		properties.setProperty("com.atomikos.icatch.force_shutdown_on_vm_exit", "true");
		return new UserTransactionServiceImp(properties);
	}

	@Bean
	@DependsOn("atomikosTransactionManager")
	public UserTransaction userTransaction() throws SystemException {
		UserTransactionImp userTransaction = new UserTransactionImp();
		userTransaction.setTransactionTimeout(60);
		return userTransaction;
	}

	@DependsOn("userTransactionService")
	@Bean(initMethod = "init", destroyMethod = "close")
	public UserTransactionManager atomikosTransactionManager() throws Exception {
		UserTransactionManager manager = new UserTransactionManager();
		manager.setStartupTransactionService(false);
		manager.setForceShutdown(true);
		return manager;
	}

	private class SpringJtaPlatform extends AbstractJtaPlatform {

		private TransactionManager transactionManager;
		private UserTransaction userTransaction;

		SpringJtaPlatform(TransactionManager transactionManager, UserTransaction userTransaction) {
			this.transactionManager = transactionManager;
			this.userTransaction = userTransaction;
		}

		@Override
		protected TransactionManager locateTransactionManager() {
			return transactionManager;
		}

		@Override
		protected UserTransaction locateUserTransaction() {
			return userTransaction;
		}
	}
}
