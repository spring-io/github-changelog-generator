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

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Milestone}.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@JsonTest
public class MilestoneTests {

	@Autowired
	private JacksonTester<Milestone> json;

	@Test
	public void milestoneCanBeDeserialized() throws Exception {
		Milestone milestone = this.json.read("milestone.json").getObject();
		assertThat(milestone.getTitle()).isEqualTo("2.0.1");
		assertThat(milestone.getDateClosed()).isEqualTo("2018-04-05T14:02:25Z");
	}

}
