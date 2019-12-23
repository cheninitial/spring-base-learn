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

package org.springframework.boot.autoconfigure.liquibase;

import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import liquibase.change.DatabaseChange;
import liquibase.integration.spring.SpringLiquibase;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcOperationsDependsOnPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.NamedParameterJdbcOperationsDependsOnPostProcessor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.util.Assert;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Liquibase.
 *
 * @author Marcel Overdijk
 * @author Dave Syer
 * @author Phillip Webb
 * @author Eddú Meléndez
 * @author Andy Wilkinson
 * @author Dominic Gunn
 * @author Dan Zheng
 * @author András Deák
 * @since 1.1.0
 */
@Configuration
@ConditionalOnClass({ SpringLiquibase.class, DatabaseChange.class })
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(prefix = "spring.liquibase", name = "enabled", matchIfMissing = true)
@AutoConfigureAfter({ DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class LiquibaseAutoConfiguration {

	@Bean
	public LiquibaseSchemaManagementProvider liquibaseDefaultDdlModeProvider(
			ObjectProvider<SpringLiquibase> liquibases) {
		return new LiquibaseSchemaManagementProvider(liquibases);
	}

	@Configuration
	@ConditionalOnMissingBean(SpringLiquibase.class)
	@EnableConfigurationProperties({ DataSourceProperties.class, LiquibaseProperties.class })
	@Import(LiquibaseJpaDependencyConfiguration.class)
	public static class LiquibaseConfiguration {

		private final LiquibaseProperties properties;

		private final DataSourceProperties dataSourceProperties;

		private final ResourceLoader resourceLoader;

		private final DataSource dataSource;

		private final DataSource liquibaseDataSource;

		public LiquibaseConfiguration(LiquibaseProperties properties, DataSourceProperties dataSourceProperties,
				ResourceLoader resourceLoader, ObjectProvider<DataSource> dataSource,
				@LiquibaseDataSource ObjectProvider<DataSource> liquibaseDataSource) {
			this.properties = properties;
			this.dataSourceProperties = dataSourceProperties;
			this.resourceLoader = resourceLoader;
			this.dataSource = dataSource.getIfUnique();
			this.liquibaseDataSource = liquibaseDataSource.getIfAvailable();
		}

		@PostConstruct
		public void checkChangelogExists() {
			if (this.properties.isCheckChangeLogLocation()) {
				Resource resource = this.resourceLoader.getResource(this.properties.getChangeLog());
				Assert.state(resource.exists(), () -> "Cannot find changelog location: " + resource
						+ " (please add changelog or check your Liquibase " + "configuration)");
			}
		}

		@Bean
		public SpringLiquibase liquibase() {
			SpringLiquibase liquibase = createSpringLiquibase();
			liquibase.setChangeLog(this.properties.getChangeLog());
			liquibase.setContexts(this.properties.getContexts());
			liquibase.setDefaultSchema(this.properties.getDefaultSchema());
			liquibase.setLiquibaseSchema(this.properties.getLiquibaseSchema());
			liquibase.setLiquibaseTablespace(this.properties.getLiquibaseTablespace());
			liquibase.setDatabaseChangeLogTable(this.properties.getDatabaseChangeLogTable());
			liquibase.setDatabaseChangeLogLockTable(this.properties.getDatabaseChangeLogLockTable());
			liquibase.setDropFirst(this.properties.isDropFirst());
			liquibase.setShouldRun(this.properties.isEnabled());
			liquibase.setLabels(this.properties.getLabels());
			liquibase.setChangeLogParameters(this.properties.getParameters());
			liquibase.setRollbackFile(this.properties.getRollbackFile());
			liquibase.setTestRollbackOnUpdate(this.properties.isTestRollbackOnUpdate());
			return liquibase;
		}

		private SpringLiquibase createSpringLiquibase() {
			DataSource liquibaseDataSource = getDataSource();
			if (liquibaseDataSource != null) {
				SpringLiquibase liquibase = new SpringLiquibase();
				liquibase.setDataSource(liquibaseDataSource);
				return liquibase;
			}
			SpringLiquibase liquibase = new DataSourceClosingSpringLiquibase();
			liquibase.setDataSource(createNewDataSource());
			return liquibase;
		}

		private DataSource getDataSource() {
			if (this.liquibaseDataSource != null) {
				return this.liquibaseDataSource;
			}
			if (this.properties.getUrl() == null && this.properties.getUser() == null) {
				return this.dataSource;
			}
			return null;
		}

		private DataSource createNewDataSource() {
			String url = getProperty(this.properties::getUrl, this.dataSourceProperties::determineUrl);
			String user = getProperty(this.properties::getUser, this.dataSourceProperties::determineUsername);
			String password = getProperty(this.properties::getPassword, this.dataSourceProperties::determinePassword);
			return DataSourceBuilder.create().url(url).username(user).password(password).build();
		}

		private String getProperty(Supplier<String> property, Supplier<String> defaultValue) {
			String value = property.get();
			return (value != null) ? value : defaultValue.get();
		}

	}

	/**
	 * Additional configuration to ensure that {@link EntityManagerFactory} beans depend
	 * on the liquibase bean.
	 */
	@Configuration
	@ConditionalOnClass(LocalContainerEntityManagerFactoryBean.class)
	@ConditionalOnBean(AbstractEntityManagerFactoryBean.class)
	protected static class LiquibaseJpaDependencyConfiguration extends EntityManagerFactoryDependsOnPostProcessor {

		public LiquibaseJpaDependencyConfiguration() {
			super("liquibase");
		}

	}

	/**
	 * Additional configuration to ensure that {@link JdbcOperations} beans depend on the
	 * liquibase bean.
	 */
	@Configuration
	@ConditionalOnClass(JdbcOperations.class)
	@ConditionalOnBean(JdbcOperations.class)
	protected static class LiquibaseJdbcOperationsDependencyConfiguration extends JdbcOperationsDependsOnPostProcessor {

		public LiquibaseJdbcOperationsDependencyConfiguration() {
			super("liquibase");
		}

	}

	/**
	 * Additional configuration to ensure that {@link NamedParameterJdbcOperations} beans
	 * depend on the liquibase bean.
	 */
	@Configuration
	@ConditionalOnClass(NamedParameterJdbcOperations.class)
	@ConditionalOnBean(NamedParameterJdbcOperations.class)
	protected static class LiquibaseNamedParameterJdbcOperationsDependencyConfiguration
			extends NamedParameterJdbcOperationsDependsOnPostProcessor {

		public LiquibaseNamedParameterJdbcOperationsDependencyConfiguration() {
			super("liquibase");
		}

	}

}
