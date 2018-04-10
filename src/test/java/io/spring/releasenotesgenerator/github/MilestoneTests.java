
package io.spring.releasenotesgenerator.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MilestoneTests {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void milestoneCanBeDeserialized() throws Exception {
		Milestone milestone = this.objectMapper.readValue(getMilestoneJson(), Milestone.class);
		assertThat(milestone.getTitle()).isEqualTo("2.0.1");
		assertThat(milestone.getDateClosed()).isEqualTo("2018-04-05T14:02:25Z");
	}

	private String getMilestoneJson() {
		return "{\n" +
				"      \"url\": \"https://api.github.com/repos/spring-projects/spring-boot/milestones/98\",\n" +
				"      \"html_url\": \"https://github.com/spring-projects/spring-boot/milestone/98\",\n" +
				"      \"labels_url\": \"https://api.github.com/repos/spring-projects/spring-boot/milestones/98/labels\",\n" +
				"      \"id\": 3152524,\n" +
				"      \"number\": 98,\n" +
				"      \"title\": \"2.0.1\",\n" +
				"      \"description\": \"\",\n" +
				"      \"creator\": {\n" +
				"        \"login\": \"philwebb\",\n" +
				"        \"id\": 519772,\n" +
				"        \"avatar_url\": \"https://avatars1.githubusercontent.com/u/519772?v=4\",\n" +
				"        \"gravatar_id\": \"\",\n" +
				"        \"url\": \"https://api.github.com/users/philwebb\",\n" +
				"        \"html_url\": \"https://github.com/philwebb\",\n" +
				"        \"followers_url\": \"https://api.github.com/users/philwebb/followers\",\n" +
				"        \"following_url\": \"https://api.github.com/users/philwebb/following{/other_user}\",\n" +
				"        \"gists_url\": \"https://api.github.com/users/philwebb/gists{/gist_id}\",\n" +
				"        \"starred_url\": \"https://api.github.com/users/philwebb/starred{/owner}{/repo}\",\n" +
				"        \"subscriptions_url\": \"https://api.github.com/users/philwebb/subscriptions\",\n" +
				"        \"organizations_url\": \"https://api.github.com/users/philwebb/orgs\",\n" +
				"        \"repos_url\": \"https://api.github.com/users/philwebb/repos\",\n" +
				"        \"events_url\": \"https://api.github.com/users/philwebb/events{/privacy}\",\n" +
				"        \"received_events_url\": \"https://api.github.com/users/philwebb/received_events\",\n" +
				"        \"type\": \"User\",\n" +
				"        \"site_admin\": false\n" +
				"      },\n" +
				"      \"open_issues\": 0,\n" +
				"      \"closed_issues\": 165,\n" +
				"      \"state\": \"closed\",\n" +
				"      \"created_at\": \"2018-03-01T06:07:33Z\",\n" +
				"      \"updated_at\": \"2018-04-05T14:02:25Z\",\n" +
				"      \"due_on\": \"2018-04-05T07:00:00Z\",\n" +
				"      \"closed_at\": \"2018-04-05T14:02:25Z\"\n" +
				"    }";
	}
}