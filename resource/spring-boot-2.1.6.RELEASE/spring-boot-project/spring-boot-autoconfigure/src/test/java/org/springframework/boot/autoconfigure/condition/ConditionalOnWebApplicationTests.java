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

package org.springframework.boot.autoconfigure.condition;

import org.junit.After;
import org.junit.Test;
import reactor.core.publisher.Mono;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.web.reactive.MockReactiveWebServerFactory;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link ConditionalOnWebApplication}.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class ConditionalOnWebApplicationTests {

	private ConfigurableApplicationContext context;

	@After
	public void closeContext() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void testWebApplicationWithServletContext() {
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(AnyWebApplicationConfiguration.class, ServletWebApplicationConfiguration.class,
				ReactiveWebApplicationConfiguration.class);
		ctx.setServletContext(new MockServletContext());
		ctx.refresh();
		this.context = ctx;
		assertThat(this.context.getBeansOfType(String.class)).containsExactly(entry("any", "any"),
				entry("servlet", "servlet"));
	}

	@Test
	public void testWebApplicationWithReactiveContext() {
		AnnotationConfigReactiveWebApplicationContext context = new AnnotationConfigReactiveWebApplicationContext();
		context.register(AnyWebApplicationConfiguration.class, ServletWebApplicationConfiguration.class,
				ReactiveWebApplicationConfiguration.class);
		context.refresh();
		this.context = context;
		assertThat(this.context.getBeansOfType(String.class)).containsExactly(entry("any", "any"),
				entry("reactive", "reactive"));
	}

	@Test
	public void testNonWebApplication() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(AnyWebApplicationConfiguration.class, ServletWebApplicationConfiguration.class,
				ReactiveWebApplicationConfiguration.class);
		ctx.refresh();
		this.context = ctx;
		assertThat(this.context.getBeansOfType(String.class)).isEmpty();
	}

	@Configuration
	@ConditionalOnWebApplication
	protected static class AnyWebApplicationConfiguration {

		@Bean
		public String any() {
			return "any";
		}

	}

	@Configuration
	@ConditionalOnWebApplication(type = Type.SERVLET)
	protected static class ServletWebApplicationConfiguration {

		@Bean
		public String servlet() {
			return "servlet";
		}

	}

	@Configuration
	@ConditionalOnWebApplication(type = Type.REACTIVE)
	protected static class ReactiveWebApplicationConfiguration {

		@Bean
		public String reactive() {
			return "reactive";
		}

		@Bean
		public ReactiveWebServerFactory reactiveWebServerFactory() {
			return new MockReactiveWebServerFactory();
		}

		@Bean
		public HttpHandler httpHandler() {
			return (request, response) -> Mono.empty();
		}

	}

}
