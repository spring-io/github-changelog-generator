/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.releasenotesgenerator.github;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details of a GitHub issue.
 *
 * @author Madhura Bhave
 */
public class Issue {

	private int id;

	private String number;

	private String title;

	private User user;

	private List<Label> labels;

	private String state;

	private Milestone milestone;

	@JsonProperty("html_url")
	private String url;

	private Type type;

	@JsonProperty("pull_request")
	private PullRequest pullRequest;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Label> getLabels() {
		return this.labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
		for (Label label : labels) {
			String name = label.getName();
			if (name.contains("bug") || name.contains("regression")) {
				this.type = Type.BUG;
			}
			else if (name.contains("enhancement")) {
				this.type = Type.ENHANCEMENT;
			}
			else if (name.contains("documentation")) {
				this.type = Type.DOCUMENTATION;
			}
			else if (name.contains("dependency-upgrade")) {
				this.type = Type.DEPENDENCY_UPGRADES;
			}
		}
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public PullRequest getPullRequest() {
		return this.pullRequest;
	}

	public void setPullRequest(PullRequest pullRequest) {
		this.pullRequest = pullRequest;
	}

	public Milestone getMilestone() {
		return this.milestone;
	}

	public void setMilestone(Milestone milestone) {
		this.milestone = milestone;
	}

	/**
	 * Describe the available issue types.
	 */
	public enum Type {

		/**
		 * Enhancement.
		 */
		ENHANCEMENT("New Features", ":star:"),

		/**
		 * Bug fix.
		 */
		BUG("Bug fixes", ":beetle:"),

		/**
		 * Documentation change.
		 */
		DOCUMENTATION("Documentation", ":notebook_with_decorative_cover:"),

		/**
		 * Dependency upgrade.
		 */
		DEPENDENCY_UPGRADES("Dependency upgrades", ":hammer:");

		private final String description;

		private final String emoji;

		Type(String description, String emoji) {
			this.description = description;
			this.emoji = emoji;
		}

		public String getDescription() {
			return this.description;
		}

		public String getEmoji() {
			return this.emoji;
		}

	}

}
