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

package org.springframework.boot.actuate.liquibase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;

import org.springframework.boot.actuate.liquibase.LiquibaseEndpoint.LiquibaseBean;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LiquibaseEndpoint}.
 *
 * @author Eddú Meléndez
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class LiquibaseEndpointTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(
					AutoConfigurations.of(DataSourceAutoConfiguration.class, LiquibaseAutoConfiguration.class))
			.withPropertyValues("spring.datasource.generate-unique-name=true");

	@Test
	public void liquibaseReportIsReturned() {
		this.contextRunner.withUserConfiguration(Config.class).run((context) -> {
			Map<String, LiquibaseBean> liquibaseBeans = context.getBean(LiquibaseEndpoint.class).liquibaseBeans()
					.getContexts().get(context.getId()).getLiquibaseBeans();
			assertThat(liquibaseBeans.get("liquibase").getChangeSets()).hasSize(1);
		});
	}

	@Test
	public void invokeWithCustomSchema() {
		this.contextRunner.withUserConfiguration(Config.class)
				.withPropertyValues("spring.liquibase.default-schema=CUSTOMSCHEMA",
						"spring.datasource.schema=classpath:/db/create-custom-schema.sql")
				.run((context) -> {
					Map<String, LiquibaseBean> liquibaseBeans = context.getBean(LiquibaseEndpoint.class)
							.liquibaseBeans().getContexts().get(context.getId()).getLiquibaseBeans();
					assertThat(liquibaseBeans.get("liquibase").getChangeSets()).hasSize(1);
				});
	}

	@Test
	public void invokeWithCustomTables() {
		this.contextRunner.withUserConfiguration(Config.class)
				.withPropertyValues("spring.liquibase.database-change-log-lock-table=liquibase_database_changelog_lock",
						"spring.liquibase.database-change-log-table=liquibase_database_changelog")
				.run((context) -> {
					Map<String, LiquibaseBean> liquibaseBeans = context.getBean(LiquibaseEndpoint.class)
							.liquibaseBeans().getContexts().get(context.getId()).getLiquibaseBeans();
					assertThat(liquibaseBeans.get("liquibase").getChangeSets()).hasSize(1);
				});
	}

	@Test
	public void connectionAutoCommitPropertyIsReset() {
		this.contextRunner.withUserConfiguration(Config.class).run((context) -> {
			DataSource dataSource = context.getBean(DataSource.class);
			assertThat(getAutoCommit(dataSource)).isTrue();
			context.getBean(LiquibaseEndpoint.class).liquibaseBeans();
			assertThat(getAutoCommit(dataSource)).isTrue();
		});
	}

	private boolean getAutoCommit(DataSource dataSource) throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			return connection.getAutoCommit();
		}
	}

	@Configuration
	public static class Config {

		@Bean
		public LiquibaseEndpoint endpoint(ApplicationContext context) {
			return new LiquibaseEndpoint(context);
		}

	}

}
