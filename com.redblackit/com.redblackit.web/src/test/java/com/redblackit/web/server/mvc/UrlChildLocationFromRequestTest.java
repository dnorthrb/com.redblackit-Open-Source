/*
 * Copyright 2002-2011 the original author or authors, or Red-Black IT Ltd, as appropriate.
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

package com.redblackit.web.server.mvc;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for UrlChildLocation constructed with HttpServletRequest
 * 
 * @author djnorth
 * 
 */
public class UrlChildLocationFromRequestTest extends UrlChildLocationTestBase {

	/**
	 * Constructor taking testParameters, and building UrlChildLocation from an
	 * HttpServletRequest returning URL string as StringBuffer
	 * 
	 * @param testParameters
	 */
	public UrlChildLocationFromRequestTest(
            TestParameters testParameters) {
		super(testParameters);
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getRequestURL()).andReturn(
				new StringBuffer(testParameters.getRequestUrl()));
		EasyMock.replay(request);

		UrlChildLocation urlChildLocation = new UrlChildLocation(request);

		EasyMock.verify(request);

		setUrlChildLocationUnderTest(urlChildLocation);
	}

	/**
	 * Test construction with null request fails
	 * 
	 * Test method for
	 * {@link UrlChildLocation#UrlChildLocation(HttpServletRequest)
	 * )}
	 */
	@Test
	public void testConstructNullRequest() {
		try {
			HttpServletRequest request = null;
			new UrlChildLocation(request);
			Assert.fail(assertMsg("null request should result in IllegalArgumentException"));
		} catch (IllegalArgumentException iae) {
			getLogger().trace("expected exception for null request", iae);
		}
	}

	/**
	 * Test construction with non-null request but null URL fails
	 * 
	 * Test method for
	 * {@link UrlChildLocation#UrlChildLocation(HttpServletRequest)
	 * )}
	 */
	@Test
	public void testConstructNonNullRequestNullUrl() {
		try {
			HttpServletRequest request = EasyMock
					.createMock(HttpServletRequest.class);
			EasyMock.expect(request.getRequestURL()).andReturn(null);
			EasyMock.replay(request);
			new UrlChildLocation(request);
			Assert.fail(assertMsg("request with null requestUrl should result in IllegalArgumentException"));
		} catch (IllegalArgumentException iae) {
			getLogger().trace(
					"expected exception for request with null requestUrl", iae);
		}
	}
}
