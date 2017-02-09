package com.azilen.spring.actuate.endpoint.health;

import com.azilen.spring.actuate.endpoint.health.Health.Builder;
import com.mongodb.CommandResult;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Assert;


public class MongoHealthIndicator extends AbstractHealthIndicator {

	private final MongoTemplate mongoTemplate;

	public MongoHealthIndicator(MongoTemplate mongoTemplate) {
		Assert.notNull(mongoTemplate, "MongoTemplate must not be null");
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void doHealthCheck(Builder builder) throws Exception {
		CommandResult result = this.mongoTemplate.executeCommand("{ buildInfo: 1 }");
		builder.up().withDetail("version", result.getString("version"));
		
	}

	/*public Health.Builder doHealthCheck(Health.Builder builder) throws Exception{
		try{
			CommandResult result = this.mongoTemplate.executeCommand("{ buildInfo: 1 }");
			return builder.up().withDetail("version", result.getString("version"));
		}
		catch(Exception e){
			return builder.down(e);
		}
		
	}*/
}
