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

package org.springframework.boot.gradle.tasks.buildinfo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.gradle.junit.GradleCompatibilitySuite;
import org.springframework.boot.gradle.testkit.GradleBuild;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the {@link BuildInfo} task.
 *
 * @author Andy Wilkinson
 */
@RunWith(GradleCompatibilitySuite.class)
public class BuildInfoIntegrationTests {

	@Rule
	public GradleBuild gradleBuild;

	@Test
	public void defaultValues() {
		assertThat(this.gradleBuild.build("buildInfo").task(":buildInfo").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
		Properties buildInfoProperties = buildInfoProperties();
		assertThat(buildInfoProperties).containsKey("build.time");
		assertThat(buildInfoProperties).containsEntry("build.artifact", "unspecified");
		assertThat(buildInfoProperties).containsEntry("build.group", "");
		assertThat(buildInfoProperties).containsEntry("build.name", this.gradleBuild.getProjectDir().getName());
		assertThat(buildInfoProperties).containsEntry("build.version", "unspecified");
	}

	@Test
	public void basicExecution() {
		assertThat(this.gradleBuild.build("buildInfo").task(":buildInfo").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
		Properties buildInfoProperties = buildInfoProperties();
		assertThat(buildInfoProperties).containsKey("build.time");
		assertThat(buildInfoProperties).containsEntry("build.artifact", "foo");
		assertThat(buildInfoProperties).containsEntry("build.group", "foo");
		assertThat(buildInfoProperties).containsEntry("build.additional", "foo");
		assertThat(buildInfoProperties).containsEntry("build.name", "foo");
		assertThat(buildInfoProperties).containsEntry("build.version", "1.0");
	}

	@Test
	public void notUpToDateWhenExecutedTwiceAsTimeChanges() {
		assertThat(this.gradleBuild.build("buildInfo").task(":buildInfo").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
		assertThat(this.gradleBuild.build("buildInfo").task(":buildInfo").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
	}

	@Test
	public void upToDateWhenExecutedTwiceWithFixedTime() {
		assertThat(this.gradleBuild.build("buildInfo", "-PnullTime").task(":buildInfo").getOutcome())
				.isEqualTo(TaskOutcome.SUCCESS);
		assertThat(this.gradleBuild.build("buildInfo", "-PnullTime").task(":buildInfo").getOutcome())
				.isEqualTo(TaskOutcome.UP_TO_DATE);
	}

	@Test
	public void notUpToDateWhenExecutedTwiceWithFixedTimeAndChangedProjectVersion() {
		assertThat(this.gradleBuild.build("buildInfo", "-PnullTime").task(":buildInfo").getOutcome())
				.isEqualTo(TaskOutcome.SUCCESS);
		BuildResult result = this.gradleBuild.build("buildInfo", "-PnullTime", "-PprojectVersion=0.2.0");
		assertThat(result.task(":buildInfo").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
	}

	private Properties buildInfoProperties() {
		File file = new File(this.gradleBuild.getProjectDir(), "build/build-info.properties");
		assertThat(file).isFile();
		Properties properties = new Properties();
		try (FileReader reader = new FileReader(file)) {
			properties.load(reader);
			return properties;
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
