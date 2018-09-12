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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Issue}.
 *
 * @author Madhura Bhave
 */
public class IssueTests {

	private ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private String issueJson = "{\n"
			+ "    \"url\": \"https://api.github.com/repos/spring-projects/spring-boot/issues/12730\",\n"
			+ "    \"repository_url\": \"https://api.github.com/repos/spring-projects/spring-boot\",\n"
			+ "    \"labels_url\": \"https://api.github.com/repos/spring-projects/spring-boot/issues/12730/labels{/name}\",\n"
			+ "    \"comments_url\": \"https://api.github.com/repos/spring-projects/spring-boot/issues/12730/comments\",\n"
			+ "    \"events_url\": \"https://api.github.com/repos/spring-projects/spring-boot/issues/12730/events\",\n"
			+ "    \"html_url\": \"https://github.com/spring-projects/spring-boot/issues/12730\",\n"
			+ "    \"id\": 310734371,\n" + "    \"number\": 12730,\n"
			+ "    \"title\": \"Upgrade to Groovy 2.4.15\",\n" + "    \"user\": {\n"
			+ "      \"login\": \"wilkinsona\",\n" + "      \"id\": 914682,\n"
			+ "      \"avatar_url\": \"https://avatars3.githubusercontent.com/u/914682?v=4\",\n"
			+ "      \"gravatar_id\": \"\",\n"
			+ "      \"url\": \"https://api.github.com/users/wilkinsona\",\n"
			+ "      \"html_url\": \"https://github.com/wilkinsona\",\n"
			+ "      \"followers_url\": \"https://api.github.com/users/wilkinsona/followers\",\n"
			+ "      \"following_url\": \"https://api.github.com/users/wilkinsona/following{/other_user}\",\n"
			+ "      \"gists_url\": \"https://api.github.com/users/wilkinsona/gists{/gist_id}\",\n"
			+ "      \"starred_url\": \"https://api.github.com/users/wilkinsona/starred{/owner}{/repo}\",\n"
			+ "      \"subscriptions_url\": \"https://api.github.com/users/wilkinsona/subscriptions\",\n"
			+ "      \"organizations_url\": \"https://api.github.com/users/wilkinsona/orgs\",\n"
			+ "      \"repos_url\": \"https://api.github.com/users/wilkinsona/repos\",\n"
			+ "      \"events_url\": \"https://api.github.com/users/wilkinsona/events{/privacy}\",\n"
			+ "      \"received_events_url\": \"https://api.github.com/users/wilkinsona/received_events\",\n"
			+ "      \"type\": \"User\",\n" + "      \"site_admin\": false\n" + "    },\n"
			+ "    \"labels\": [\n" + "      {\n" + "        \"id\": 568657130,\n"
			+ "        \"url\": \"https://api.github.com/repos/spring-projects/spring-boot/labels/type:%20regression\",\n"
			+ "        \"name\": \"type: regression\",\n"
			+ "        \"color\": \"d4c5f9\",\n" + "        \"default\": false\n"
			+ "      }\n" + "    ],\n" + "    \"state\": \"closed\",\n"
			+ "    \"locked\": false,\n" + "    \"assignee\": null,\n"
			+ "    \"assignees\": [\n" + "\n" + "    ],\n" + "    \"milestone\": {\n"
			+ "      \"url\": \"https://api.github.com/repos/spring-projects/spring-boot/milestones/97\",\n"
			+ "      \"html_url\": \"https://github.com/spring-projects/spring-boot/milestone/97\",\n"
			+ "      \"labels_url\": \"https://api.github.com/repos/spring-projects/spring-boot/milestones/97/labels\",\n"
			+ "      \"id\": 3072279,\n" + "      \"number\": 97,\n"
			+ "      \"title\": \"1.5.11\",\n" + "      \"description\": \"\",\n"
			+ "      \"creator\": {\n" + "        \"login\": \"wilkinsona\",\n"
			+ "        \"id\": 914682,\n"
			+ "        \"avatar_url\": \"https://avatars3.githubusercontent.com/u/914682?v=4\",\n"
			+ "        \"gravatar_id\": \"\",\n"
			+ "        \"url\": \"https://api.github.com/users/wilkinsona\",\n"
			+ "        \"html_url\": \"https://github.com/wilkinsona\",\n"
			+ "        \"followers_url\": \"https://api.github.com/users/wilkinsona/followers\",\n"
			+ "        \"following_url\": \"https://api.github.com/users/wilkinsona/following{/other_user}\",\n"
			+ "        \"gists_url\": \"https://api.github.com/users/wilkinsona/gists{/gist_id}\",\n"
			+ "        \"starred_url\": \"https://api.github.com/users/wilkinsona/starred{/owner}{/repo}\",\n"
			+ "        \"subscriptions_url\": \"https://api.github.com/users/wilkinsona/subscriptions\",\n"
			+ "        \"organizations_url\": \"https://api.github.com/users/wilkinsona/orgs\",\n"
			+ "        \"repos_url\": \"https://api.github.com/users/wilkinsona/repos\",\n"
			+ "        \"events_url\": \"https://api.github.com/users/wilkinsona/events{/privacy}\",\n"
			+ "        \"received_events_url\": \"https://api.github.com/users/wilkinsona/received_events\",\n"
			+ "        \"type\": \"User\",\n" + "        \"site_admin\": false\n"
			+ "      },\n" + "      \"open_issues\": 0,\n"
			+ "      \"closed_issues\": 71,\n" + "      \"state\": \"closed\",\n"
			+ "      \"created_at\": \"2018-01-29T17:00:32Z\",\n"
			+ "      \"updated_at\": \"2018-04-05T09:30:36Z\",\n"
			+ "      \"due_on\": \"2018-04-05T07:00:00Z\",\n"
			+ "      \"closed_at\": \"2018-04-05T09:30:36Z\"\n" + "    },\n"
			+ "    \"comments\": 0,\n" + "    \"created_at\": \"2018-04-03T08:06:13Z\",\n"
			+ "    \"updated_at\": \"2018-04-03T08:07:53Z\",\n"
			+ "    \"closed_at\": \"2018-04-03T08:07:53Z\",\n"
			+ "    \"author_association\": \"MEMBER\",\n" + "    \"body\": null\n"
			+ "  }";

	@Test
	public void regressionIssueDeserialization() throws Exception {
		Issue issue = this.mapper.readValue(this.issueJson, Issue.class);
		assertThat(issue.getId()).isEqualTo(310734371);
		assertThat(issue.getType()).isEqualTo(Issue.Type.BUG);
	}

}
