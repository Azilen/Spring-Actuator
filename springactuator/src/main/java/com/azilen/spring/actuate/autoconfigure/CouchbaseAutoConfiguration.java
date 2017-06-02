package com.azilen.spring.actuate.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.couchbase.config.CouchbaseConfigurer;

import com.azilen.spring.actuate.autoconfigure.CouchbaseProperties.Endpoints;
import com.azilen.spring.actuate.autoconfigure.CouchbaseProperties.Ssl;
import com.azilen.spring.actuate.autoconfigure.CouchbaseProperties.Timeouts;
import com.azilen.spring.common.configure.condition.AnyNestedCondition;
import com.azilen.spring.common.configure.condition.ConditionalOnBean;
import com.azilen.spring.common.configure.condition.ConditionalOnMissingBean;
import com.azilen.spring.common.configure.condition.ConditionalOnProperty;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.cluster.ClusterInfo;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment.Builder;

public class CouchbaseAutoConfiguration {
	static class CouchbaseCondition extends AnyNestedCondition {
		CouchbaseCondition() {
			super(ConfigurationPhase.REGISTER_BEAN);
		}

		@ConditionalOnBean({ CouchbaseConfigurer.class })
		static class CouchbaseConfigurerAvailable {
		}

		@ConditionalOnProperty(prefix = "spring.couchbase", name = { "bootstrapHosts" })
		static class BootstrapHostsProperty {
		}
	}

	@Configuration
	@ConditionalOnMissingBean({ CouchbaseConfigurer.class,
			CouchbaseAutoConfiguration.CouchbaseConfiguration.class })
	public static class CouchbaseConfiguration {
		private final CouchbaseProperties properties;

		public CouchbaseConfiguration(CouchbaseProperties properties) {
			this.properties = properties;
		}

		@Bean
		@Primary
		public DefaultCouchbaseEnvironment couchbaseEnvironment()
				throws Exception {
			return this.initializeEnvironmentBuilder(this.properties).build();
		}

		@Bean
		@Primary
		public Cluster couchbaseCluster() throws Exception {
			return CouchbaseCluster.create(this.couchbaseEnvironment(),
					this.properties.getBootstrapHosts());
		}

		@Bean
		@Primary
		public ClusterInfo couchbaseClusterInfo() throws Exception {
			return this
					.couchbaseCluster()
					.clusterManager(this.properties.getBucket().getName(),
							this.properties.getBucket().getPassword()).info();
		}

		@Bean
		@Primary
		public Bucket couchbaseClient() throws Exception {
			return this.couchbaseCluster().openBucket(
					this.properties.getBucket().getName(),
					this.properties.getBucket().getPassword());
		}

		protected Builder initializeEnvironmentBuilder(
				CouchbaseProperties properties) {
			Endpoints endpoints = properties.getEnv().getEndpoints();
			Timeouts timeouts = properties.getEnv().getTimeouts();
			Builder builder = DefaultCouchbaseEnvironment.builder()
					.connectTimeout(timeouts.getConnect())
					.kvEndpoints(endpoints.getKeyValue())
					.kvTimeout(timeouts.getKeyValue())
					.queryEndpoints(endpoints.getQuery())
					.queryTimeout(timeouts.getQuery())
					.viewEndpoints(endpoints.getView())
					.socketConnectTimeout(timeouts.getSocketConnect())
					.viewTimeout(timeouts.getView());
			Ssl ssl = properties.getEnv().getSsl();
			if (ssl.getEnabled().booleanValue()) {
				builder.sslEnabled(true);
				if (ssl.getKeyStore() != null) {
					builder.sslKeystoreFile(ssl.getKeyStore());
				}

				if (ssl.getKeyStorePassword() != null) {
					builder.sslKeystorePassword(ssl.getKeyStorePassword());
				}
			}

			return builder;
		}
	}
}
