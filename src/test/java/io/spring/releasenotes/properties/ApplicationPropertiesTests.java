/*
 * Copyright 2018-2019 the original author or authors.
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

package io.spring.releasenotes.properties;

import java.util.List;

import io.spring.releasenotes.properties.ApplicationProperties.Github;
import io.spring.releasenotes.properties.ApplicationProperties.Section;
import org.junit.Test;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ApplicationProperties}.
 *
 * @author Phillip Webb
 */
public class ApplicationPropertiesTests {

	@Test
	public void loadYaml() throws Exception {
		YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
		List<PropertySource<?>> yaml = yamlLoader.load("application",
				new ClassPathResource("test-application.yml", getClass()));
		Binder binder = new Binder(ConfigurationPropertySources.from(yaml));
		ApplicationProperties properties = binder
				.bind("releasenotes", ApplicationProperties.class).get();
		Github github = properties.getGithub();
		assertThat(github.getUsername()).isEqualTo("testuser");
		assertThat(github.getPassword()).isEqualTo("testpass");
		assertThat(github.getOrganization()).isEqualTo("testorg");
		assertThat(github.getRepository()).isEqualTo("testrepo");
		List<Section> sections = properties.getSections();
		assertThat(sections.get(0).getTitle()).isEqualTo("New Features");
		assertThat(sections.get(0).getEmoji()).isEqualTo(":star:");
		assertThat(sections.get(0).getLabels()).containsExactly("enhancement");
	}

}
