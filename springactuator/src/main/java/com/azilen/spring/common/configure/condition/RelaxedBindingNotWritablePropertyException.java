package com.azilen.spring.common.configure.condition;

import org.springframework.beans.NotWritablePropertyException;

public class RelaxedBindingNotWritablePropertyException extends NotWritablePropertyException {
	private final String message;

	private final PropertyOrigin propertyOrigin;

	RelaxedBindingNotWritablePropertyException(NotWritablePropertyException ex,
			PropertyOrigin propertyOrigin) {
		super(ex.getBeanClass(), ex.getPropertyName());
		this.propertyOrigin = propertyOrigin;
		this.message = "Failed to bind '" + propertyOrigin.getName() + "' from '"
				+ propertyOrigin.getSource().getName() + "' to '" + ex.getPropertyName()
				+ "' property on '" + ex.getBeanClass().getName() + "'";
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	public PropertyOrigin getPropertyOrigin() {
		return this.propertyOrigin;
	}

}
