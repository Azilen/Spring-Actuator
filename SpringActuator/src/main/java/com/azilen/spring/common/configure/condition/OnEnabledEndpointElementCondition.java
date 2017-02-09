package com.azilen.spring.common.configure.condition;

import java.lang.annotation.Annotation;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Base endpoint element condition. An element can be disabled globally via the
 * {@code defaults} name or individually via the name of the element.
 *
 * @author Stephane Nicoll
 */

abstract class OnEnabledEndpointElementCondition extends SpringExtendedCondition{
	
	private final String prefix;

	private final Class<? extends Annotation> annotationType;

	OnEnabledEndpointElementCondition(String prefix,
			Class<? extends Annotation> annotationType) {
		this.prefix = prefix;
		this.annotationType = annotationType;
	}

	protected String getEndpointElementOutcomeMessage(String name, boolean match) {
		return "The endpoint element " + name + " is " + (match ? "enabled" : "disabled");
	}

	protected String getDefaultEndpointElementOutcomeMessage(boolean match) {
		return "All default endpoint elements are " + (match ? "enabled" : "disabled")
				+ " by default";
	}

	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context,
			AnnotatedTypeMetadata metadata) {
		AnnotationAttributes annotationAttributes = AnnotationAttributes
				.fromMap(metadata.getAnnotationAttributes(this.annotationType.getName()));
		String endpointName = annotationAttributes.getString("value");
		ConditionOutcome outcome = getEndpointOutcome(context, endpointName);
		if (outcome != null) {
			return outcome;
		}
		return getDefaultEndpointsOutcome(context);
	}

	protected ConditionOutcome getEndpointOutcome(ConditionContext context,
			String endpointName) {
		RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(
				context.getEnvironment(), this.prefix + endpointName + ".");
		if (resolver.containsProperty("enabled")) {
			boolean match = resolver.getProperty("enabled", Boolean.class, true);
			return new ConditionOutcome(match,
					ConditionMessage.forCondition(this.annotationType).because(
							this.prefix + endpointName + ".enabled is " + match));
		}
		return null;
	}

	protected ConditionOutcome getDefaultEndpointsOutcome(ConditionContext context) {
		RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(
				context.getEnvironment(), this.prefix + "defaults.");
		boolean match = Boolean.valueOf(resolver.getProperty("enabled", "true"));
		return new ConditionOutcome(match,
				ConditionMessage.forCondition(this.annotationType).because(
						this.prefix + "defaults.enabled is considered " + match));
	}
	
}
