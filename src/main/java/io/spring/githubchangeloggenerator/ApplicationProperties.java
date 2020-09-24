/*
 * Copyright 2018-2020 the original author or authors.
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

package io.spring.githubchangeloggenerator;

import java.util.Arrays;
import java.util.List;

import io.spring.githubchangeloggenerator.github.service.Repository;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.Assert;

/**
 * Configuration properties for the Github repo.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
@ConfigurationProperties(prefix = "changelog")
@ConstructorBinding
public class ApplicationProperties {

	/**
	 * GitHub repository to use in the form "owner/repository".
	 */
	private final Repository repository;

	/**
	 * The way that milestones are referenced. Supports "title", "id".
	 */
	private final MilestoneReference milestoneReference;

	/**
	 * Section definitions in the order that they should appear.
	 */
	private final List<Section> sections;

	public ApplicationProperties(Repository repository, @DefaultValue("title") MilestoneReference milestoneReference,
			List<Section> sections) {
		Assert.notNull(repository, "Repository must not be null");
		this.repository = repository;
		this.milestoneReference = milestoneReference;
		this.sections = sections;
	}

	public Repository getRepository() {
		return this.repository;
	}

	public MilestoneReference getMilestoneReference() {
		return this.milestoneReference;
	}

	public List<Section> getSections() {
		return this.sections;
	}

	/**
	 * Properties for a single changelog section.
	 */
	public static class Section {

		/**
		 * Title of the section.
		 */
		private final String title;

		/**
		 * Group used to bound the contained issues. Issues appear in the first section of
		 * each group.
		 */
		private final String group;

		/**
		 * Labels used to identify if an issue is for the section.
		 */
		private final List<String> labels;

		public Section(String title, @DefaultValue("default") String group, String... labels) {
			this.title = title;
			this.group = (group != null) ? group : "default";
			this.labels = Arrays.asList(labels);
		}

		public String getTitle() {
			return this.title;
		}

		public String getGroup() {
			return this.group;
		}

		public List<String> getLabels() {
			return this.labels;
		}

	}

}
