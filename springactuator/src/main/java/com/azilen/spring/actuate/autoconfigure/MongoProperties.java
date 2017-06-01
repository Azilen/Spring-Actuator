package com.azilen.spring.actuate.autoconfigure;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.core.env.Environment;

import com.azilen.spring.common.configure.condition.ConfigurationProperties;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MongoProperties {
	public static final int DEFAULT_PORT = 27017;
	private String host;
	private Integer port = null;
	private String uri = "mongodb://localhost/test";
	private String database;
	private String authenticationDatabase;
	private String gridFsDatabase;
	private String username;
	private char[] password;
	private Class<?> fieldNamingStrategy;

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDatabase() {
		return this.database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getAuthenticationDatabase() {
		return this.authenticationDatabase;
	}

	public void setAuthenticationDatabase(String authenticationDatabase) {
		this.authenticationDatabase = authenticationDatabase;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public char[] getPassword() {
		return this.password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public Class<?> getFieldNamingStrategy() {
		return this.fieldNamingStrategy;
	}

	public void setFieldNamingStrategy(Class<?> fieldNamingStrategy) {
		this.fieldNamingStrategy = fieldNamingStrategy;
	}

	public void clearPassword() {
		if (this.password != null) {
			for (int i = 0; i < this.password.length; ++i) {
				this.password[i] = 0;
			}

		}
	}

	public String getUri() {
		return this.uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Integer getPort() {
		return this.port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getGridFsDatabase() {
		return this.gridFsDatabase;
	}

	public void setGridFsDatabase(String gridFsDatabase) {
		this.gridFsDatabase = gridFsDatabase;
	}

	public String getMongoClientDatabase() {
		return this.database != null ? this.database : (new MongoClientURI(this.uri)).getDatabase();
	}

	public MongoClient createMongoClient(MongoClientOptions options, Environment environment)
			throws UnknownHostException {
		MongoClient arg5;
		try {
			if (!this.hasCustomAddress() && !this.hasCustomCredentials()) {
				MongoClient credentials1 = new MongoClient(new MongoClientURI(this.uri, this.builder(options)));
				return credentials1;
			}

			if (options == null) {
				options = MongoClientOptions.builder().socketTimeout(3000).build();
			}

			ArrayList credentials = new ArrayList();
			String host;
			if (this.hasCustomCredentials()) {
				host = this.authenticationDatabase == null ? this.getMongoClientDatabase()
						: this.authenticationDatabase;
				credentials.add(MongoCredential.createCredential(this.username, host, this.password));
			}

			host = this.host == null ? "localhost" : this.host;
			int port = this.determinePort(environment);
			arg5 = new MongoClient(Collections.singletonList(new ServerAddress(host, port)), credentials, options);
		} finally {
			this.clearPassword();
		}

		return arg5;
	}

	private boolean hasCustomAddress() {
		return this.host != null || this.port != null;
	}

	private boolean hasCustomCredentials() {
		return this.username != null && this.password != null;
	}

	private int determinePort(Environment environment) {
		if (this.port == null) {
			return 27017;
		} else if (this.port.intValue() == 0) {
			if (environment != null) {
				String localPort = environment.getProperty("local.mongo.port");
				if (localPort != null) {
					return Integer.valueOf(localPort).intValue();
				}
			}

			throw new IllegalStateException(
					"spring.data.mongodb.port=0 and no local mongo port configuration is available");
		} else {
			return this.port.intValue();
		}
	}

	private Builder builder(MongoClientOptions options) {
		return options != null ? MongoClientOptions.builder(options) : MongoClientOptions.builder();
	}
}
