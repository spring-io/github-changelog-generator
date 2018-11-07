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

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details of a GitHub milestone.
 *
 * @author Madhura Bhave
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Milestone {

	private final String title;

	private final Date dateClosed;

	public Milestone(@JsonProperty("title") String title,
			@JsonProperty("closed_at") Date dateClosed) {
		this.title = title;
		this.dateClosed = dateClosed;
	}

	public String getTitle() {
		return this.title;
	}

	public Date getDateClosed() {
		return this.dateClosed;
	}

}
