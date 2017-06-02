package com.azilen.spring.common.configure.condition;

import org.springframework.core.convert.converter.Converter;

public class StringToCharArrayConverter implements Converter<String, char[]>{
	@Override
	public char[] convert(String source) {
		return source.toCharArray();
	}
}
