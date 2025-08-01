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

package org.springframework.boot.liquibase.autoconfigure.endpoint;

import liquibase.integration.spring.SpringLiquibase;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.liquibase.autoconfigure.DataSourceClosingSpringLiquibase;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration;
import org.springframework.boot.liquibase.endpoint.LiquibaseEndpoint;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link LiquibaseEndpoint}.
 *
 * @author Phillip Webb
 * @since 4.0.0
 */
@AutoConfiguration(after = LiquibaseAutoConfiguration.class)
@ConditionalOnClass({ SpringLiquibase.class, LiquibaseEndpoint.class, ConditionalOnAvailableEndpoint.class })
@ConditionalOnAvailableEndpoint(LiquibaseEndpoint.class)
public final class LiquibaseEndpointAutoConfiguration {

	@Bean
	@ConditionalOnBean(SpringLiquibase.class)
	@ConditionalOnMissingBean
	LiquibaseEndpoint liquibaseEndpoint(ApplicationContext context) {
		return new LiquibaseEndpoint(context);
	}

	@Bean
	@ConditionalOnBean(SpringLiquibase.class)
	static BeanPostProcessor preventDataSourceCloseBeanPostProcessor() {
		return new BeanPostProcessor() {

			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				if (bean instanceof DataSourceClosingSpringLiquibase dataSource) {
					dataSource.setCloseDataSourceOnceMigrated(false);
				}
				return bean;
			}

			@Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				return bean;
			}

		};
	}

}
