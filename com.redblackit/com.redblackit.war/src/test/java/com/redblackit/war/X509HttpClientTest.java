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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLPeerUnverifiedException;

import junit.framework.Assert;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author djnorth
 * 
 *         Test using X509HttpClientFactory bean, and also plain
 *         DefaultHttpCLient for non-client auth.
 *         
 *         The system property
 *         clientAuthMandatory can be used to allow successful tests with
 *         servers such as GlassFish that do not allow optional client
 *         authentication ("want").
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class X509HttpClientTest {

	private Logger logger = Logger.getLogger("security");

	/**
	 * X509 Http Clients
	 */
	@Autowired
	@Qualifier("httpClientGoodCert")
	private HttpClient httpClientGoodCert;

	@Autowired
	@Qualifier("httpClientGoodCertNotUser")
	private HttpClient httpClientGoodCertNotUser;

	@Autowired
	@Qualifier("httpClientBadCert")
	private HttpClient httpClientBadCert;

	/**
	 * Commonly used attributes from testProperties and messages
	 */

	@Value("#{'https://' + testProperties.serverHost + ':' + ( systemProperties['httpsPort.override'] ?: testProperties.httpsPort ) + '/' + testProperties.appPath}")
	private String baseHttpsUrl;
	@Value("#{messages['login.title']}")
	private String loginTitle;
	@Value("#{messages['welcome.title']}")
	private String welcomeTitle;
	@Value("#{systemProperties['clientAuthMandatory'] ?: false}")
	private boolean clientAuthMandatory = false;

	/**
	 * Make connection with good certificate client and verify response is home
	 * page
	 */
	@Test
	public void testX509ConnectHttpsGoodCertificate() throws Exception {
		validateResponseToRequest(httpClientGoodCert, baseHttpsUrl,
				welcomeTitle);
	}

	/**
	 * Make connection with good certificate client which is not a user in Spring security and verify response is login
	 * page
	 */
	@Test
	public void testX509ConnectHttpsGoodCertificateNotUser() throws Exception {
		validateResponseToRequest(httpClientGoodCertNotUser, baseHttpsUrl,
				loginTitle);
	}

	/**
	 * Make connection with bad certificate client and verify response is login
	 * page
	 */
	@Test
	public void testX509ConnectHttpsBadCertificate() throws Exception {
		if (clientAuthMandatory) {
			validateRequestFails(httpClientBadCert, baseHttpsUrl);
		} else {
			validateResponseToRequest(httpClientBadCert, baseHttpsUrl,
					loginTitle);
		}
	}

	/**
	 * Do request and validate response
	 * 
	 * @param httpClientToTest
	 * @param url
	 * @param expectedTitle
	 */
	private void validateResponseToRequest(HttpClient httpClientToTest,
			String url, String expectedTitle) throws Exception {
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClientToTest.execute(request);

		logger.info("request:" + request);

		StatusLine status = response.getStatusLine();
		Assert.assertEquals("Status code:" + status, HttpStatus.SC_OK,
				status.getStatusCode());
		logger.info(status);

		BasicResponseHandler responseHandler = new BasicResponseHandler();
		String responseBody = responseHandler.handleResponse(response).trim();
		final int xmlstart = responseBody.indexOf("<?xml");
		if (xmlstart > 0) {
			responseBody = responseBody.substring(xmlstart);
		}
		logger.debug("responseBody*>>");
		logger.debug(responseBody);
		logger.debug("responseBody*<<");

		Pattern titlePattern = Pattern.compile("(<title>)([^<]+)(</title>)");
		Matcher matcher = titlePattern.matcher(responseBody);
		Assert.assertTrue("title element found", matcher.find());

		String title = matcher.group(2);
		Assert.assertEquals("title", expectedTitle, title);
	}

	/**
	 * Validate request fails (for bad certificates when server only supports
	 * clientAuthMandatory e.g. GlassFish
	 * 
	 * @param httpClientToTest
	 * @param url
	 */
	private void validateRequestFails(HttpClient httpClientToTest, String url)
			throws Exception {

		HttpGet request = new HttpGet(url);
		logger.info("request:" + request);
		try {
			HttpResponse response = httpClientToTest.execute(request);

			StatusLine status = response.getStatusLine();
			logger.error(status);

			BasicResponseHandler responseHandler = new BasicResponseHandler();
			String responseBody = responseHandler.handleResponse(response)
					.trim();
			final int xmlstart = responseBody.indexOf("<?xml");
			if (xmlstart > 0) {
				responseBody = responseBody.substring(xmlstart);
			}
			logger.error("responseBody*>>");
			logger.error(responseBody);
			logger.error("responseBody*<<");

			Assert.fail("expected exception for bad certifiate:but got response");
		} catch (SSLPeerUnverifiedException pue) {
			logger.debug("expected exception", pue);
		}

	}

}
