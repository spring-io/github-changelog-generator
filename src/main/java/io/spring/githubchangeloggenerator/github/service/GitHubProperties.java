/*
 * Copyright 2018-2024 the original author or authors.
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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * GitHub related properties.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
@ConfigurationProperties("github")
public class GitHubProperties {

	/**
	 * Base url to github's api.
	 */
	private final String apiUrl;

	/**
	 * Token used for authentication.
	 */
	private final String token;

	public GitHubProperties(@DefaultValue("https://api.github.com") String apiUrl, String token) {
		this.apiUrl = apiUrl;
		this.token = token;
	}

	public String getApiUrl() {
		return this.apiUrl;
	}

	public String getToken() {
		return this.token;
	}

}
