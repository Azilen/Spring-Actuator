package com.azilen.spring.actuate.autoconfigure;

import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.azilen.spring.common.configure.condition.ConditionalOnClass;
import com.azilen.spring.common.configure.condition.ConditionalOnMissingBean;
import com.azilen.spring.common.configure.condition.EnableConfigurationProperties;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.ReconnectionPolicy;
import com.datastax.driver.core.policies.RetryPolicy;

@Configuration
@ConditionalOnClass({ Cluster.class })
@EnableConfigurationProperties(CassandraProperties.class)
public class CassandraAutoConfiguration {
	private final CassandraProperties properties;

	public CassandraAutoConfiguration(CassandraProperties properties) {
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean	
	public Cluster cluster() {
		CassandraProperties properties = this.properties;
		Builder builder = Cluster.builder()
				.withClusterName(properties.getClusterName())
				.withPort(properties.getPort());
		if (properties.getUsername() != null) {
			builder.withCredentials(properties.getUsername(),
					properties.getPassword());
		}

		if (properties.getCompression() != null) {
			builder.withCompression(properties.getCompression());
		}

		if (properties.getLoadBalancingPolicy() != null) {
			LoadBalancingPolicy points = (LoadBalancingPolicy) instantiate(properties
					.getLoadBalancingPolicy());
			builder.withLoadBalancingPolicy(points);
		}

		builder.withQueryOptions(this.getQueryOptions());
		if (properties.getReconnectionPolicy() != null) {
			ReconnectionPolicy points1 = (ReconnectionPolicy) instantiate(properties
					.getReconnectionPolicy());
			builder.withReconnectionPolicy(points1);
		}

		if (properties.getRetryPolicy() != null) {
			RetryPolicy points2 = (RetryPolicy) instantiate(properties
					.getRetryPolicy());
			builder.withRetryPolicy(points2);
		}

		builder.withSocketOptions(this.getSocketOptions());
		if (properties.isSsl()) {
			builder.withSSL();
		}

		String points3 = properties.getContactPoints();
		builder.addContactPoints(StringUtils
				.commaDelimitedListToStringArray(points3));
		return builder.build();
	}

	public static <T> T instantiate(Class<T> type) {
		return BeanUtils.instantiate(type);
	}

	private QueryOptions getQueryOptions() {
		QueryOptions options = new QueryOptions();
		CassandraProperties properties = this.properties;
		if (properties.getConsistencyLevel() != null) {
			options.setConsistencyLevel(properties.getConsistencyLevel());
		}

		if (properties.getSerialConsistencyLevel() != null) {
			options.setSerialConsistencyLevel(properties
					.getSerialConsistencyLevel());
		}

		options.setFetchSize(properties.getFetchSize());
		return options;
	}

	private SocketOptions getSocketOptions() {
		SocketOptions options = new SocketOptions();
		options.setConnectTimeoutMillis(this.properties
				.getConnectTimeoutMillis());
		options.setReadTimeoutMillis(this.properties.getReadTimeoutMillis());
		return options;
	}

}
