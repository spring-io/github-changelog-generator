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

package io.spring.releasenotesgenerator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.spring.releasenotesgenerator.github.GithubProperties;
import io.spring.releasenotesgenerator.github.GithubService;
import io.spring.releasenotesgenerator.github.Issue;
import io.spring.releasenotesgenerator.github.User;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Generates a file which includes bug fixes, enhancements and contributors for a given
 * milestone.
 *
 * @author Madhura Bhave
 */
@Component
public class ChangelogGenerator {

	private static final String THANK_YOU = "## :heart: Contributors\n\nWe’d like to thank all the contributors who worked on our current release!";

	private final GithubService service;

	private final GithubProperties githubProperties;

	public ChangelogGenerator(GithubService service, GithubProperties githubProperties) {
		this.service = service;
		this.githubProperties = githubProperties;
	}

	/**
	 * Generates a file at the given path which includes bug fixes, enhancements and
	 * contributors for the given milestone.
	 * @param milestone the milestone to generate the release notes for
	 * @param path the path to the file
	 * @throws IOException if writing to file failed
	 */
	public void generate(int milestone, String path) throws IOException {
		List<Issue> issues = this.service.getIssuesForMilestone(milestone,
				this.githubProperties.getOrganization(), this.githubProperties.getName());
		String output = generateContent(issues);
		writeContentToFile(path, output);
	}

	private void writeContentToFile(String path, String output) throws IOException {
		File file = new File(path);
		InputStream stream = new ByteArrayInputStream(output.getBytes());
		Files.copy(stream, file.toPath());
	}

	private String generateContent(List<Issue> issues) {
		String users = getContributors(issues).stream().map(this::formatContributors)
				.collect(Collectors.joining("\n"));
		LinkedHashSet<String> outputs = sortIssues(issues).entrySet().stream()
				.map((entry) -> getOutput(entry.getKey(), entry.getValue()))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		String issuesOutput = outputs.stream().collect(Collectors.joining("\n"));
		return issuesOutput + "\n" + getContributorSummary(users);
	}

	private String getContributorSummary(String users) {
		if (StringUtils.hasText(users)) {
			return THANK_YOU + "\n\n" + users;
		}
		return "";
	}

	private Set<User> getContributors(List<Issue> issues) {
		return issues.stream().filter((issue) -> issue.getPullRequest() != null)
				.map(Issue::getUser).collect(Collectors.toSet());
	}

	private Map<Issue.Type, List<Issue>> sortIssues(List<Issue> issues) {
		return issues.stream().filter((i) -> i.getType() != null)
				.sorted(Comparator.comparing(Issue::getType))
				.collect(Collectors.groupingBy(Issue::getType, LinkedHashMap::new,
						Collectors.toList()));
	}

	private String formatContributors(User c) {
		return "- [@" + c.getName() + "]" + "(" + c.getUrl() + ")";
	}

	private String getOutput(Issue.Type key, List<Issue> issues) {
		String output = "## " + key.getEmoji() + " " + key.getDescription() + "\n\n";
		for (Issue issue : issues) {
			output = output + getFormattedIssue(issue);
		}
		return output;
	}

	private String getFormattedIssue(Issue issue) {
		return "- " + issue.getTitle() + " " + getLinkToIssue(issue) + "\n";
	}

	private String getLinkToIssue(Issue issue) {
		return "[#" + issue.getNumber() + "]" + "(" + issue.getUrl() + ")";
	}

}
