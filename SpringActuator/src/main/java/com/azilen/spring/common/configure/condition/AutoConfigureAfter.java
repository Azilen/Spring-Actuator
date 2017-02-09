package com.azilen.spring.common.configure.condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface AutoConfigureAfter {
	/**
	 * The auto-configure classes that should have already been applied.
	 * @return the classes
	 */
	Class<?>[] value() default {};

	/**
	 * The names of the auto-configure classes that should have already been applied.
	 * @return the class names
	 * @since 1.2.2
	 */
	String[] name() default {};
}
