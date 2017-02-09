package com.azilen.spring.common.configure.condition;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.PropertySources;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

public class PropertiesConfigurationFactory<T> implements FactoryBean<T>,
MessageSourceAware, InitializingBean{
	private static final char[] EXACT_DELIMITERS = new char[] { '_', '.', '[' };
	private static final char[] TARGET_NAME_DELIMITERS = new char[] { '_', '.' };
	private final Log logger = LogFactory.getLog(this.getClass());
	private boolean ignoreUnknownFields = true;
	private boolean ignoreInvalidFields;
	private boolean exceptionIfInvalid = true;
	private Properties properties;
	private PropertySources propertySources;
	private final T target;
	private Validator validator;
	private MessageSource messageSource;
	private boolean hasBeenBound = false;
	private boolean ignoreNestedProperties = false;
	private String targetName;
	private ConversionService conversionService;
	private boolean resolvePlaceholders = true;

	public PropertiesConfigurationFactory(T target) {
		Assert.notNull(target);
		this.target = target;
	}

	public PropertiesConfigurationFactory(Class<T> type) { //Added Class<T> instead of Class<?>
		Assert.notNull(type);
		this.target = BeanUtils.instantiate(type);
	}

	public void setIgnoreNestedProperties(boolean ignoreNestedProperties) {
		this.ignoreNestedProperties = ignoreNestedProperties;
	}

	public void setIgnoreUnknownFields(boolean ignoreUnknownFields) {
		this.ignoreUnknownFields = ignoreUnknownFields;
	}

	public void setIgnoreInvalidFields(boolean ignoreInvalidFields) {
		this.ignoreInvalidFields = ignoreInvalidFields;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Deprecated
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void setPropertySources(PropertySources propertySources) {
		this.propertySources = propertySources;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public void setExceptionIfInvalid(boolean exceptionIfInvalid) {
		this.exceptionIfInvalid = exceptionIfInvalid;
	}

	public void setResolvePlaceholders(boolean resolvePlaceholders) {
		this.resolvePlaceholders = resolvePlaceholders;
	}

	public void afterPropertiesSet() throws Exception {
		this.bindPropertiesToTarget();
	}

	public Class<?> getObjectType() {
		return this.target == null ? Object.class : this.target.getClass();
	}

	public boolean isSingleton() {
		return true;
	}

	public T getObject() throws Exception {
		if (!this.hasBeenBound) {
			this.bindPropertiesToTarget();
		}

		return this.target;
	}

	public void bindPropertiesToTarget() throws BindException {
		Assert.state(this.properties != null || this.propertySources != null,
				"Properties or propertySources should not be null");

		try {
			if (this.logger.isTraceEnabled()) {
				if (this.properties != null) {
					this.logger.trace(String.format("Properties:%n%s",
							new Object[] { this.properties }));
				} else {
					this.logger.trace("Property Sources: "
							+ this.propertySources);
				}
			}

			this.hasBeenBound = true;
			this.doBindPropertiesToTarget();
		} catch (BindException arg1) {
			if (this.exceptionIfInvalid) {
				throw arg1;
			}

			this.logger
					.error("Failed to load Properties validation bean. Your Properties may be invalid.",
							arg1);
		}

	}

	private void doBindPropertiesToTarget() throws BindException {
		RelaxedDataBinder dataBinder = this.targetName != null ? new RelaxedDataBinder(
				this.target, this.targetName) : new RelaxedDataBinder(
				this.target);
		if (this.validator != null) {
			dataBinder.setValidator(this.validator);
		}

		if (this.conversionService != null) {
			dataBinder.setConversionService(this.conversionService);
		}

		dataBinder.setAutoGrowCollectionLimit(Integer.MAX_VALUE);
		dataBinder.setIgnoreNestedProperties(this.ignoreNestedProperties);
		dataBinder.setIgnoreInvalidFields(this.ignoreInvalidFields);
		dataBinder.setIgnoreUnknownFields(this.ignoreUnknownFields);
		this.customizeBinder(dataBinder);
		Iterable relaxedTargetNames = this.getRelaxedTargetNames();
		Set names = this.getNames(relaxedTargetNames);
		PropertyValues propertyValues = this.getPropertyValues(names,
				relaxedTargetNames);
		dataBinder.bind(propertyValues);
		if (this.validator != null) {
			this.validate(dataBinder);
		}

	}

	private Iterable<String> getRelaxedTargetNames() {
		return this.target != null && StringUtils.hasLength(this.targetName) ? new RelaxedNames(
				this.targetName) : null;
	}

	private Set<String> getNames(Iterable<String> prefixes) {
		LinkedHashSet names = new LinkedHashSet();
		if (this.target != null) {
			PropertyDescriptor[] descriptors = BeanUtils
					.getPropertyDescriptors(this.target.getClass());
			PropertyDescriptor[] arg3 = descriptors;
			int arg4 = descriptors.length;

			for (int arg5 = 0; arg5 < arg4; ++arg5) {
				PropertyDescriptor descriptor = arg3[arg5];
				String name = descriptor.getName();
				if (!name.equals("class")) {
					RelaxedNames relaxedNames = RelaxedNames.forCamelCase(name);
					Iterator arg9;
					String prefix;
					if (prefixes == null) {
						arg9 = relaxedNames.iterator();

						while (arg9.hasNext()) {
							prefix = (String) arg9.next();
							names.add(prefix);
						}
					} else {
						arg9 = prefixes.iterator();

						while (arg9.hasNext()) {
							prefix = (String) arg9.next();
							Iterator arg11 = relaxedNames.iterator();

							while (arg11.hasNext()) {
								String relaxedName = (String) arg11.next();
								names.add(prefix + "." + relaxedName);
								names.add(prefix + "_" + relaxedName);
							}
						}
					}
				}
			}
		}

		return names;
	}

	private PropertyValues getPropertyValues(Set<String> names,
			Iterable<String> relaxedTargetNames) {
		return (PropertyValues) (this.properties != null ? new MutablePropertyValues(
				this.properties) : this.getPropertySourcesPropertyValues(names,
				relaxedTargetNames));
	}

	private PropertyValues getPropertySourcesPropertyValues(Set<String> names,
			Iterable<String> relaxedTargetNames) {
		PropertyNamePatternsMatcher includes = this
				.getPropertyNamePatternsMatcher(names, relaxedTargetNames);
		return new PropertySourcesPropertyValues(this.propertySources, names,
				includes, this.resolvePlaceholders);
	}

	private PropertyNamePatternsMatcher getPropertyNamePatternsMatcher(
			Set<String> names, Iterable<String> relaxedTargetNames) {
		if (this.ignoreUnknownFields && !this.isMapTarget()) {
			return new DefaultPropertyNamePatternsMatcher(EXACT_DELIMITERS,
					true, names);
		} else if (relaxedTargetNames == null) {
			return PropertyNamePatternsMatcher.ALL;
		} else {
			HashSet relaxedNames = new HashSet();
			Iterator arg3 = relaxedTargetNames.iterator();

			while (arg3.hasNext()) {
				String relaxedTargetName = (String) arg3.next();
				relaxedNames.add(relaxedTargetName);
			}

			return new DefaultPropertyNamePatternsMatcher(
					TARGET_NAME_DELIMITERS, true, relaxedNames);
		}
	}

	private boolean isMapTarget() {
		return this.target != null
				&& Map.class.isAssignableFrom(this.target.getClass());
	}

	private void validate(RelaxedDataBinder dataBinder) throws BindException {
		dataBinder.validate();
		BindingResult errors = dataBinder.getBindingResult();
		if (errors.hasErrors()) {
			this.logger.error("Properties configuration failed validation");
			Iterator arg2 = errors.getAllErrors().iterator();

			while (arg2.hasNext()) {
				ObjectError error = (ObjectError) arg2.next();
				this.logger
						.error(this.messageSource != null ? this.messageSource
								.getMessage(error, Locale.getDefault())
								+ " ("
								+ error + ")" : error);
			}

			if (this.exceptionIfInvalid) {
				throw new BindException(errors);
			}
		}

	}

	protected void customizeBinder(DataBinder dataBinder) {
	}
}
