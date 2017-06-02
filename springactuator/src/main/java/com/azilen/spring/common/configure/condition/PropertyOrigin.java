package com.azilen.spring.common.configure.condition;

import org.springframework.core.env.PropertySource;

public class PropertyOrigin {
	private final PropertySource<?> source;

	private final String name;

	PropertyOrigin(PropertySource<?> source, String name) {
		this.name = name;
		this.source = source;
	}

	public PropertySource<?> getSource() {
		return this.source;
	}

	public String getName() {
		return this.name;
	}

}
