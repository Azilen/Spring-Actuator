package com.azilen.spring.common.configure.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;


/**
 * {@link Conditional} that checks whether or not a default health indicator is enabled.
 * Matches if the value of the {@code management.health.<name>.enabled} property is
 * {@code true}. Otherwise, matches if the value of the
 * {@code management.health.defaults.enabled} property is {@code true} or if it is not
 * configured.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnEnabledHealthIndicatorCondition.class)
public @interface ConditionalOnEnabledHealthIndicator {
	/**
	 * The name of the health indicator.
	 * @return the name of the health indicator
	 */
	String value();
}
