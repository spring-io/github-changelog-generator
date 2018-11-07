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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/**
 * Generates a file which includes bug fixes, enhancements and contributors for a given
 * milestone.
 *
 * @author Madhura Bhave
 */
@Component
public class ChangelogGenerator {

	private static final String THANK_YOU = "## :heart: Contributors\n\n"
			+ "We'd like to thank all the contributors who worked on our current release!";

	private final GithubService service;

	private final GithubProperties properties;

	public ChangelogGenerator(GithubService service, GithubProperties properties) {
		this.service = service;
		this.properties = properties;
	}

	/**
	 * Generates a file at the given path which includes bug fixes, enhancements and
	 * contributors for the given milestone.
	 * @param path the path to the file
	 * @param milestone the milestone to generate the release notes for
	 * @throws IOException if writing to file failed
	 */
	public void generate(String path, int milestone) throws IOException {
		List<Issue> issues = this.service.getIssuesForMilestone(milestone,
				this.properties.getOrganization(), this.properties.getName());
		String content = generateContent(issues);
		writeContentToFile(content, path);
	}

	private String generateContent(List<Issue> issues) {
		String contributors = getContributors(issues).stream()
				.map(this::formatContributors).collect(Collectors.joining("\n"));
		Set<String> output = sortIssues(issues).entrySet().stream()
				.map((entry) -> getOutput(entry.getKey(), entry.getValue()))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		String issuesOutput = output.stream().collect(Collectors.joining("\n"));
		return issuesOutput + "\n" + getContributorSummary(contributors);
	}

	private Set<User> getContributors(List<Issue> issues) {
		return issues.stream().filter((issue) -> issue.getPullRequest() != null)
				.map(Issue::getUser).collect(Collectors.toSet());
	}

	private String formatContributors(User c) {
		return "- [@" + c.getName() + "]" + "(" + c.getUrl() + ")";
	}

	private Map<Issue.Type, List<Issue>> sortIssues(List<Issue> issues) {
		return issues.stream().filter((issue) -> issue.getType() != null)
				.sorted(Comparator.comparing(Issue::getType))
				.collect(Collectors.groupingBy(Issue::getType, LinkedHashMap::new,
						Collectors.toList()));
	}

	private String getOutput(Issue.Type key, List<Issue> issues) {
		String output = "## " + key.getEmoji() + " " + key.getDescription() + "\n\n";
		for (Issue issue : issues) {
			output = output + getFormattedIssue(issue);
		}
		return output;
	}

	private String getContributorSummary(String users) {
		if (StringUtils.hasText(users)) {
			return THANK_YOU + "\n\n" + users;
		}
		return "";
	}

	private String getFormattedIssue(Issue issue) {
		return "- " + issue.getTitle() + " " + getLinkToIssue(issue) + "\n";
	}

	private String getLinkToIssue(Issue issue) {
		return "[#" + issue.getNumber() + "]" + "(" + issue.getUrl() + ")";
	}

	private void writeContentToFile(String content, String path) throws IOException {
		FileCopyUtils.copy(content, new FileWriter(new File(path)));
	}

}
