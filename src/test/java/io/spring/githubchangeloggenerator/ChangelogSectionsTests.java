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

package io.spring.githubchangeloggenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import io.spring.githubchangeloggenerator.github.payload.Issue;
import io.spring.githubchangeloggenerator.github.payload.Label;
import io.spring.githubchangeloggenerator.github.service.Repository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ChangelogSections}.
 *
 * @author Eleftheria Stein
 * @author Phillip Webb
 */
class ChangelogSectionsTests {

	private static final Repository REPO = Repository.of("org/name");

	@Test
	void collateWhenNoCustomSectionsUsesDefaultSections() {
		Issue enhancement = createIssue("1", "enhancement");
		Issue bug = createIssue("2", "bug");
		Issue documentation = createIssue("3", "documentation");
		Issue dependencyUpgrade = createIssue("4", "dependency-upgrade");
		ApplicationProperties properties = new ApplicationProperties(REPO, MilestoneReference.TITLE, "## ", null, null,
				null, null);
		ChangelogSections sections = new ChangelogSections(properties);
		Map<ChangelogSection, List<Issue>> collated = sections
				.collate(Arrays.asList(enhancement, bug, documentation, dependencyUpgrade));
		Map<String, List<Issue>> bySection = getBySection(collated);
		assertThat(bySection).containsOnlyKeys(":star: New Features", ":beetle: Bug Fixes",
				":notebook_with_decorative_cover: Documentation", ":hammer: Dependency Upgrades");
		assertThat(bySection.get(":star: New Features")).containsExactly(enhancement);
		assertThat(bySection.get(":beetle: Bug Fixes")).containsExactly(bug);
		assertThat(bySection.get(":notebook_with_decorative_cover: Documentation")).containsExactly(documentation);
		assertThat(bySection.get(":hammer: Dependency Upgrades")).containsExactly(dependencyUpgrade);
	}

	@Test
	void collateWhenHasCustomSectionsUsesDefinedSections() {
		ApplicationProperties.Section breaksPassivitySection = new ApplicationProperties.Section(":rewind: Non-passive",
				null, null, Collections.singleton("breaks-passivity"));
		ApplicationProperties.Section bugsSection = new ApplicationProperties.Section(":beetle: Bug Fixes", null, null,
				Collections.singleton("bug"));
		List<ApplicationProperties.Section> customSections = Arrays.asList(breaksPassivitySection, bugsSection);
		ApplicationProperties properties = new ApplicationProperties(REPO, MilestoneReference.TITLE, "## ",
				customSections, null, null, null);
		ChangelogSections sections = new ChangelogSections(properties);
		Issue bug = createIssue("1", "bug");
		Issue nonPassive = createIssue("1", "breaks-passivity");
		Map<ChangelogSection, List<Issue>> collated = sections.collate(Arrays.asList(bug, nonPassive));
		Map<String, List<Issue>> bySection = getBySection(collated);
		assertThat(bySection).containsOnlyKeys(":beetle: Bug Fixes", ":rewind: Non-passive");
	}

	@Test
	void collateWhenNoIssuesInSectionExcludesSection() {
		Issue bug = createIssue("1", "bug");
		ApplicationProperties properties = new ApplicationProperties(REPO, MilestoneReference.TITLE, "## ", null, null,
				null, null);
		ChangelogSections sections = new ChangelogSections(properties);
		Map<ChangelogSection, List<Issue>> collated = sections.collate(Collections.singletonList(bug));
		Map<String, List<Issue>> bySection = getBySection(collated);
		assertThat(bySection.keySet()).containsExactly(":beetle: Bug Fixes");
	}

	@Test
	void collateWhenIssueDoesNotMatchAnySectionLabelThenExcludesIssue() {
		Issue bug = createIssue("1", "bug");
		Issue nonPassive = createIssue("2", "non-passive");
		ApplicationProperties properties = new ApplicationProperties(REPO, MilestoneReference.TITLE, "## ", null, null,
				null, null);
		ChangelogSections sections = new ChangelogSections(properties);
		Map<ChangelogSection, List<Issue>> collated = sections.collate(Arrays.asList(bug, nonPassive));
		Map<String, List<Issue>> bySection = getBySection(collated);
		assertThat(bySection).containsOnlyKeys(":beetle: Bug Fixes");
		assertThat(bySection.get(":beetle: Bug Fixes")).containsExactly(bug);
	}

	@Test
	void collateWithDefaultsDoesNotAddIssueToMultipleSections() {
		Issue bug = createIssue("1", "bug");
		Issue highlight = createIssue("2", "highlight");
		Issue bugAndHighlight = createIssue("3", "bug", "highlight");
		ApplicationProperties.Section bugs = new ApplicationProperties.Section("Bugs", null, null,
				Collections.singleton("bug"));
		ApplicationProperties.Section highlights = new ApplicationProperties.Section("Highlights", null, null,
				Collections.singleton("highlight"));
		List<ApplicationProperties.Section> customSections = Arrays.asList(bugs, highlights);
		ApplicationProperties properties = new ApplicationProperties(REPO, MilestoneReference.TITLE, "## ",
				customSections, null, null, null);
		ChangelogSections sections = new ChangelogSections(properties);
		Map<ChangelogSection, List<Issue>> collated = sections.collate(Arrays.asList(bug, highlight, bugAndHighlight));
		Map<String, List<Issue>> bySection = getBySection(collated);
		assertThat(bySection).containsOnlyKeys("Bugs", "Highlights");
		assertThat(bySection.get("Bugs")).containsExactly(bug, bugAndHighlight);
		assertThat(bySection.get("Highlights")).containsExactly(highlight);
	}

	@Test
	void collateWithGroupsAddsIssuePerGroup() {
		Issue bug = createIssue("1", "bug");
		Issue highlight = createIssue("2", "highlight");
		Issue bugAndHighlight = createIssue("3", "bug", "highlight");
		ApplicationProperties.Section bugs = new ApplicationProperties.Section("Bugs", null, null,
				Collections.singleton("bug"));
		ApplicationProperties.Section highlights = new ApplicationProperties.Section("Highlights", "highlights", null,
				Collections.singleton("highlight"));
		List<ApplicationProperties.Section> customSections = Arrays.asList(bugs, highlights);
		ApplicationProperties properties = new ApplicationProperties(REPO, MilestoneReference.TITLE, "## ",
				customSections, null, null, null);
		ChangelogSections sections = new ChangelogSections(properties);
		Map<ChangelogSection, List<Issue>> collated = sections.collate(Arrays.asList(bug, highlight, bugAndHighlight));
		Map<String, List<Issue>> bySection = getBySection(collated);
		assertThat(bySection).containsOnlyKeys("Bugs", "Highlights");
		assertThat(bySection.get("Bugs")).containsExactly(bug, bugAndHighlight);
		assertThat(bySection.get("Highlights")).containsExactly(highlight, bugAndHighlight);
	}

	private Issue createIssue(String number, String... labels) {
		return new Issue(number, "I am #" + number, null,
				Arrays.stream(labels).map(Label::new).collect(Collectors.toList()), "https://example.com/" + number,
				null, null);
	}

	private Map<String, List<Issue>> getBySection(Map<ChangelogSection, List<Issue>> collatedIssues) {
		return collatedIssues.entrySet().stream()
				.collect(Collectors.toMap((entry) -> entry.getKey().toString(), Entry::getValue));
	}

}
