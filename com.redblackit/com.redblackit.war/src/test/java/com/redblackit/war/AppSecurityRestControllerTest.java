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


package com.redblackit.war;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.redblackit.web.test.RestTemplateTestHelper;

/**
 * @author djnorth
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/com/redblackit/war/RestControllerClientTest-context.xml")
public class AppSecurityRestControllerTest {
	
	private Logger logger = Logger.getLogger("web.client");

	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * helper
	 */
	private RestTemplateTestHelper helper;

	/**
	 * Commonly used attributes from testProperties and messages
	 */
	@Value("#{'https://' + testProperties.serverHost + ':' + ( systemProperties['httpsPort.override'] ?: testProperties.httpsPort ) + '/' + testProperties.appPath}")
	private String baseHttpsUrl;
	
	/**
	 * URL for about
	 */
	private String inaccessibleUrl;

	/**
	 * Set-up
	 */
	@Before
	public void setUp()
	{
		if (helper == null)
		{
			helper = new RestTemplateTestHelper(restTemplate, logger);
			inaccessibleUrl = baseHttpsUrl + "about";
		}
	}
	
	/**
	 * Test GET method for human page about (should get 403)
	 * {@link com.redblackit.web.controller.AdminRestController#getVersion()}
	 * with https.
	 */
	@Test
	public void testGetAbout() {
		helper.doGetForHttpStatusCodeException(inaccessibleUrl, null, "inaccessible URL for REST", HttpStatus.FORBIDDEN);
	}

	/**
	 * Test PUT method for human page about (should get 403)
	 * {@link com.redblackit.web.controller.AdminRestController#getVersion()}
	 * with https.
	 */
	@Test
	public void testPutAbout() {
		helper.doPutForHttpStatusCodeException(inaccessibleUrl, "About", null, "inaccessible URL for REST", HttpStatus.FORBIDDEN);
	}

	/**
	 * Test POST method for human page about (should get 403)
	 * {@link com.redblackit.web.controller.AdminRestController#getVersion()}
	 * with https.
	 */
	@Test
	public void testPostAbout() {
		helper.doPostForHttpStatusCodeException(inaccessibleUrl, "About", null, "inaccessible URL for REST", HttpStatus.FORBIDDEN);
	}

	/**
	 * Test DELETE method for human page about (should get 403)
	 * {@link com.redblackit.web.controller.AdminRestController#getVersion()}
	 * with https.
	 */
	@Test
	public void testDeleteAbout() {
		helper.doDeleteForHttpStatusCodeException(inaccessibleUrl, null, "inaccessible URL for REST", HttpStatus.FORBIDDEN);
	}

	/**
	 * Test HEAD method for human page about (should get 403)
	 * {@link com.redblackit.web.controller.AdminRestController#getVersion()}
	 * with https.
	 */
	@Test
	public void testHeadAbout() {
		helper.doHeadForHttpStatusCodeException(inaccessibleUrl, null, "inaccessible URL for REST", HttpStatus.FORBIDDEN);
	}

	/**
	 * Test OPTIONS method for human page about (should get 403)
	 * {@link com.redblackit.web.controller.AdminRestController#getVersion()}
	 * with https.
	 */
	@Test
	public void testOptionsAbout() {
		helper.doOptionsForHttpStatusCodeException(inaccessibleUrl, null, "inaccessible URL for REST", HttpStatus.FORBIDDEN);
	}

}
