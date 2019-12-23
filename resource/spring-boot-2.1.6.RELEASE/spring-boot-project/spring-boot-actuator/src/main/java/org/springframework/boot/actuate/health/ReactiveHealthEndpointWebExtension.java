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

package org.springframework.boot.actuate.health;

import reactor.core.publisher.Mono;

import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;

/**
 * Reactive {@link EndpointWebExtension} for the {@link HealthEndpoint}.
 *
 * @author Stephane Nicoll
 * @since 2.0.0
 */
@EndpointWebExtension(endpoint = HealthEndpoint.class)
public class ReactiveHealthEndpointWebExtension {

	private final ReactiveHealthIndicator delegate;

	private final HealthWebEndpointResponseMapper responseMapper;

	public ReactiveHealthEndpointWebExtension(ReactiveHealthIndicator delegate,
			HealthWebEndpointResponseMapper responseMapper) {
		this.delegate = delegate;
		this.responseMapper = responseMapper;
	}

	@ReadOperation
	public Mono<WebEndpointResponse<Health>> health(SecurityContext securityContext) {
		return this.delegate.health().map((health) -> this.responseMapper.map(health, securityContext));
	}

	@ReadOperation
	public Mono<WebEndpointResponse<Health>> healthForComponent(SecurityContext securityContext,
			@Selector String component) {
		return responseFromIndicator(getNestedHealthIndicator(this.delegate, component), securityContext);
	}

	@ReadOperation
	public Mono<WebEndpointResponse<Health>> healthForComponentInstance(SecurityContext securityContext,
			@Selector String component, @Selector String instance) {
		ReactiveHealthIndicator indicator = getNestedHealthIndicator(this.delegate, component);
		if (indicator != null) {
			indicator = getNestedHealthIndicator(indicator, instance);
		}
		return responseFromIndicator(indicator, securityContext);
	}

	public Mono<WebEndpointResponse<Health>> health(SecurityContext securityContext, ShowDetails showDetails) {
		return this.delegate.health().map((health) -> this.responseMapper.map(health, securityContext, showDetails));
	}

	private Mono<WebEndpointResponse<Health>> responseFromIndicator(ReactiveHealthIndicator indicator,
			SecurityContext securityContext) {
		return (indicator != null)
				? indicator.health().map((health) -> this.responseMapper.map(health, securityContext)) : Mono.empty();
	}

	private ReactiveHealthIndicator getNestedHealthIndicator(ReactiveHealthIndicator healthIndicator, String name) {
		if (healthIndicator instanceof CompositeReactiveHealthIndicator) {
			return ((CompositeReactiveHealthIndicator) healthIndicator).getRegistry().get(name);
		}
		return null;
	}

}
