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

package org.springframework.boot.webservices.autoconfigure;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link OnWsdlLocationsCondition}.
 *
 * @author Eneias Silva
 * @author Stephane Nicoll
 */
class OnWsdlLocationsConditionTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withUserConfiguration(TestConfig.class);

	@Test
	void wsdlLocationsNotDefined() {
		this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean("foo"));
	}

	@Test
	void wsdlLocationsDefinedAsCommaSeparated() {
		this.contextRunner.withPropertyValues("spring.webservices.wsdl-locations=value1")
			.run((context) -> assertThat(context).hasBean("foo"));
	}

	@Test
	void wsdlLocationsDefinedAsList() {
		this.contextRunner.withPropertyValues("spring.webservices.wsdl-locations[0]=value1")
			.run((context) -> assertThat(context).hasBean("foo"));
	}

	@Configuration(proxyBeanMethods = false)
	@Conditional(OnWsdlLocationsCondition.class)
	static class TestConfig {

		@Bean
		String foo() {
			return "foo";
		}

	}

}
