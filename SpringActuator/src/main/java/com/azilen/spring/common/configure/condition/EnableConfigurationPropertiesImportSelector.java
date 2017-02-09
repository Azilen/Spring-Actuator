package com.azilen.spring.common.configure.condition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public class EnableConfigurationPropertiesImportSelector implements ImportSelector {
	public String[] selectImports(AnnotationMetadata metadata) {
		MultiValueMap attributes = metadata.getAllAnnotationAttributes(
				EnableConfigurationProperties.class.getName(), false);
		Object[] type = attributes == null ? null
				: (Object[]) ((Object[]) attributes.getFirst("value"));
		return type != null && type.length != 0 ? new String[] {
				EnableConfigurationPropertiesImportSelector.ConfigurationPropertiesBeanRegistrar.class
						.getName(),
				ConfigurationPropertiesBindingPostProcessorRegistrar.class
						.getName() }
				: new String[] { ConfigurationPropertiesBindingPostProcessorRegistrar.class
						.getName() };
	}

	public static class ConfigurationPropertiesBeanRegistrar implements
			ImportBeanDefinitionRegistrar {
		public void registerBeanDefinitions(AnnotationMetadata metadata,
				BeanDefinitionRegistry registry) {
			MultiValueMap attributes = metadata.getAllAnnotationAttributes(
					EnableConfigurationProperties.class.getName(), false);
			List types = this.collectClasses((List) attributes.get("value"));
			Iterator arg4 = types.iterator();

			while (arg4.hasNext()) {
				Class type = (Class) arg4.next();
				String prefix = this.extractPrefix(type);
				String name = StringUtils.hasText(prefix) ? prefix + "-"
						+ type.getName() : type.getName();
				if (!registry.containsBeanDefinition(name)) {
					this.registerBeanDefinition(registry, type, name);
				}
			}

		}

		private String extractPrefix(Class<?> type) {
			ConfigurationProperties annotation = (ConfigurationProperties) AnnotationUtils
					.findAnnotation(type, ConfigurationProperties.class);
			return annotation != null ? annotation.prefix() : "";
		}

		private List<Class<?>> collectClasses(List<Object> list) {
			ArrayList result = new ArrayList();
			Iterator arg2 = list.iterator();

			while (arg2.hasNext()) {
				Object object = arg2.next();
				Object[] arg4 = (Object[]) ((Object[]) object);
				int arg5 = arg4.length;

				for (int arg6 = 0; arg6 < arg5; ++arg6) {
					Object value = arg4[arg6];
					if (value instanceof Class && value != Void.TYPE) {
						result.add((Class) value);
					}
				}
			}

			return result;
		}

		private void registerBeanDefinition(BeanDefinitionRegistry registry,
				Class<?> type, String name) {
			BeanDefinitionBuilder builder = BeanDefinitionBuilder
					.genericBeanDefinition(type);
			AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
			registry.registerBeanDefinition(name, beanDefinition);
			ConfigurationProperties properties = (ConfigurationProperties) AnnotationUtils
					.findAnnotation(type, ConfigurationProperties.class);
			Assert.notNull(properties,
					"No " + ConfigurationProperties.class.getSimpleName()
							+ " annotation found on  \'" + type.getName()
							+ "\'.");
		}
	}
}
