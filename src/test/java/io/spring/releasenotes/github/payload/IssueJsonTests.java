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

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Issue}.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@JsonTest
public class IssueJsonTests {

	@Autowired
	private JacksonTester<Issue> json;

	@Test
	public void issueDeserialization() throws Exception {
		Issue issue = this.json.read("issue.json").getObject();
		assertThat(issue.getNumber()).isEqualTo("12730");
		assertThat(issue.getLabels()).flatExtracting(Label::getName)
				.containsExactly("type: regression");
	}

}
