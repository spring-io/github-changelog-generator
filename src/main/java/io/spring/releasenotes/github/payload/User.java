/*
 * Copyright 2018 the original author or authors.
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

package io.spring.releasenotes.github.payload;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details of a Github user.
 *
 * @author Madhura Bhave
 */
public class User {

	private final String name;

	private final String url;

	public User(@JsonProperty("login") String name,
			@JsonProperty("html_url") String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return this.name;
	}

	public String getUrl() {
		return this.url;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		User other = (User) o;
		return Objects.equals(this.name, other.name)
				&& Objects.equals(this.url, other.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.url);
	}

}
