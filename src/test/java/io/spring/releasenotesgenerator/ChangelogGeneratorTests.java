package io.spring.releasenotesgenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import io.spring.releasenotesgenerator.github.GithubService;
import io.spring.releasenotesgenerator.github.Issue;
import io.spring.releasenotesgenerator.github.RepositoryProperties;
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
		RepositoryProperties properties = new RepositoryProperties();
		properties.setName("name");
		properties.setOrganization("org");
		service = mock(GithubService.class);
		this.generator = new ChangelogGenerator(service, properties);
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
		this.generator.generate(23, file.getPath());
		assertOutputisCorrect(file, "output-with-no-prs");
	}

	@Test
	public void generateWhenNoEnhancements() throws Exception {
		List<Issue> issues = new ArrayList<>();
		issues.add(MockIssues.getBug("Bug 1", "1", "bug-1-url"));
		issues.add(MockIssues.getBug("Bug 3", "3", "bug-3-url"));
		given(this.service.getIssuesForMilestone(23, "org", "name")).willReturn(issues);
		File file = new File(this.temporaryFolder.getRoot().getPath() + "foo");
		this.generator.generate(23, file.getPath());
		assertOutputisCorrect(file, "output-with-no-enhancements");
	}

	@Test
	public void generateWhenNoBugFixes() throws Exception {
		List<Issue> issues = new ArrayList<>();
		issues.add(MockIssues.getEnhancement("Enhancement 1", "2", "enhancement-1-url"));
		issues.add(MockIssues.getEnhancement("Enhancement 2", "4", "enhancement-2-url"));
		given(this.service.getIssuesForMilestone(23, "org", "name")).willReturn(issues);
		File file = new File(this.temporaryFolder.getRoot().getPath() + "foo");
		this.generator.generate(23, file.getPath());
		assertOutputisCorrect(file, "output-with-no-bugs");
	}

	@Test
	public void generateWhenFileExists() throws Exception {

	}

	@Test
	public void generateWhenDuplicateContributor() throws Exception {

	}

	private void assertOutputisCorrect(File file, String path) throws IOException {
		byte[] bytes = Files.readAllBytes(file.toPath());
		String output = new String(bytes);
		byte[] expectedBytes = Files.readAllBytes(getClassPathResource(path).getFile().toPath());
		String expectedOutput = new String(expectedBytes);
		assertThat(output).isEqualTo(expectedOutput);
	}

	private ClassPathResource getClassPathResource(String path) {
		return new ClassPathResource(path, getClass());
	}

}