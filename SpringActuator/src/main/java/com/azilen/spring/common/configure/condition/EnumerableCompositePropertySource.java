package com.azilen.spring.common.configure.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

public class EnumerableCompositePropertySource extends EnumerablePropertySource<Collection<PropertySource<?>>>{
	private volatile String[] names;

	public EnumerableCompositePropertySource(String sourceName) {
		super(sourceName, new LinkedHashSet<PropertySource<?>>());
	}

	@Override
	public Object getProperty(String name) {
		for (PropertySource<?> propertySource : getSource()) {
			Object value = propertySource.getProperty(name);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	@Override
	public String[] getPropertyNames() {
		String[] result = this.names;
		if (result == null) {
			List<String> names = new ArrayList<String>();
			for (PropertySource<?> source : new ArrayList<PropertySource<?>>(
					getSource())) {
				if (source instanceof EnumerablePropertySource) {
					names.addAll(Arrays.asList(
							((EnumerablePropertySource<?>) source).getPropertyNames()));
				}
			}
			this.names = names.toArray(new String[0]);
			result = this.names;
		}
		return result;
	}

	public void add(PropertySource<?> source) {
		getSource().add(source);
		this.names = null;
	}
}
