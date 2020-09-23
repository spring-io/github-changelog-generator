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

package io.spring.githubchangeloggenerator;

import java.util.Arrays;
import java.util.List;

import io.spring.githubchangeloggenerator.github.payload.Issue;
import io.spring.githubchangeloggenerator.github.payload.Label;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * A single section of a changelog report.
 *
 * @author Phillip Webb
 */
class ChangelogSection {

	private final String title;

	private final String emoji;

	private final List<String> labels;

	ChangelogSection(String title, String emoji, String... labels) {
		this(title, emoji, Arrays.asList(labels));
	}

	ChangelogSection(String title, String emoji, List<String> labels) {
		Assert.hasText(title, "Title must not be empty");
		Assert.hasText(emoji, "Emoji must not be empty");
		Assert.isTrue(!CollectionUtils.isEmpty(labels), "Labels must not be empty");
		this.title = title;
		this.emoji = emoji;
		this.labels = labels;
	}

	boolean isMatchFor(Issue issue) {
		for (String candidate : this.labels) {
			for (Label label : issue.getLabels()) {
				if (label.getName().contains(candidate)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return this.emoji + " " + this.title;
	}

}
