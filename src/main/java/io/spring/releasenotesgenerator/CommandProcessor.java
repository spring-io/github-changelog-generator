package io.spring.releasenotesgenerator;

import java.io.IOException;
import java.util.List;

import io.spring.releasenotesgenerator.ChangelogGenerator;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author Madhura Bhave
 */
@Component
public class CommandProcessor implements ApplicationRunner {

	private final ChangelogGenerator generator;

	public CommandProcessor(ChangelogGenerator generator) {
		this.generator = generator;
	}

	@Override
	public void run(ApplicationArguments args) throws IOException {
		List<String> nonOptionArgs = args.getNonOptionArgs();
		String milestone = nonOptionArgs.get(0);
		String path = nonOptionArgs.get(1);
		this.generator.generate(Integer.parseInt(milestone), path);
	}

}
