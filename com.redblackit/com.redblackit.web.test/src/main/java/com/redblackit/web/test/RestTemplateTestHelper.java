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

package com.redblackit.web.test;

import java.net.URI;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 * @author djnorth
 * 
 *         Class to help with error test cases with RestTemplate
 */
public class RestTemplateTestHelper {

	/**
	 * Rest template
	 */
	private RestTemplate restTemplate;

	/**
	 * Logger to use
	 */
	private Logger logger;

	/**
	 * Constructor taking combination to test
	 * 
	 * @param restTemplate
	 * @param logger
	 */
	public RestTemplateTestHelper(RestTemplate restTemplate, Logger logger) {
		Assert.assertNotNull("restTemplate", restTemplate);
		Assert.assertNotNull("logger", logger);

		this.restTemplate = restTemplate;
		this.logger = logger;
	}

	/**
	 * Test GET for object which should fail
	 * 
	 * @param url
	 * @param urlArgs
	 * @param assertMsg
	 * @param expectedStatusCode
	 */
	public void doGetForHttpStatusCodeException(String url, Object[] urlArgs,
			String assertMsg, HttpStatus expectedStatusCode) {
		StringBuilder builder = buildDebugMsg(url, urlArgs, assertMsg);
		builder.append(":getting object:expecting HttpStatusCodeException");
		try {
			Object obj = restTemplate.getForObject(url, Object.class,
					(urlArgs == null ? new Object[0] : urlArgs));
			Assert.fail(builder.append(":no exception:object returned=")
					.append(obj).toString());
		} catch (HttpStatusCodeException hsce) {
			Assert.assertEquals(
					builder.append(":statusCode:hsce=")
							.append(hsce.getMessage()).toString(),
					expectedStatusCode, hsce.getStatusCode());
			logger.debug(builder.append(":OK"), hsce);
		}
	}

	/**
	 * Test POST for location which should fail
	 * 
	 * @param url
	 * @param objToPost
	 * @param urlArgs
	 * @param assertMsg
	 * @param expectedStatusCode
	 */
	public void doPostForHttpStatusCodeException(String url, Object objToPost,
			Object[] urlArgs, String assertMsg, HttpStatus expectedStatusCode) {
		StringBuilder builder = buildDebugMsg(url, urlArgs, assertMsg);
		builder.append(":posting object=").append(objToPost)
				.append(":expecting HttpStatusCodeException");
		try {
			URI location = restTemplate.postForLocation(url, objToPost,
					(urlArgs == null ? new Object[0] : urlArgs));
			Assert.fail(builder.append(":no exception:location returned=")
					.append(location).toString());
		} catch (HttpStatusCodeException hsce) {
			Assert.assertEquals(
					builder.append(":statusCode:hsce=")
							.append(hsce.getMessage()).toString(),
					expectedStatusCode, hsce.getStatusCode());
			logger.debug(builder.append(":OK"), hsce);
		}
	}

	/**
	 * Test PUT which should fail
	 * 
	 * @param url
	 * @param objToPut
	 * @param urlArgs
	 * @param assertMsg
	 * @param expectedStatusCode
	 */
	public void doPutForHttpStatusCodeException(String url, Object objToPut,
			Object[] urlArgs, String assertMsg, HttpStatus expectedStatusCode) {
		StringBuilder builder = buildDebugMsg(url, urlArgs, assertMsg);
		builder.append(":putting object=").append(objToPut)
				.append(":expecting HttpStatusCodeException");
		try {
			restTemplate.put(url, objToPut, (urlArgs == null ? new Object[0]
					: urlArgs));
			Assert.fail(builder.append(":no exception").toString());
		} catch (HttpStatusCodeException hsce) {
			Assert.assertEquals(
					builder.append(":statusCode:hsce=")
							.append(hsce.getMessage()).toString(),
					expectedStatusCode, hsce.getStatusCode());
			logger.debug(builder.append(":OK"), hsce);
		}
	}

	/**
	 * Test DELETE which should fail
	 * 
	 * @param url
	 * @param urlArgs
	 * @param assertMsg
	 * @param expectedStatusCode
	 */
	public void doDeleteForHttpStatusCodeException(String url,
			Object[] urlArgs, String assertMsg, HttpStatus expectedStatusCode) {

		StringBuilder builder = buildDebugMsg(url, urlArgs, assertMsg);
		builder.append(":deleting object:expecting HttpStatusCodeException");
		try {
			restTemplate.delete(url,
					(urlArgs == null ? new Object[0] : urlArgs));
			Assert.fail(builder.append(":no exception").toString());
		} catch (HttpStatusCodeException hsce) {
			Assert.assertEquals(
					builder.append(":statusCode:hsce=")
							.append(hsce.getMessage()).toString(),
					expectedStatusCode, hsce.getStatusCode());
			logger.debug(builder.append(":OK"), hsce);
		}
	}

	/**
	 * Test HEAD which should fail.
	 * Note that the old HttpClient wrongly causes an IOException instead of a HttpStatusCodeException.
	 * 
	 * @param url
	 * @param urlArgs
	 * @param assertMsg
	 * @param expectedStatusCode
	 */
	public void doHeadForHttpStatusCodeException(String url, Object[] urlArgs,
			String assertMsg, HttpStatus expectedStatusCode) {

		StringBuilder builder = buildDebugMsg(url, urlArgs, assertMsg);
		builder.append(":getting headers:expecting HttpStatusCodeException");
		try {
			restTemplate.headForHeaders(url, (urlArgs == null ? new Object[0]
					: urlArgs));
			Assert.fail(builder.append(":no exception").toString());
		} catch (HttpStatusCodeException hsce) {
			Assert.assertEquals(
					builder.append(":statusCode:hsce=")
							.append(hsce.getMessage()).toString(),
					expectedStatusCode, hsce.getStatusCode());
			logger.debug(builder.append(":OK"), hsce);
		}
	}

	/**
	 * Test OPTIONS which should fail
	 * 
	 * @param url
	 * @param urlArgs
	 * @param assertMsg
	 * @param expectedStatusCode
	 */
	public void doOptionsForHttpStatusCodeException(String url,
			Object[] urlArgs, String assertMsg, HttpStatus expectedStatusCode) {

		StringBuilder builder = buildDebugMsg(url, urlArgs, assertMsg);
		builder.append(":getting options:expecting HttpStatusCodeException");
		try {
			restTemplate.optionsForAllow(url, (urlArgs == null ? new Object[0]
					: urlArgs));
			Assert.fail(builder.append(":no exception").toString());
		} catch (HttpStatusCodeException hsce) {
			Assert.assertEquals(
					builder.append(":statusCode:hsce=")
							.append(hsce.getMessage()).toString(),
					expectedStatusCode, hsce.getStatusCode());
			logger.debug(builder.append(":OK"), hsce);
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return buildToString().toString();
	}

	/**
	 * Create StringBuilder starting with our toString and set message
	 * 
	 * @param url
	 * @param urlArgs
	 * @param assertMsg
	 */
	private StringBuilder buildDebugMsg(String url, Object[] urlArgs,
			String assertMsg) {
		StringBuilder builder = buildToString();

		builder.append(":url=").append(url);

		if (urlArgs != null) {
			builder.append(":urlArgs=").append(Arrays.toString(urlArgs));
		}

		if (assertMsg != null) {
			builder.append(':').append(assertMsg);
		}

		return builder;
	}

	/**
	 * @return builder
	 */
	private StringBuilder buildToString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RestTemplateTestHelper [restTemplate=");
		builder.append(restTemplate);
		builder.append("]");
		return builder;
	}

}
