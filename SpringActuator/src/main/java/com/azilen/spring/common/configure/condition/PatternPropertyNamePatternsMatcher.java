package com.azilen.spring.common.configure.condition;

import java.util.Collection;

import org.springframework.util.PatternMatchUtils;

class PatternPropertyNamePatternsMatcher implements PropertyNamePatternsMatcher{
	private final String[] patterns;

	PatternPropertyNamePatternsMatcher(Collection<String> patterns) {
		this.patterns = patterns == null ? new String[0] : (String[]) patterns
				.toArray(new String[patterns.size()]);
	}

	public boolean matches(String propertyName) {
		return PatternMatchUtils.simpleMatch(this.patterns, propertyName);
	}
}
