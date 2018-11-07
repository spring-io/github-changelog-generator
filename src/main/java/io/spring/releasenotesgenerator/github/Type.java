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

package io.spring.releasenotesgenerator.github;

import java.util.List;

/**
 * Describe the available issue types.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
public enum Type {

	/**
	 * Enhancement.
	 */
	ENHANCEMENT("New Features", ":star:"),

	/**
	 * Bug fix.
	 */
	BUG("Bug fixes", ":beetle:"),

	/**
	 * Documentation change.
	 */
	DOCUMENTATION("Documentation", ":notebook_with_decorative_cover:"),

	/**
	 * Dependency upgrade.
	 */
	DEPENDENCY_UPGRADES("Dependency upgrades", ":hammer:");

	private final String description;

	private final String emoji;

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

	public static Type fromLabels(List<Label> labels) {
		for (Label label : labels) {
			String name = label.getName();
			if (name.contains("bug") || name.contains("regression")) {
				return Type.BUG;
			}
			else if (name.contains("enhancement")) {
				return Type.ENHANCEMENT;
			}
			else if (name.contains("documentation")) {
				return Type.DOCUMENTATION;
			}
			else if (name.contains("dependency-upgrade")) {
				return Type.DEPENDENCY_UPGRADES;
			}
		}
		return null;
	}

}
