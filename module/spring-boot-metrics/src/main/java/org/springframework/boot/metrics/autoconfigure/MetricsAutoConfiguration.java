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

package org.springframework.boot.metrics.autoconfigure;

import java.util.List;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.observation.DefaultMeterObservationHandler;
import io.micrometer.core.instrument.observation.DefaultMeterObservationHandler.IgnoredMeters;
import io.micrometer.core.instrument.observation.MeterObservationHandler;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.observation.autoconfigure.ObservationHandlerGroup;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.annotation.Order;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Micrometer-based metrics.
 *
 * @author Jon Schneider
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 * @author Phillip Webb
 * @since 4.0.0
 */
@AutoConfiguration(before = CompositeMeterRegistryAutoConfiguration.class)
@ConditionalOnClass(Timed.class)
@EnableConfigurationProperties(MetricsProperties.class)
public final class MetricsAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	Clock micrometerClock() {
		return Clock.SYSTEM;
	}

	@Bean
	static MeterRegistryPostProcessor meterRegistryPostProcessor(ApplicationContext applicationContext,
			ObjectProvider<MetricsProperties> metricsProperties,
			ObjectProvider<MeterRegistryCustomizer<?>> meterRegistryCustomizers,
			ObjectProvider<MeterFilter> meterFilters, ObjectProvider<MeterBinder> meterBinders) {
		return new MeterRegistryPostProcessor(applicationContext, metricsProperties, meterRegistryCustomizers,
				meterFilters, meterBinders);
	}

	@Bean
	@Order(0)
	PropertiesMeterFilter propertiesMeterFilter(MetricsProperties properties) {
		return new PropertiesMeterFilter(properties);
	}

	@Bean
	MeterRegistryCloser meterRegistryCloser(ObjectProvider<MeterRegistry> meterRegistries) {
		return new MeterRegistryCloser(meterRegistries.orderedStream().toList());
	}

	@Bean
	ObservationHandlerGroup metricsObservationHandlerGroup() {
		return ObservationHandlerGroup.of(MeterObservationHandler.class);
	}

	@Bean
	DefaultMeterObservationHandler defaultMeterObservationHandler(ObjectProvider<MeterRegistry> meterRegistryProvider,
			Clock clock, MetricsProperties properties) {
		MeterRegistry meterRegistry = meterRegistryProvider.getIfAvailable(() -> new CompositeMeterRegistry(clock));
		return new DefaultMeterObservationHandler(meterRegistry,
				properties.getObservations().getIgnoredMeters().toArray(IgnoredMeters[]::new));
	}

	/**
	 * Ensures that {@link MeterRegistry meter registries} are closed early in the
	 * shutdown process.
	 */
	static class MeterRegistryCloser implements ApplicationListener<ContextClosedEvent> {

		private final List<MeterRegistry> meterRegistries;

		MeterRegistryCloser(List<MeterRegistry> meterRegistries) {
			this.meterRegistries = meterRegistries;
		}

		@Override
		public void onApplicationEvent(ContextClosedEvent event) {
			for (MeterRegistry meterRegistry : this.meterRegistries) {
				if (!meterRegistry.isClosed()) {
					meterRegistry.close();
				}
			}
		}

	}

}
