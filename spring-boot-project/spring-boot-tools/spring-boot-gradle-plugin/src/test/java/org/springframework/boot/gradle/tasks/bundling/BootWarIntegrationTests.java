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

package org.springframework.boot.gradle.tasks.bundling;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.assertj.core.api.Assumptions;
import org.gradle.util.GradleVersion;

import org.springframework.boot.gradle.junit.GradleCompatibility;

/**
 * Integration tests for {@link BootWar}.
 *
 * @author Andy Wilkinson
 * @author Scott Frederick
 */
@GradleCompatibility(configurationCache = true)
class BootWarIntegrationTests extends AbstractBootArchiveIntegrationTests {

	BootWarIntegrationTests() {
		super("bootWar", "WEB-INF/lib/", "WEB-INF/classes/", "WEB-INF/");
	}

	@Override
	String[] getExpectedApplicationLayerContents(String... additionalFiles) {
		Set<String> contents = new TreeSet<>(Arrays.asList(additionalFiles));
		contents.addAll(Arrays.asList("WEB-INF/classpath.idx", "WEB-INF/layers.idx", "META-INF/"));
		return contents.toArray(new String[0]);
	}

	@Override
	void multiModuleImplicitLayers() throws IOException {
		whenTestingWithTheConfigurationCacheAssumeThatTheGradleVersionIsLessThan8();
		super.multiModuleImplicitLayers();
	}

	@Override
	void multiModuleCustomLayers() throws IOException {
		whenTestingWithTheConfigurationCacheAssumeThatTheGradleVersionIsLessThan8();
		super.multiModuleCustomLayers();
	}

	private void whenTestingWithTheConfigurationCacheAssumeThatTheGradleVersionIsLessThan8() {
		if (this.gradleBuild.isConfigurationCache()) {
			// With Gradle 8.0, a configuration cache bug prevents ResolvedDependencies
			// from processing dependencies on the runtime classpath
			Assumptions.assumeThat(GradleVersion.version(this.gradleBuild.getGradleVersion()))
				.isLessThan(GradleVersion.version("8.0"));
		}
	}

}
