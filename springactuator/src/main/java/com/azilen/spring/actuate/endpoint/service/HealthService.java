/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.azilen.spring.actuate.endpoint.service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.azilen.spring.actuate.autoconfigure.MongoProperties;
import com.azilen.spring.actuate.endpoint.health.CassandraHealthIndicator;
import com.azilen.spring.actuate.endpoint.health.CouchbaseHealthIndicator;
import com.azilen.spring.actuate.endpoint.health.DiskSpaceHealthIndicator;
import com.azilen.spring.actuate.endpoint.health.DiskSpaceHealthIndicatorProperties;
import com.azilen.spring.actuate.endpoint.health.Health;
import com.azilen.spring.actuate.endpoint.health.HealthMvcEndpointProperties;
import com.azilen.spring.actuate.endpoint.health.MongoHealthIndicator;
import com.azilen.spring.actuate.endpoint.health.Status;
import com.azilen.spring.common.property.bind.RelaxedNames;
import com.couchbase.client.java.Bucket;

/**
 *
 * @author harshil.monani
 */
@Component
public class HealthService {

	private static final Log logger = LogFactory.getLog(HealthService.class);

	public static final String UP = "up";

	public static final String DOWN = "down";

	private volatile String applicationStatus = "";

	@Autowired
	ApplicationContext applicationContext;

	@Autowired(required = false)
	MongoTemplate mongoTemplate;

	@Autowired
	MongoProperties mongoProperties;

	@Autowired(required = false)
	CouchbaseOperations couchbaseOperations;

	@Autowired(required = false)
	CassandraTemplate cassandraTemplate;

	private void mongoHealthCheck(Health.Builder mongoBuilder, String code) {
		try {
			if (mongoTemplate != null) {
				MongoHealthIndicator mongoHealthIndicator = new MongoHealthIndicator(mongoTemplate);
				mongoHealthIndicator.doHealthCheck(mongoBuilder);
			}
		} catch (Exception e) {
			logger.error("MongoDB service is not running", e);
			mongoBuilder.down();
		}
		if ((UP).equalsIgnoreCase(code) && (UP).equalsIgnoreCase(mongoBuilder.build().getStatus().getCode()))
			applicationStatus = Status.UP.getCode();
		else if ((DOWN).equalsIgnoreCase(code) || (DOWN).equalsIgnoreCase(mongoBuilder.build().getStatus().getCode()))
			applicationStatus = Status.DOWN.getCode();

	}

	private void couchHealthCheck(Health.Builder couchbaseBuilder, String code) {
		boolean isReachable = true;
		int nodeCount = 0;
		try {
			if (couchbaseOperations != null) {

				CouchbaseHealthIndicator couchbaseHealthIndicator = new CouchbaseHealthIndicator(couchbaseOperations);
				couchbaseHealthIndicator.doHealthCheck(couchbaseBuilder);
				Integer timeoutInMs = 2000;
				Bucket bucket = this.couchbaseOperations.getCouchbaseBucket();
				nodeCount = bucket.bucketManager().info().nodeCount();
				Boolean[] isReachableArray = new Boolean[nodeCount];
				for (int i = 0; i < nodeCount; i++) {
					isReachableArray[i] = bucket.bucketManager().info().nodeList().get(i).isReachable(timeoutInMs);
				}
				if (Arrays.asList(isReachableArray).contains(true)) {
					isReachable = true;
				} else {
					isReachable = false;
				}
				if (!isReachable) {
					throw new Exception("Not able to access the couchbase server");
				}
				/* end :: Couchbase health status */
			}
		} catch (Exception e) {
			if (!isReachable) {
				logger.error("CouchDB service is not running", e);
				couchbaseBuilder.down();
			}
		}
		if ((UP).equalsIgnoreCase(code) && (UP).equalsIgnoreCase(couchbaseBuilder.build().getStatus().getCode()))
			applicationStatus = Status.UP.getCode();
		else if ((DOWN).equalsIgnoreCase(code)
				|| (DOWN).equalsIgnoreCase(couchbaseBuilder.build().getStatus().getCode()))
			applicationStatus = Status.DOWN.getCode();
	}

	private void cassandraHealthCheck(Health.Builder cassandraBuilder, String code) {
		try {
			if (cassandraTemplate != null) {
				CassandraHealthIndicator cassandraHealthIndicator = new CassandraHealthIndicator(cassandraTemplate);
				cassandraHealthIndicator.doHealthCheck(cassandraBuilder);
			}
		} catch (Exception e) {
			logger.error("CassandraDB service is not running", e);
			cassandraBuilder.down();
		}
		if ((UP).equalsIgnoreCase(code) && (UP).equalsIgnoreCase(cassandraBuilder.build().getStatus().getCode()))
			applicationStatus = Status.UP.getCode();
		else if ((DOWN).equalsIgnoreCase(code)
				|| (DOWN).equalsIgnoreCase(cassandraBuilder.build().getStatus().getCode()))
			applicationStatus = Status.DOWN.getCode();
	}

	public Object getHealthDetails(HealthMvcEndpointProperties healthMvcEndpointProperties) {
		DiskSpaceHealthIndicator diskSpaceHealthIndicator = new DiskSpaceHealthIndicator(
				new DiskSpaceHealthIndicatorProperties());
		Health.Builder builder = new Health.Builder();
		Health.Builder mongoBuilder = new Health.Builder();
		Health.Builder couchbaseBuilder = new Health.Builder();
		Health.Builder cassandraBuilder = new Health.Builder();
		Map<String, Object> healthMap = new LinkedHashMap<String, Object>();

		try {
			builder = diskSpaceHealthIndicator.doHealthCheck(builder);

		} catch (Exception e) {
			logger.error("Error while checking disk health", e);
		}

		Health health = builder.build();
		String code = health.getStatus().getCode();

		mongoHealthCheck(mongoBuilder, code);

		couchHealthCheck(couchbaseBuilder, code);

		cassandraHealthCheck(cassandraBuilder, code);

		Health mongoHealth = mongoBuilder.build();
		Health couchbaseHealth = couchbaseBuilder.build();
		Health cassandraHealth = cassandraBuilder.build();

		HttpStatus status = getStatus(health, healthMvcEndpointProperties);

		if (status != null) {
			return new ResponseEntity<Health>(health, status);
		}
		healthMap.put("status", applicationStatus);

		healthMap.put("diskSpace", health);
		if (Status.UP.getCode().equals(mongoHealth.getStatus().getCode())) {
			healthMap.put("mongo", mongoHealth);
		}

		if (Status.UP.getCode().equals(couchbaseHealth.getStatus().getCode())) {
			healthMap.put("couchbase", couchbaseHealth);
		}

		if (Status.UP.getCode().equals(cassandraHealth.getStatus().getCode())) {
			healthMap.put("cassandra", cassandraHealth);
		}


		return healthMap;
	}

	private HttpStatus getStatus(Health health, HealthMvcEndpointProperties healthMvcEndpointProperties) {
		String code = health.getStatus().getCode();
		if (code != null) {
			code = code.toLowerCase().replace("_", "-");
			for (String candidate : RelaxedNames.forCamelCase(code)) {
				HttpStatus status = healthMvcEndpointProperties.getMapping().get(candidate);
				if (status != null) {
					return status;
				}
			}
		}
		return null;
	}
}