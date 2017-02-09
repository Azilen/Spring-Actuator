/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.azilen.spring.common.configure.condition;


import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Outcome for a condition match, including log message.
 *
 * @author Phillip Webb
 */
public class ConditionOutcome {

	private final boolean match;

	//private final String message;
	private final ConditionMessage message;

	public ConditionOutcome(boolean match, String message) {
		this.match = match;
		this.message = ConditionMessage.of(message);
	}
	
	/**
	 * Create a new {@link ConditionOutcome} instance.
	 * @param match if the condition is a match
	 * @param message the condition message
	 */
	public ConditionOutcome(boolean match, ConditionMessage message) {
		Assert.notNull(message, "ConditionMessage must not be null");
		this.match = match;
		this.message = message;
	}

	/**
	 * Create a new {@link ConditionOutcome} instance for a 'match'.
	 * @return the {@link ConditionOutcome}
	 */
	public static ConditionOutcome match() {
		return match(null);
	}

	/**
	 * Create a new {@link ConditionOutcome} instance for 'match'.
	 * @param message the message
	 * @return the {@link ConditionOutcome}
	 */
	public static ConditionOutcome match(String message) {
		return new ConditionOutcome(true, message);
	}

	/**
	 * Create a new {@link ConditionOutcome} instance for 'no match'.
	 * @param message the message
	 * @return the {@link ConditionOutcome}
	 */
	public static ConditionOutcome noMatch(String message) {
		return new ConditionOutcome(false, message);
	}

	/**
	 * Return {@code true} if the outcome was a match.
	 * @return {@code true} if the outcome matches
	 */
	public boolean isMatch() {
		return this.match;
	}

	/**
	 * Return an outcome message or {@code null}.
	 * @return the message or {@code null}
	 */
	public String getMessage() {
		return (this.message.isEmpty() ? null : this.message.toString());
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.match) * 31
				+ ObjectUtils.nullSafeHashCode(this.message);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() == obj.getClass()) {
			ConditionOutcome other = (ConditionOutcome) obj;
			return (this.match == other.match
					&& ObjectUtils.nullSafeEquals(this.message, other.message));
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return (this.message == null ? "" : this.message.toString());
	}

	/**
	 * Return the inverse of the specified condition outcome.
	 * @param outcome the outcome to inverse
	 * @return the inverse of the condition outcome
	 * @since 1.3.0
	 */
	public static ConditionOutcome inverse(ConditionOutcome outcome) {
		return new ConditionOutcome(!outcome.isMatch(), outcome.getMessage());
	}

}
