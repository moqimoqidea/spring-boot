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

package org.springframework.boot.webflux.observation.autoconfigure;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.metrics.autoconfigure.MetricsAutoConfiguration;
import org.springframework.boot.observation.autoconfigure.ObservationAutoConfiguration;
import org.springframework.boot.test.context.assertj.AssertableReactiveWebApplicationContext;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webflux.autoconfigure.WebFluxAutoConfiguration;
import org.springframework.boot.webflux.autoconfigure.WebFluxObservationAutoConfiguration;
import org.springframework.http.server.reactive.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.reactive.observation.ServerRequestObservationConvention;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link WebFluxObservationAutoConfiguration}
 *
 * @author Brian Clozel
 * @author Dmytro Nosan
 * @author Madhura Bhave
 * @author Moritz Halbritter
 */
@ExtendWith(OutputCaptureExtension.class)
class WebFluxObservationAutoConfigurationTests {

	private final ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner()
		.withBean(SimpleMeterRegistry.class)
		.withConfiguration(
				AutoConfigurations.of(ObservationAutoConfiguration.class, WebFluxObservationAutoConfiguration.class));

	@Test
	void afterMaxUrisReachedFurtherUrisAreDenied(CapturedOutput output) {
		this.contextRunner.withUserConfiguration(TestController.class)
			.withConfiguration(AutoConfigurations.of(MetricsAutoConfiguration.class, ObservationAutoConfiguration.class,
					WebFluxAutoConfiguration.class))
			.withPropertyValues("management.metrics.web.server.max-uri-tags=2")
			.run((context) -> {
				MeterRegistry registry = getInitializedMeterRegistry(context);
				assertThat(registry.get("http.server.requests").meters()).hasSizeLessThanOrEqualTo(2);
				assertThat(output).contains("Reached the maximum number of URI tags for 'http.server.requests'");
			});
	}

	@Test
	void afterMaxUrisReachedFurtherUrisAreDeniedWhenUsingCustomObservationName(CapturedOutput output) {
		this.contextRunner.withUserConfiguration(TestController.class)
			.withConfiguration(AutoConfigurations.of(MetricsAutoConfiguration.class, ObservationAutoConfiguration.class,
					WebFluxAutoConfiguration.class))
			.withPropertyValues("management.metrics.web.server.max-uri-tags=2",
					"management.observations.http.server.requests.name=my.http.server.requests")
			.run((context) -> {
				MeterRegistry registry = getInitializedMeterRegistry(context, "my.http.server.requests");
				assertThat(registry.get("my.http.server.requests").meters()).hasSizeLessThanOrEqualTo(2);
				assertThat(output).contains("Reached the maximum number of URI tags for 'my.http.server.requests'");
			});
	}

	@Test
	void shouldNotDenyNorLogIfMaxUrisIsNotReached(CapturedOutput output) {
		this.contextRunner.withUserConfiguration(TestController.class)
			.withConfiguration(AutoConfigurations.of(MetricsAutoConfiguration.class, ObservationAutoConfiguration.class,
					WebFluxAutoConfiguration.class))
			.withPropertyValues("management.metrics.web.server.max-uri-tags=5")
			.run((context) -> {
				MeterRegistry registry = getInitializedMeterRegistry(context);
				assertThat(registry.get("http.server.requests").meters()).hasSize(3);
				assertThat(output).doesNotContain("Reached the maximum number of URI tags for 'http.server.requests'");
			});
	}

	@Test
	void shouldSupplyDefaultServerRequestObservationConvention() {
		this.contextRunner.withPropertyValues("management.observations.http.server.requests.name=some-other-name")
			.run((context) -> {
				assertThat(context).hasSingleBean(DefaultServerRequestObservationConvention.class);
				DefaultServerRequestObservationConvention bean = context
					.getBean(DefaultServerRequestObservationConvention.class);
				assertThat(bean.getName()).isEqualTo("some-other-name");
			});
	}

	@Test
	void shouldBackOffOnCustomServerRequestObservationConvention() {
		this.contextRunner
			.withBean("customServerRequestObservationConvention", ServerRequestObservationConvention.class,
					() -> mock(ServerRequestObservationConvention.class))
			.run((context) -> {
				assertThat(context).hasBean("customServerRequestObservationConvention");
				assertThat(context).hasSingleBean(ServerRequestObservationConvention.class);
			});
	}

	private MeterRegistry getInitializedMeterRegistry(AssertableReactiveWebApplicationContext context) {
		return getInitializedMeterRegistry(context, "http.server.requests");
	}

	private MeterRegistry getInitializedMeterRegistry(AssertableReactiveWebApplicationContext context,
			String metricName) {
		MeterRegistry meterRegistry = context.getBean(MeterRegistry.class);
		meterRegistry.timer(metricName, "uri", "/test0").record(Duration.of(500, ChronoUnit.SECONDS));
		meterRegistry.timer(metricName, "uri", "/test1").record(Duration.of(500, ChronoUnit.SECONDS));
		meterRegistry.timer(metricName, "uri", "/test2").record(Duration.of(500, ChronoUnit.SECONDS));
		return meterRegistry;
	}

	@RestController
	static class TestController {

		@GetMapping("test0")
		String test0() {
			return "test0";
		}

		@GetMapping("test1")
		String test1() {
			return "test1";
		}

		@GetMapping("test2")
		String test2() {
			return "test2";
		}

	}

}
