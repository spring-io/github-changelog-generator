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

import java.util.List;

import io.spring.releasenotes.github.payload.Issue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.test.web.client.response.DefaultResponseCreator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests for {@link GithubService}.
 *
 * @author Madhura Bhave
 */
@RunWith(SpringRunner.class)
@RestClientTest({ GithubService.class })
public class GithubServiceTests {

	/**
	 *
	 */
	private static final String URL = "https://api.github.com/repos/org/repo/issues?milestone=";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private GithubService service;

	@Test
	public void getIssuesWhenNoIssues() {
		expectGet(URL + "23&state=closed").andRespond(withJsonOf("[]"));
		List<Issue> issues = this.service.getIssuesForMilestone(23, "org", "repo");
		assertThat(issues.size()).isEqualTo(0);
	}

	@Test
	public void getIssuesWhenSinglePageOfIssuesPresent() {
		expectGet(URL + "23&state=closed")
				.andRespond(withJsonFrom("closed-issues-for-milestone-page-1.json"));
		List<Issue> issues = this.service.getIssuesForMilestone(23, "org", "repo");
		assertThat(issues.size()).isEqualTo(30);
	}

	@Test
	public void getIssuesWhenMultiplePagesOfIssuesPresent() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "<page-two>; rel=\"next\"");
		expectGet(URL + "23&state=closed").andRespond(
				withJsonFrom("closed-issues-for-milestone-page-1.json").headers(headers));
		expectGet("/page-two")
				.andRespond(withJsonFrom("closed-issues-for-milestone-page-2.json"));
		List<Issue> issues = this.service.getIssuesForMilestone(23, "org", "repo");
		assertThat(issues.size()).isEqualTo(60);
	}

	private ResponseActions expectGet(String expectedUri) {
		return this.server.expect(requestTo(expectedUri))
				.andExpect(method(HttpMethod.GET));
	}

	private DefaultResponseCreator withJsonFrom(String path) {
		return withSuccess(getClassPathResource(path), MediaType.APPLICATION_JSON);
	}

	private DefaultResponseCreator withJsonOf(String json) {
		return withSuccess(json, MediaType.APPLICATION_JSON);
	}

	private ClassPathResource getClassPathResource(String path) {
		return new ClassPathResource(path, getClass());
	}

}
