/*
 * Copyright 2018-2019 the original author or authors.
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

package io.spring.githubchangeloggenerator.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import io.spring.githubchangeloggenerator.github.payload.Issue;
import io.spring.githubchangeloggenerator.properties.ApplicationProperties;

import org.springframework.util.CollectionUtils;

/**
 * Manages sections of the changelog report.
 *
 * @author Phillip Webb
 */
class Sections {

	private static final List<Section> DEFAULT_SECTIONS;
	static {
		List<Section> sections = new ArrayList<>();
		add(sections, "New Features", ":star:", "enhancement");
		add(sections, "Bug Fixes", ":beetle:", "bug", "regression");
		add(sections, "Documentation", ":notebook_with_decorative_cover:", "documentation");
		add(sections, "Dependency Upgrades", ":hammer:", "dependency-upgrade");
		DEFAULT_SECTIONS = Collections.unmodifiableList(sections);
	}

	private static void add(List<Section> sections, String title, String emoji, String... labels) {
		sections.add(new Section(title, emoji, labels));
	}

	private final List<Section> sections;

	Sections(ApplicationProperties properties) {
		this.sections = adapt(properties.getSections());
	}

	private List<Section> adapt(List<ApplicationProperties.Section> propertySections) {
		if (CollectionUtils.isEmpty(propertySections)) {
			return DEFAULT_SECTIONS;
		}
		return propertySections.stream().map(this::adapt).collect(Collectors.toList());
	}

	private Section adapt(ApplicationProperties.Section propertySection) {
		return new Section(propertySection.getTitle(), propertySection.getEmoji(), propertySection.getLabels());
	}

	Map<Section, List<Issue>> collate(List<Issue> issues) {
		SortedMap<Section, List<Issue>> collated = new TreeMap<>(Comparator.comparing(this.sections::indexOf));
		for (Issue issue : issues) {
			Section section = getSection(issue);
			if (section != null) {
				collated.computeIfAbsent(section, (key) -> new ArrayList<>());
				collated.get(section).add(issue);
			}
		}
		return collated;
	}

	private Section getSection(Issue issue) {
		for (Section section : this.sections) {
			if (section.isMatchFor(issue)) {
				return section;
			}
		}
		return null;
	}

}
