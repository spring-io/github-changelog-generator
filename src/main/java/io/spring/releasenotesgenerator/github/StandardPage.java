package io.spring.releasenotesgenerator.github;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author Madhura Bhave
 */
public class StandardPage<T> implements Page<T> {

	private List<T> content;

	private Supplier<Page<T>> nextSupplier;

	/**
	 * Creates a new {@code StandardPage} that has the given {@code content}. The given
	 * {@code nextSupplier} will be used to obtain the next page {@link #next when
	 * requested}.
	 *
	 * @param content the content
	 * @param nextSupplier the supplier of the next page
	 */
	public StandardPage(List<T> content, Supplier<Page<T>> nextSupplier) {
		this.content = content;
		this.nextSupplier = nextSupplier;
	}

	@Override
	public Page<T> next() {
		return this.nextSupplier.get();
	}

	@Override
	public List<T> getContent() {
		return this.content;
	}

}
