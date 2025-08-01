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

package org.springframework.boot.actuate.endpoint;

import org.jspecify.annotations.Nullable;

/**
 * Indicate that an endpoint request is invalid.
 *
 * @author Stephane Nicoll
 * @since 2.0.0
 */
public class InvalidEndpointRequestException extends RuntimeException {

	private final @Nullable String reason;

	public InvalidEndpointRequestException(String message, @Nullable String reason) {
		super(message);
		this.reason = reason;
	}

	public InvalidEndpointRequestException(String message, @Nullable String reason, @Nullable Throwable cause) {
		super(message, cause);
		this.reason = reason;
	}

	/**
	 * Return the reason explaining why the request is invalid, potentially {@code null}.
	 * @return the reason for the failure
	 */
	public @Nullable String getReason() {
		return this.reason;
	}

}
