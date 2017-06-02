package com.azilen.spring.actuate.autoconfigure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public abstract class AutoConfigurationPackages {
	private static final Log logger = LogFactory.getLog(AutoConfigurationPackages.class);
	private static final String BEAN = AutoConfigurationPackages.class.getName();

	public static boolean has(BeanFactory beanFactory) {
		return beanFactory.containsBean(BEAN) && !get(beanFactory).isEmpty();
	}

	public static List<String> get(BeanFactory beanFactory) {
		try {
			return ((AutoConfigurationPackages.BasePackages) beanFactory.getBean(BEAN,
					AutoConfigurationPackages.BasePackages.class)).get();
		} catch (NoSuchBeanDefinitionException arg1) {
			throw new IllegalStateException("Unable to retrieve @EnableAutoConfiguration base packages");
		}
	}

	public static void register(BeanDefinitionRegistry registry, String... packageNames) {
		if (registry.containsBeanDefinition(BEAN)) {
			BeanDefinition beanDefinition = registry.getBeanDefinition(BEAN);
			ConstructorArgumentValues constructorArguments = beanDefinition.getConstructorArgumentValues();
			constructorArguments.addIndexedArgumentValue(0, addBasePackages(constructorArguments, packageNames));
		} else {
			GenericBeanDefinition beanDefinition1 = new GenericBeanDefinition();
			beanDefinition1.setBeanClass(AutoConfigurationPackages.BasePackages.class);
			beanDefinition1.getConstructorArgumentValues().addIndexedArgumentValue(0, packageNames);
			beanDefinition1.setRole(2);
			registry.registerBeanDefinition(BEAN, beanDefinition1);
		}

	}

	private static String[] addBasePackages(ConstructorArgumentValues constructorArguments, String[] packageNames) {
		String[] existing = (String[]) ((String[]) constructorArguments.getIndexedArgumentValue(0, String[].class)
				.getValue());
		LinkedHashSet merged = new LinkedHashSet();
		merged.addAll(Arrays.asList(existing));
		merged.addAll(Arrays.asList(packageNames));
		return (String[]) merged.toArray(new String[merged.size()]);
	}

	static final class BasePackages {
		private final List<String> packages;
		private boolean loggedBasePackageInfo;

		BasePackages(String... names) {
			ArrayList packages = new ArrayList();
			String[] arg2 = names;
			int arg3 = names.length;

			for (int arg4 = 0; arg4 < arg3; ++arg4) {
				String name = arg2[arg4];
				if (StringUtils.hasText(name)) {
					packages.add(name);
				}
			}

			this.packages = packages;
		}

		public List<String> get() {
			if (!this.loggedBasePackageInfo) {
				if (this.packages.isEmpty()) {
					if (AutoConfigurationPackages.logger.isWarnEnabled()) {
						AutoConfigurationPackages.logger.warn(
								"@EnableAutoConfiguration was declared on a class in the default package. Automatic @Repository and @Entity scanning is not enabled.");
					}
				} else if (AutoConfigurationPackages.logger.isDebugEnabled()) {
					String packageNames = StringUtils.collectionToCommaDelimitedString(this.packages);
					AutoConfigurationPackages.logger
							.debug("@EnableAutoConfiguration was declared on a class in the package \'" + packageNames
									+ "\'. Automatic @Repository and @Entity scanning is " + "enabled.");
				}

				this.loggedBasePackageInfo = true;
			}

			return this.packages;
		}
	}

	@Order(Integer.MIN_VALUE)
	static class Registrar implements ImportBeanDefinitionRegistrar {
		public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
			AutoConfigurationPackages.register(registry,
					new String[] { ClassUtils.getPackageName(metadata.getClassName()) });
		}
	}
}
