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

package org.springframework.boot.configurationsample.immutable;

import org.springframework.boot.configurationsample.ConfigurationProperties;
import org.springframework.boot.configurationsample.Name;

/**
 * Immutable class properties making use of {@code @Name}.
 *
 * @author Phillip Webb
 */
@ConfigurationProperties("named")
public class ConstructorParameterNameAnnotationProperties {

	private final String imports;

	public ConstructorParameterNameAnnotationProperties(@Name("import") String imports) {
		this.imports = imports;
	}

	public String getImports() {
		return this.imports;
	}

}
