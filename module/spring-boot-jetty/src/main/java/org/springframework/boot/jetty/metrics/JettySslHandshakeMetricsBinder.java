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

package org.springframework.boot.jetty.metrics;

import java.util.Collections;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jetty.JettySslHandshakeMetrics;
import org.eclipse.jetty.server.Server;

/**
 * {@link AbstractJettyMetricsBinder} for {@link JettySslHandshakeMetrics}.
 *
 * @author Chris Bono
 * @since 4.0.0
 */
public class JettySslHandshakeMetricsBinder extends AbstractJettyMetricsBinder {

	private final MeterRegistry meterRegistry;

	private final Iterable<Tag> tags;

	public JettySslHandshakeMetricsBinder(MeterRegistry meterRegistry) {
		this(meterRegistry, Collections.emptyList());
	}

	public JettySslHandshakeMetricsBinder(MeterRegistry meterRegistry, Iterable<Tag> tags) {
		this.meterRegistry = meterRegistry;
		this.tags = tags;
	}

	@Override
	protected void bindMetrics(Server server) {
		JettySslHandshakeMetrics.addToAllConnectors(server, this.meterRegistry, this.tags);
	}

}
