package io.spring.releasenotesgenerator.github;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details of a GitHub milestone.
 *
 * @author Madhura Bhave
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Milestone {

	private String title;

	@JsonProperty("closed_at")
	private Date dateClosed;

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDateClosed() {
		return this.dateClosed;
	}

	public void setDateClosed(Date dateClosed) {
		this.dateClosed = dateClosed;
	}
}
