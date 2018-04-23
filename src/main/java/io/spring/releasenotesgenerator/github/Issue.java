package io.spring.releasenotesgenerator.github;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details of a GitHub issue.
 *
 * @author Madhura Bhave
 */
public class Issue {

	private int id;

	private String number;

	private String title;

	private User user;

	private List<Label> labels;

	private String state;

	private Milestone milestone;

	@JsonProperty("html_url")
	private String url;

	private Type type;

	@JsonProperty("pull_request")
	private PullRequest pullRequest;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Label> getLabels() {
		return this.labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
		for (Label label : labels) {
			if (label.getName().contains("bug")) {
				this.type = Type.BUG;
			}
			else if (label.getName().contains("enhancement")) {
				this.type = Type.ENHANCEMENT;
			}
			else if (label.getName().contains("documentation")) {
				this.type = Type.DOCUMENTATION;
			}
			else if (label.getName().contains("dependency-upgrade")) {
				this.type = Type.DEPENDENCY_UPGRADES;
			}
		}
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public PullRequest getPullRequest() {
		return this.pullRequest;
	}

	public void setPullRequest(PullRequest pullRequest) {
		this.pullRequest = pullRequest;
	}

	public Milestone getMilestone() {
		return this.milestone;
	}

	public void setMilestone(Milestone milestone) {
		this.milestone = milestone;
	}

	public enum Type {

		ENHANCEMENT("New Features", ":star:"),

		BUG("Bug fixes", ":beetle:"),

		DOCUMENTATION("Documentation", ":notebook_with_decorative_cover:"),

		DEPENDENCY_UPGRADES("Dependency upgrades", ":hammer:");

		private String description;

		private String emoji;

		Type(String description, String emoji) {
			this.description = description;
			this.emoji = emoji;
		}

		public String getDescription() {
			return this.description;
		}

		public String getEmoji() {
			return this.emoji;
		}
	}
}
