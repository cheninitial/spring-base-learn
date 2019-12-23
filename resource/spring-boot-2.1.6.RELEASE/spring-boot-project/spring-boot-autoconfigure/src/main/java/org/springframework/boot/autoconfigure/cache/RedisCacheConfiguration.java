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

package org.springframework.boot.autoconfigure.cache;

import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

/**
 * Redis cache configuration.
 *
 * @author Stephane Nicoll
 * @author Mark Paluch
 * @author Ryon Day
 * @since 1.3.0
 */
@Configuration
@ConditionalOnClass(RedisConnectionFactory.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnBean(RedisConnectionFactory.class)
@ConditionalOnMissingBean(CacheManager.class)
@Conditional(CacheCondition.class)
class RedisCacheConfiguration {

	private final CacheProperties cacheProperties;

	private final CacheManagerCustomizers customizerInvoker;

	private final org.springframework.data.redis.cache.RedisCacheConfiguration redisCacheConfiguration;

	RedisCacheConfiguration(CacheProperties cacheProperties, CacheManagerCustomizers customizerInvoker,
			ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration) {
		this.cacheProperties = cacheProperties;
		this.customizerInvoker = customizerInvoker;
		this.redisCacheConfiguration = redisCacheConfiguration.getIfAvailable();
	}

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
			ResourceLoader resourceLoader) {
		RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory)
				.cacheDefaults(determineConfiguration(resourceLoader.getClassLoader()));
		List<String> cacheNames = this.cacheProperties.getCacheNames();
		if (!cacheNames.isEmpty()) {
			builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
		}
		return this.customizerInvoker.customize(builder.build());
	}

	private org.springframework.data.redis.cache.RedisCacheConfiguration determineConfiguration(
			ClassLoader classLoader) {
		if (this.redisCacheConfiguration != null) {
			return this.redisCacheConfiguration;
		}
		Redis redisProperties = this.cacheProperties.getRedis();
		org.springframework.data.redis.cache.RedisCacheConfiguration config = org.springframework.data.redis.cache.RedisCacheConfiguration
				.defaultCacheConfig();
		config = config.serializeValuesWith(
				SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(classLoader)));
		if (redisProperties.getTimeToLive() != null) {
			config = config.entryTtl(redisProperties.getTimeToLive());
		}
		if (redisProperties.getKeyPrefix() != null) {
			config = config.prefixKeysWith(redisProperties.getKeyPrefix());
		}
		if (!redisProperties.isCacheNullValues()) {
			config = config.disableCachingNullValues();
		}
		if (!redisProperties.isUseKeyPrefix()) {
			config = config.disableKeyPrefix();
		}
		return config;
	}

}
