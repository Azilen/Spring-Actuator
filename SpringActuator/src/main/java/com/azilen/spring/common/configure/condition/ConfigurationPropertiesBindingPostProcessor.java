package com.azilen.spring.common.configure.condition;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class ConfigurationPropertiesBindingPostProcessor implements
BeanPostProcessor, BeanFactoryAware, ResourceLoaderAware,
EnvironmentAware, ApplicationContextAware, InitializingBean,
DisposableBean, ApplicationListener<ContextRefreshedEvent>,
PriorityOrdered{
	public static final String VALIDATOR_BEAN_NAME = "configurationPropertiesValidator";
	private static final String[] VALIDATOR_CLASSES = new String[] {
			"javax.validation.Validator", "javax.validation.ValidatorFactory" };
	private static final Log logger = LogFactory
			.getLog(ConfigurationPropertiesBindingPostProcessor.class);
	private ConfigurationBeanFactoryMetaData beans = new ConfigurationBeanFactoryMetaData();
	private PropertySources propertySources;
	private Validator validator;
	private volatile Validator localValidator;
	private ConversionService conversionService;
	private DefaultConversionService defaultConversionService;
	private BeanFactory beanFactory;
	private ResourceLoader resourceLoader = new DefaultResourceLoader();
	private Environment environment = new StandardEnvironment();
	private ApplicationContext applicationContext;
	private List<Converter<?, ?>> converters = Collections.emptyList();
	private List<GenericConverter> genericConverters = Collections.emptyList();
	private int order = -2147483647;

	@Autowired(required = false)
	@ConfigurationPropertiesBinding
	public void setConverters(List<Converter<?, ?>> converters) {
		this.converters = converters;
	}

	@Autowired(required = false)
	@ConfigurationPropertiesBinding
	public void setGenericConverters(List<GenericConverter> converters) {
		this.genericConverters = converters;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return this.order;
	}

	public void setPropertySources(PropertySources propertySources) {
		this.propertySources = propertySources;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public void setBeanMetaDataStore(ConfigurationBeanFactoryMetaData beans) {
		this.beans = beans;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void afterPropertiesSet() throws Exception {
		if (this.propertySources == null) {
			this.propertySources = this.deducePropertySources();
		}

		if (this.validator == null) {
			this.validator = (Validator) this.getOptionalBean(
					"configurationPropertiesValidator", Validator.class);
		}

		if (this.conversionService == null) {
			this.conversionService = (ConversionService) this.getOptionalBean(
					"conversionService", ConversionService.class);
		}

	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		this.freeLocalValidator();
	}

	public void destroy() throws Exception {
		this.freeLocalValidator();
	}

	private void freeLocalValidator() {
		try {
			Validator ex = this.localValidator;
			this.localValidator = null;
			if (ex != null) {
				((DisposableBean) ex).destroy();
			}

		} catch (Exception arg1) {
			throw new IllegalStateException(arg1);
		}
	}

	private PropertySources deducePropertySources() {
		PropertySourcesPlaceholderConfigurer configurer = this
				.getSinglePropertySourcesPlaceholderConfigurer();
		if (configurer != null) {
			return new ConfigurationPropertiesBindingPostProcessor.FlatPropertySources(
					configurer.getAppliedPropertySources());
		} else if (this.environment instanceof ConfigurableEnvironment) {
			MutablePropertySources propertySources = ((ConfigurableEnvironment) this.environment)
					.getPropertySources();
			return new ConfigurationPropertiesBindingPostProcessor.FlatPropertySources(
					propertySources);
		} else {
			logger.warn("Unable to obtain PropertySources from PropertySourcesPlaceholderConfigurer or Environment");
			return new MutablePropertySources();
		}
	}

	private PropertySourcesPlaceholderConfigurer getSinglePropertySourcesPlaceholderConfigurer() {
		if (this.beanFactory instanceof ListableBeanFactory) {
			ListableBeanFactory listableBeanFactory = (ListableBeanFactory) this.beanFactory;
			Map beans = listableBeanFactory.getBeansOfType(
					PropertySourcesPlaceholderConfigurer.class, false, false);
			if (beans.size() == 1) {
				return (PropertySourcesPlaceholderConfigurer) beans.values()
						.iterator().next();
			}

			if (beans.size() > 1 && logger.isWarnEnabled()) {
				logger.warn("Multiple PropertySourcesPlaceholderConfigurer beans registered "
						+ beans.keySet() + ", falling back to Environment");
			}
		}

		return null;
	}

	private <T> T getOptionalBean(String name, Class<T> type) {
		try {
			return this.beanFactory.getBean(name, type);
		} catch (NoSuchBeanDefinitionException arg3) {
			return null;
		}
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		ConfigurationProperties annotation = (ConfigurationProperties) AnnotationUtils
				.findAnnotation(bean.getClass(), ConfigurationProperties.class);
		if (annotation != null) {
			this.postProcessBeforeInitialization(bean, beanName, annotation);
		}

		annotation = (ConfigurationProperties) this.beans
				.findFactoryAnnotation(beanName, ConfigurationProperties.class);
		if (annotation != null) {
			this.postProcessBeforeInitialization(bean, beanName, annotation);
		}

		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	private void postProcessBeforeInitialization(Object bean, String beanName,
			ConfigurationProperties annotation) {
		PropertiesConfigurationFactory factory = new PropertiesConfigurationFactory(
				bean);
		if (annotation != null && annotation.locations().length != 0) {
			factory.setPropertySources(this.loadPropertySources(
					annotation.locations(), annotation.merge()));
		} else {
			factory.setPropertySources(this.propertySources);
		}

		factory.setValidator(this.determineValidator(bean));
		factory.setConversionService(this.conversionService == null ? this
				.getDefaultConversionService() : this.conversionService);
		if (annotation != null) {
			factory.setIgnoreInvalidFields(annotation.ignoreInvalidFields());
			factory.setIgnoreUnknownFields(annotation.ignoreUnknownFields());
			factory.setExceptionIfInvalid(annotation.exceptionIfInvalid());
			factory.setIgnoreNestedProperties(annotation
					.ignoreNestedProperties());
			if (StringUtils.hasLength(annotation.prefix())) {
				factory.setTargetName(annotation.prefix());
			}
		}

		try {
			factory.bindPropertiesToTarget();
		} catch (Exception arg7) {
			String targetClass = ClassUtils.getShortName(bean.getClass());
			throw new BeanCreationException(beanName,
					"Could not bind properties to " + targetClass + " ("
							+ this.getAnnotationDetails(annotation) + ")", arg7);
		}
	}

	private String getAnnotationDetails(ConfigurationProperties annotation) {
		if (annotation == null) {
			return "";
		} else {
			StringBuilder details = new StringBuilder();
			details.append("prefix=").append(annotation.prefix());
			details.append(", ignoreInvalidFields=").append(
					annotation.ignoreInvalidFields());
			details.append(", ignoreUnknownFields=").append(
					annotation.ignoreUnknownFields());
			details.append(", ignoreNestedProperties=").append(
					annotation.ignoreNestedProperties());
			return details.toString();
		}
	}

	private Validator determineValidator(Object bean) {
		Validator validator = this.getValidator();
		boolean supportsBean = validator != null
				&& validator.supports(bean.getClass());
		return (Validator) (ClassUtils.isAssignable(Validator.class,
				bean.getClass()) ? (supportsBean ? new ConfigurationPropertiesBindingPostProcessor.ChainingValidator(
				new Validator[] { validator, (Validator) bean })
				: (Validator) bean)
				: (supportsBean ? validator : null));
	}

	private Validator getValidator() {
		if (this.validator != null) {
			return this.validator;
		} else {
			if (this.localValidator == null && this.isJsr303Present()) {
				this.localValidator = (new ConfigurationPropertiesBindingPostProcessor.LocalValidatorFactory())
						.run(this.applicationContext);
			}

			return this.localValidator;
		}
	}

	private boolean isJsr303Present() {
		String[] arg0 = VALIDATOR_CLASSES;
		int arg1 = arg0.length;

		for (int arg2 = 0; arg2 < arg1; ++arg2) {
			String validatorClass = arg0[arg2];
			if (!ClassUtils.isPresent(validatorClass,
					this.applicationContext.getClassLoader())) {
				return false;
			}
		}

		return true;
	}

	private PropertySources loadPropertySources(String[] locations,
			boolean mergeDefaultSources) {
		try {
			PropertySourcesLoader ex = new PropertySourcesLoader();
			String[] loaded = locations;
			int arg4 = locations.length;

			for (int propertySource = 0; propertySource < arg4; ++propertySource) {
				String location = loaded[propertySource];
				Resource resource = this.resourceLoader
						.getResource(this.environment
								.resolvePlaceholders(location));
				String[] profiles = this.environment.getActiveProfiles();
				int i = profiles.length;

				while (i-- > 0) {
					String profile = profiles[i];
					ex.load(resource, profile);
				}

				ex.load(resource);
			}

			MutablePropertySources arg12 = ex.getPropertySources();
			if (mergeDefaultSources) {
				Iterator arg13 = this.propertySources.iterator();

				while (arg13.hasNext()) {
					PropertySource arg14 = (PropertySource) arg13.next();
					arg12.addLast(arg14);
				}
			}

			return arg12;
		} catch (IOException arg11) {
			throw new IllegalStateException(arg11);
		}
	}

	private ConversionService getDefaultConversionService() {
		if (this.defaultConversionService == null) {
			DefaultConversionService conversionService = new DefaultConversionService();
			this.applicationContext.getAutowireCapableBeanFactory()
					.autowireBean(this);
			Iterator arg1 = this.converters.iterator();

			while (arg1.hasNext()) {
				Converter genericConverter = (Converter) arg1.next();
				conversionService.addConverter(genericConverter);
			}

			arg1 = this.genericConverters.iterator();

			while (arg1.hasNext()) {
				GenericConverter genericConverter1 = (GenericConverter) arg1
						.next();
				conversionService.addConverter(genericConverter1);
			}

			this.defaultConversionService = conversionService;
		}

		return this.defaultConversionService;
	}

	private static class FlatPropertySources implements PropertySources {
		private PropertySources propertySources;

		FlatPropertySources(PropertySources propertySources) {
			this.propertySources = propertySources;
		}

		public Iterator<PropertySource<?>> iterator() {
			MutablePropertySources result = this.getFlattened();
			return result.iterator();
		}

		public boolean contains(String name) {
			return this.get(name) != null;
		}

		public PropertySource<?> get(String name) {
			return this.getFlattened().get(name);
		}

		private MutablePropertySources getFlattened() {
			MutablePropertySources result = new MutablePropertySources();
			Iterator arg1 = this.propertySources.iterator();

			while (arg1.hasNext()) {
				PropertySource propertySource = (PropertySource) arg1.next();
				this.flattenPropertySources(propertySource, result);
			}

			return result;
		}

		private void flattenPropertySources(PropertySource<?> propertySource,
				MutablePropertySources result) {
			Object source = propertySource.getSource();
			if (source instanceof ConfigurableEnvironment) {
				ConfigurableEnvironment environment = (ConfigurableEnvironment) source;
				Iterator arg4 = environment.getPropertySources().iterator();

				while (arg4.hasNext()) {
					PropertySource childSource = (PropertySource) arg4.next();
					this.flattenPropertySources(childSource, result);
				}
			} else {
				result.addLast(propertySource);
			}

		}
	}

	private static class ChainingValidator implements Validator {
		private Validator[] validators;

		ChainingValidator(Validator... validators) {
			Assert.notNull(validators, "Validators must not be null");
			this.validators = validators;
		}

		public boolean supports(Class<?> clazz) {
			Validator[] arg1 = this.validators;
			int arg2 = arg1.length;

			for (int arg3 = 0; arg3 < arg2; ++arg3) {
				Validator validator = arg1[arg3];
				if (validator.supports(clazz)) {
					return true;
				}
			}

			return false;
		}

		public void validate(Object target, Errors errors) {
			Validator[] arg2 = this.validators;
			int arg3 = arg2.length;

			for (int arg4 = 0; arg4 < arg3; ++arg4) {
				Validator validator = arg2[arg4];
				if (validator.supports(target.getClass())) {
					validator.validate(target, errors);
				}
			}

		}
	}

	private static class LocalValidatorFactory {
		private LocalValidatorFactory() {
		}

		public Validator run(ApplicationContext applicationContext) {
			LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
			validator.setApplicationContext(applicationContext);
			validator.afterPropertiesSet();
			return validator;
		}
	}

}
