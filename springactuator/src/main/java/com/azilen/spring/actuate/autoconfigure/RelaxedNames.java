package com.azilen.spring.actuate.autoconfigure;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class RelaxedNames implements Iterable<String> {
	private static final Pattern CAMEL_CASE_PATTERN = Pattern
			.compile("([^A-Z-])([A-Z])");
	private static final Pattern SEPARATED_TO_CAMEL_CASE_PATTERN = Pattern
			.compile("[_\\-.]");
	private final String name;
	private final Set<String> values = new LinkedHashSet<>();

	public RelaxedNames(String name) {
		this.name = name == null ? "" : name;
		this.initialize(this.name, this.values);
	}

	public Iterator<String> iterator() {
		return this.values.iterator();
	}

	private void initialize(String name, Set<String> values) {
		if (!values.contains(name)) {
			RelaxedNames.Variation[] arg2 = RelaxedNames.Variation.values();
			int arg3 = arg2.length;

			for (int arg4 = 0; arg4 < arg3; ++arg4) {
				RelaxedNames.Variation variation = arg2[arg4];
				RelaxedNames.Manipulation[] arg6 = RelaxedNames.Manipulation
						.values();
				int arg7 = arg6.length;

				for (int arg8 = 0; arg8 < arg7; ++arg8) {
					RelaxedNames.Manipulation manipulation = arg6[arg8];
					String result = manipulation.apply(name);
					result = variation.apply(result);
					values.add(result);
					this.initialize(result, values);
				}
			}

		}
	}

	public static RelaxedNames forCamelCase(String name) {
		StringBuffer result = new StringBuffer();
		char[] arg1 = name.toCharArray();
		int arg2 = arg1.length;

		for (int arg3 = 0; arg3 < arg2; ++arg3) {
			char c = arg1[arg3];
			result.append(Character.isUpperCase(c) && result.length() > 0
					&& result.charAt(result.length() - 1) != 45 ? "-"
					+ Character.toLowerCase(c) : Character.valueOf(c));
		}

		return new RelaxedNames(result.toString());
	}

	static enum Manipulation {
		NONE {
			public String apply(String value) {
				return value;
			}
		},
		HYPHEN_TO_UNDERSCORE {
			public String apply(String value) {
				return value.replace("-", "_");
			}
		},
		UNDERSCORE_TO_PERIOD {
			public String apply(String value) {
				return value.replace("_", ".");
			}
		},
		PERIOD_TO_UNDERSCORE {
			public String apply(String value) {
				return value.replace(".", "_");
			}
		},
		CAMELCASE_TO_UNDERSCORE {
			public String apply(String value) {
				Matcher matcher = RelaxedNames.CAMEL_CASE_PATTERN
						.matcher(value);
				StringBuffer result = new StringBuffer();

				while (matcher.find()) {
					matcher.appendReplacement(result, matcher.group(1) + '_'
							+ StringUtils.uncapitalize(matcher.group(2)));
				}

				matcher.appendTail(result);
				return result.toString();
			}
		},
		CAMELCASE_TO_HYPHEN {
			public String apply(String value) {
				Matcher matcher = RelaxedNames.CAMEL_CASE_PATTERN
						.matcher(value);
				StringBuffer result = new StringBuffer();

				while (matcher.find()) {
					matcher.appendReplacement(result, matcher.group(1) + '-'
							+ StringUtils.uncapitalize(matcher.group(2)));
				}

				matcher.appendTail(result);
				return result.toString();
			}
		},
		SEPARATED_TO_CAMELCASE {
			public String apply(String value) {
				return RelaxedNames.Manipulation.separatedToCamelCase(value,
						false);
			}
		},
		CASE_INSENSITIVE_SEPARATED_TO_CAMELCASE {
			public String apply(String value) {
				return RelaxedNames.Manipulation.separatedToCamelCase(value,
						true);
			}
		};

		private static final char[] SUFFIXES = new char[] { '_', '-', '.' };

		private Manipulation() {
		}

		public abstract String apply(String arg0);

		private static String separatedToCamelCase(String value,
				boolean caseInsensitive) {
			if (value.length() == 0) {
				return value;
			} else {
				StringBuilder builder = new StringBuilder();
				String[] lastChar = RelaxedNames.SEPARATED_TO_CAMEL_CASE_PATTERN
						.split(value);
				int arg3 = lastChar.length;

				int arg4;
				for (arg4 = 0; arg4 < arg3; ++arg4) {
					String field = lastChar[arg4];
					field = caseInsensitive ? field.toLowerCase() : field;
					builder.append(builder.length() == 0 ? field : StringUtils
							.capitalize(field));
				}

				char arg7 = value.charAt(value.length() - 1);
				char[] arg8 = SUFFIXES;
				arg4 = arg8.length;

				for (int arg9 = 0; arg9 < arg4; ++arg9) {
					char suffix = arg8[arg9];
					if (arg7 == suffix) {
						builder.append(suffix);
						break;
					}
				}

				return builder.toString();
			}
		}
	}

	static enum Variation {
		NONE {
			public String apply(String value) {
				return value;
			}
		},
		LOWERCASE {
			public String apply(String value) {
				return value.toLowerCase();
			}
		},
		UPPERCASE {
			public String apply(String value) {
				return value.toUpperCase();
			}
		};

		private Variation() {
		}

		public abstract String apply(String arg0);
	}
}
