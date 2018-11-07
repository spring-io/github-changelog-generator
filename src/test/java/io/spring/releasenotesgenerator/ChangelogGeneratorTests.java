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
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import io.spring.releasenotesgenerator.github.GithubProperties;
import io.spring.releasenotesgenerator.github.GithubService;
import io.spring.releasenotesgenerator.github.Issue;
import io.spring.releasenotesgenerator.github.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link ChangelogGenerator}.
 *
 * @author Madhura Bhave
 */
public class ChangelogGeneratorTests {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private ChangelogGenerator generator;

	private GithubService service;

	@Before
	public void setup() {
		GithubProperties properties = new GithubProperties();
		properties.setName("name");
		properties.setOrganization("org");
		this.service = mock(GithubService.class);
		this.generator = new ChangelogGenerator(this.service, properties);
	}

	@Test
	public void generateWhenNoPullRequests() throws Exception {
		List<Issue> issues = new ArrayList<>();
		issues.add(MockIssues.getBug("Bug 1", "1", "bug-1-url"));
		issues.add(MockIssues.getEnhancement("Enhancement 1", "2", "enhancement-1-url"));
		issues.add(MockIssues.getEnhancement("Enhancement 2", "4", "enhancement-2-url"));
		issues.add(MockIssues.getBug("Bug 3", "3", "bug-3-url"));
		given(this.service.getIssuesForMilestone(23, "org", "name")).willReturn(issues);
		File file = new File(this.temporaryFolder.getRoot().getPath() + "foo");
		this.generator.generate(file.getPath(), 23);
		assertOutputisCorrect(file, "output-with-no-prs");
	}

	@Test
	public void generateWhenNoEnhancements() throws Exception {
		User contributor1 = getUser("contributor1", "contributor1-github-url");
		List<Issue> issues = new ArrayList<>();
		issues.add(MockIssues.getPullRequest("Bug 1", "1", "type: bug", "bug-1-url",
				contributor1));
		issues.add(MockIssues.getBug("Bug 3", "3", "bug-3-url"));
		given(this.service.getIssuesForMilestone(23, "org", "name")).willReturn(issues);
		File file = new File(this.temporaryFolder.getRoot().getPath() + "foo");
		this.generator.generate(file.getPath(), 23);
		assertOutputisCorrect(file, "output-with-no-enhancements");
	}

	@Test
	public void generateWhenNoBugFixes() throws Exception {
		User contributor1 = getUser("contributor1", "contributor1-github-url");
		User contributor2 = getUser("contributor2", "contributor2-github-url");
		List<Issue> issues = new ArrayList<>();
		issues.add(MockIssues.getEnhancement("Enhancement 1", "2", "enhancement-1-url"));
		issues.add(MockIssues.getEnhancement("Enhancement 2", "4", "enhancement-2-url"));
		issues.add(MockIssues.getPullRequest("Enhancement 3", "5", "type: enhancement",
				"enhancement-5-url", contributor1));
		issues.add(MockIssues.getPullRequest("Enhancement 4", "6", "type: enhancement",
				"enhancement-6-url", contributor2));
		given(this.service.getIssuesForMilestone(23, "org", "name")).willReturn(issues);
		File file = new File(this.temporaryFolder.getRoot().getPath() + "foo");
		this.generator.generate(file.getPath(), 23);
		assertOutputisCorrect(file, "output-with-no-bugs");
	}

	private User getUser(String contributor12, String url) {
		return new User(contributor12, url);
	}

	@Test
	public void generateWhenDuplicateContributor() throws Exception {
		User contributor1 = getUser("contributor1", "contributor1-github-url");
		List<Issue> issues = new ArrayList<>();
		issues.add(MockIssues.getEnhancement("Enhancement 1", "2", "enhancement-1-url"));
		issues.add(MockIssues.getEnhancement("Enhancement 2", "4", "enhancement-2-url"));
		issues.add(MockIssues.getPullRequest("Enhancement 3", "5", "type: enhancement",
				"enhancement-5-url", contributor1));
		issues.add(MockIssues.getPullRequest("Enhancement 4", "6", "type: enhancement",
				"enhancement-6-url", contributor1));
		given(this.service.getIssuesForMilestone(23, "org", "name")).willReturn(issues);
		File file = new File(this.temporaryFolder.getRoot().getPath() + "foo");
		this.generator.generate(file.getPath(), 23);
		assertOutputisCorrect(file, "output-with-duplicate-contributors");
	}

	private void assertOutputisCorrect(File file, String path) throws IOException {
		byte[] bytes = Files.readAllBytes(file.toPath());
		String output = new String(bytes);
		byte[] expectedBytes = Files
				.readAllBytes(getClassPathResource(path).getFile().toPath());
		String expectedOutput = new String(expectedBytes);
		assertThat(output).isEqualTo(expectedOutput);
	}

	private ClassPathResource getClassPathResource(String path) {
		return new ClassPathResource(path, getClass());
	}

}
