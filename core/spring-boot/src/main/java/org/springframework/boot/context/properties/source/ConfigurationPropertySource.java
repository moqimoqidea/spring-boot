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

package org.springframework.boot.context.properties.source;

import java.util.function.Predicate;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

/**
 * A source of {@link ConfigurationProperty ConfigurationProperties}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 * @since 2.0.0
 * @see ConfigurationPropertyName
 * @see OriginTrackedValue
 * @see #getConfigurationProperty(ConfigurationPropertyName)
 */
@FunctionalInterface
public interface ConfigurationPropertySource {

	/**
	 * Return a single {@link ConfigurationProperty} from the source or {@code null} if no
	 * property can be found.
	 * @param name the name of the property
	 * @return the associated object or {@code null}.
	 */
	@Nullable ConfigurationProperty getConfigurationProperty(ConfigurationPropertyName name);

	/**
	 * Returns if the source contains any descendants of the specified name. May return
	 * {@link ConfigurationPropertyState#PRESENT} or
	 * {@link ConfigurationPropertyState#ABSENT} if an answer can be determined or
	 * {@link ConfigurationPropertyState#UNKNOWN} if it's not possible to determine a
	 * definitive answer.
	 * @param name the name to check
	 * @return if the source contains any descendants
	 */
	default ConfigurationPropertyState containsDescendantOf(ConfigurationPropertyName name) {
		return ConfigurationPropertyState.UNKNOWN;
	}

	/**
	 * Return a filtered variant of this source, containing only names that match the
	 * given {@link Predicate}.
	 * @param filter the filter to match
	 * @return a filtered {@link ConfigurationPropertySource} instance
	 */
	default ConfigurationPropertySource filter(Predicate<ConfigurationPropertyName> filter) {
		return new FilteredConfigurationPropertiesSource(this, filter);
	}

	/**
	 * Return a variant of this source that supports name aliases.
	 * @param aliases a function that returns a stream of aliases for any given name
	 * @return a {@link ConfigurationPropertySource} instance supporting name aliases
	 */
	default ConfigurationPropertySource withAliases(ConfigurationPropertyNameAliases aliases) {
		return new AliasedConfigurationPropertySource(this, aliases);
	}

	/**
	 * Return a variant of this source that supports a prefix.
	 * @param prefix the prefix for properties in the source
	 * @return a {@link ConfigurationPropertySource} instance supporting a prefix
	 * @since 2.5.0
	 */
	default ConfigurationPropertySource withPrefix(@Nullable String prefix) {
		return (StringUtils.hasText(prefix)) ? new PrefixedConfigurationPropertySource(this, prefix) : this;
	}

	/**
	 * Return the underlying source that is actually providing the properties.
	 * @return the underlying property source or {@code null}.
	 */
	default @Nullable Object getUnderlyingSource() {
		return null;
	}

	/**
	 * Return a single new {@link ConfigurationPropertySource} adapted from the given
	 * Spring {@link PropertySource} or {@code null} if the source cannot be adapted.
	 * @param source the Spring property source to adapt
	 * @return an adapted source or {@code null} {@link SpringConfigurationPropertySource}
	 * @since 2.4.0
	 */
	static @Nullable ConfigurationPropertySource from(PropertySource<?> source) {
		if (source instanceof ConfigurationPropertySourcesPropertySource) {
			return null;
		}
		return SpringConfigurationPropertySource.from(source);
	}

}
