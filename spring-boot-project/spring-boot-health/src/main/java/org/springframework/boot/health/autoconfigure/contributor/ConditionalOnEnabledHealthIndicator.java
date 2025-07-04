/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.health.autoconfigure.contributor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

/**
 * {@link Conditional @Conditional} that checks whether a default health indicator is
 * enabled. Matches if the value of the {@code management.health.<name>.enabled} property
 * is {@code true}. Otherwise, matches if the value of the
 * {@code management.health.defaults.enabled} property is {@code true} or if it is not
 * configured.
 *
 * @author Stephane Nicoll
 * @since 4.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnEnabledHealthIndicatorCondition.class)
public @interface ConditionalOnEnabledHealthIndicator {

	/**
	 * The name of the health indicator.
	 * @return the name of the health indicator
	 */
	String value();

}
