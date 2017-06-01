package com.azilen.spring.actuate.endpoint.health;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.util.Assert;

import com.azilen.spring.actuate.endpoint.health.Health.Builder;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;

public class CassandraHealthIndicator extends AbstractHealthIndicator {

	private CassandraOperations cassandraOperations;

	/**
	 * Create a new {@link CassandraHealthIndicator} instance.
	 * 
	 * @param cassandraOperations
	 *            the Cassandra operations
	 */
	public CassandraHealthIndicator(CassandraOperations cassandraOperations) {
		Assert.notNull(cassandraOperations, "CassandraOperations must not be null");
		this.cassandraOperations = cassandraOperations;
	}

	@Override
	public void doHealthCheck(Builder builder) throws Exception {
		try {
			Select select = QueryBuilder.select("release_version").from("system", "local");
			ResultSet results = this.cassandraOperations.query(select.getQueryString());
			
			if (results.isExhausted()) {
				builder.up();
				return;
			}
			String version = results.one().getString(0);
			builder.up().withDetail("version", version);
		} catch (Exception ex) {
			builder.down(ex);
		}
	}

}
