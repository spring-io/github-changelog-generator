package io.spring.releasenotesgenerator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.spring.releasenotesgenerator.github.GithubService;
import io.spring.releasenotesgenerator.github.Issue;
import io.spring.releasenotesgenerator.github.RepositoryProperties;
import io.spring.releasenotesgenerator.github.User;

import org.springframework.stereotype.Component;

/**
 * Generates a file which includes bug fixes, enhancements and contributors for a
 * given milestone.
 *
 * @author Madhura Bhave
 */
@Component
public class ChangelogGenerator {

	private final GithubService service;

	private final RepositoryProperties repositoryProperties;

	public ChangelogGenerator(GithubService service, RepositoryProperties repositoryProperties) {
		this.service = service;
		this.repositoryProperties = repositoryProperties;
	}

	/**
	 * Generates a file at the given path which includes bug fixes, enhancements and contributors for the
	 * given milestone.
	 * @param milestone the milestone to generate the release notes for
	 * @param path the path to the file
	 * @throws IOException
	 */
	public void generate(int milestone, String path) throws IOException {
		List<Issue> issues = this.service.getIssuesForMilestone(milestone, this.repositoryProperties.getOrganization(),
				this.repositoryProperties.getName());
		String output = generateContent(issues);
		writeContentToFile(path, output);
	}

	private void writeContentToFile(String path, String output) throws IOException {
		File file = new File(path);
		InputStream stream = new ByteArrayInputStream(output.getBytes());
		Files.copy(stream, file.toPath());
	}

	private String generateContent(List<Issue> issues) {
		String users = getContributors(issues).stream()
				.map(this::formatContributors).collect(Collectors.joining("\n"));
		List<String> outputs = sortIssues(issues)
				.entrySet().stream()
				.map(e -> getOutput(e.getKey(), e.getValue())).collect(Collectors.toList());
		String issuesOutput = outputs.stream().collect(Collectors.joining("\n"));
		return users + "\n" + issuesOutput;
	}

	private Set<User> getContributors(List<Issue> issues) {
		return issues.stream().filter(i -> i.getPullRequest() != null)
				.map(Issue::getUser).distinct().collect(Collectors.toSet());
	}

	public Map<Issue.Type, List<Issue>> sortIssues(List<Issue> issues) {
		return issues.stream()
				.filter(this::isBugOrEnhancement)
				.collect(Collectors.groupingBy(Issue::getType));
	}

	private boolean isBugOrEnhancement(Issue issue) {
		return Issue.Type.BUG.equals(issue.getType()) ||
					Issue.Type.ENHANCEMENT.equals(issue.getType());
	}

	private String formatContributors(User c) {
		return "* [@" + c.getName() + "]" + "(" + c.getUrl() + ")";
	}

	private String getOutput(Issue.Type key, List<Issue> issues) {
		String output = "# " + key.getEmoji() + key.getDescription() + "\n";
		for (Issue issue : issues) {
			output = output + getFormattedIssue(issue);
		}
		return output;
	}

	private String getFormattedIssue(Issue issue) {
		return "\n* " + issue.getTitle() + " " + getLinkToIssue(issue) + "\n";
	}

	private String getLinkToIssue(Issue issue) {
		return "[#" + issue.getNumber() + "]" + "(" + issue.getUrl() + ")";
	}

}
