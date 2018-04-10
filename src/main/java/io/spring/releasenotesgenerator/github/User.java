package io.spring.releasenotesgenerator.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details of a GitHub user.
 *
 * @author Madhura Bhave
 */
public class User {

	@JsonProperty("login")
	private String name;

	@JsonProperty("html_url")
	private String url;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(name, user.name) &&
				Objects.equals(url, user.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, url);
	}

}
