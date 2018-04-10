package io.spring.releasenotesgenerator.github;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Central class for interacting with GitHub's REST API.
 *
 * @author Madhura Bhave
 */
@Component
public class GithubService {

	private final RestTemplate restTemplate;

	private final LinkParser linkParser;

	private static final String ROOT_URI = "https://api.github.com/";

	public GithubService(RestTemplateBuilder restTemplateBuilder,
			LinkParser linkParser) {
		this.restTemplate = restTemplateBuilder.build();
		this.linkParser = linkParser;
	}

	public List<Issue> getIssuesForMilestone(int milestone, String org, String repo) {
		String url = ROOT_URI + "repos/" + org
				+ "/" + repo + "/issues?milestone=" + milestone + "&state=closed";
		List<Issue> issues = new ArrayList<>();
		Page<Issue> page = getPage(url, Issue[].class);
		while (page != null) {
			issues.addAll(page.getContent());
			page = page.next();
		}
		return issues;
	}

	private <T> Page<T> getPage(String url, Class<T[]> type) {
		if (!StringUtils.hasText(url)) {
			return null;
		}
		ResponseEntity<T[]> response = this.restTemplate.getForEntity(url, type);
		return new StandardPage<>(Arrays.asList(response.getBody()),
				() -> getPage(getNextUrl(response), type));
	}

	private String getNextUrl(ResponseEntity<?> response) {
		return this.linkParser.parse(response.getHeaders().getFirst("Link")).get("next");
	}

}
