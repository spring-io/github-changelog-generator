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

package io.spring.releasenotesgenerator;

import io.spring.releasenotesgenerator.github.GithubProperties;
import io.spring.releasenotesgenerator.github.GithubService;
import io.spring.releasenotesgenerator.github.RegexLinkParser;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link Configuration @Configuration} for the GitHub service.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
@Configuration
@EnableConfigurationProperties(GithubProperties.class)
public class ServiceConfiguration {

	@Bean
	public GithubService githubService(RestTemplateBuilder builder,
			GithubProperties properties) {
		return new GithubService(properties.getUsername(), properties.getPassword(),
				builder, new RegexLinkParser());
	}

}
