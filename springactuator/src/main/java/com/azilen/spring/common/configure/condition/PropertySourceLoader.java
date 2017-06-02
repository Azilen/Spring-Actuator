package com.azilen.spring.common.configure.condition;

import java.io.IOException;

import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

public interface PropertySourceLoader {
	/**
	 * Returns the file extensions that the loader supports (excluding the '.').
	 * @return the file extensions
	 */
	String[] getFileExtensions();

	/**
	 * Load the resource into a property source.
	 * @param name the name of the property source
	 * @param resource the resource to load
	 * @param profile the name of the profile to load or {@code null}. The profile can be
	 * used to load multi-document files (such as YAML). Simple property formats should
	 * {@code null} when asked to load a profile.
	 * @return a property source or {@code null}
	 * @throws IOException if the source cannot be loaded
	 */
	PropertySource<?> load(String name, Resource resource, String profile)
			throws IOException;

}
