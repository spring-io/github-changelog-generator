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

package io.spring.releasenotes;

import java.io.IOException;
import java.util.List;

import io.spring.releasenotes.generator.ReleaseNotesGenerator;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * {@link ApplicationRunner} that triggers the generation of the release notes based on
 * application arguments.
 *
 * @author Madhura Bhave
 */
@Component
public class CommandProcessor implements ApplicationRunner {

	private final ReleaseNotesGenerator generator;

	public CommandProcessor(ReleaseNotesGenerator generator) {
		this.generator = generator;
	}

	@Override
	public void run(ApplicationArguments args) throws IOException {
		run(args.getNonOptionArgs());
	}

	private void run(List<String> args) throws IOException {
		String milestone = args.get(0);
		String path = args.get(1);
		run(milestone, path);
	}

	private void run(String milestone, String path) throws IOException {
		Assert.hasLength(milestone, "Milestone must not be specified");
		Assert.hasLength(path, "Path must be specified");
		this.generator.generate(Integer.parseInt(milestone), path);
	}

}
