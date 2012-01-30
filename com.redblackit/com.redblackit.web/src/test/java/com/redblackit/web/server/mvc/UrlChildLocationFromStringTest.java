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

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for UrlChildLocation constructed with requestUrl string
 * 
 * @author djnorth
 *
 */
public class UrlChildLocationFromStringTest extends UrlChildLocationTestBase {

	/**
	 * Constructor taking testParameters, and building UrlChildLocation from the URL string
	 * 
	 * @param testParameters
	 */
	public UrlChildLocationFromStringTest(
            TestParameters testParameters) {
		super(testParameters);
		setUrlChildLocationUnderTest(new UrlChildLocation(
                testParameters.getRequestUrl()));
	}
	
	/**
	 * Test construction with null requestUrl fails
	 * 
	 * Test method for
	 * {@link UrlChildLocation#UrlChildLocation(String))}
	 */
	@Test
	public void testConstructNullRequestUrl() {
		try
		{
			String rurl = null;
			new UrlChildLocation(rurl);
			Assert.fail(assertMsg("null requestUrl should result in IllegalArgumentException"));
		} 
		catch (IllegalArgumentException iae) {
			getLogger().trace("expected exception for null requestUrl", iae);
		}
	}
}
