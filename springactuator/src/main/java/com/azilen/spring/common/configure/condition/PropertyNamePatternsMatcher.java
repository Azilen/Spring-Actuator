package com.azilen.spring.common.configure.condition;


interface PropertyNamePatternsMatcher {
	PropertyNamePatternsMatcher ALL = new PropertyNamePatternsMatcher() {
		public boolean matches(String propertyName) {
			return true;
		}
	};
	PropertyNamePatternsMatcher NONE = new PropertyNamePatternsMatcher() {
		public boolean matches(String propertyName) {
			return false;
		}
	};

	boolean matches(String arg0);
}
