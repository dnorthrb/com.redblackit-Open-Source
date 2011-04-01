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

import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.redblackit.version.VersionInfo;
import com.redblackit.version.VersionInfoFromProperties;

/**
 * @author djnorth
 * 
 *         Integration test for AdminRestController using RestTemplate
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/com/redblackit/war/RestControllerClientTest-context.xml")
public class AdminRestControllerClientTest {

	private Logger logger = Logger.getLogger("web.client");

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	@Qualifier("versionProperties")
	private Properties versionProps;

	/**
	 * Commonly used attributes from testProperties and messages
	 */
	@Value("#{'https://' + testProperties.serverHost + ':' + ( systemProperties['httpsPort.override'] ?: testProperties.httpsPort ) + '/' + testProperties.appPath}")
	private String baseHttpsUrl;

	/**
	 * Test method for
	 * {@link com.redblackit.web.controller.AdminRestController#getVersionSummary()}
	 * with https.
	 */
	@Test
	public void testGetVersionSummary() {
		final String url = baseHttpsUrl + "rest/version/summary";
		String versionString = restTemplate.getForObject(url, String.class);
		logger.debug("versionString=" + versionString);
		VersionInfo expectedVersionInfo = new VersionInfoFromProperties(versionProps);
		Assert.assertEquals("versionInfo.getVersionString():",
				expectedVersionInfo.getVersionString(), versionString);
	}

	/**
	 * Test method for
	 * {@link com.redblackit.web.controller.AdminRestController#getVersion()}
	 * with https.
	 */
	@Test
	public void testGetVersion() {
		final String url = baseHttpsUrl + "rest/version";
		VersionInfo versionInfo = restTemplate.getForObject(url,
				VersionInfoFromProperties.class);
		logger.debug("versionInfo=" + versionInfo);
		VersionInfo expectedVersionInfo = new VersionInfoFromProperties(versionProps);
		Assert.assertEquals("versionInfo.getVersionMap():",
				expectedVersionInfo, versionInfo);
	}

	/**
	 * Test method for
	 * {@link com.redblackit.web.controller.AdminRestController#getVersionHead()}
	 * with https.
	 */
	@Test
	public void testGetVersionHead() {
		final String url = baseHttpsUrl + "rest/version";
		HttpHeaders headers = restTemplate.headForHeaders(url);
		logger.debug("headers=" + headers);
	}
}
