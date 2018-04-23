package io.spring.releasenotesgenerator.github;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Github repo.
 *
 * @author Madhura Bhave
 */
@ConfigurationProperties(prefix = "releasenotes.github")
public class GithubProperties {

	/**
	 * The username for the github user.
	 */
	private String username;

	/**
	 * The password for the github user.
	 */
	private String password;

	/**
	 * The github org this repository is under.
	 */
	private String organization;

	/**
	 * The name of the github repository.
	 */
	private String name;

	public String getOrganization() {
		return this.organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
