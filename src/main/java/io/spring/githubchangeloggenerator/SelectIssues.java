/*
 * Copyright 2018-2024 the original author or authors.
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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import io.spring.githubchangeloggenerator.ApplicationProperties.IssueType;
import io.spring.githubchangeloggenerator.ApplicationProperties.LabelMatch;
import io.spring.githubchangeloggenerator.github.payload.Issue;
import io.spring.githubchangeloggenerator.github.payload.Label;

/**
 * Utility to select issues.
 *
 * @author Phillip Webb
 * @author Steven Sheehy
 * @author Venkata Naga Sai Srikanth Gollapudi
 */
final class SelectIssues {

	private SelectIssues() {
	}

	static Predicate<Issue> withLabelNamesContaining(String... nameContent) {
		return withLabelNamesContaining(Set.of(nameContent));
	}

	static Predicate<Issue> withLabelNamesContaining(Collection<String> nameContent) {
		return withLabelNamesContaining(nameContent, LabelMatch.ANY);
	}

	static Predicate<Issue> withLabelNamesContaining(Collection<String> nameContent, LabelMatch labelMatch) {
		if (nameContent == null || nameContent.isEmpty()) {
			return (issue) -> false;
		}
		LabelMatch match = (labelMatch != null) ? labelMatch : LabelMatch.ANY;
		return (issue) -> {
			List<String> labelNames = issue.getLabels().stream().map(Label::getName).toList();
			return switch (match) {
				case ANY ->
					labelNames.stream().anyMatch((labelName) -> nameContent.stream().anyMatch(labelName::contains));
				case ALL -> nameContent.stream()
					.allMatch((content) -> labelNames.stream().anyMatch((labelName) -> labelName.contains(content)));
			};
		};
	}

	static Predicate<? super Issue> withType(IssueType type) {
		return (issue) -> {
			return switch (type) {
				case ANY -> true;
				case ISSUE -> issue.getPullRequest() == null;
				case PULL_REQUEST -> issue.getPullRequest() != null;
			};
		};
	}

}
