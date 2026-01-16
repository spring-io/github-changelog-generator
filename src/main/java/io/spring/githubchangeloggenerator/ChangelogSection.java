/*
 * Copyright 2018-2025 the original author or authors.
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

import java.util.function.Predicate;

import org.springframework.util.Assert;

import io.spring.githubchangeloggenerator.ApplicationProperties.IssueSort;
import io.spring.githubchangeloggenerator.github.payload.Issue;

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

	private Predicate<Issue> filter;

	private IssueSummarizer summarizer;

	ChangelogSection(String title, String group, IssueSort sort, Predicate<Issue> filter) {
		this(title, group, sort, filter, Issue::getTitle);
	}

	ChangelogSection(String title, String group, IssueSort sort, Predicate<Issue> filter, IssueSummarizer summarizer) {
		Assert.hasText(title, "Title must not be empty");
		Assert.notNull(filter, "Filter must not be null");
		Assert.notNull(summarizer, "Summarizer must not be null");
		this.title = title;
		this.group = group;
		this.sort = sort;
		this.filter = filter;
		this.summarizer = summarizer;
	}

	String getGroup() {
		return this.group;
	}

	IssueSort getSort() {
		return this.sort;
	}

	@Override
	public String toString() {
		return this.title;
	}

	boolean isMatchFor(Issue issue) {
		return this.filter.test(issue);
	}

	String summarize(Issue issue) {
		return this.summarizer.summarize(issue);
	}

}
