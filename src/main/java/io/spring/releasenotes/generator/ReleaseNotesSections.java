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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import io.spring.releasenotes.github.payload.Issue;
import io.spring.releasenotes.properties.ApplicationProperties;
import io.spring.releasenotes.properties.ApplicationProperties.Section;

import org.springframework.util.CollectionUtils;

/**
 * Manages sections of the change log report.
 *
 * @author Phillip Webb
 */
class ReleaseNotesSections {

	private static final List<ReleaseNotesSection> DEFAULT_SECTIONS;
	static {
		List<ReleaseNotesSection> sections = new ArrayList<>();
		add(sections, "New Features", ":star:", "enhancement");
		add(sections, "Bug Fixes", ":beetle:", "bug", "regression");
		add(sections, "Documentation", ":notebook_with_decorative_cover:", "documentation");
		add(sections, "Dependency Upgrades", ":hammer:", "dependency-upgrade");
		DEFAULT_SECTIONS = Collections.unmodifiableList(sections);
	}

	private static void add(List<ReleaseNotesSection> sections, String title, String emoji, String... labels) {
		sections.add(new ReleaseNotesSection(title, emoji, labels));
	}

	private final List<ReleaseNotesSection> sections;

	private final Boolean allowInMultipleSections;

	ReleaseNotesSections(ApplicationProperties properties) {
		this.sections = adapt(properties.getSections());
		this.allowInMultipleSections = properties.getIssues().getAllowInMultipleSections();
	}

	private List<ReleaseNotesSection> adapt(List<Section> propertySections) {
		if (CollectionUtils.isEmpty(propertySections)) {
			return DEFAULT_SECTIONS;
		}
		return propertySections.stream().map(this::adapt).collect(Collectors.toList());
	}

	private ReleaseNotesSection adapt(Section propertySection) {
		return new ReleaseNotesSection(propertySection.getTitle(), propertySection.getEmoji(),
				propertySection.getLabels());
	}

	Map<ReleaseNotesSection, List<Issue>> collate(List<Issue> issues) {
		SortedMap<ReleaseNotesSection, List<Issue>> collated = new TreeMap<>(
				Comparator.comparing(this.sections::indexOf));
		for (Issue issue : issues) {
			List<ReleaseNotesSection> sections = this.allowInMultipleSections ? getAllMatchingSections(issue)
					: Collections.singletonList(getSection(issue));
			for (ReleaseNotesSection section : sections) {
				if (section != null) {
					collated.computeIfAbsent(section, (key) -> new ArrayList<>());
					collated.get(section).add(issue);
				}
			}
		}
		return collated;
	}

	private ReleaseNotesSection getSection(Issue issue) {
		for (ReleaseNotesSection section : this.sections) {
			if (section.isMatchFor(issue)) {
				return section;
			}
		}
		return null;
	}

	private List<ReleaseNotesSection> getAllMatchingSections(Issue issue) {
		List<ReleaseNotesSection> sections = new ArrayList<>();
		for (ReleaseNotesSection section : this.sections) {
			if (section.isMatchFor(issue)) {
				sections.add(section);
			}
		}
		return sections;
	}

}
