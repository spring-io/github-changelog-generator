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

import io.spring.releasenotesgenerator.github.Issue;

/**
 * @author Madhura Bhave
 */
public class MockIssues {

	public static Issue getBug(String title, String number, String url) {
		return getIssue(title, number, url, Issue.Type.BUG);
	}

	public static Issue getEnhancement(String title, String number, String url) {
		return getIssue(title, number, url, Issue.Type.ENHANCEMENT);
	}

	private static Issue getIssue(String title, String number, String url, Issue.Type enhancement) {
		Issue issue = new Issue();
		issue.setTitle(title);
		issue.setNumber(number);
		issue.setUrl(url);
		issue.setType(enhancement);
		return issue;
	}

}
