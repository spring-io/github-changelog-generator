/*
 * Copyright 2018-2026 the original author or authors.
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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import io.spring.githubchangeloggenerator.ApplicationProperties.PortedIssue;
import io.spring.githubchangeloggenerator.github.payload.Issue;
import io.spring.githubchangeloggenerator.github.payload.Label;
import io.spring.githubchangeloggenerator.github.service.GitHubService;
import io.spring.githubchangeloggenerator.github.service.Repository;

/**
 * An {@link IssueChain} based on {@link PortedIssue ported issues}.
 *
 * @author Andy Wilkinson
 */
class PortedIssueChain implements IssueChain {

	private final Set<PortedIssue> portedIssues;

	private final GitHubService github;

	private final Repository repository;

	PortedIssueChain(Set<PortedIssue> portedIssues, GitHubService github, Repository repository) {
		this.portedIssues = portedIssues;
		this.github = github;
		this.repository = repository;
	}

	@Override
	public Issue nextIssue(Issue issue) {
		if (!StringUtils.hasText(issue.getBody())) {
			return null;
		}
		for (PortedIssue portedIssue : this.portedIssues) {
			List<String> labelNames = issue.getLabels().stream().map(Label::getName).toList();
			if (labelNames.contains(portedIssue.getLabel())) {
				Pattern pattern = portedIssue.getBodyExpression();
				Matcher matcher = pattern.matcher(issue.getBody());
				if (matcher.matches()) {
					String issueNumber = matcher.group(1);
					Issue referencedIssue = this.github.getIssue(issueNumber, this.repository);
					if (referencedIssue != null) {
						return referencedIssue;
					}
				}
			}
		}
		return null;
	}

}
