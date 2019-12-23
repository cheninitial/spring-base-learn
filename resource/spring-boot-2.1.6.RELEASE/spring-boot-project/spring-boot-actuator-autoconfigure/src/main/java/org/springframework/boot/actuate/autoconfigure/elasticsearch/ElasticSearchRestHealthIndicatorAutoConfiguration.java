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

package org.springframework.boot.actuate.autoconfigure.elasticsearch;

import java.util.Map;

import org.elasticsearch.client.RestClient;

import org.springframework.boot.actuate.autoconfigure.health.CompositeHealthIndicatorConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.elasticsearch.ElasticsearchRestHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for
 * {@link ElasticsearchRestHealthIndicator} using the {@link RestClient}.
 *
 * @author Artsiom Yudovin
 * @since 2.1.1
 */

@Configuration
@ConditionalOnClass(RestClient.class)
@ConditionalOnBean(RestClient.class)
@ConditionalOnEnabledHealthIndicator("elasticsearch")
@AutoConfigureBefore(HealthIndicatorAutoConfiguration.class)
@AutoConfigureAfter({ RestClientAutoConfiguration.class, ElasticSearchClientHealthIndicatorAutoConfiguration.class })
public class ElasticSearchRestHealthIndicatorAutoConfiguration
		extends CompositeHealthIndicatorConfiguration<ElasticsearchRestHealthIndicator, RestClient> {

	private final Map<String, RestClient> clients;

	public ElasticSearchRestHealthIndicatorAutoConfiguration(Map<String, RestClient> clients) {
		this.clients = clients;
	}

	@Bean
	@ConditionalOnMissingBean(name = "elasticsearchRestHealthIndicator")
	public HealthIndicator elasticsearchRestHealthIndicator() {
		return createHealthIndicator(this.clients);
	}

	@Override
	protected ElasticsearchRestHealthIndicator createHealthIndicator(RestClient client) {
		return new ElasticsearchRestHealthIndicator(client);
	}

}
