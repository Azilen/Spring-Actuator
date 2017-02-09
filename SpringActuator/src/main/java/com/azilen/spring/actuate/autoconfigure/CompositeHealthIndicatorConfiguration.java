package com.azilen.spring.actuate.autoconfigure;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;

import com.azilen.spring.actuate.endpoint.health.CompositeHealthIndicator;
import com.azilen.spring.actuate.endpoint.health.HealthAggregator;
import com.azilen.spring.actuate.endpoint.health.HealthIndicator;


public class CompositeHealthIndicatorConfiguration<H, S> {
	@Autowired
	private HealthAggregator healthAggregator;

	protected HealthIndicator createHealthIndicator(Map<String, S> beans) {
		if (beans.size() == 1) {
			return (HealthIndicator) createHealthIndicator(beans.values().iterator().next());
		}
		CompositeHealthIndicator composite = new CompositeHealthIndicator(
				this.healthAggregator);
		for (Map.Entry<String, S> entry : beans.entrySet()) {
			composite.addHealthIndicator(entry.getKey(),
					createHealthIndicator(entry.getValue()));
		}
		return composite;
	}

	@SuppressWarnings("unchecked")
	protected HealthIndicator createHealthIndicator(S source) {
		Class<?>[] generics = ResolvableType
				.forClass(CompositeHealthIndicatorConfiguration.class, getClass())
				.resolveGenerics();
		Class<H> indicatorClass = (Class<H>) generics[0];
		Class<S> sourceClass = (Class<S>) generics[1];
		try {
			return (HealthIndicator) indicatorClass.getConstructor(sourceClass).newInstance(source);
		}
		catch (Exception ex) {
			throw new IllegalStateException("Unable to create indicator " + indicatorClass
					+ " for source " + sourceClass, ex);
		}
	}
}
