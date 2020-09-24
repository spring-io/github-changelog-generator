/*
 * Copyright 2018-2020 the original author or authors.
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.spring.githubchangeloggenerator.ApplicationProperties.IssueExcludes;
import io.spring.githubchangeloggenerator.ApplicationProperties.Issues;
import io.spring.githubchangeloggenerator.github.payload.Issue;
import io.spring.githubchangeloggenerator.github.payload.Label;
import io.spring.githubchangeloggenerator.github.payload.PullRequest;
import io.spring.githubchangeloggenerator.github.payload.User;
import io.spring.githubchangeloggenerator.github.service.GitHubService;
import io.spring.githubchangeloggenerator.github.service.Repository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link ChangelogGenerator}.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
public class ChangelogGeneratorTests {

	private static final Repository REPO = Repository.of("org/name");

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private ChangelogGenerator generator;

	private GitHubService service;

	@Before
	public void setup() {
		this.service = mock(GitHubService.class);
		setupGenerator(MilestoneReference.ID);
	}

	@Test
	public void generateWhenNoPullRequests() throws Exception {
		List<Issue> issues = new ArrayList<>();
		issues.add(newIssue("Bug 1", "1", "bug-1-url", Type.BUG));
		issues.add(newIssue("Enhancement 1", "2", "enhancement-1-url", Type.ENHANCEMENT));
		issues.add(newIssue("Enhancement 2", "4", "enhancement-2-url", Type.ENHANCEMENT));
		issues.add(newIssue("Bug 3", "3", "bug-3-url", Type.BUG));
		given(this.service.getIssuesForMilestone(23, REPO)).willReturn(issues);
		File file = new File(this.temporaryFolder.getRoot().getPath() + "foo");
		this.generator.generate("23", file.getPath());
		assertThat(file).hasContent(from("output-with-no-prs"));
	}

	@Test
	public void generateWhenNoEnhancements() throws Exception {
		User contributor1 = createUser("contributor1", "contributor1-github-url");
		List<Issue> issues = new ArrayList<>();
		issues.add(newPullRequest("Bug 1", "1", Type.BUG, "bug-1-url", contributor1));
		issues.add(newIssue("Bug 3", "3", "bug-3-url", Type.BUG));
		given(this.service.getIssuesForMilestone(23, REPO)).willReturn(issues);
		File file = new File(this.temporaryFolder.getRoot().getPath() + "foo");
		this.generator.generate("23", file.getPath());
		assertThat(file).hasContent(from("output-with-no-enhancements"));
	}

	@Test
	public void generateWhenNoBugFixes() throws Exception {
		User contributor1 = createUser("contributor1", "contributor1-github-url");
		User contributor2 = createUser("contributor2", "contributor2-github-url");
		List<Issue> issues = new ArrayList<>();
		issues.add(newIssue("Enhancement 1", "2", "enhancement-1-url", Type.ENHANCEMENT));
		issues.add(newIssue("Enhancement 2", "4", "enhancement-2-url", Type.ENHANCEMENT));
		issues.add(newPullRequest("Enhancement 3", "5", Type.ENHANCEMENT, "enhancement-5-url", contributor1));
		issues.add(newPullRequest("Enhancement 4", "6", Type.ENHANCEMENT, "enhancement-6-url", contributor2));
		given(this.service.getIssuesForMilestone(23, REPO)).willReturn(issues);
		File file = new File(this.temporaryFolder.getRoot().getPath() + "foo");
		this.generator.generate("23", file.getPath());
		assertThat(file).hasContent(from("output-with-no-bugs"));
	}

	@Test
	public void generateWhenDuplicateContributor() throws Exception {
		User contributor1 = createUser("contributor1", "contributor1-github-url");
		List<Issue> issues = new ArrayList<>();
		issues.add(newIssue("Enhancement 1", "2", "enhancement-1-url", Type.ENHANCEMENT));
		issues.add(newIssue("Enhancement 2", "4", "enhancement-2-url", Type.ENHANCEMENT));
		issues.add(newPullRequest("Enhancement 3", "5", Type.ENHANCEMENT, "enhancement-5-url", contributor1));
		issues.add(newPullRequest("Enhancement 4", "6", Type.ENHANCEMENT, "enhancement-6-url", contributor1));
		given(this.service.getIssuesForMilestone(23, REPO)).willReturn(issues);
		File file = new File(this.temporaryFolder.getRoot().getPath() + "foo");
		this.generator.generate("23", file.getPath());
		assertThat(file).hasContent(from("output-with-duplicate-contributors"));
	}

	@Test
	public void generateWhenNoIgnoredLabels() throws Exception {
		User contributor1 = createUser("contributor1", "contributor1-github-url");
		List<Issue> issues = new ArrayList<>();
		issues.add(newIssue("Bug 1", "1", "bug-1-url", Type.BUG));
		issues.add(newIssue("Ignored bug 2", "2", "bug-2-url", Type.BUG, "wontfix"));
		issues.add(newPullRequest("PR 3", "3", Type.ENHANCEMENT, "pr-3-url", contributor1));
		issues.add(newPullRequest("PR 4", "4", Type.ENHANCEMENT, "pr-4-url", contributor1, "duplicate"));
		given(this.service.getIssuesForMilestone(23, REPO)).willReturn(issues);
		File file = new File(this.temporaryFolder.getRoot().getPath() + "foo");
		this.generator.generate("23", file.getPath());
		assertThat(file).hasContent(from("output-with-ignored-labels"));
	}

	@Test
	public void runWhenMilestoneIsNotNumberCallsGeneratorWithResolvedNumber() throws Exception {
		setupGenerator(MilestoneReference.TITLE);
		List<Issue> issues = new ArrayList<>();
		issues.add(newIssue("Bug 1", "1", "bug-1-url", Type.BUG));
		issues.add(newIssue("Enhancement 1", "2", "enhancement-1-url", Type.ENHANCEMENT));
		issues.add(newIssue("Enhancement 2", "4", "enhancement-2-url", Type.ENHANCEMENT));
		issues.add(newIssue("Bug 3", "3", "bug-3-url", Type.BUG));
		given(this.service.getMilestoneNumber("v2.3", REPO)).willReturn(23);
		given(this.service.getIssuesForMilestone(23, REPO)).willReturn(issues);
		File file = new File(this.temporaryFolder.getRoot().getPath() + "foo");
		this.generator.generate("v2.3", file.getPath());
		assertThat(file).hasContent(from("output-with-no-prs"));
	}

	@Test
	public void whenUserMentionIsInIssueTitleItIsEscaped() throws IOException {
		setupGenerator(MilestoneReference.TITLE);
		List<Issue> issues = new ArrayList<>();
		issues.add(newIssue("Bug 1 for @Value", "1", "bug-1-url", Type.BUG));
		given(this.service.getMilestoneNumber("v2.3", REPO)).willReturn(23);
		given(this.service.getIssuesForMilestone(23, REPO)).willReturn(issues);
		File file = new File(this.temporaryFolder.getRoot().getPath() + "foo");
		this.generator.generate("v2.3", file.getPath());
		assertThat(new String(Files.readAllBytes(file.toPath()))).contains("Bug 1 for `@Value`");
	}

	@Test
	public void whenEscapedUserMentionIsInIssueTitleItIsNotEscapedAgain() throws IOException {
		setupGenerator(MilestoneReference.TITLE);
		List<Issue> issues = new ArrayList<>();
		issues.add(newIssue("Bug 1 for `@Value`", "1", "bug-1-url", Type.BUG));
		given(this.service.getMilestoneNumber("v2.3", REPO)).willReturn(23);
		given(this.service.getIssuesForMilestone(23, REPO)).willReturn(issues);
		File file = new File(this.temporaryFolder.getRoot().getPath() + "foo");
		this.generator.generate("v2.3", file.getPath());
		assertThat(new String(Files.readAllBytes(file.toPath()))).contains("Bug 1 for `@Value`");
	}

	private void setupGenerator(MilestoneReference id) {
		Set<String> labels = new HashSet<>(Arrays.asList("duplicate", "wontfix"));
		ApplicationProperties properties = new ApplicationProperties(REPO, id, null,
				new Issues(new IssueExcludes(labels)));
		this.generator = new ChangelogGenerator(this.service, properties);
	}

	private User createUser(String contributor12, String s) {
		return new User(contributor12, s);
	}

	private String from(String path) throws IOException {
		return FileCopyUtils.copyToString(new InputStreamReader(getClass().getResourceAsStream(path)));
	}

	private Issue newIssue(String title, String number, String url, Type type) {
		return new Issue(number, title, null, type.getLabels(), url, null);
	}

	private Issue newIssue(String title, String number, String url, Type type, String... extraLabels) {
		List<Label> labels = new ArrayList<>(type.getLabels());
		Arrays.stream(extraLabels).map(Label::new).forEach(labels::add);
		return new Issue(number, title, null, labels, url, null);
	}

	public Issue newPullRequest(String title, String number, Type type, String url, User user) {
		return new Issue(number, title, user, type.getLabels(), url, new PullRequest("https://example.com"));
	}

	public Issue newPullRequest(String title, String number, Type type, String url, User user, String... extraLabels) {
		List<Label> labels = new ArrayList<>(type.getLabels());
		Arrays.stream(extraLabels).map(Label::new).forEach(labels::add);
		return new Issue(number, title, user, labels, url, new PullRequest("https://example.com"));
	}

	private enum Type {

		BUG("type: bug"),

		ENHANCEMENT("type: enhancement");

		private List<Label> labels;

		Type(String label) {
			this.labels = Collections.singletonList(new Label(label));
		}

		public List<Label> getLabels() {
			return this.labels;
		}

	}

}
