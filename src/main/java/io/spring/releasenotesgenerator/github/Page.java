package io.spring.releasenotesgenerator.github;

import java.util.List;

/**
 * A page of results.
 *
 * @param <T> the type of the contents of the page
 * @author Madhura Bhave
 */
public interface Page<T> {

	/**
	 * Returns the next page, if any.
	 *
	 * @return The next page or {@code null}
	 */
	Page<T> next();

	/**
	 * Returns the contents of the page.
	 *
	 * @return the contents
	 */
	List<T> getContent();

}
