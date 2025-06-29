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

package org.springframework.boot.web.client;

import java.net.URI;
import java.time.Duration;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link ClientHttpRequestFactories}.
 *
 * @author Andy Wilkinson
 * @deprecated since 3.4.0 for removal in 4.0.0
 */
@SuppressWarnings("removal")
@Deprecated(since = "3.4.0", forRemoval = true)
class ClientHttpRequestFactoriesTests {

	@Test
	void getReturnsRequestFactoryOfExpectedType() {
		ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories
			.get(ClientHttpRequestFactorySettings.DEFAULTS);
		assertThat(requestFactory).isInstanceOf(HttpComponentsClientHttpRequestFactory.class);
	}

	@Test
	void getOfGeneralTypeReturnsRequestFactoryOfExpectedType() {
		ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(ClientHttpRequestFactory.class,
				ClientHttpRequestFactorySettings.DEFAULTS);
		assertThat(requestFactory).isInstanceOf(HttpComponentsClientHttpRequestFactory.class);
	}

	@Test
	void getOfSimpleFactoryReturnsSimpleFactory() {
		ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(SimpleClientHttpRequestFactory.class,
				ClientHttpRequestFactorySettings.DEFAULTS);
		assertThat(requestFactory).isInstanceOf(SimpleClientHttpRequestFactory.class);
	}

	@Test
	void getOfHttpComponentsFactoryReturnsHttpComponentsFactory() {
		ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories
			.get(HttpComponentsClientHttpRequestFactory.class, ClientHttpRequestFactorySettings.DEFAULTS);
		assertThat(requestFactory).isInstanceOf(HttpComponentsClientHttpRequestFactory.class);
	}

	@Test
	void getOfReactorFactoryReturnsReactorFactory() {
		ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(ReactorClientHttpRequestFactory.class,
				ClientHttpRequestFactorySettings.DEFAULTS);
		assertThat(requestFactory).isInstanceOf(ReactorClientHttpRequestFactory.class);
	}

	@Test
	void getOfJdkFactoryReturnsJdkFactory() {
		ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class,
				ClientHttpRequestFactorySettings.DEFAULTS);
		assertThat(requestFactory).isInstanceOf(JdkClientHttpRequestFactory.class);
	}

	@Test
	void getOfUnknownTypeCreatesFactory() {
		ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(TestClientHttpRequestFactory.class,
				ClientHttpRequestFactorySettings.DEFAULTS);
		assertThat(requestFactory).isInstanceOf(TestClientHttpRequestFactory.class);
	}

	@Test
	void getOfUnknownTypeWithConnectTimeoutCreatesFactoryAndConfiguresConnectTimeout() {
		ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(TestClientHttpRequestFactory.class,
				ClientHttpRequestFactorySettings.DEFAULTS.withConnectTimeout(Duration.ofSeconds(60)));
		assertThat(requestFactory).isInstanceOf(TestClientHttpRequestFactory.class);
		assertThat(((TestClientHttpRequestFactory) requestFactory).connectTimeout)
			.isEqualTo(Duration.ofSeconds(60).toMillis());
	}

	@Test
	void getOfUnknownTypeWithReadTimeoutCreatesFactoryAndConfiguresReadTimeout() {
		ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(TestClientHttpRequestFactory.class,
				ClientHttpRequestFactorySettings.DEFAULTS.withReadTimeout(Duration.ofSeconds(90)));
		assertThat(requestFactory).isInstanceOf(TestClientHttpRequestFactory.class);
		assertThat(((TestClientHttpRequestFactory) requestFactory).readTimeout)
			.isEqualTo(Duration.ofSeconds(90).toMillis());
	}

	@Test
	void getOfUnconfigurableTypeWithConnectTimeoutThrows() {
		assertThatIllegalStateException()
			.isThrownBy(() -> ClientHttpRequestFactories.get(UnconfigurableClientHttpRequestFactory.class,
					ClientHttpRequestFactorySettings.DEFAULTS.withConnectTimeout(Duration.ofSeconds(60))))
			.withMessageContaining("suitable setConnectTimeout method");
	}

	@Test
	void getOfUnconfigurableTypeWithReadTimeoutThrows() {
		assertThatIllegalStateException()
			.isThrownBy(() -> ClientHttpRequestFactories.get(UnconfigurableClientHttpRequestFactory.class,
					ClientHttpRequestFactorySettings.DEFAULTS.withReadTimeout(Duration.ofSeconds(60))))
			.withMessageContaining("suitable setReadTimeout method");
	}

	@Test
	void getOfTypeWithDeprecatedConnectTimeoutThrowsWithConnectTimeout() {
		assertThatIllegalStateException()
			.isThrownBy(() -> ClientHttpRequestFactories.get(DeprecatedMethodsClientHttpRequestFactory.class,
					ClientHttpRequestFactorySettings.DEFAULTS.withConnectTimeout(Duration.ofSeconds(60))))
			.withMessageContaining("setConnectTimeout method marked as deprecated");
	}

	@Test
	void getOfTypeWithDeprecatedReadTimeoutThrowsWithReadTimeout() {
		assertThatIllegalStateException()
			.isThrownBy(() -> ClientHttpRequestFactories.get(DeprecatedMethodsClientHttpRequestFactory.class,
					ClientHttpRequestFactorySettings.DEFAULTS.withReadTimeout(Duration.ofSeconds(60))))
			.withMessageContaining("setReadTimeout method marked as deprecated");
	}

	@Test
	void connectTimeoutCanBeConfiguredOnAWrappedRequestFactory() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		BufferingClientHttpRequestFactory result = ClientHttpRequestFactories.get(
				() -> new BufferingClientHttpRequestFactory(requestFactory),
				ClientHttpRequestFactorySettings.DEFAULTS.withConnectTimeout(Duration.ofMillis(1234)));
		assertThat(result).extracting("requestFactory").isSameAs(requestFactory);
		assertThat(requestFactory).hasFieldOrPropertyWithValue("connectTimeout", 1234);
	}

	@Test
	void readTimeoutCanBeConfiguredOnAWrappedRequestFactory() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		BufferingClientHttpRequestFactory result = ClientHttpRequestFactories.get(
				() -> new BufferingClientHttpRequestFactory(requestFactory),
				ClientHttpRequestFactorySettings.DEFAULTS.withReadTimeout(Duration.ofMillis(1234)));
		assertThat(result).extracting("requestFactory").isSameAs(requestFactory);
		assertThat(requestFactory).hasFieldOrPropertyWithValue("readTimeout", 1234);
	}

	@Test
	void reflectiveShouldFavorDurationTimeoutMethods() {
		IntAndDurationTimeoutsClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(
				IntAndDurationTimeoutsClientHttpRequestFactory.class,
				ClientHttpRequestFactorySettings.DEFAULTS.withConnectTimeout(Duration.ofSeconds(1))
					.withReadTimeout(Duration.ofSeconds(2)));
		assertThat((requestFactory).connectTimeout).isZero();
		assertThat((requestFactory).readTimeout).isZero();
		assertThat((requestFactory).connectTimeoutDuration).isEqualTo(Duration.ofSeconds(1));
		assertThat((requestFactory).readTimeoutDuration).isEqualTo(Duration.ofSeconds(2));
	}

	public static class TestClientHttpRequestFactory implements ClientHttpRequestFactory {

		private int connectTimeout;

		private int readTimeout;

		@Override
		public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) {
			throw new UnsupportedOperationException();
		}

		public void setConnectTimeout(int timeout) {
			this.connectTimeout = timeout;
		}

		public void setReadTimeout(int timeout) {
			this.readTimeout = timeout;
		}

	}

	public static class UnconfigurableClientHttpRequestFactory implements ClientHttpRequestFactory {

		@Override
		public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) {
			throw new UnsupportedOperationException();
		}

	}

	public static class DeprecatedMethodsClientHttpRequestFactory implements ClientHttpRequestFactory {

		@Override
		public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) {
			throw new UnsupportedOperationException();
		}

		@Deprecated(since = "3.0.0", forRemoval = false)
		public void setConnectTimeout(int timeout) {
		}

		@Deprecated(since = "3.0.0", forRemoval = false)
		public void setReadTimeout(int timeout) {
		}

		@Deprecated(since = "3.0.0", forRemoval = false)
		public void setBufferRequestBody(boolean bufferRequestBody) {
		}

	}

	public static class IntAndDurationTimeoutsClientHttpRequestFactory implements ClientHttpRequestFactory {

		private int readTimeout;

		private int connectTimeout;

		private Duration readTimeoutDuration;

		private Duration connectTimeoutDuration;

		@Override
		public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) {
			throw new UnsupportedOperationException();
		}

		public void setConnectTimeout(int timeout) {
			this.connectTimeout = timeout;
		}

		public void setReadTimeout(int timeout) {
			this.readTimeout = timeout;
		}

		public void setConnectTimeout(Duration timeout) {
			this.connectTimeoutDuration = timeout;
		}

		public void setReadTimeout(Duration timeout) {
			this.readTimeoutDuration = timeout;
		}

	}

}
