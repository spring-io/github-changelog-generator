/*
 * Copyright 2018-2019 the original author or authors.
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

package io.spring.releasenotes.github.service;

import java.util.List;
import java.util.function.Supplier;

/**
 * A {@link Page} of content.
 *
 * @param <T> the type of the contents of the page
 * @author Madhura Bhave
 */
class Page<T> {

	private final List<T> content;

	private final Supplier<Page<T>> nextPageSupplier;

	/**
	 * Creates a new {@code StandardPage} that has the given {@code content}. The given
	 * {@code nextSupplier} will be used to obtain the next page {@link #getNextPage()
	 * when requested}.
	 * @param content the content
	 * @param nextPageSupplier the supplier of the next page
	 */
	Page(List<T> content, Supplier<Page<T>> nextPageSupplier) {
		this.content = content;
		this.nextPageSupplier = nextPageSupplier;
	}

	List<T> getContent() {
		return this.content;
	}

	Page<T> getNextPage() {
		return this.nextPageSupplier.get();
	}

}
