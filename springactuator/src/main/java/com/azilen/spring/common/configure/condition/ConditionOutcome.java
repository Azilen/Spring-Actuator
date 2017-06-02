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
	private final ConditionMessage message;

	public ConditionOutcome(boolean match, String message) {
		this(match, ConditionMessage.of(message, new Object[0]));
	}

	public ConditionOutcome(boolean match, ConditionMessage message) {
		Assert.notNull(message, "ConditionMessage must not be null");
		this.match = match;
		this.message = message;
	}

	public static ConditionOutcome match() {
		return match(ConditionMessage.empty());
	}

	public static ConditionOutcome match(String message) {
		return new ConditionOutcome(true, message);
	}

	public static ConditionOutcome match(ConditionMessage message) {
		return new ConditionOutcome(true, message);
	}

	public static ConditionOutcome noMatch(String message) {
		return new ConditionOutcome(false, message);
	}

	public static ConditionOutcome noMatch(ConditionMessage message) {
		return new ConditionOutcome(false, message);
	}

	public boolean isMatch() {
		return this.match;
	}

	public String getMessage() {
		return this.message.isEmpty() ? null : this.message.toString();
	}

	public ConditionMessage getConditionMessage() {
		return this.message;
	}

	public int hashCode() {
		return ObjectUtils.hashCode(this.match) * 31
				+ ObjectUtils.nullSafeHashCode(this.message);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (this.getClass() != obj.getClass()) {
			return super.equals(obj);
		} else {
			ConditionOutcome other = (ConditionOutcome) obj;
			return this.match == other.match
					&& ObjectUtils.nullSafeEquals(this.message, other.message);
		}
	}

	public String toString() {
		return this.message == null ? "" : this.message.toString();
	}

	public static ConditionOutcome inverse(ConditionOutcome outcome) {
		return new ConditionOutcome(!outcome.isMatch(),
				outcome.getConditionMessage());
	}

}
