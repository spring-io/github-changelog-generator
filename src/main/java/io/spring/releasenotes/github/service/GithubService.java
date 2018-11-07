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

package io.spring.releasenotes.github.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.spring.releasenotes.github.payload.Issue;
import io.spring.releasenotes.properties.ApplicationProperties;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Central class for interacting with GitHub's REST API.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
@Component
public class GithubService {

	private static final Pattern LINK_PATTERN = Pattern.compile("<(.+)>; rel=\"(.+)\"");

	private static final String URI = "https://api.github.com/repos/{organization}/{repository}/"
			+ "issues?milestone={milestone}&state=closed";

	private final RestTemplate restTemplate;

	public GithubService(RestTemplateBuilder builder, ApplicationProperties properties) {
		String username = properties.getGithub().getUsername();
		String password = properties.getGithub().getPassword();
		if (StringUtils.hasLength(username)) {
			builder = builder.basicAuthentication(username, password);
		}
		this.restTemplate = builder.build();
	}

	public List<Issue> getIssuesForMilestone(int milestone, String organization,
			String repository) {
		List<Issue> issues = new ArrayList<>();
		Page<Issue> page = getPage(URI, organization, repository, milestone);
		while (page != null) {
			issues.addAll(page.getContent());
			page = page.getNextPage();
		}
		return issues;
	}

	private Page<Issue> getPage(String url, Object... uriVariables) {
		if (!StringUtils.hasText(url)) {
			return null;
		}
		ResponseEntity<Issue[]> response = this.restTemplate.getForEntity(url,
				Issue[].class, uriVariables);
		return new Page<Issue>(Arrays.asList(response.getBody()),
				() -> getPage(getNextUrl(response.getHeaders())));
	}

	private String getNextUrl(HttpHeaders headers) {
		String links = headers.getFirst("Link");
		for (String link : StringUtils.commaDelimitedListToStringArray(links)) {
			Matcher matcher = LINK_PATTERN.matcher(link.trim());
			if (matcher.matches() && "next".equals(matcher.group(2))) {
				return matcher.group(1);
			}
		}
		return null;
	}

}
