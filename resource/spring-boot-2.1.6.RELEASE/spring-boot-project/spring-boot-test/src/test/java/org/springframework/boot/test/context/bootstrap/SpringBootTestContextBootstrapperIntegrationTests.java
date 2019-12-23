/*
 * Copyright 2012-2018 the original author or authors.
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

package org.springframework.boot.test.context.bootstrap;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SpringBootTestContextBootstrapper} (in its own package so
 * we can test detection).
 *
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
public class SpringBootTestContextBootstrapperIntegrationTests {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private SpringBootTestContextBootstrapperExampleConfig config;

	boolean defaultTestExecutionListenersPostProcessorCalled = false;

	@Test
	public void findConfigAutomatically() {
		assertThat(this.config).isNotNull();
	}

	@Test
	public void contextWasCreatedViaSpringApplication() {
		assertThat(this.context.getId()).startsWith("application");
	}

	@Test
	public void testConfigurationWasApplied() {
		assertThat(this.context.getBean(ExampleBean.class)).isNotNull();
	}

	@Test
	public void defaultTestExecutionListenersPostProcessorShouldBeCalled() {
		assertThat(this.defaultTestExecutionListenersPostProcessorCalled).isTrue();
	}

	@TestConfiguration
	static class TestConfig {

		@Bean
		public ExampleBean exampleBean() {
			return new ExampleBean();
		}

	}

	static class ExampleBean {

	}

}
