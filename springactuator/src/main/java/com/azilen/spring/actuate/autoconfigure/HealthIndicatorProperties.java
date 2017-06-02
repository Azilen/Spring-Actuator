package com.azilen.spring.actuate.autoconfigure;

import java.util.List;

import com.azilen.spring.common.configure.condition.ConfigurationProperties;


@ConfigurationProperties("management.health.status")
public class HealthIndicatorProperties {
	/**
	 * Comma-separated list of health statuses in order of severity.
	 */
	private List<String> order = null;

	public List<String> getOrder() {
		return this.order;
	}

	public void setOrder(List<String> statusOrder) {
		if (statusOrder != null && !statusOrder.isEmpty()) {
			this.order = statusOrder;
		}
	}
}
