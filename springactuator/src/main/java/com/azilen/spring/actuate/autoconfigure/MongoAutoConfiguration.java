package com.azilen.spring.actuate.autoconfigure;

import java.net.UnknownHostException;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.azilen.spring.common.configure.condition.ConditionalOnClass;
import com.azilen.spring.common.configure.condition.ConditionalOnMissingBean;
import com.azilen.spring.common.configure.condition.EnableConfigurationProperties;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

@Configuration
@ConditionalOnClass({ MongoClient.class })
@EnableConfigurationProperties({ MongoProperties.class })
@ConditionalOnMissingBean(type = { "org.springframework.data.mongodb.MongoDbFactory" })
public class MongoAutoConfiguration {
	private final MongoProperties properties;
	private final MongoClientOptions options;
	private final Environment environment;
	private MongoClient mongo;

	public MongoAutoConfiguration(MongoProperties properties,
			ObjectProvider<MongoClientOptions> optionsProvider,
			Environment environment) {
		this.properties = properties;
		this.options = (MongoClientOptions) optionsProvider.getIfAvailable();
		this.environment = environment;
	}

	@PreDestroy
	public void close() {
		if (this.mongo != null) {
			this.mongo.close();
		}

	}

	@Bean
	@ConditionalOnMissingBean
	public MongoClient mongo() throws UnknownHostException {
		this.mongo = this.properties.createMongoClient(this.options,
				this.environment);
		return this.mongo;
	}
}
