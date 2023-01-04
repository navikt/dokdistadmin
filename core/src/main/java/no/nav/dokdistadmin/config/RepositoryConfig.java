package no.nav.dokdistadmin.config;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import oracle.jdbc.pool.OracleDataSource;
import oracle.net.ns.SQLnetDef;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
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

		Properties connProperties = new Properties();
		connProperties.setProperty(SQLnetDef.TCP_CONNTIMEOUT_STR, "3000");
		connProperties.setProperty("oracle.jdbc.thinForceDNSLoadBalancing", "true");
		int poolsize = dokdistadminProperties.getDatabase().getPoolsize();
		log.info("Setter dokdistadmin database poolsize=" + poolsize);

		poolDataSource.setInitialPoolSize(poolsize);
		poolDataSource.setMinPoolSize(poolsize);
		poolDataSource.setMaxPoolSize(poolsize);
		poolDataSource.setMaxConnectionReuseTime(300); // 5min
		poolDataSource.setMaxConnectionReuseCount(1000);
		poolDataSource.setConnectionProperties(connProperties);

		return poolDataSource;
	}

	@Bean
	@Primary
	NamedParameterJdbcTemplate namedParameterJdbcTemplate(final DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}
}
