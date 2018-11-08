/*
 * Copyright 2018 the original author or authors.
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

package io.spring.releasenotes.github.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details of a GitHub issue.
 *
 * @author Madhura Bhave
 */
public class Issue {

	private final String number;

	private final String title;

	private final User user;

	private final List<Label> labels;

	private final String url;

	private final PullRequest pullRequest;

	public Issue(@JsonProperty("number") String number,
			@JsonProperty("title") String title, @JsonProperty("user") User user,
			@JsonProperty("labels") List<Label> labels,
			@JsonProperty("html_url") String url,
			@JsonProperty("pull_request") PullRequest pullRequest) {
		super();
		this.number = number;
		this.title = title;
		this.user = user;
		this.labels = labels;
		this.url = url;
		this.pullRequest = pullRequest;
	}

	public String getTitle() {
		return this.title;
	}

	public List<Label> getLabels() {
		return this.labels;
	}

	public User getUser() {
		return this.user;
	}

	public String getUrl() {
		return this.url;
	}

	public String getNumber() {
		return this.number;
	}

	public PullRequest getPullRequest() {
		return this.pullRequest;
	}

}
