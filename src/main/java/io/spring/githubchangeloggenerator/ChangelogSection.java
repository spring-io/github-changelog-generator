/*
 * Copyright 2018-2022 the original author or authors.
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
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.spring.githubchangeloggenerator.ApplicationProperties.IssueSort;
import io.spring.githubchangeloggenerator.ApplicationProperties.IssueType;
import io.spring.githubchangeloggenerator.github.payload.Issue;
import io.spring.githubchangeloggenerator.github.payload.Label;

/**
 * A single section of a changelog report.
 *
 * @author Phillip Webb
 * @author Steven Sheehy
 */
class ChangelogSection {

	private final String title;

	private final String group;

	private final IssueSort sort;

	private final Set<String> labels;

	private final IssueType type;

	ChangelogSection(String title, String group, IssueSort sort, String... labels) {
		this(title, group, sort, new LinkedHashSet<>(Arrays.asList(labels)), IssueType.ANY);
	}

	ChangelogSection(String title, String group, IssueSort sort, Set<String> labels, IssueType type) {
		Assert.hasText(title, "Title must not be empty");
		Assert.isTrue(!CollectionUtils.isEmpty(labels), "Labels must not be empty");
		this.title = title;
		this.group = group;
		this.sort = sort;
		this.labels = labels;
		this.type = type;
	}

	String getGroup() {
		return this.group;
	}

	IssueSort getSort() {
		return this.sort;
	}

	boolean isMatchFor(Issue issue) {
		for (String candidate : this.labels) {
			for (Label label : issue.getLabels()) {
				if (label.getName().contains(candidate)) {
					switch (this.type) {
						case ISSUE:
							return issue.getPullRequest() == null;
						case PULL_REQUEST:
							return issue.getPullRequest() != null;
						default:
							return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return this.title;
	}

}
