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
import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.http.client.ClientHttpRequestFactory}
 * implementation that uses <a
 * href="http://hc.apache.org/httpcomponents-client-ga/">Apache HttpComponents
 * HttpClient</a> to create requests.
 * 
 * <p>
 * Allows to use a pre-configured {@link HttpClient} instance - potentially with
 * authentication, HTTP connection pooling, etc.
 * 
 * <p>
 * Based on
 * {@link org.springframework.http.client.CommonsClientHttpRequestFactory}.
 */
public class HttpComponents4ClientHttpRequestFactory implements
		ClientHttpRequestFactory, DisposableBean {
	private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);

	private Logger logger = Logger.getLogger("web.client");

	/**
	 * HttpClient object we are using
	 */
	private HttpClient httpClient;

	/**
	 * Create a new instance of the
	 * <code>HttpComponents4ClientHttpRequestFactory</code> with a default
	 * {@link DefaultHttpClient}.
	 */
	public HttpComponents4ClientHttpRequestFactory() {
		httpClient = new DefaultHttpClient();
		this.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS);
	}

	/**
	 * Create a new instance of the
	 * <code>HttpComponents4ClientHttpRequestFactory</code> with the given
	 * {@link HttpClient} instance.
	 * 
	 * @param httpClient
	 *            the HttpClient instance to use for this factory
	 */
	public HttpComponents4ClientHttpRequestFactory(HttpClient httpClient) {
		Assert.notNull(httpClient, "httpClient must not be null");
		this.httpClient = httpClient;
	}

	/**
	 * Set the <code>HttpClient</code> used by this factory.
	 */
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	/**
	 * Return the <code>HttpClient</code> used by this factory.
	 */
	public HttpClient getHttpClient() {
		return this.httpClient;
	}

	/**
	 * Set the socket read timeout for the underlying HttpClient. A value of 0
	 * means <em>never</em> timeout.
	 * 
	 * @param timeout
	 *            the timeout value in milliseconds
	 */
	public void setReadTimeout(int timeout) {
		if (timeout < 0) {
			throw new IllegalArgumentException(
					"timeout must be a non-negative value");
		}
		getHttpClient().getParams().setIntParameter(
				CoreConnectionPNames.SO_TIMEOUT, timeout);
	}

	/**
	 * Create the request object from the supplied Spring method enum.
	 * 
	 * Following the Spring approach, we include a template method for
	 * post-processing the request.
	 * 
	 * @param uri
	 * @param httpMethod
	 */
	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod)
			throws IOException {

		HttpRequestBase commonsHttpMethod = createComponentsHttpMethod(
				httpMethod, uri.toString());
		postProcessComponentsHttpMethod(commonsHttpMethod);
		return new HttpComponents4HttpRequest(getHttpClient(),
				commonsHttpMethod);
	}

	/**
	 * Create a Component HttpMethodBase object for the given HTTP method and
	 * URI specification.
	 * 
	 * @param httpMethod
	 *            the HTTP method
	 * @param uri
	 *            the URI
	 * @return the Commons HttpMethodBase object
	 */
	protected HttpRequestBase createComponentsHttpMethod(HttpMethod httpMethod,
			String uri) {
		switch (httpMethod) {
		case GET:
			return new HttpGet(uri);
		case DELETE:
			return new HttpDelete(uri);
		case HEAD:
			return new HttpHead(uri);
		case OPTIONS:
			return new HttpOptions(uri);
		case POST:
			return new HttpPost(uri);
		case PUT:
			return new HttpPut(uri);
		case TRACE:
			return new HttpTrace(uri);
		default:
			throw new IllegalArgumentException("Invalid HTTP method: "
					+ httpMethod);
		}
	}

	/**
	 * Template method that allows for manipulating the {@link HttpRequestBase}
	 * before it is returned as part of a
	 * {@link HttpComponentsClientHttpRequest}.
	 * <p>
	 * The default implementation is empty.
	 * 
	 * @param httpMethod
	 *            the Component HTTP method object to process
	 */
	protected void postProcessComponentsHttpMethod(HttpRequestBase httpMethod) {
	}

	/**
	 * Shutdown hook that closes the underlying {@link ClientConnectionManager}
	 * 's connection pool, if any.
	 */
	public void destroy() {
		ClientConnectionManager connectionManager = getHttpClient()
				.getConnectionManager();
		if (connectionManager instanceof ThreadSafeClientConnManager) {
			((ThreadSafeClientConnManager) connectionManager).shutdown();
		}
	}

}
