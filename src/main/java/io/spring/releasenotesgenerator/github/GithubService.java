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

package io.spring.releasenotesgenerator.github;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Central class for interacting with GitHub's REST API.
 *
 * @author Madhura Bhave
 */
public class GithubService {

	private static final String ROOT_URI = "https://api.github.com/";

	private final RestTemplate restTemplate;

	private final LinkParser linkParser;

	public GithubService(String username, String password,
			RestTemplateBuilder restTemplateBuilder, LinkParser linkParser) {
		if (StringUtils.hasLength(username)) {
			restTemplateBuilder = restTemplateBuilder.basicAuthentication(username,
					password);
		}
		this.restTemplate = restTemplateBuilder.build();
		this.linkParser = linkParser;
	}

	public List<Issue> getIssuesForMilestone(int milestone, String org, String repo) {
		String url = ROOT_URI + "repos/" + org + "/" + repo + "/issues?milestone="
				+ milestone + "&state=closed";
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
