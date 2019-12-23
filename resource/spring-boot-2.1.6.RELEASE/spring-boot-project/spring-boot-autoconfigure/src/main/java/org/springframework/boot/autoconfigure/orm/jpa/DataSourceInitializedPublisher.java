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

import java.util.Map;
import java.util.function.Supplier;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.jdbc.DataSourceSchemaCreatedEvent;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * {@link BeanPostProcessor} used to fire {@link DataSourceSchemaCreatedEvent}s. Should
 * only be registered via the inner {@link Registrar} class.
 *
 * @author Dave Syer
 * @since 1.1.0
 */
class DataSourceInitializedPublisher implements BeanPostProcessor {

	@Autowired
	private ApplicationContext applicationContext;

	private DataSource dataSource;

	private JpaProperties jpaProperties;

	private HibernateProperties hibernateProperties;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof LocalContainerEntityManagerFactoryBean) {
			LocalContainerEntityManagerFactoryBean factory = (LocalContainerEntityManagerFactoryBean) bean;
			factory.setJpaVendorAdapter(new DataSourceSchemaCreatedPublisher(factory));
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof DataSource) {
			// Normally this will be the right DataSource
			this.dataSource = (DataSource) bean;
		}
		if (bean instanceof JpaProperties) {
			this.jpaProperties = (JpaProperties) bean;
		}
		if (bean instanceof HibernateProperties) {
			this.hibernateProperties = (HibernateProperties) bean;
		}
		if (bean instanceof LocalContainerEntityManagerFactoryBean) {
			LocalContainerEntityManagerFactoryBean factory = (LocalContainerEntityManagerFactoryBean) bean;
			if (factory.getBootstrapExecutor() == null) {
				publishEventIfRequired(factory.getNativeEntityManagerFactory());
			}
		}
		return bean;
	}

	private void publishEventIfRequired(EntityManagerFactory entityManagerFactory) {
		DataSource dataSource = findDataSource(entityManagerFactory);
		if (dataSource != null && isInitializingDatabase(dataSource)) {
			this.applicationContext.publishEvent(new DataSourceSchemaCreatedEvent(dataSource));
		}
	}

	private DataSource findDataSource(EntityManagerFactory entityManagerFactory) {
		Object dataSource = entityManagerFactory.getProperties().get("javax.persistence.nonJtaDataSource");
		return (dataSource instanceof DataSource) ? (DataSource) dataSource : this.dataSource;
	}

	private boolean isInitializingDatabase(DataSource dataSource) {
		if (this.jpaProperties == null || this.hibernateProperties == null) {
			return true; // better safe than sorry
		}
		Supplier<String> defaultDdlAuto = () -> (EmbeddedDatabaseConnection.isEmbedded(dataSource) ? "create-drop"
				: "none");
		Map<String, Object> hibernate = this.hibernateProperties.determineHibernateProperties(
				this.jpaProperties.getProperties(), new HibernateSettings().ddlAuto(defaultDdlAuto));
		if (hibernate.containsKey("hibernate.hbm2ddl.auto")) {
			return true;
		}
		return false;
	}

	/**
	 * {@link ImportBeanDefinitionRegistrar} to register the
	 * {@link DataSourceInitializedPublisher} without causing early bean instantiation
	 * issues.
	 */
	static class Registrar implements ImportBeanDefinitionRegistrar {

		private static final String BEAN_NAME = "dataSourceInitializedPublisher";

		@Override
		public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
				BeanDefinitionRegistry registry) {
			if (!registry.containsBeanDefinition(BEAN_NAME)) {
				GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
				beanDefinition.setBeanClass(DataSourceInitializedPublisher.class);
				beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				// We don't need this one to be post processed otherwise it can cause a
				// cascade of bean instantiation that we would rather avoid.
				beanDefinition.setSynthetic(true);
				registry.registerBeanDefinition(BEAN_NAME, beanDefinition);
			}
		}

	}

	final class DataSourceSchemaCreatedPublisher implements JpaVendorAdapter {

		private final JpaVendorAdapter delegate;

		private final LocalContainerEntityManagerFactoryBean factory;

		private DataSourceSchemaCreatedPublisher(LocalContainerEntityManagerFactoryBean factory) {
			this.delegate = factory.getJpaVendorAdapter();
			this.factory = factory;
		}

		@Override
		public PersistenceProvider getPersistenceProvider() {
			return this.delegate.getPersistenceProvider();
		}

		@Override
		public String getPersistenceProviderRootPackage() {
			return this.delegate.getPersistenceProviderRootPackage();
		}

		@Override
		public Map<String, ?> getJpaPropertyMap(PersistenceUnitInfo pui) {
			return this.delegate.getJpaPropertyMap(pui);
		}

		@Override
		public Map<String, ?> getJpaPropertyMap() {
			return this.delegate.getJpaPropertyMap();
		}

		@Override
		public JpaDialect getJpaDialect() {
			return this.delegate.getJpaDialect();
		}

		@Override
		public Class<? extends EntityManagerFactory> getEntityManagerFactoryInterface() {
			return this.delegate.getEntityManagerFactoryInterface();
		}

		@Override
		public Class<? extends EntityManager> getEntityManagerInterface() {
			return this.delegate.getEntityManagerInterface();
		}

		@Override
		public void postProcessEntityManagerFactory(EntityManagerFactory emf) {
			this.delegate.postProcessEntityManagerFactory(emf);
			AsyncTaskExecutor bootstrapExecutor = this.factory.getBootstrapExecutor();
			if (bootstrapExecutor != null) {
				bootstrapExecutor.execute(() -> DataSourceInitializedPublisher.this.publishEventIfRequired(emf));
			}
		}

	}

}
