package com.azilen.spring.actuate.autoconfigure;

import java.util.List;

import org.springframework.util.StringUtils;

import com.azilen.spring.common.configure.condition.ConfigurationProperties;
import com.azilen.spring.common.configure.condition.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "spring.couchbase")
public class CouchbaseProperties {
	private List<String> bootstrapHosts;
	private final CouchbaseProperties.Bucket bucket = new CouchbaseProperties.Bucket();
	private final CouchbaseProperties.Env env = new CouchbaseProperties.Env();

	public List<String> getBootstrapHosts() {
		return this.bootstrapHosts;
	}

	public void setBootstrapHosts(List<String> bootstrapHosts) {
		this.bootstrapHosts = bootstrapHosts;
	}

	public CouchbaseProperties.Bucket getBucket() {
		return this.bucket;
	}

	public CouchbaseProperties.Env getEnv() {
		return this.env;
	}

	public static class Timeouts {
		private long connect = 5000L;
		private long keyValue = 2500L;
		private long query = 7500L;
		private int socketConnect = 1000;
		private long view = 7500L;

		public long getConnect() {
			return this.connect;
		}

		public void setConnect(long connect) {
			this.connect = connect;
		}

		public long getKeyValue() {
			return this.keyValue;
		}

		public void setKeyValue(long keyValue) {
			this.keyValue = keyValue;
		}

		public long getQuery() {
			return this.query;
		}

		public void setQuery(long query) {
			this.query = query;
		}

		public int getSocketConnect() {
			return this.socketConnect;
		}

		public void setSocketConnect(int socketConnect) {
			this.socketConnect = socketConnect;
		}

		public long getView() {
			return this.view;
		}

		public void setView(long view) {
			this.view = view;
		}
	}

	public static class Ssl {
		private Boolean enabled;
		private String keyStore;
		private String keyStorePassword;

		public Boolean getEnabled() {
			return Boolean.valueOf(this.enabled != null ? this.enabled
					.booleanValue() : StringUtils.hasText(this.keyStore));
		}

		public void setEnabled(Boolean enabled) {
			this.enabled = enabled;
		}

		public String getKeyStore() {
			return this.keyStore;
		}

		public void setKeyStore(String keyStore) {
			this.keyStore = keyStore;
		}

		public String getKeyStorePassword() {
			return this.keyStorePassword;
		}

		public void setKeyStorePassword(String keyStorePassword) {
			this.keyStorePassword = keyStorePassword;
		}
	}

	public static class Endpoints {
		private int keyValue = 1;
		private int query = 1;
		private int view = 1;

		public int getKeyValue() {
			return this.keyValue;
		}

		public void setKeyValue(int keyValue) {
			this.keyValue = keyValue;
		}

		public int getQuery() {
			return this.query;
		}

		public void setQuery(int query) {
			this.query = query;
		}

		public int getView() {
			return this.view;
		}

		public void setView(int view) {
			this.view = view;
		}
	}

	public static class Env {
		@NestedConfigurationProperty
		private final CouchbaseProperties.Endpoints endpoints = new CouchbaseProperties.Endpoints();
		@NestedConfigurationProperty
		private final CouchbaseProperties.Ssl ssl = new CouchbaseProperties.Ssl();
		@NestedConfigurationProperty
		private final CouchbaseProperties.Timeouts timeouts = new CouchbaseProperties.Timeouts();

		public CouchbaseProperties.Endpoints getEndpoints() {
			return this.endpoints;
		}

		public CouchbaseProperties.Ssl getSsl() {
			return this.ssl;
		}

		public CouchbaseProperties.Timeouts getTimeouts() {
			return this.timeouts;
		}
	}

	public static class Bucket {
		private String name = "default";
		private String password = "";

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return this.password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}
