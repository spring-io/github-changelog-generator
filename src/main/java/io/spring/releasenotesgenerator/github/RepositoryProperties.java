package io.spring.releasenotesgenerator.github;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Madhura Bhave
 */
@ConfigurationProperties(prefix = "repo")
public class RepositoryProperties {

	private String organization;

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

}
