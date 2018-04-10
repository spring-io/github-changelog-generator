
package io.spring.releasenotesgenerator.github;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Github Pull Request.
 *
 * @author Madhura Bhave
 */
public class PullRequest {

	@JsonProperty("url")
	private String url;

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
