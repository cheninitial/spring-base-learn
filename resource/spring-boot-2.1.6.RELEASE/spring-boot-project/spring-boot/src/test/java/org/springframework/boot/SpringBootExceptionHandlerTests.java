/*
 * Copyright 2012-2019 the original author or authors.
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

package org.springframework.boot;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Tests for {@link SpringBootExceptionHandler}.
 *
 * @author Henri Tremblay
 * @author Andy Wilkinson
 */
public class SpringBootExceptionHandlerTests {

	private final UncaughtExceptionHandler parent = mock(UncaughtExceptionHandler.class);

	private final SpringBootExceptionHandler handler = new SpringBootExceptionHandler(this.parent);

	@Test
	public void uncaughtExceptionDoesNotForwardLoggedErrorToParent() {
		Thread thread = Thread.currentThread();
		Exception ex = new Exception();
		this.handler.registerLoggedException(ex);
		this.handler.uncaughtException(thread, ex);
		verifyZeroInteractions(this.parent);
	}

	@Test
	public void uncaughtExceptionForwardsLogConfigurationErrorToParent() {
		Thread thread = Thread.currentThread();
		Exception ex = new Exception("[stuff] Logback configuration error detected [stuff]");
		this.handler.registerLoggedException(ex);
		this.handler.uncaughtException(thread, ex);
		verify(this.parent).uncaughtException(thread, ex);
	}

	@Test
	public void uncaughtExceptionForwardsWrappedLogConfigurationErrorToParent() {
		Thread thread = Thread.currentThread();
		Exception ex = new InvocationTargetException(
				new Exception("[stuff] Logback configuration error detected [stuff]", new Exception()));
		this.handler.registerLoggedException(ex);
		this.handler.uncaughtException(thread, ex);
		verify(this.parent).uncaughtException(thread, ex);
	}

}
