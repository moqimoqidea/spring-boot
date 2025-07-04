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

package org.springframework.boot.loader.jarmode;

import java.util.List;

import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;

/**
 * Delegate class used to launch the uber jar in a specific mode.
 *
 * @author Phillip Webb
 * @since 2.3.0
 */
public final class JarModeLauncher {

	static final String DISABLE_SYSTEM_EXIT = JarModeLauncher.class.getName() + ".DISABLE_SYSTEM_EXIT";

	static final String SUPPRESSED_SYSTEM_EXIT_CODE = JarModeLauncher.class.getName() + ".SUPPRESSED_SYSTEM_EXIT_CODE";

	private JarModeLauncher() {
	}

	public static void main(String[] args) {
		String mode = System.getProperty("jarmode");
		boolean disableSystemExit = Boolean.getBoolean(DISABLE_SYSTEM_EXIT);
		try {
			runJarMode(mode, args);
			if (disableSystemExit) {
				System.setProperty(SUPPRESSED_SYSTEM_EXIT_CODE, "0");
			}
		}
		catch (Throwable ex) {
			printError(ex);
			if (disableSystemExit) {
				System.setProperty(SUPPRESSED_SYSTEM_EXIT_CODE, "1");
				return;
			}
			System.exit(1);
		}
	}

	private static void runJarMode(String mode, String[] args) {
		List<JarMode> candidates = SpringFactoriesLoader.loadFactories(JarMode.class,
				ClassUtils.getDefaultClassLoader());
		for (JarMode candidate : candidates) {
			if (candidate.accepts(mode)) {
				candidate.run(mode, args);
				return;
			}
		}
		throw new JarModeErrorException("Unsupported jarmode '" + mode + "'");
	}

	private static void printError(Throwable ex) {
		if (ex instanceof JarModeErrorException) {
			String message = ex.getMessage();
			System.err.println("Error: " + message);
			System.err.println();
			return;
		}
		ex.printStackTrace();
	}

}
