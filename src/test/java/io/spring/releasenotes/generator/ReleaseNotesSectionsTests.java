/*
 * Copyright 2018-2020 the original author or authors.
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

package io.spring.releasenotes.generator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.spring.releasenotes.github.payload.Issue;
import io.spring.releasenotes.github.payload.Label;
import io.spring.releasenotes.properties.ApplicationProperties;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ReleaseNotesSections}.
 *
 * @author Eleftheria Stein
 */
public class ReleaseNotesSectionsTests {

	@Test
	public void whenNoCustomSectionsThenDefaultSectionsUsed() {
		Issue enhancement = new Issue("1", "Enhancement", null, Collections.singletonList(new Label("enhancement")),
				"url1", null);
		Issue bug = new Issue("2", "Bug", null, Collections.singletonList(new Label("bug")), "url2", null);
		Issue documentation = new Issue("3", "Documentation Change", null,
				Collections.singletonList(new Label("documentation")), "url3", null);
		Issue dependencyUpgrade = new Issue("4", "Dependency Upgrade", null,
				Collections.singletonList(new Label("dependency-upgrade")), "url4", null);
		List<Issue> issues = Arrays.asList(enhancement, bug, documentation, dependencyUpgrade);
		ReleaseNotesSections sections = new ReleaseNotesSections(new ApplicationProperties());
		Map<ReleaseNotesSection, List<Issue>> collated = sections.collate(issues);
		Map<String, List<Issue>> titlesToIssues = getSectionNameToIssuesMap(collated);
		assertThat(titlesToIssues.keySet()).containsExactlyInAnyOrder(":star: New Features", ":beetle: Bug Fixes",
				":notebook_with_decorative_cover: Documentation", ":hammer: Dependency Upgrades");
		assertThat(titlesToIssues.get(":star: New Features")).containsExactly(enhancement);
		assertThat(titlesToIssues.get(":beetle: Bug Fixes")).containsExactly(bug);
		assertThat(titlesToIssues.get(":notebook_with_decorative_cover: Documentation")).containsExactly(documentation);
		assertThat(titlesToIssues.get(":hammer: Dependency Upgrades")).containsExactly(dependencyUpgrade);
	}

	@Test
	public void whenCustomSectionsThenUsed() {
		ApplicationProperties properties = new ApplicationProperties();
		ApplicationProperties.Section breaksPassivitySection = new ApplicationProperties.Section();
		breaksPassivitySection.setEmoji(":rewind:");
		breaksPassivitySection.setTitle("Non-passive");
		breaksPassivitySection.setLabels(Collections.singletonList("breaks-passivity"));
		ApplicationProperties.Section bugsSection = new ApplicationProperties.Section();
		bugsSection.setEmoji(":beetle:");
		bugsSection.setTitle("Bug Fixes");
		bugsSection.setLabels(Collections.singletonList("bug"));
		List<ApplicationProperties.Section> customSections = Arrays.asList(breaksPassivitySection, bugsSection);
		properties.setSections(customSections);

		ReleaseNotesSections sections = new ReleaseNotesSections(properties);
		Issue bug = new Issue("1", "Bug", null, Collections.singletonList(new Label("bug")), "url1", null);
		Issue nonPassive = new Issue("2", "Non-passive change", null,
				Collections.singletonList(new Label("breaks-passivity")), "url2", null);
		List<Issue> issues = Arrays.asList(bug, nonPassive);

		Map<ReleaseNotesSection, List<Issue>> collated = sections.collate(issues);
		List<String> sectionTitles = collated.keySet().stream().map(ReleaseNotesSection::toString)
				.collect(Collectors.toList());
		assertThat(sectionTitles).containsExactlyInAnyOrder(":beetle: Bug Fixes", ":rewind: Non-passive");
	}

	@Test
	public void whenNoIssuesInSectionThenSectionExcluded() {
		Issue bug = new Issue("1", "Bug", null, Collections.singletonList(new Label("bug")), "url1", null);
		List<Issue> issues = Collections.singletonList(bug);
		ReleaseNotesSections sections = new ReleaseNotesSections(new ApplicationProperties());
		Map<ReleaseNotesSection, List<Issue>> collated = sections.collate(issues);
		Map<String, List<Issue>> titlesToIssues = getSectionNameToIssuesMap(collated);
		assertThat(titlesToIssues.keySet()).containsExactly(":beetle: Bug Fixes");
	}

	@Test
	public void whenIssueDoesNotMatchAnySectionLabelThenIssueExcluded() {
		Issue bug = new Issue("1", "Bug", null, Collections.singletonList(new Label("bug")), "url1", null);
		Issue nonPassive = new Issue("2", "Non-passive change", null,
				Collections.singletonList(new Label("non-passive")), "url2", null);
		List<Issue> issues = Arrays.asList(bug, nonPassive);
		ReleaseNotesSections sections = new ReleaseNotesSections(new ApplicationProperties());
		Map<ReleaseNotesSection, List<Issue>> collated = sections.collate(issues);
		Map<String, List<Issue>> titlesToIssues = getSectionNameToIssuesMap(collated);
		assertThat(titlesToIssues.keySet()).containsExactly(":beetle: Bug Fixes");
		assertThat(titlesToIssues.get(":beetle: Bug Fixes")).containsExactly(bug);
	}

	@Test
	public void byDefaultIssueDoesNotAppearInMultipleSections() {
		Issue bugAndDocumentation = new Issue("1", "Bug", null,
				Arrays.asList(new Label("bug"), new Label("documentation")), "url1", null);
		List<Issue> issues = Collections.singletonList(bugAndDocumentation);
		ReleaseNotesSections sections = new ReleaseNotesSections(new ApplicationProperties());
		Map<ReleaseNotesSection, List<Issue>> collated = sections.collate(issues);
		Map<String, List<Issue>> titlesToIssues = getSectionNameToIssuesMap(collated);
		assertThat(titlesToIssues.keySet()).containsExactly(":beetle: Bug Fixes");
		assertThat(titlesToIssues.get(":beetle: Bug Fixes")).containsExactly(bugAndDocumentation);
	}

	@Test
	public void whenAllowInMultipleSectionsIssueAppearsInAllMatchingSections() {
		Issue bugAndDocumentation = new Issue("1", "Bug", null,
				Arrays.asList(new Label("bug"), new Label("documentation")), "url1", null);
		List<Issue> issueList = Collections.singletonList(bugAndDocumentation);
		ApplicationProperties properties = new ApplicationProperties();
		ApplicationProperties.Issues issueProperties = new ApplicationProperties.Issues();
		issueProperties.setAllowInMultipleSections(true);
		properties.setIssues(issueProperties);
		ReleaseNotesSections sections = new ReleaseNotesSections(properties);
		Map<ReleaseNotesSection, List<Issue>> collated = sections.collate(issueList);
		Map<String, List<Issue>> titlesToIssues = getSectionNameToIssuesMap(collated);
		assertThat(titlesToIssues.keySet()).containsExactlyInAnyOrder(":beetle: Bug Fixes",
				":notebook_with_decorative_cover: Documentation");
		assertThat(titlesToIssues.get(":beetle: Bug Fixes")).containsExactly(bugAndDocumentation);
		assertThat(titlesToIssues.get(":notebook_with_decorative_cover: Documentation"))
				.containsExactly(bugAndDocumentation);
	}

	private Map<String, List<Issue>> getSectionNameToIssuesMap(Map<ReleaseNotesSection, List<Issue>> collatedIssues) {
		return collatedIssues.entrySet().stream()
				.collect(Collectors.toMap((entry) -> entry.getKey().toString(), (entry) -> entry.getValue()));
	}

}
