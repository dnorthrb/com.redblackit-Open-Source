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

package com.redblackit.war;

import com.redblackit.version.CompositeVersionInfo;
import com.redblackit.version.CompositeVersionInfoMap;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

/**
 * @author djnorth
 *         <p/>
 *         Integration test for AdminRestController using RestTemplate
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/com/redblackit/war/RestControllerClientTest-context.xml")
public class AdminRestControllerClientTestBase {

    private Logger logger = Logger.getLogger("web.client");

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CompositeVersionInfo expectedVersionInfo;

    /**
     * Commonly used attributes from testProperties and messages
     */
    @Value("#{'https://' + testProperties.serverHost + ':' + ( systemProperties['httpsPort.override'] ?: testProperties.httpsPort ) + '/' + testProperties.appPath + urlProperties.servletPath}")
    private String baseHttpsUrl;

    /**
     * Test method for
     * {@link com.redblackit.web.controller.AdminRestController#getVersionSummary()}
     * with https.
     */
    @Test
    public void testGetVersionSummary() throws Throwable {
        final String url = baseHttpsUrl + "rest/version/summary";
        try {
            String versionString = restTemplate.getForObject(url, String.class);
            logger.debug("versionString=" + versionString);
            Assert.assertEquals("versionInfo.getVersionString():",
                                expectedVersionInfo.getVersionString(), versionString);
        } catch (Throwable t) {
            logger.fatal(url, t);
            throw t;
        }
    }

    /**
     * Test method for
     * {@link com.redblackit.web.controller.AdminRestController#getVersion()}
     * with https.
     */
    @Test
    public void testGetVersion() {
        final String url = baseHttpsUrl + "rest/version";
        CompositeVersionInfo versionInfo = restTemplate.getForObject(url,
                                                                     CompositeVersionInfoMap.class);
        logger.debug("versionInfo=" + versionInfo);
        Assert.assertEquals("versionInfo",
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
