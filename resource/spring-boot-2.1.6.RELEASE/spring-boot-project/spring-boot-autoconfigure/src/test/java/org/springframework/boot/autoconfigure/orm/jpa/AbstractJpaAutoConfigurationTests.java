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

package org.springframework.boot.autoconfigure.orm.jpa;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform;
import org.junit.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.test.City;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ContextConsumer;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.transaction.PlatformTransactionManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base for JPA tests and tests for {@link JpaBaseConfiguration}.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public abstract class AbstractJpaAutoConfigurationTests {

	private final Class<?> autoConfiguredClass;

	private final ApplicationContextRunner contextRunner;

	protected AbstractJpaAutoConfigurationTests(Class<?> autoConfiguredClass) {
		this.autoConfiguredClass = autoConfiguredClass;
		this.contextRunner = new ApplicationContextRunner()
				.withPropertyValues("spring.datasource.generate-unique-name=true")
				.withUserConfiguration(TestConfiguration.class).withConfiguration(AutoConfigurations.of(
						DataSourceAutoConfiguration.class, TransactionAutoConfiguration.class, autoConfiguredClass));
	}

	protected ApplicationContextRunner contextRunner() {
		return this.contextRunner;
	}

	@Test
	public void notConfiguredIfDataSourceIsNotAvailable() {
		new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(this.autoConfiguredClass))
				.run(assertJpaIsNotAutoConfigured());
	}

	@Test
	public void notConfiguredIfNoSingleDataSourceCandidateIsAvailable() {
		new ApplicationContextRunner().withUserConfiguration(TestTwoDataSourcesConfiguration.class)
				.withConfiguration(AutoConfigurations.of(this.autoConfiguredClass)).run(assertJpaIsNotAutoConfigured());
	}

	protected ContextConsumer<AssertableApplicationContext> assertJpaIsNotAutoConfigured() {
		return (context) -> {
			assertThat(context).hasNotFailed();
			assertThat(context).hasSingleBean(JpaProperties.class);
			assertThat(context).doesNotHaveBean(PlatformTransactionManager.class);
			assertThat(context).doesNotHaveBean(EntityManagerFactory.class);
		};
	}

	@Test
	public void configuredWithAutoConfiguredDataSource() {
		this.contextRunner.run((context) -> {
			assertThat(context).hasSingleBean(DataSource.class);
			assertThat(context).hasSingleBean(JpaTransactionManager.class);
			assertThat(context).hasSingleBean(EntityManagerFactory.class);
		});
	}

	@Test
	public void configuredWithSingleCandidateDataSource() {
		this.contextRunner.withUserConfiguration(TestTwoDataSourcesAndPrimaryConfiguration.class).run((context) -> {
			assertThat(context).getBeans(DataSource.class).hasSize(2);
			assertThat(context).hasSingleBean(JpaTransactionManager.class);
			assertThat(context).hasSingleBean(EntityManagerFactory.class);
		});
	}

	@Test
	public void jtaTransactionManagerTakesPrecedence() {
		this.contextRunner.withConfiguration(AutoConfigurations.of(DataSourceTransactionManagerAutoConfiguration.class))
				.run((context) -> {
					assertThat(context).hasSingleBean(DataSource.class);
					assertThat(context).hasSingleBean(JpaTransactionManager.class);
					assertThat(context).getBean("transactionManager").isInstanceOf(JpaTransactionManager.class);
				});
	}

	@Test
	public void openEntityManagerInViewInterceptorIsCreated() {
		new WebApplicationContextRunner().withPropertyValues("spring.datasource.generate-unique-name=true")
				.withUserConfiguration(TestConfiguration.class)
				.withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class,
						TransactionAutoConfiguration.class, this.autoConfiguredClass))
				.run((context) -> assertThat(context).hasSingleBean(OpenEntityManagerInViewInterceptor.class));
	}

	@Test
	public void openEntityManagerInViewInterceptorIsNotRegisteredWhenFilterPresent() {
		new WebApplicationContextRunner().withPropertyValues("spring.datasource.generate-unique-name=true")
				.withUserConfiguration(TestFilterConfiguration.class)
				.withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class,
						TransactionAutoConfiguration.class, this.autoConfiguredClass))
				.run((context) -> assertThat(context).doesNotHaveBean(OpenEntityManagerInViewInterceptor.class));
	}

	@Test
	public void openEntityManagerInViewInterceptorIsNotRegisteredWhenFilterRegistrationPresent() {
		new WebApplicationContextRunner().withPropertyValues("spring.datasource.generate-unique-name=true")
				.withUserConfiguration(TestFilterRegistrationConfiguration.class)
				.withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class,
						TransactionAutoConfiguration.class, this.autoConfiguredClass))
				.run((context) -> assertThat(context).doesNotHaveBean(OpenEntityManagerInViewInterceptor.class));
	}

	@Test
	public void openEntityManagerInViewInterceptorAutoConfigurationBacksOffWhenManuallyRegistered() {
		new WebApplicationContextRunner().withPropertyValues("spring.datasource.generate-unique-name=true")
				.withUserConfiguration(TestInterceptorManualConfiguration.class)
				.withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class,
						TransactionAutoConfiguration.class, this.autoConfiguredClass))
				.run((context) -> assertThat(context).getBean(OpenEntityManagerInViewInterceptor.class)
						.isExactlyInstanceOf(
								TestInterceptorManualConfiguration.ManualOpenEntityManagerInViewInterceptor.class));
	}

	@Test
	public void openEntityManagerInViewInterceptorISNotRegisteredWhenExplicitlyOff() {
		new WebApplicationContextRunner()
				.withPropertyValues("spring.datasource.generate-unique-name=true", "spring.jpa.open-in-view=false")
				.withUserConfiguration(TestConfiguration.class)
				.withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class,
						TransactionAutoConfiguration.class, this.autoConfiguredClass))
				.run((context) -> assertThat(context).doesNotHaveBean(OpenEntityManagerInViewInterceptor.class));
	}

	@Test
	public void customJpaProperties() {
		this.contextRunner.withPropertyValues("spring.jpa.properties.a:b", "spring.jpa.properties.a.b:c",
				"spring.jpa.properties.c:d").run((context) -> {
					LocalContainerEntityManagerFactoryBean bean = context
							.getBean(LocalContainerEntityManagerFactoryBean.class);
					Map<String, Object> map = bean.getJpaPropertyMap();
					assertThat(map.get("a")).isEqualTo("b");
					assertThat(map.get("c")).isEqualTo("d");
					assertThat(map.get("a.b")).isEqualTo("c");
				});
	}

	@Test
	public void usesManuallyDefinedLocalContainerEntityManagerFactoryBeanIfAvailable() {
		this.contextRunner.withUserConfiguration(TestConfigurationWithLocalContainerEntityManagerFactoryBean.class)
				.run((context) -> {
					LocalContainerEntityManagerFactoryBean factoryBean = context
							.getBean(LocalContainerEntityManagerFactoryBean.class);
					Map<String, Object> map = factoryBean.getJpaPropertyMap();
					assertThat(map.get("configured")).isEqualTo("manually");
				});
	}

	@Test
	public void usesManuallyDefinedEntityManagerFactoryIfAvailable() {
		this.contextRunner.withUserConfiguration(TestConfigurationWithLocalContainerEntityManagerFactoryBean.class)
				.run((context) -> {
					EntityManagerFactory factoryBean = context.getBean(EntityManagerFactory.class);
					Map<String, Object> map = factoryBean.getProperties();
					assertThat(map.get("configured")).isEqualTo("manually");
				});
	}

	@Test
	public void usesManuallyDefinedTransactionManagerBeanIfAvailable() {
		this.contextRunner.withUserConfiguration(TestConfigurationWithTransactionManager.class).run((context) -> {
			PlatformTransactionManager txManager = context.getBean(PlatformTransactionManager.class);
			assertThat(txManager).isInstanceOf(CustomJpaTransactionManager.class);
		});
	}

	@Test
	public void customPersistenceUnitManager() {
		this.contextRunner.withUserConfiguration(TestConfigurationWithCustomPersistenceUnitManager.class)
				.run((context) -> {
					LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = context
							.getBean(LocalContainerEntityManagerFactoryBean.class);
					Field field = LocalContainerEntityManagerFactoryBean.class
							.getDeclaredField("persistenceUnitManager");
					field.setAccessible(true);
					assertThat(field.get(entityManagerFactoryBean))
							.isEqualTo(context.getBean(PersistenceUnitManager.class));
				});
	}

	@Configuration
	protected static class TestTwoDataSourcesConfiguration {

		@Bean
		public DataSource firstDataSource() {
			return createRandomDataSource();
		}

		@Bean
		public DataSource secondDataSource() {
			return createRandomDataSource();
		}

		private DataSource createRandomDataSource() {
			String url = "jdbc:h2:mem:init-" + UUID.randomUUID();
			return DataSourceBuilder.create().url(url).build();
		}

	}

	@Configuration
	static class TestTwoDataSourcesAndPrimaryConfiguration {

		@Bean
		@Primary
		public DataSource firstDataSource() {
			return createRandomDataSource();
		}

		@Bean
		public DataSource secondDataSource() {
			return createRandomDataSource();
		}

		private DataSource createRandomDataSource() {
			String url = "jdbc:h2:mem:init-" + UUID.randomUUID();
			return DataSourceBuilder.create().url(url).build();
		}

	}

	@Configuration
	@TestAutoConfigurationPackage(City.class)
	protected static class TestConfiguration {

	}

	@Configuration
	@TestAutoConfigurationPackage(City.class)
	protected static class TestFilterConfiguration {

		@Bean
		public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
			return new OpenEntityManagerInViewFilter();
		}

	}

	@Configuration
	@TestAutoConfigurationPackage(City.class)
	protected static class TestFilterRegistrationConfiguration {

		@Bean
		public FilterRegistrationBean<OpenEntityManagerInViewFilter> OpenEntityManagerInViewFilterFilterRegistrationBean() {
			return new FilterRegistrationBean<>();
		}

	}

	@Configuration
	@TestAutoConfigurationPackage(City.class)
	protected static class TestInterceptorManualConfiguration {

		@Bean
		public OpenEntityManagerInViewInterceptor openEntityManagerInViewInterceptor() {
			return new ManualOpenEntityManagerInViewInterceptor();
		}

		protected static class ManualOpenEntityManagerInViewInterceptor extends OpenEntityManagerInViewInterceptor {

		}

	}

	@Configuration
	protected static class TestConfigurationWithLocalContainerEntityManagerFactoryBean extends TestConfiguration {

		@Bean
		public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
				JpaVendorAdapter adapter) {
			LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
			factoryBean.setJpaVendorAdapter(adapter);
			factoryBean.setDataSource(dataSource);
			factoryBean.setPersistenceUnitName("manually-configured");
			Map<String, Object> properties = new HashMap<>();
			properties.put("configured", "manually");
			properties.put("hibernate.transaction.jta.platform", NoJtaPlatform.INSTANCE);
			factoryBean.setJpaPropertyMap(properties);
			return factoryBean;
		}

	}

	@Configuration
	protected static class TestConfigurationWithEntityManagerFactory extends TestConfiguration {

		@Bean
		public EntityManagerFactory entityManagerFactory(DataSource dataSource, JpaVendorAdapter adapter) {
			LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
			factoryBean.setJpaVendorAdapter(adapter);
			factoryBean.setDataSource(dataSource);
			factoryBean.setPersistenceUnitName("manually-configured");
			Map<String, Object> properties = new HashMap<>();
			properties.put("configured", "manually");
			properties.put("hibernate.transaction.jta.platform", NoJtaPlatform.INSTANCE);
			factoryBean.setJpaPropertyMap(properties);
			factoryBean.afterPropertiesSet();
			return factoryBean.getObject();
		}

		@Bean
		public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
			JpaTransactionManager transactionManager = new JpaTransactionManager();
			transactionManager.setEntityManagerFactory(emf);
			return transactionManager;
		}

	}

	@Configuration
	@TestAutoConfigurationPackage(City.class)
	protected static class TestConfigurationWithTransactionManager {

		@Bean
		public PlatformTransactionManager transactionManager() {
			return new CustomJpaTransactionManager();
		}

	}

	@Configuration
	@TestAutoConfigurationPackage(AbstractJpaAutoConfigurationTests.class)
	public static class TestConfigurationWithCustomPersistenceUnitManager {

		private final DataSource dataSource;

		public TestConfigurationWithCustomPersistenceUnitManager(DataSource dataSource) {
			this.dataSource = dataSource;
		}

		@Bean
		public PersistenceUnitManager persistenceUnitManager() {
			DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();
			persistenceUnitManager.setDefaultDataSource(this.dataSource);
			persistenceUnitManager.setPackagesToScan(City.class.getPackage().getName());
			return persistenceUnitManager;
		}

	}

	@SuppressWarnings("serial")
	static class CustomJpaTransactionManager extends JpaTransactionManager {

	}

}
