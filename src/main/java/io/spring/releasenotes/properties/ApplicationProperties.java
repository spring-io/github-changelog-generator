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

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Github repo.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
@ConfigurationProperties(prefix = "releasenotes")
public class ApplicationProperties {

	/**
	 * GitHub properties.
	 */
	private Github github = new Github();

	/**
	 * Section definitions in the order that they should appear.
	 */
	private List<Section> sections = new ArrayList<>();

	public Github getGithub() {
		return this.github;
	}

	public List<Section> getSections() {
		return this.sections;
	}

	/**
	 * Github related properties.
	 */
	public static class Github {

		/**
		 * The base url to github's api.
		 */
		private String apiUrl = "https://api.github.com";

		/**
		 * The username for the github user.
		 */
		private String username;

		/**
		 * The password for the github user.
		 */
		private String password;

		/**
		 * The github org this repository is under.
		 */
		private String organization;

		/**
		 * The name of the github repository.
		 */
		private String repository;

		public String getApiUrl() {
			return this.apiUrl;
		}

		public void setApiUrl(String apiUrl) {
			this.apiUrl = apiUrl;
		}

		public String getOrganization() {
			return this.organization;
		}

		public void setOrganization(String organization) {
			this.organization = organization;
		}

		public String getRepository() {
			return this.repository;
		}

		public void setRepository(String repository) {
			this.repository = repository;
		}

		public String getUsername() {
			return this.username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return this.password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

	/**
	 * Properties for a single release notes section.
	 */
	public static class Section {

		/**
		 * The title of the section.
		 */
		private String title;

		/**
		 * The emoji character to use, for example ":star:".
		 */
		private String emoji;

		/**
		 * The labels used to identify if an issue is for the section.
		 */
		private List<String> labels = new ArrayList<>();

		public String getTitle() {
			return this.title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getEmoji() {
			return this.emoji;
		}

		public void setEmoji(String emoji) {
			this.emoji = emoji;
		}

		public List<String> getLabels() {
			return this.labels;
		}

		public void setLabels(List<String> labels) {
			this.labels = labels;
		}

	}

}
