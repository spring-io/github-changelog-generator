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

package io.spring.githubchangeloggenerator;

import java.util.List;

import io.spring.githubchangeloggenerator.github.service.Repository;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
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
	 * The GitHub repository to use in the form "owner/repository".
	 */
	private final Repository repository;

	/**
	 * Section definitions in the order that they should appear.
	 */
	private final List<Section> sections;

	public ApplicationProperties(Repository repository, List<Section> sections) {
		Assert.notNull(repository, "Repository must not be null");
		this.repository = repository;
		this.sections = sections;
	}

	public Repository getRepository() {
		return this.repository;
	}

	public List<Section> getSections() {
		return this.sections;
	}

	/**
	 * Properties for a single changelog section.
	 */
	public static class Section {

		/**
		 * The title of the section.
		 */
		private final String title;

		/**
		 * The emoji character to use, for example ":star:".
		 */
		private final String emoji;

		/**
		 * The labels used to identify if an issue is for the section.
		 */
		private final List<String> labels;

		public Section(String title, String emoji, List<String> labels) {
			this.title = title;
			this.emoji = emoji;
			this.labels = labels;
		}

		public String getTitle() {
			return this.title;
		}

		public String getEmoji() {
			return this.emoji;
		}

		public List<String> getLabels() {
			return this.labels;
		}

	}

}
