/*
 * Copyright 2018-2025 the original author or authors.
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

package io.spring.githubchangeloggenerator.github.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A comment on a GitHub issue.
 *
 * @author Andy Wilkinson
 */
public class Comment {

	private final String body;

	private final AuthorAssociation authorAssociation;

	public Comment(@JsonProperty("body") String body,
			@JsonProperty("author_association") AuthorAssociation authorAssociation) {
		this.body = body;
		this.authorAssociation = authorAssociation;
	}

	public String getBody() {
		return this.body;
	}

	public AuthorAssociation getAuthorAssociation() {
		return this.authorAssociation;
	}

	public enum AuthorAssociation {

		/**
		 * The user who authored the comment has previously committed to the issue's
		 * repository.
		 */
		CONTRIBUTOR,

		/**
		 * The user who authored the comment is a member of the organization that own's
		 * the issue's repository.
		 */
		MEMBER,

		/**
		 * The user who authored the comment has no association with the issue's
		 * repository.
		 */
		NONE;

	}

}
