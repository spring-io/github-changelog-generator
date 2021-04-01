/*
 * Copyright 2018-2021 the original author or authors.
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.spring.githubchangeloggenerator.ApplicationProperties.ExternalLink;
import io.spring.githubchangeloggenerator.ApplicationProperties.IssueSort;
import io.spring.githubchangeloggenerator.ApplicationProperties.PortedIssue;
import io.spring.githubchangeloggenerator.github.payload.Issue;
import io.spring.githubchangeloggenerator.github.payload.Label;
import io.spring.githubchangeloggenerator.github.payload.User;
import io.spring.githubchangeloggenerator.github.service.GitHubService;
import io.spring.githubchangeloggenerator.github.service.Repository;

import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

/**
 * Generates a changelog markdown file which includes bug fixes, enhancements and
 * contributors for a given milestone.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 * @author Mahendra Bishnoi
 */
@Component
public class ChangelogGenerator {

	private static final Comparator<Issue> TITLE_COMPARATOR = Comparator.comparing(Issue::getTitle,
			String.CASE_INSENSITIVE_ORDER);

	private static final Pattern ghUserMentionPattern = Pattern.compile("(^|[^\\w`])(@[\\w-]+)");

	private final GitHubService service;

	private final Repository repository;

	private final MilestoneReference milestoneReference;

	private final IssueSort sort;

	private final Set<String> excludeLabels;

	private final Set<PortedIssue> portedIssues;

	private final Set<String> excludeContributors;

	private final String contributorsTitle;

	private final ChangelogSections sections;

	private final List<ExternalLink> externalLinks;

	public ChangelogGenerator(GitHubService service, ApplicationProperties properties) {
		this.service = service;
		this.repository = properties.getRepository();
		this.milestoneReference = properties.getMilestoneReference();
		this.sort = properties.getIssues().getSort();
		this.excludeLabels = properties.getIssues().getExcludes().getLabels();
		this.excludeContributors = properties.getContributors().getExclude().getNames();
		this.contributorsTitle = properties.getContributors().getTitle();
		this.sections = new ChangelogSections(properties);
		this.portedIssues = properties.getIssues().getPorts();
		this.externalLinks = properties.getExternalLinks();
	}

	/**
	 * Generates a file at the given path which includes bug fixes, enhancements and
	 * contributors for the given milestone.
	 * @param milestone the milestone to generate the changelog for
	 * @param path the path to the file
	 * @throws IOException if writing to file failed
	 */
	public void generate(String milestone, String path) throws IOException {
		int milestoneNumber = resolveMilestoneReference(milestone);
		List<Issue> issues = getIssues(milestoneNumber);
		String content = generateContent(issues);
		writeContentToFile(content, path);
	}

	private List<Issue> getIssues(int milestoneNumber) {
		List<Issue> issues = new ArrayList<>(this.service.getIssuesForMilestone(milestoneNumber, this.repository));
		issues.removeIf(this::isExcluded);
		return issues;
	}

	private boolean isExcluded(Issue issue) {
		return issue.getLabels().stream().anyMatch(this::isExcluded);
	}

	private boolean isExcluded(Label label) {
		return this.excludeLabels.contains(label.getName());
	}

	private int resolveMilestoneReference(String milestone) {
		switch (this.milestoneReference) {
		case TITLE:
			return this.service.getMilestoneNumber(milestone, this.repository);
		case ID:
			return Integer.parseInt(milestone);
		default:
			throw new IllegalStateException("Unsupported milestone reference value " + this.milestoneReference);
		}
	}

	private String generateContent(List<Issue> issues) {
		StringBuilder content = new StringBuilder();
		addSectionContent(content, this.sections.collate(issues));
		Set<User> contributors = getContributors(issues);
		if (!contributors.isEmpty()) {
			addContributorsContent(content, contributors);
		}
		if (!this.externalLinks.isEmpty()) {
			addExternalLinksContent(content, this.externalLinks);
		}
		return content.toString();
	}

	private void addSectionContent(StringBuilder content, Map<ChangelogSection, List<Issue>> sectionIssues) {
		sectionIssues.forEach((section, issues) -> {
			sort(section.getSort(), issues);
			content.append((content.length() != 0) ? String.format("%n") : "");
			content.append("## ").append(section).append(String.format("%n%n"));
			issues.stream().map(this::getFormattedIssue).forEach(content::append);
		});
	}

	private void sort(IssueSort sort, List<Issue> issues) {
		sort = (sort != null) ? sort : this.sort;
		if (sort == IssueSort.TITLE) {
			issues.sort(TITLE_COMPARATOR);
		}
	}

	private String getFormattedIssue(Issue issue) {
		String title = issue.getTitle();
		title = ghUserMentionPattern.matcher(title).replaceAll("$1`$2`");
		return String.format("- %s %s%n", title, getLinkToIssue(issue));
	}

	private String getLinkToIssue(Issue issue) {
		return "[#" + issue.getNumber() + "]" + "(" + issue.getUrl() + ")";
	}

	private Set<User> getContributors(List<Issue> issues) {
		if (this.excludeContributors.contains("*")) {
			return Collections.emptySet();
		}
		return issues.stream().map(this::getPortedReferenceIssue).filter((issue) -> issue.getPullRequest() != null)
				.map(Issue::getUser).filter(this::isIncludedContributor).collect(Collectors.toSet());
	}

	private Issue getPortedReferenceIssue(Issue issue) {
		for (PortedIssue portedIssue : this.portedIssues) {
			List<String> labelNames = issue.getLabels().stream().map(Label::getName).collect(Collectors.toList());
			if (labelNames.contains(portedIssue.getLabel())) {
				Pattern pattern = portedIssue.getBodyExpression();
				Matcher matcher = pattern.matcher(issue.getBody());
				if (matcher.matches()) {
					String issueNumber = matcher.group(1);
					Issue referencedIssue = this.service.getIssue(issueNumber, this.repository);
					if (referencedIssue != null) {
						return getPortedReferenceIssue(referencedIssue);
					}
				}
			}
		}
		return issue;
	}

	private boolean isIncludedContributor(User user) {
		return !this.excludeContributors.contains(user.getName());
	}

	private void addContributorsContent(StringBuilder content, Set<User> contributors) {
		content.append(String.format("%n## "));
		content.append((this.contributorsTitle != null) ? this.contributorsTitle : ":heart: Contributors");
		content.append(String.format("%n%nWe'd like to thank all the contributors who worked on this release!%n%n"));
		contributors.stream().map(this::formatContributors).forEach(content::append);
	}

	private String formatContributors(User c) {
		return String.format("- [@%s](%s)%n", c.getName(), c.getUrl());
	}

	private void addExternalLinksContent(StringBuilder content, List<ExternalLink> externalLinks) {
		content.append(String.format("%n## "));
		content.append(String.format("External Links%n%n"));
		externalLinks.stream().map(this::formatExternalLinks).forEach(content::append);
	}

	private String formatExternalLinks(ExternalLink externalLink) {
		return String.format("- [%s](%s)%n", externalLink.getName(), externalLink.getLocation());
	}

	private void writeContentToFile(String content, String path) throws IOException {
		FileCopyUtils.copy(content, new FileWriter(new File(path)));
	}

}
