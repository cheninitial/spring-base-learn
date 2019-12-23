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

package org.springframework.boot.cloud;

import org.junit.Test;

import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CloudPlatform}.
 *
 * @author Phillip Webb
 */
public class CloudPlatformTests {

	@Test
	public void getActiveWhenEnvironmentIsNullShouldReturnNull() {
		CloudPlatform platform = CloudPlatform.getActive(null);
		assertThat(platform).isNull();
	}

	@Test
	public void getActiveWhenNotInCloudShouldReturnNull() {
		Environment environment = new MockEnvironment();
		CloudPlatform platform = CloudPlatform.getActive(environment);
		assertThat(platform).isNull();

	}

	@Test
	public void getActiveWhenHasVcapApplicationShouldReturnCloudFoundry() {
		Environment environment = new MockEnvironment().withProperty("VCAP_APPLICATION", "---");
		CloudPlatform platform = CloudPlatform.getActive(environment);
		assertThat(platform).isEqualTo(CloudPlatform.CLOUD_FOUNDRY);
		assertThat(platform.isActive(environment)).isTrue();
	}

	@Test
	public void getActiveWhenHasVcapServicesShouldReturnCloudFoundry() {
		Environment environment = new MockEnvironment().withProperty("VCAP_SERVICES", "---");
		CloudPlatform platform = CloudPlatform.getActive(environment);
		assertThat(platform).isEqualTo(CloudPlatform.CLOUD_FOUNDRY);
		assertThat(platform.isActive(environment)).isTrue();
	}

	@Test
	public void getActiveWhenHasDynoShouldReturnHeroku() {
		Environment environment = new MockEnvironment().withProperty("DYNO", "---");
		CloudPlatform platform = CloudPlatform.getActive(environment);
		assertThat(platform).isEqualTo(CloudPlatform.HEROKU);
		assertThat(platform.isActive(environment)).isTrue();
	}

	@Test
	public void getActiveWhenHasHcLandscapeShouldReturnSap() {
		Environment environment = new MockEnvironment().withProperty("HC_LANDSCAPE", "---");
		CloudPlatform platform = CloudPlatform.getActive(environment);
		assertThat(platform).isEqualTo(CloudPlatform.SAP);
		assertThat(platform.isActive(environment)).isTrue();
	}

}
