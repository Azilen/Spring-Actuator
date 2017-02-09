package com.azilen.spring.common.configure.condition;

import org.springframework.context.annotation.Condition;


/**
 * {@link Condition} that checks if a health indicator is enabled.
 *
 * @author Stephane Nicoll
 */

class OnEnabledHealthIndicatorCondition extends OnEnabledEndpointElementCondition {
	OnEnabledHealthIndicatorCondition() {
		super("management.health.", ConditionalOnEnabledHealthIndicator.class);
	}

	@Override
	protected String getEndpointElementOutcomeMessage(String name, boolean match) {
		return "The health indicator " + name + " is " + (match ? "enabled" : "disabled");
	}

	@Override
	protected String getDefaultEndpointElementOutcomeMessage(boolean match) {
		return "All default health indicators are " + (match ? "enabled" : "disabled")
				+ " by default";
	}
}
