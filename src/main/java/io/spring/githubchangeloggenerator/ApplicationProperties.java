/*
 * Copyright 2018-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.githubchangeloggenerator;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.Assert;

import io.spring.githubchangeloggenerator.github.service.Repository;

/**
 * Configuration properties for the Github repo.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 * @author Mahendra Bishnoi
 * @author Gary Russell
 * @author Steven Sheehy
 */
@ConfigurationProperties(prefix = "changelog")
@ConstructorBinding
public class ApplicationProperties {

	/**
	 * GitHub repository to use in the form "owner/repository".
	 */
	private final Repository repository;

	/**
	 * The way that milestones are referenced. Supports "title", "id".
	 */
	private final MilestoneReference milestoneReference;

	/**
	 * Section definitions in the order that they should appear.
	 */
	private final List<Section> sections;

	/**
	 * Settings specific to issues.
	 */
	private final Issues issues;

	/**
	 * Settings specific to contributors.
	 */
	private final Contributors contributors;

	/**
	 * Settings specific to external links.
	 */
	private final List<ExternalLink> externalLinks;

	/**
	 * True to add sections to default instead of replacing.
	 */
	private final boolean addSections;

	public ApplicationProperties(Repository repository, @DefaultValue("title") MilestoneReference milestoneReference,
			List<Section> sections, Issues issues, Contributors contributors, List<ExternalLink> externalLinks,
			@DefaultValue("false") boolean addSections) {
		Assert.notNull(repository, "Repository must not be null");
		this.repository = repository;
		this.milestoneReference = milestoneReference;
		this.sections = (sections != null) ? sections : Collections.emptyList();
		this.issues = (issues != null) ? issues : new Issues(null, null, null);
		this.contributors = (contributors != null) ? contributors : new Contributors(null, null);
		this.externalLinks = (externalLinks != null) ? externalLinks : Collections.emptyList();
		this.addSections = addSections;
	}

	public Repository getRepository() {
		return this.repository;
	}

	public MilestoneReference getMilestoneReference() {
		return this.milestoneReference;
	}

	public List<Section> getSections() {
		return this.sections;
	}

	public Issues getIssues() {
		return this.issues;
	}

	public Contributors getContributors() {
		return this.contributors;
	}

	public List<ExternalLink> getExternalLinks() {
		return this.externalLinks;
	}

	public boolean isAddSections() {
		return this.addSections;
	}

	/**
	 * Properties for a single changelog section.
	 */
	public static class Section {

		/**
		 * Title of the section.
		 */
		private final String title;

		/**
		 * Group used to bound the contained issues. Issues appear in the first section of
		 * each group.
		 */
		private final String group;

		/**
		 * Sort order for issues within this section.
		 */
		private final IssueSort sort;

		/**
		 * Labels used to identify if an issue is for the section.
		 */
		private final Set<String> labels;

		/**
		 * Whether issues, pull requests or both should be included in this section.
		 */
		private final IssueType type;

		public Section(String title, @DefaultValue("default") String group, IssueSort sort, Set<String> labels,
				@DefaultValue("ANY") IssueType type) {
			this.title = title;
			this.group = (group != null) ? group : "default";
			this.sort = sort;
			this.labels = labels;
			this.type = type;
		}

		public String getTitle() {
			return this.title;
		}

		public String getGroup() {
			return this.group;
		}

		public IssueSort getSort() {
			return this.sort;
		}

		public Set<String> getLabels() {
			return this.labels;
		}

		public IssueType getType() {
			return this.type;
		}

	}

	/**
	 * Properties relating to issues.
	 */
	public static class Issues {

		/**
		 * The issue sort order.
		 */
		private final IssueSort sort;

		/**
		 * Issue exclusions.
		 */
		private final IssuesExclude exclude;

		/**
		 * Identification of issues that are a forward-port or back-port of another issue.
		 */
		private final Set<PortedIssue> ports;

		public Issues(IssueSort sort, IssuesExclude exclude, Set<PortedIssue> ports) {
			this.sort = sort;
			this.exclude = (exclude != null) ? exclude : new IssuesExclude(null);
			this.ports = (ports != null) ? ports : Collections.emptySet();
		}

		public IssueSort getSort() {
			return this.sort;
		}

		public IssuesExclude getExcludes() {
			return this.exclude;
		}

		public Set<PortedIssue> getPorts() {
			return this.ports;
		}

	}

	/**
	 * Issues exclude.
	 */
	public static class IssuesExclude {

		/**
		 * Labels used to exclude issues.
		 */
		private final Set<String> labels;

		public IssuesExclude(Set<String> labels) {
			this.labels = (labels != null) ? labels : Collections.emptySet();
		}

		public Set<String> getLabels() {
			return this.labels;
		}

	}

	/**
	 * Properties related to identification of ported issues.
	 */
	public static class PortedIssue {

		/**
		 * Label used to identify a ported issue.
		 */
		private final String label;

		/**
		 * Regular expression used to extract the upstream or downstream issue ID from the
		 * body of a ported issue.
		 */
		private final Pattern bodyExpression;

		public PortedIssue(String label, String bodyExpression) {
			this.label = label;
			this.bodyExpression = Pattern.compile(bodyExpression);
		}

		public String getLabel() {
			return this.label;
		}

		public Pattern getBodyExpression() {
			return this.bodyExpression;
		}

	}

	/**
	 * Properties relating to constructors.
	 */
	public static class Contributors {

		/**
		 * Title for the contributors section.
		 */
		private final String title;

		/**
		 * Contributor exclusions.
		 */
		private final ContributorsExclude exclude;

		public Contributors(String title, ContributorsExclude exclude) {
			this.title = title;
			this.exclude = (exclude != null) ? exclude : new ContributorsExclude(null);
		}

		public String getTitle() {
			return this.title;
		}

		public ContributorsExclude getExclude() {
			return this.exclude;
		}

	}

	/**
	 * Contributors exclude.
	 */
	public static class ContributorsExclude {

		/**
		 * Contributor names to exclude.
		 */
		private final Set<String> names;

		public ContributorsExclude(Set<String> names) {
			this.names = (names != null) ? names : Collections.emptySet();
		}

		public Set<String> getNames() {
			return this.names;
		}

	}

	/**
	 * Properties for a single external link.
	 */
	public static class ExternalLink {

		/**
		 * Name to be shown for an external link.
		 */
		private final String name;

		/**
		 * URL for an external link.
		 */
		private final String location;

		public ExternalLink(String name, String location) {
			this.name = name;
			this.location = location;
		}

		public String getName() {
			return this.name;
		}

		public String getLocation() {
			return this.location;
		}

	}

	public enum IssueSort {

		/**
		 * Sort by the created date.
		 */
		CREATED,

		/**
		 * Sort by the title.
		 */
		TITLE

	}

	/**
	 * The type of changelog entry.
	 */
	public enum IssueType {

		/**
		 * Either issue or pull requests.
		 */
		ANY,

		/**
		 * GitHub issue.
		 */
		ISSUE,

		/**
		 * GitHub pull request.
		 */
		PULL_REQUEST

	}

}
