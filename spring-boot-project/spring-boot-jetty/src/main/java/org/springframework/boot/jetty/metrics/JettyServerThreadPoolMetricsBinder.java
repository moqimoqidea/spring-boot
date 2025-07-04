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
import io.micrometer.core.instrument.binder.jetty.JettyServerThreadPoolMetrics;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;

/**
 * {@link AbstractJettyMetricsBinder} for {@link JettyServerThreadPoolMetrics}.
 *
 * @author Andy Wilkinson
 * @since 4.0.0
 */
public class JettyServerThreadPoolMetricsBinder extends AbstractJettyMetricsBinder {

	private final MeterRegistry meterRegistry;

	private final Iterable<Tag> tags;

	public JettyServerThreadPoolMetricsBinder(MeterRegistry meterRegistry) {
		this(meterRegistry, Collections.emptyList());
	}

	public JettyServerThreadPoolMetricsBinder(MeterRegistry meterRegistry, Iterable<Tag> tags) {
		this.meterRegistry = meterRegistry;
		this.tags = tags;
	}

	@Override
	@SuppressWarnings("resource")
	protected void bindMetrics(Server server) {
		ThreadPool threadPool = server.getThreadPool();
		if (threadPool != null) {
			new JettyServerThreadPoolMetrics(threadPool, this.tags).bindTo(this.meterRegistry);
		}
	}

}
