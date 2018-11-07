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

package io.spring.releasenotes.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.spring.releasenotes.github.payload.Issue;
import io.spring.releasenotes.github.payload.User;
import io.spring.releasenotes.github.service.GithubService;
import io.spring.releasenotes.properties.ApplicationProperties;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.FileCopyUtils;

/**
 * Generates a file which includes bug fixes, enhancements and contributors for a given
 * milestone.
 *
 * @author Madhura Bhave
 */
@Configuration
public class ReleaseNotesGenerator {

	private static final String THANK_YOU = "## :heart: Contributors\n\n"
			+ "We'd like to thank all the contributors who worked on this release!";

	private final GithubService service;

	private final String organization;

	private final String repository;

	private final ReleaseNotesSections sections;

	public ReleaseNotesGenerator(GithubService service,
			ApplicationProperties properties) {
		this.service = service;
		this.organization = properties.getGithub().getOrganization();
		this.repository = properties.getGithub().getRepository();
		this.sections = new ReleaseNotesSections();
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
				this.organization, this.repository);
		String content = generateContent(issues);
		writeContentToFile(content, path);
	}

	private String generateContent(List<Issue> issues) {
		StringBuilder content = new StringBuilder();
		addSectionContent(content, this.sections.collate(issues));
		Set<User> contributors = getContributors(issues);
		if (!contributors.isEmpty()) {
			addContributorsContent(content, contributors);
		}
		return content.toString();
	}

	private StringBuilder addSectionContent(StringBuilder content,
			Map<ReleaseNotesSection, List<Issue>> sectionIssues) {
		sectionIssues.forEach((section, issues) -> {
			content.append((content.length() != 0) ? "\n" : "");
			content.append("## " + section + "\n\n");
			issues.stream().map(this::getFormattedIssue).forEach(content::append);
		});
		return content;
	}

	private String getFormattedIssue(Issue issue) {
		return "- " + issue.getTitle() + " " + getLinkToIssue(issue) + "\n";
	}

	private String getLinkToIssue(Issue issue) {
		return "[#" + issue.getNumber() + "]" + "(" + issue.getUrl() + ")";
	}

	private Set<User> getContributors(List<Issue> issues) {
		return issues.stream().filter((issue) -> issue.getPullRequest() != null)
				.map(Issue::getUser).collect(Collectors.toSet());
	}

	private void addContributorsContent(StringBuilder content, Set<User> contributors) {
		content.append("\n" + THANK_YOU + "\n\n");
		contributors.stream().map(this::formatContributors).forEach(content::append);
	}

	private String formatContributors(User c) {
		return "- [@" + c.getName() + "]" + "(" + c.getUrl() + ")\n";
	}

	private void writeContentToFile(String content, String path) throws IOException {
		FileCopyUtils.copy(content, new FileWriter(new File(path)));
	}

}
