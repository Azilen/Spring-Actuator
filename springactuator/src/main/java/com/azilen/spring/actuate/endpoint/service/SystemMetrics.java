/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.azilen.spring.actuate.endpoint.service;

import com.azilen.spring.actuate.endpoint.metrics.Metric;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 * @author harshil.monani
 */
@Component
public class SystemMetrics {

	private static final Log logger = LogFactory.getLog(HealthService.class);

	private Map<String, Object> result = new LinkedHashMap<>();

	private final long timestamp;

	public SystemMetrics() {
		this.timestamp = System.currentTimeMillis();
		for (Metric<?> metric : metrics()) {
			result.put(metric.getName(), metric.getValue());
		}
	}

	public Map<String, Object> getMetrics() {
		// this.timestamp = System.currentTimeMillis();
		for (Metric<?> metric : metrics()) {
			result.put(metric.getName(), metric.getValue());
		}
		return result;
	}

	public final Collection<Metric<?>> metrics() {
		Collection<Metric<?>> collection = new LinkedHashSet<>();
		addBasicMetrics(collection);
		addManagementMetrics(collection);
		return collection;
	}

	/**
	 * Add basic system metrics.
	 *
	 * @param result
	 *            the result
	 */
	protected void addBasicMetrics(Collection<Metric<?>> result) {
		// NOTE: ManagementFactory must not be used here since it fails on GAE
		Runtime runtime = Runtime.getRuntime();
		result.add(newMemoryMetric("mem", runtime.totalMemory() + getTotalNonHeapMemoryIfPossible()));
		result.add(newMemoryMetric("mem.free", runtime.freeMemory()));
		result.add(new Metric<Integer>("processors", runtime.availableProcessors()));
		result.add(new Metric<Long>("instance.uptime", System.currentTimeMillis() - this.timestamp));
	}

	private long getTotalNonHeapMemoryIfPossible() {
		try {
			return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
		} catch (Exception e) {
			logger.error("Error while getting non-heap memory", e);
			return 0;
		}
	}

	private Metric<Long> newMemoryMetric(String name, long bytes) {
		return new Metric<>(name, bytes / 1024);
	}

	/**
	 * Add metrics from ManagementFactory if possible. Note that
	 * ManagementFactory is not available on Google App Engine.
	 *
	 * @param result
	 *            the result
	 */
	private void addManagementMetrics(Collection<Metric<?>> result) {
		try {
			// Add JVM up time in ms
			result.add(new Metric<Long>("uptime", ManagementFactory.getRuntimeMXBean().getUptime()));
			result.add(new Metric<Double>("systemload.average",
					ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()));
			addHeapMetrics(result);
			addNonHeapMetrics(result);
			addThreadMetrics(result);
			addClassLoadingMetrics(result);
			addGarbageCollectionMetrics(result);
		} catch (NoClassDefFoundError e) {
			logger.error("Error while adding management metrics", e);
		}
	}

	/**
	 * Add JVM heap metrics.
	 *
	 * @param result
	 *            the result
	 */
	protected void addHeapMetrics(Collection<Metric<?>> result) {
		MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		result.add(newMemoryMetric("heap.committed", memoryUsage.getCommitted()));
		result.add(newMemoryMetric("heap.init", memoryUsage.getInit()));
		result.add(newMemoryMetric("heap.used", memoryUsage.getUsed()));
		result.add(newMemoryMetric("heap", memoryUsage.getMax()));
	}

	/**
	 * Add JVM non-heap metrics.
	 *
	 * @param result
	 *            the result
	 */
	private void addNonHeapMetrics(Collection<Metric<?>> result) {
		MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
		result.add(newMemoryMetric("nonheap.committed", memoryUsage.getCommitted()));
		result.add(newMemoryMetric("nonheap.init", memoryUsage.getInit()));
		result.add(newMemoryMetric("nonheap.used", memoryUsage.getUsed()));
		result.add(newMemoryMetric("nonheap", memoryUsage.getMax()));
	}

	/**
	 * Add thread metrics.
	 *
	 * @param result
	 *            the result
	 */
	protected void addThreadMetrics(Collection<Metric<?>> result) {
		ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
		result.add(new Metric<Long>("threads.peak", (long) threadMxBean.getPeakThreadCount()));
		result.add(new Metric<Long>("threads.daemon", (long) threadMxBean.getDaemonThreadCount()));
		result.add(new Metric<Long>("threads.totalStarted", threadMxBean.getTotalStartedThreadCount()));
		result.add(new Metric<Long>("threads", (long) threadMxBean.getThreadCount()));
	}

	/**
	 * Add class loading metrics.
	 *
	 * @param result
	 *            the result
	 */
	protected void addClassLoadingMetrics(Collection<Metric<?>> result) {
		ClassLoadingMXBean classLoadingMxBean = ManagementFactory.getClassLoadingMXBean();
		result.add(new Metric<Long>("classes", (long) classLoadingMxBean.getLoadedClassCount()));
		result.add(new Metric<Long>("classes.loaded", classLoadingMxBean.getTotalLoadedClassCount()));
		result.add(new Metric<Long>("classes.unloaded", classLoadingMxBean.getUnloadedClassCount()));
	}

	/**
	 * Add garbage collection metrics.
	 *
	 * @param result
	 *            the result
	 */
	protected void addGarbageCollectionMetrics(Collection<Metric<?>> result) {
		List<GarbageCollectorMXBean> garbageCollectorMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
		for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMxBeans) {
			String name = beautifyGcName(garbageCollectorMXBean.getName());
			result.add(new Metric<Long>("gc." + name + ".count", garbageCollectorMXBean.getCollectionCount()));
			result.add(new Metric<Long>("gc." + name + ".time", garbageCollectorMXBean.getCollectionTime()));
		}
	}

	/**
	 * Turn GC names like 'PS Scavenge' or 'PS MarkSweep' into something that is
	 * more metrics friendly.
	 *
	 * @param name
	 *            the source name
	 * @return a metric friendly name
	 */
	private String beautifyGcName(String name) {
		return StringUtils.replace(name, " ", "_").toLowerCase();
	}
}
