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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.redblackit.web.KeyAndTrustStoreInfo;
import com.redblackit.web.server.DefaultEmbeddedJettyServer;
import com.redblackit.web.server.EchoServlet;
import com.redblackit.web.server.EmbeddedJettyServer;
import com.redblackit.web.server.HostNetUtils;

/**
 * @author djnorth
 * 
 *         Unit test for X509HttpClientFactory
 */
public class X509HttpClientFactoryTest {

	/**
	 * Path for echo servlet
	 */
	private static final String ECHO_PATH = "/echo";

	/**
	 * Full URL for deployed echo server
	 */
	private static String echoUrl;

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger("web.client");

	/**
	 * Embedded server
	 */
	private static EmbeddedJettyServer server;

	/**
	 * Object under test
	 */
	private X509HttpClientFactoryBean x509HttpClientFactory;

	/**
	 * Set up embedded server
	 */
	@BeforeClass
	public static void setUpServer() {
		final int httpsPort = HostNetUtils.getFreePort(8443);
		server = new DefaultEmbeddedJettyServer(0, httpsPort,
				KeyAndTrustStoreInfo.SERVER1_KS,
				KeyAndTrustStoreInfo.SERVER1_KS_PWD);
		ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.getServer().setHandler(context);
		context.addServlet(new ServletHolder(new EchoServlet()), ECHO_PATH);
		Assert.assertTrue("Server not started", server.startWait());

		String localhost = HostNetUtils.getLocalHostname();

		echoUrl = "https://" + localhost + ':' + httpsPort + ECHO_PATH;
		logger.info("echoUrl=" + echoUrl);
	}

	/**
	 * Test afterPropertiesSet for undefined keyStore
	 */
	@Test
	public void testAfterPropertiesSet() throws Exception {

		x509HttpClientFactory = new X509HttpClientFactoryBean();
		logger.info("x509HttpClientFactory=" + x509HttpClientFactory);

		try {
			x509HttpClientFactory.afterPropertiesSet();
			Assert.fail("expected exception for missing properties");
		} catch (IllegalArgumentException iae) {
			logger.info("expected exception", iae);
		}
	}

	/**
	 * Test object creation with default values, using system properties
	 */
	@Test
	public void testGetObjectWithDefaults() throws Exception {
		System.setProperty("javax.net.ssl.keyStore",
				KeyAndTrustStoreInfo.CLIENT0_KS);
		System.setProperty("javax.net.ssl.keyStorePassword",
				KeyAndTrustStoreInfo.CLIENT0_KS_PWD);
		x509HttpClientFactory = new X509HttpClientFactoryBean();
		logger.info("x509HttpClientFactory=" + x509HttpClientFactory);
		x509HttpClientFactory.afterPropertiesSet();

		Assert.assertEquals("getObjectType", HttpClient.class,
				x509HttpClientFactory.getObjectType());
		Assert.assertEquals("isSingleton", false,
				x509HttpClientFactory.isSingleton());

		HttpClient httpClient = x509HttpClientFactory.getObject();

		logger.info("httpClient=" + httpClient);
		Assert.assertNotNull(httpClient);

		checkConnection(httpClient);
	}

	/**
	 * Test object creation with client0 specific files and passwords
	 */
	@Test
	public void testGetObjectWithFilesAndPasswords0() throws Exception {
		x509HttpClientFactory = new X509HttpClientFactoryBean();
		x509HttpClientFactory.setKeyStore(KeyAndTrustStoreInfo.CLIENT0_KS);
		x509HttpClientFactory
				.setKeyStorePassword(KeyAndTrustStoreInfo.CLIENT0_KS_PWD);
		x509HttpClientFactory.setTrustStore(KeyAndTrustStoreInfo.CLIENT0_TS);
		x509HttpClientFactory
				.setTrustStorePassword(KeyAndTrustStoreInfo.CLIENT0_TS_PWD);
		logger.info("x509HttpClientFactory=" + x509HttpClientFactory);
		x509HttpClientFactory.afterPropertiesSet();

		Assert.assertEquals("getObjectType", HttpClient.class,
				x509HttpClientFactory.getObjectType());
		Assert.assertEquals("isSingleton", false,
				x509HttpClientFactory.isSingleton());

		HttpClient httpClient = x509HttpClientFactory.getObject();

		logger.info("httpClient=" + httpClient);
		Assert.assertNotNull(httpClient);

		checkConnection(httpClient);
	}

	/**
	 * Test object creation with client1 specific files and passwords
	 */
	@Test
	public void testGetObjectWithFilesAndPasswords1() throws Exception {
		x509HttpClientFactory = new X509HttpClientFactoryBean();
		x509HttpClientFactory.setKeyStore(KeyAndTrustStoreInfo.CLIENT1_KS);
		x509HttpClientFactory
				.setKeyStorePassword(KeyAndTrustStoreInfo.CLIENT1_KS_PWD);
		x509HttpClientFactory.setTrustStore(KeyAndTrustStoreInfo.CLIENT1_TS);
		x509HttpClientFactory
				.setTrustStorePassword(KeyAndTrustStoreInfo.CLIENT1_TS_PWD);
		logger.info("x509HttpClientFactory=" + x509HttpClientFactory);
		x509HttpClientFactory.afterPropertiesSet();

		Assert.assertEquals("getObjectType", HttpClient.class,
				x509HttpClientFactory.getObjectType());
		Assert.assertEquals("isSingleton", false,
				x509HttpClientFactory.isSingleton());

		HttpClient httpClient = x509HttpClientFactory.getObject();

		logger.info("httpClient=" + httpClient);
		Assert.assertNotNull(httpClient);

		checkConnection(httpClient);
	}

	/**
	 * Tear down embedded server
	 */
	@AfterClass
	public static void tearDownServer() {
		Assert.assertTrue("Server not stopped", server.stopWait());
	}

	/**
	 * Common method for testing client works
	 * 
	 */
	private void checkConnection(HttpClient httpClient) throws Exception {
		Assert.assertTrue("Server not started", server.getServer().isStarted());

		HttpPost req = new HttpPost(echoUrl);
		req.setEntity(new StringEntity("test"));
		HttpResponse resp = httpClient.execute(req);

		StatusLine status = resp.getStatusLine();
		logger.debug("status:" + status);
		for (Header header : resp.getAllHeaders()) {
			logger.debug(" [" + header.getName() + "]=" + header.getValue());
		}
		logger.debug("headers:" + resp.getAllHeaders());
		Assert.assertEquals("status should be OK:" + status, HttpStatus.SC_OK,
				status.getStatusCode());

		HttpEntity entity = resp.getEntity();
		Assert.assertNotNull("response entity", entity);
		logger.debug("entity=" + entity);

		InputStream is = null;
		StringBuffer respbody = new StringBuffer();
		try {

			is = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String inputLine = null;
			int linei = 0;
			while ((inputLine = reader.readLine()) != null) {
				logger.debug("response line[" + linei + "]:" + inputLine);
				if (respbody.length() > 0)
				{
					respbody.append('\n');
				}
				respbody.append(inputLine);
				++linei;
			}
			
			Assert.assertEquals("request and response body", "test", respbody.toString());

		} finally {

			// Closing the input stream will trigger connection release
			is.close();
		}

	}
}
