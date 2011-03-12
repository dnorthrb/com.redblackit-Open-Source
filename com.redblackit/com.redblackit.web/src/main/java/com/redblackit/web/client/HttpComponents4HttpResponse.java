/*
 * Copyright 2002-2011 the original author or authors.
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
package com.redblackit.web.client;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

/**
 * {@link org.springframework.http.client.ClientHttpResponse} implementation
 * that uses Apache HttpComponent 4.x's HttpClient to execute requests. </p>
 * <p>
 * Based on the
 * {@link org.springframework.http.client.CommonsClientHttpResponse}.
 * 
 * @author djnorth
 */
final class HttpComponents4HttpResponse implements ClientHttpResponse {

	/**
	 * The Apache response object we're adapting
	 */
	private final HttpResponse httpResponse;

	/**
	 * Spring format headers (lazily created)
	 */
	private HttpHeaders headers;

	/**
	 * Create from new HttpResponse object
	 * 
	 * @param httpResponse
	 */
	HttpComponents4HttpResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	/**
	 * Return the appropriate code enum
	 * 
	 * @return status code
	 */
	public HttpStatus getStatusCode() {
		return HttpStatus.valueOf(this.httpResponse.getStatusLine()
				.getStatusCode());
	}

	/**
	 * Return the status code text
	 * 
	 * @return reason text
	 */
	public String getStatusText() {
		return this.httpResponse.getStatusLine().getReasonPhrase();
	}

	/**
	 * Get headers in Spring format
	 * 
	 * @return headers
	 */
	public HttpHeaders getHeaders() {
		if (this.headers == null) {
			this.headers = new HttpHeaders();
			for (Header header : this.httpResponse.getAllHeaders()) {
				this.headers.add(header.getName(), header.getValue());
			}
		}
		return this.headers;
	}

	/**
	 * Get the response body as a stream
	 * 
	 * N.B. It is not unknown for extra blank lines to be inserted after the
	 * headers, and so appear inthe body before the real content.
	 * 
	 * @return response body stream
	 */
	public InputStream getBody() throws IOException {
		return this.httpResponse.getEntity().getContent();
	}

	/**
	 * Close the underlying connection via the entity body
	 */
	public void close() {
		// Closing the input stream will trigger connection release
		if (httpResponse.getEntity() != null) {
			try {
				getBody().close();
			} catch (Exception ignore) {
			}
		}
	}

}