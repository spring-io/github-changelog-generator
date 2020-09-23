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

package io.spring.githubchangeloggenerator.github.service;

import org.springframework.util.Assert;

/**
 * A reference to a specific GitHub repository.
 *
 * @author Phillip Webb
 */
public final class Repository {

	private final String owner;

	private final String name;

	private Repository(String owner, String name) {
		this.owner = owner;
		this.name = name;
	}

	public String getOwner() {
		return this.owner;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Repository other = (Repository) obj;
		return this.name.equals(other.name) && this.owner.equals(other.owner);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.name.hashCode();
		result = prime * result + this.owner.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return this.owner + "/" + this.name;
	}

	public static Repository of(String reference) {
		Assert.hasText(reference, "GitHub repository references must not be empty");
		int slashIndex = reference.indexOf('/');
		Assert.isTrue(slashIndex >= 0, "GitHub repository references must include '/'");
		return new Repository(reference.substring(0, slashIndex), reference.substring(slashIndex + 1));
	}

}
