package io.spring.releasenotesgenerator.github;

import java.util.Map;

/**
 * A {@code LinkParser} that uses a regular expression to parse the header.
 *
 * @author Madhura Bhave
 */
public interface LinkParser {

	Map<String, String> parse(String header);

}
