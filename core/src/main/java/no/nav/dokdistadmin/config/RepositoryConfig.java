package no.nav.dokdistadmin.config;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.pool.OracleDataSource;
import oracle.net.ns.SQLnetDef;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = {
		"no.nav.dokdistadmin.domain"
})
@EnableJpaRepositories(basePackages = {
		"no.nav.dokdistadmin.repository"
})
@EnableConfigurationProperties({DataSourceProperties.class, DokdistadminProperties.class})
public class RepositoryConfig {

	@Bean
	@Primary
	DataSource dataSource(final DataSourceProperties dataSourceProperties,
						  final DokdistadminProperties dokdistadminProperties) throws SQLException {
		PoolDataSource poolDataSource = PoolDataSourceFactory.getPoolDataSource();
		poolDataSource.setConnectionFactoryClassName(OracleDataSource.class.getName());
		poolDataSource.setURL(dataSourceProperties.getUrl());
		poolDataSource.setUser(dataSourceProperties.getUsername());
		poolDataSource.setPassword(dataSourceProperties.getPassword());
		poolDataSource.registerConnectionInitializationCallback(connection ->
				connection.setSchema(dokdistadminProperties.getDatabase().getSchema()));
		poolDataSource.setMaxConnectionReuseTime(MINUTES.toSeconds(5));
		// Behøver ikke sette setSQLForValidateConnection pga UCP gjør intern ping mot Oracle
		poolDataSource.setValidateConnectionOnBorrow(true);
		poolDataSource.setSecondsToTrustIdleConnection((int) MINUTES.toSeconds(3));

		String onshosts = dokdistadminProperties.getDatabase().getOnshosts();
		if (isOracleFastConnectionFailoverSupported(dataSourceProperties.getUrl(), onshosts)) {
			poolDataSource.setFastConnectionFailoverEnabled(true);
			String onsConfiguration = "nodes=" + onshosts;
			poolDataSource.setONSConfiguration(onsConfiguration);
			log.info("RepositoryConfig - Skrur på FCF/FAN. onsConfiguration={}", onsConfiguration);
		} else {
			poolDataSource.setFastConnectionFailoverEnabled(false);
			poolDataSource.setONSConfiguration("");
			log.info("RepositoryConfig - FCF/FAN er skrudd av");
		}

		Properties properties = new Properties();
		properties.setProperty(SQLnetDef.TCP_CONNTIMEOUT_STR, "3000");
		properties.setProperty("oracle.jdbc.implicitStatementCacheSize", "100");
		properties.setProperty("oracle.jdbc.thinForceDNSLoadBalancing", "true");

		int poolsize = dokdistadminProperties.getDatabase().getPoolsize();
		log.info("Setter dokdistadmin database poolsize={}", poolsize);
		poolDataSource.setInitialPoolSize(poolsize);
		poolDataSource.setMinPoolSize(poolsize);
		poolDataSource.setMaxPoolSize(poolsize);
		poolDataSource.setConnectionProperties(properties);
		return poolDataSource;
	}

	@Bean
	@Primary
	NamedParameterJdbcTemplate namedParameterJdbcTemplate(final DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

	private boolean isOracleFastConnectionFailoverSupported(String jdbcurl, String onshosts) {
		return jdbcurl.toLowerCase().contains("failover") && isNotBlank(onshosts);
	}
}
