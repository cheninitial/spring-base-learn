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

package org.springframework.boot.autoconfigure.jms.artemis;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.server.config.JMSConfiguration;
import org.apache.activemq.artemis.jms.server.config.JMSQueueConfiguration;
import org.apache.activemq.artemis.jms.server.config.TopicConfiguration;
import org.apache.activemq.artemis.jms.server.config.impl.JMSConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.JMSQueueConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.TopicConfigurationImpl;
import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.SessionCallback;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ArtemisAutoConfiguration}.
 *
 * @author Eddú Meléndez
 * @author Stephane Nicoll
 */
public class ArtemisAutoConfigurationTests {

	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(ArtemisAutoConfiguration.class, JmsAutoConfiguration.class));

	@Test
	public void connectionFactoryIsCachedByDefault() {
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class).run((context) -> {
			assertThat(context).hasSingleBean(ConnectionFactory.class);
			assertThat(context).hasSingleBean(CachingConnectionFactory.class);
			CachingConnectionFactory connectionFactory = context.getBean(CachingConnectionFactory.class);
			assertThat(connectionFactory.getTargetConnectionFactory()).isInstanceOf(ActiveMQConnectionFactory.class);
			assertThat(connectionFactory.isCacheConsumers()).isFalse();
			assertThat(connectionFactory.isCacheProducers()).isTrue();
			assertThat(connectionFactory.getSessionCacheSize()).isEqualTo(1);
		});
	}

	@Test
	public void connectionFactoryCachingCanBeCustomized() {
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.withPropertyValues("spring.jms.cache.consumers=true", "spring.jms.cache.producers=false",
						"spring.jms.cache.session-cache-size=10")
				.run((context) -> {
					assertThat(context).hasSingleBean(ConnectionFactory.class);
					assertThat(context).hasSingleBean(CachingConnectionFactory.class);
					CachingConnectionFactory connectionFactory = context.getBean(CachingConnectionFactory.class);
					assertThat(connectionFactory.isCacheConsumers()).isTrue();
					assertThat(connectionFactory.isCacheProducers()).isFalse();
					assertThat(connectionFactory.getSessionCacheSize()).isEqualTo(10);
				});
	}

	@Test
	public void connectionFactoryCachingCanBeDisabled() {
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.withPropertyValues("spring.jms.cache.enabled=false").run((context) -> {
					assertThat(context).hasSingleBean(ConnectionFactory.class);
					assertThat(context).doesNotHaveBean(CachingConnectionFactory.class);
					assertThat(context.getBean(ConnectionFactory.class)).isInstanceOf(ActiveMQConnectionFactory.class);
				});
	}

	@Test
	public void nativeConnectionFactory() {
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.withPropertyValues("spring.artemis.mode:native").run((context) -> {
					JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
					ConnectionFactory connectionFactory = context.getBean(ConnectionFactory.class);
					assertThat(connectionFactory).isEqualTo(jmsTemplate.getConnectionFactory());
					ActiveMQConnectionFactory activeMQConnectionFactory = getActiveMQConnectionFactory(
							connectionFactory);
					assertNettyConnectionFactory(activeMQConnectionFactory, "localhost", 61616);
					assertThat(activeMQConnectionFactory.getUser()).isNull();
					assertThat(activeMQConnectionFactory.getPassword()).isNull();
				});
	}

	@Test
	public void nativeConnectionFactoryCustomHost() {
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.withPropertyValues("spring.artemis.mode:native", "spring.artemis.host:192.168.1.144",
						"spring.artemis.port:9876")
				.run((context) -> assertNettyConnectionFactory(
						getActiveMQConnectionFactory(context.getBean(ConnectionFactory.class)), "192.168.1.144", 9876));
	}

	@Test
	public void nativeConnectionFactoryCredentials() {
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.withPropertyValues("spring.artemis.mode:native", "spring.artemis.user:user",
						"spring.artemis.password:secret")
				.run((context) -> {
					JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
					ConnectionFactory connectionFactory = context.getBean(ConnectionFactory.class);
					assertThat(connectionFactory).isEqualTo(jmsTemplate.getConnectionFactory());
					ActiveMQConnectionFactory activeMQConnectionFactory = getActiveMQConnectionFactory(
							connectionFactory);
					assertNettyConnectionFactory(activeMQConnectionFactory, "localhost", 61616);
					assertThat(activeMQConnectionFactory.getUser()).isEqualTo("user");
					assertThat(activeMQConnectionFactory.getPassword()).isEqualTo("secret");
				});
	}

	@Test
	public void embeddedConnectionFactory() {
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.withPropertyValues("spring.artemis.mode:embedded").run((context) -> {
					ArtemisProperties properties = context.getBean(ArtemisProperties.class);
					assertThat(properties.getMode()).isEqualTo(ArtemisMode.EMBEDDED);
					assertThat(context).hasSingleBean(EmbeddedJMS.class);
					org.apache.activemq.artemis.core.config.Configuration configuration = context
							.getBean(org.apache.activemq.artemis.core.config.Configuration.class);
					assertThat(configuration.isPersistenceEnabled()).isFalse();
					assertThat(configuration.isSecurityEnabled()).isFalse();
					assertInVmConnectionFactory(getActiveMQConnectionFactory(context.getBean(ConnectionFactory.class)));
				});
	}

	@Test
	public void embeddedConnectionFactoryByDefault() {
		// No mode is specified
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class).run((context) -> {
			assertThat(context).hasSingleBean(EmbeddedJMS.class);
			org.apache.activemq.artemis.core.config.Configuration configuration = context
					.getBean(org.apache.activemq.artemis.core.config.Configuration.class);
			assertThat(configuration.isPersistenceEnabled()).isFalse();
			assertThat(configuration.isSecurityEnabled()).isFalse();
			assertInVmConnectionFactory(getActiveMQConnectionFactory(context.getBean(ConnectionFactory.class)));
		});
	}

	@Test
	public void nativeConnectionFactoryIfEmbeddedServiceDisabledExplicitly() {
		// No mode is specified
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.withPropertyValues("spring.artemis.embedded.enabled:false").run((context) -> {
					assertThat(context).doesNotHaveBean(EmbeddedJMS.class);
					assertNettyConnectionFactory(getActiveMQConnectionFactory(context.getBean(ConnectionFactory.class)),
							"localhost", 61616);
				});
	}

	@Test
	public void embeddedConnectionFactoryEvenIfEmbeddedServiceDisabled() {
		// No mode is specified
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.withPropertyValues("spring.artemis.mode:embedded", "spring.artemis.embedded.enabled:false")
				.run((context) -> {
					assertThat(context.getBeansOfType(EmbeddedJMS.class)).isEmpty();
					assertInVmConnectionFactory(getActiveMQConnectionFactory(context.getBean(ConnectionFactory.class)));
				});
	}

	@Test
	public void embeddedServerWithDestinations() {
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.withPropertyValues("spring.artemis.embedded.queues=Queue1,Queue2",
						"spring.artemis.embedded.topics=Topic1")
				.run((context) -> {
					DestinationChecker checker = new DestinationChecker(context);
					checker.checkQueue("Queue1", true);
					checker.checkQueue("Queue2", true);
					checker.checkQueue("QueueWillNotBeAutoCreated", true);
					checker.checkTopic("Topic1", true);
					checker.checkTopic("TopicWillBeAutoCreated", true);
				});
	}

	@Test
	public void embeddedServerWithDestinationConfig() {
		this.contextRunner.withUserConfiguration(DestinationConfiguration.class).run((context) -> {
			DestinationChecker checker = new DestinationChecker(context);
			checker.checkQueue("sampleQueue", true);
			checker.checkTopic("sampleTopic", true);
		});
	}

	@Test
	public void embeddedServiceWithCustomJmsConfiguration() {
		// Ignored with custom config
		this.contextRunner.withUserConfiguration(CustomJmsConfiguration.class)
				.withPropertyValues("spring.artemis.embedded.queues=Queue1,Queue2").run((context) -> {
					DestinationChecker checker = new DestinationChecker(context);
					checker.checkQueue("custom", true); // See CustomJmsConfiguration
					checker.checkQueue("Queue1", true);
					checker.checkQueue("Queue2", true);
				});
	}

	@Test
	public void embeddedServiceWithCustomArtemisConfiguration() {
		this.contextRunner.withUserConfiguration(CustomArtemisConfiguration.class)
				.run((context) -> assertThat(
						context.getBean(org.apache.activemq.artemis.core.config.Configuration.class).getName())
								.isEqualTo("customFooBar"));
	}

	@Test
	public void embeddedWithPersistentMode() throws IOException {
		File dataFolder = this.folder.newFolder();
		final String messageId = UUID.randomUUID().toString();
		// Start the server and post a message to some queue
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.withPropertyValues("spring.artemis.embedded.queues=TestQueue",
						"spring.artemis.embedded.persistent:true",
						"spring.artemis.embedded.dataDirectory:" + dataFolder.getAbsolutePath())
				.run((context) -> context.getBean(JmsTemplate.class).send("TestQueue",
						(session) -> session.createTextMessage(messageId)))
				.run((context) -> {
					// Start the server again and check if our message is still here
					JmsTemplate jmsTemplate2 = context.getBean(JmsTemplate.class);
					jmsTemplate2.setReceiveTimeout(1000L);
					Message message = jmsTemplate2.receive("TestQueue");
					assertThat(message).isNotNull();
					assertThat(((TextMessage) message).getText()).isEqualTo(messageId);
				});
	}

	@Test
	public void severalEmbeddedBrokers() {
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.withPropertyValues("spring.artemis.embedded.queues=Queue1").run((first) -> {
					this.contextRunner.withPropertyValues("spring.artemis.embedded.queues=Queue2").run((second) -> {
						ArtemisProperties firstProperties = first.getBean(ArtemisProperties.class);
						ArtemisProperties secondProperties = second.getBean(ArtemisProperties.class);
						assertThat(firstProperties.getEmbedded().getServerId())
								.isLessThan(secondProperties.getEmbedded().getServerId());
						DestinationChecker firstChecker = new DestinationChecker(first);
						firstChecker.checkQueue("Queue1", true);
						firstChecker.checkQueue("Queue2", true);
						DestinationChecker secondChecker = new DestinationChecker(second);
						secondChecker.checkQueue("Queue2", true);
						secondChecker.checkQueue("Queue1", true);
					});
				});
	}

	@Test
	public void connectToASpecificEmbeddedBroker() {
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.withPropertyValues("spring.artemis.embedded.serverId=93", "spring.artemis.embedded.queues=Queue1")
				.run((first) -> {
					this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
							.withPropertyValues("spring.artemis.mode=embedded",
									// Connect to the "main" broker
									"spring.artemis.embedded.serverId=93",
									// Do not start a specific one
									"spring.artemis.embedded.enabled=false")
							.run((secondContext) -> {
								DestinationChecker firstChecker = new DestinationChecker(first);
								firstChecker.checkQueue("Queue1", true);
								DestinationChecker secondChecker = new DestinationChecker(secondContext);
								secondChecker.checkQueue("Queue1", true);
							});
				});
	}

	@Test
	public void defaultPoolConnectionFactoryIsApplied() {
		this.contextRunner.withPropertyValues("spring.artemis.pool.enabled=true").run((context) -> {
			assertThat(context.getBeansOfType(JmsPoolConnectionFactory.class)).hasSize(1);
			JmsPoolConnectionFactory connectionFactory = context.getBean(JmsPoolConnectionFactory.class);
			JmsPoolConnectionFactory defaultFactory = new JmsPoolConnectionFactory();
			assertThat(connectionFactory.isBlockIfSessionPoolIsFull())
					.isEqualTo(defaultFactory.isBlockIfSessionPoolIsFull());
			assertThat(connectionFactory.getBlockIfSessionPoolIsFullTimeout())
					.isEqualTo(defaultFactory.getBlockIfSessionPoolIsFullTimeout());
			assertThat(connectionFactory.getConnectionIdleTimeout())
					.isEqualTo(defaultFactory.getConnectionIdleTimeout());
			assertThat(connectionFactory.getMaxConnections()).isEqualTo(defaultFactory.getMaxConnections());
			assertThat(connectionFactory.getMaxSessionsPerConnection())
					.isEqualTo(defaultFactory.getMaxSessionsPerConnection());
			assertThat(connectionFactory.getConnectionCheckInterval())
					.isEqualTo(defaultFactory.getConnectionCheckInterval());
			assertThat(connectionFactory.isUseAnonymousProducers()).isEqualTo(defaultFactory.isUseAnonymousProducers());
		});
	}

	@Test
	public void customPoolConnectionFactoryIsApplied() {
		this.contextRunner
				.withPropertyValues("spring.artemis.pool.enabled=true", "spring.artemis.pool.blockIfFull=false",
						"spring.artemis.pool.blockIfFullTimeout=64", "spring.artemis.pool.idleTimeout=512",
						"spring.artemis.pool.maxConnections=256", "spring.artemis.pool.maxSessionsPerConnection=1024",
						"spring.artemis.pool.timeBetweenExpirationCheck=2048",
						"spring.artemis.pool.useAnonymousProducers=false")
				.run((context) -> {
					assertThat(context.getBeansOfType(JmsPoolConnectionFactory.class)).hasSize(1);
					JmsPoolConnectionFactory connectionFactory = context.getBean(JmsPoolConnectionFactory.class);
					assertThat(connectionFactory.isBlockIfSessionPoolIsFull()).isFalse();
					assertThat(connectionFactory.getBlockIfSessionPoolIsFullTimeout()).isEqualTo(64);
					assertThat(connectionFactory.getConnectionIdleTimeout()).isEqualTo(512);
					assertThat(connectionFactory.getMaxConnections()).isEqualTo(256);
					assertThat(connectionFactory.getMaxSessionsPerConnection()).isEqualTo(1024);
					assertThat(connectionFactory.getConnectionCheckInterval()).isEqualTo(2048);
					assertThat(connectionFactory.isUseAnonymousProducers()).isFalse();
				});
	}

	@Test
	public void customPoolConnectionFactoryIsAppliedWithDeprecatedSettings() {
		this.contextRunner.withPropertyValues("spring.artemis.pool.enabled=true",
				"spring.artemis.pool.maximumActiveSessionPerConnection=1024").run((context) -> {
					assertThat(context.getBeansOfType(JmsPoolConnectionFactory.class)).hasSize(1);
					JmsPoolConnectionFactory connectionFactory = context.getBean(JmsPoolConnectionFactory.class);
					assertThat(connectionFactory.getMaxSessionsPerConnection()).isEqualTo(1024);
				});
	}

	@Test
	public void poolConnectionFactoryConfiguration() {
		this.contextRunner.withPropertyValues("spring.artemis.pool.enabled:true").run((context) -> {
			ConnectionFactory factory = context.getBean(ConnectionFactory.class);
			assertThat(factory).isInstanceOf(JmsPoolConnectionFactory.class);
			context.getSourceApplicationContext().close();
			assertThat(factory.createConnection()).isNull();
		});
	}

	private ActiveMQConnectionFactory getActiveMQConnectionFactory(ConnectionFactory connectionFactory) {
		assertThat(connectionFactory).isInstanceOf(CachingConnectionFactory.class);
		return (ActiveMQConnectionFactory) ((CachingConnectionFactory) connectionFactory).getTargetConnectionFactory();
	}

	private TransportConfiguration assertInVmConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
		TransportConfiguration transportConfig = getSingleTransportConfiguration(connectionFactory);
		assertThat(transportConfig.getFactoryClassName()).isEqualTo(InVMConnectorFactory.class.getName());
		return transportConfig;
	}

	private TransportConfiguration assertNettyConnectionFactory(ActiveMQConnectionFactory connectionFactory,
			String host, int port) {
		TransportConfiguration transportConfig = getSingleTransportConfiguration(connectionFactory);
		assertThat(transportConfig.getFactoryClassName()).isEqualTo(NettyConnectorFactory.class.getName());
		assertThat(transportConfig.getParams().get("host")).isEqualTo(host);
		assertThat(transportConfig.getParams().get("port")).isEqualTo(port);
		return transportConfig;
	}

	private TransportConfiguration getSingleTransportConfiguration(ActiveMQConnectionFactory connectionFactory) {
		TransportConfiguration[] transportConfigurations = connectionFactory.getServerLocator()
				.getStaticTransportConfigurations();
		assertThat(transportConfigurations.length).isEqualTo(1);
		return transportConfigurations[0];
	}

	private static final class DestinationChecker {

		private final JmsTemplate jmsTemplate;

		private final DestinationResolver destinationResolver;

		private DestinationChecker(ApplicationContext applicationContext) {
			this.jmsTemplate = applicationContext.getBean(JmsTemplate.class);
			this.destinationResolver = new DynamicDestinationResolver();
		}

		public void checkQueue(String name, boolean shouldExist) {
			checkDestination(name, false, shouldExist);
		}

		public void checkTopic(String name, boolean shouldExist) {
			checkDestination(name, true, shouldExist);
		}

		public void checkDestination(String name, final boolean pubSub, final boolean shouldExist) {
			this.jmsTemplate.execute((SessionCallback<Void>) (session) -> {
				try {
					Destination destination = this.destinationResolver.resolveDestinationName(session, name, pubSub);
					if (!shouldExist) {
						throw new IllegalStateException(
								"Destination '" + name + "' was not expected but got " + destination);
					}
				}
				catch (JMSException ex) {
					if (shouldExist) {
						throw new IllegalStateException(
								"Destination '" + name + "' was expected but got " + ex.getMessage());
					}
				}
				return null;
			});
		}

	}

	@Configuration
	protected static class EmptyConfiguration {

	}

	@Configuration
	protected static class DestinationConfiguration {

		@Bean
		JMSQueueConfiguration sampleQueueConfiguration() {
			JMSQueueConfigurationImpl jmsQueueConfiguration = new JMSQueueConfigurationImpl();
			jmsQueueConfiguration.setName("sampleQueue");
			jmsQueueConfiguration.setSelector("foo=bar");
			jmsQueueConfiguration.setDurable(false);
			jmsQueueConfiguration.setBindings("/queue/1");
			return jmsQueueConfiguration;
		}

		@Bean
		TopicConfiguration sampleTopicConfiguration() {
			TopicConfigurationImpl topicConfiguration = new TopicConfigurationImpl();
			topicConfiguration.setName("sampleTopic");
			topicConfiguration.setBindings("/topic/1");
			return topicConfiguration;
		}

	}

	@Configuration
	protected static class CustomJmsConfiguration {

		@Bean
		public JMSConfiguration myJmsConfiguration() {
			JMSConfiguration config = new JMSConfigurationImpl();
			JMSQueueConfiguration jmsQueueConfiguration = new JMSQueueConfigurationImpl();
			jmsQueueConfiguration.setName("custom");
			jmsQueueConfiguration.setDurable(false);
			config.getQueueConfigurations().add(jmsQueueConfiguration);
			return config;
		}

	}

	@Configuration
	protected static class CustomArtemisConfiguration {

		@Bean
		public ArtemisConfigurationCustomizer myArtemisCustomize() {
			return (configuration) -> {
				configuration.setClusterPassword("Foobar");
				configuration.setName("customFooBar");
			};
		}

	}

}
