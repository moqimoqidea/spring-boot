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

package org.springframework.boot.context.properties;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.BindMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.PropertySources;
import org.springframework.util.Assert;

/**
 * {@link BeanPostProcessor} to bind {@link PropertySources} to beans annotated with
 * {@link ConfigurationProperties @ConfigurationProperties}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Christian Dupuis
 * @author Stephane Nicoll
 * @author Madhura Bhave
 * @since 1.0.0
 */
public class ConfigurationPropertiesBindingPostProcessor
		implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware, InitializingBean {

	/**
	 * The bean name that this post-processor is registered with.
	 */
	public static final String BEAN_NAME = ConfigurationPropertiesBindingPostProcessor.class.getName();

	@SuppressWarnings("NullAway.Init")
	private ApplicationContext applicationContext;

	@SuppressWarnings("NullAway.Init")
	private BeanDefinitionRegistry registry;

	@SuppressWarnings("NullAway.Init")
	private ConfigurationPropertiesBinder binder;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// We can't use constructor injection of the application context because
		// it causes eager factory bean initialization
		this.registry = (BeanDefinitionRegistry) this.applicationContext.getAutowireCapableBeanFactory();
		this.binder = ConfigurationPropertiesBinder.get(this.applicationContext);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 1;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (!hasBoundValueObject(beanName)) {
			bind(ConfigurationPropertiesBean.get(this.applicationContext, bean, beanName));
		}
		return bean;
	}

	private boolean hasBoundValueObject(String beanName) {
		return BindMethod.VALUE_OBJECT.equals(BindMethodAttribute.get(this.registry, beanName));
	}

	private void bind(@Nullable ConfigurationPropertiesBean bean) {
		if (bean == null) {
			return;
		}
		Assert.state(bean.asBindTarget().getBindMethod() != BindMethod.VALUE_OBJECT,
				"Cannot bind @ConfigurationProperties for bean '" + bean.getName()
						+ "'. Ensure that @ConstructorBinding has not been applied to regular bean");
		try {
			this.binder.bind(bean);
		}
		catch (Exception ex) {
			throw new ConfigurationPropertiesBindException(bean, ex);
		}
	}

	/**
	 * Register a {@link ConfigurationPropertiesBindingPostProcessor} bean if one is not
	 * already registered.
	 * @param registry the bean definition registry
	 * @since 2.2.0
	 */
	public static void register(BeanDefinitionRegistry registry) {
		Assert.notNull(registry, "'registry' must not be null");
		if (!registry.containsBeanDefinition(BEAN_NAME)) {
			BeanDefinition definition = BeanDefinitionBuilder
				.rootBeanDefinition(ConfigurationPropertiesBindingPostProcessor.class)
				.getBeanDefinition();
			definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			registry.registerBeanDefinition(BEAN_NAME, definition);
		}
		ConfigurationPropertiesBinder.register(registry);
	}

}
