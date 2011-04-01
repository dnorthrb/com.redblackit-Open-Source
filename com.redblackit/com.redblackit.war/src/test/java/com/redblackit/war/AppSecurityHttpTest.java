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

import java.net.SocketException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;

/**
 * @author djnorth
 * 
 *         Test application security for at http level i.e. before involving
 *         RestTemplate
 * 
 *         The system property clientAuthMandatory can be used to allow
 *         successful tests with servers such as GlassFish that do not allow
 *         optional client authentication ("want").
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AppSecurityHttpTest {

	private Logger logger = Logger.getLogger("web.client");

	/**
	 * Commonly used attributes from testProperties and messages
	 */
	@Value("#{'https://' + testProperties.serverHost + ':' + ( systemProperties['httpsPort.override'] ?: testProperties.httpsPort ) + '/' + testProperties.appPath}")
	private String baseHttpsUrl;
	@Value("#{messages['login.title']}")
	private String loginTitle;
	@Value("#{systemProperties['clientAuthMandatory'] ?: false}")
	private boolean clientAuthMandatory = false;

	/**
	 * Setup invalid certificates
	 */
	@Before
	public void setUp() {
		System.setProperty("javax.net.ssl.keyStore",
				"/Users/djnorth/untrusted-client-keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "clientpwd");
	}

	/**
	 * Test home page to https URL, with valid certificate.
	 * 
	 * We expect the login form to be presented, as the fall-back.
	 */
	@Test
	public void testGetHomePageHttpsBadClientCert() throws Exception {
		testGetUrl(baseHttpsUrl, loginTitle);
	}

	/**
	 * @param inaccessibleUrl
	 * @throws Exception
	 */
	private void testGetUrl(final String url, final String expectedTitle)
			throws Exception {
		logger.info(url + "  :expecting " + expectedTitle);
		final String[] spropkeys = { "user.name", "javax.net.ssl.keyStore",
				"javax.net.ssl.keyStorePassword", "javax.net.ssl.trustStore",
				"javax.net.ssl.trustStorePassword" };
		for (String spropkey : spropkeys) {
			logger.info("  [" + spropkey + "]=" + System.getProperty(spropkey));
		}

		WebConversation conversation = new WebConversation();
		WebResponse response = null;
		if (clientAuthMandatory) {
			try {
				response = conversation.getResponse(url);
				logger.error("response=" + response);
				Assert.fail("expected exception for bad certifiate:but got response");

			} catch (SocketException se) {
				logger.debug("expected exception", se);
				Throwable t = se.getCause();
				while (t instanceof SocketException) {
					t = t.getCause();
				}

				if (t != null) {
					logger.debug("root cause exception", t);
				}

			}
		} else {
			response = conversation.getResponse(url);
			Assert.assertNotNull("response", response);
			logger.info(response);

			String respUrl = response.getURL().toString();

			Assert.assertTrue("URL should start with '" + baseHttpsUrl
					+ "' ... but was '" + respUrl + "'",
					respUrl.startsWith(baseHttpsUrl));
			Assert.assertEquals("Title for response page", expectedTitle,
					response.getTitle().trim());
		}
	}

}
