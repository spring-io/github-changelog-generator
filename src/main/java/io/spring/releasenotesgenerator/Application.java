package io.spring.releasenotesgenerator;

import io.spring.releasenotesgenerator.github.GithubService;
import io.spring.releasenotesgenerator.github.GithubProperties;
import io.spring.releasenotesgenerator.github.RegexLinkParser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(GithubProperties.class)
public class Application {

	@Bean
	public GithubService githubService(RestTemplateBuilder builder, GithubProperties properties) {
		return new GithubService(properties.getUsername(), properties.getPassword(),
				builder, new RegexLinkParser());
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
