/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

import io.spring.releasenotes.github.payload.Issue;

/**
 * Manages sections of the change log report.
 *
 * @author Phillip Webb
 */
class ReleaseNotesSections {

	private static final List<ReleaseNotesSection> SECTIONS;
	static {
		List<ReleaseNotesSection> sections = new ArrayList<>();
		sections.add(new ReleaseNotesSection("New Features", ":star:", "enhancement"));
		sections.add(
				new ReleaseNotesSection("Bug Fixes", ":beetle:", "bug", "regression"));
		sections.add(new ReleaseNotesSection("Documentation",
				":notebook_with_decorative_cover:", "documentation"));
		sections.add(new ReleaseNotesSection("Dependency Upgrades", ":hammer:",
				"dependency-upgrade"));
		SECTIONS = Collections.unmodifiableList(sections);
	}

	public Map<ReleaseNotesSection, List<Issue>> collate(List<Issue> issues) {
		SortedMap<ReleaseNotesSection, List<Issue>> collated = new TreeMap<>(
				Comparator.comparing(SECTIONS::indexOf));
		for (Issue issue : issues) {
			ReleaseNotesSection section = getSection(issue);
			if (section != null) {
				collated.computeIfAbsent(section, (key) -> new ArrayList<>());
				collated.get(section).add(issue);
			}
		}
		return collated;
	}

	private ReleaseNotesSection getSection(Issue issue) {
		for (ReleaseNotesSection section : SECTIONS) {
			if (section.isMatchFor(issue)) {
				return section;
			}
		}
		return null;
	}

}
