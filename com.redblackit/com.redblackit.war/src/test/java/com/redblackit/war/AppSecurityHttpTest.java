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

import java.io.IOException;
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

import javax.net.ssl.SSLHandshakeException;

/**
 * @author djnorth
 *         <p/>
 *         Test application security for at http level i.e. before involving
 *         RestTemplate
 *         <p/>
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
    //@Value("#{systemProperties['clientAuthMandatory'] ?: false}")
    private boolean clientAuthMandatory = false;

    /**
     * setUp with trust store
     */
    @Before
    public void setupClientTrustInfo() {
        System.setProperty("javax.net.ssl.trustStore",
                           "/Users/djnorth/client-keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "clientpwd");
    }

    /**
     * Test home page to https URL, with valid certificate, but not Spring security user.
     * <p/>
     * We expect the login form to be presented, as the fall-back.
     */
    @Test
    public void testGetHomePageHttpsGoodClientCertNotUser() throws Exception {
        System.setProperty("javax.net.ssl.keyStore",
                           "/Users/djnorth/client2-keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "client2pwd");
        testGetUrl(baseHttpsUrl, loginTitle, false);
    }

    /**
     * Test home page to https URL, with bad certificate.
     * <p/>
     * We expect the login form to be presented, as the fall-back.
     */
    @Test
    public void testGetHomePageHttpsBadClientCert() throws Exception {
        System.setProperty("javax.net.ssl.keyStore",
                           "/Users/djnorth/untrusted-client-keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "clientpwd");
        testGetUrl(baseHttpsUrl, loginTitle, true);
    }

    /**
     * Test GET using supplied URL, expectedTitle (to identify login or welcome page), and an indication of whether we
     * should authenticate OK, or not.
     *
     * @param url
     * @param expectedTitle
     * @param badClientCertificate
     * @throws Exception
     */
    private void testGetUrl(final String url, final String expectedTitle, final boolean badClientCertificate)
            throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(":url=").append(url).append(":expectedTitle=").append(expectedTitle).append(":badClientCertificate=").append(badClientCertificate).append(":clientAuthMandatory=").append(clientAuthMandatory);
        final String[] spropkeys = {"user.name", "clientAuthMandatory", "javax.net.ssl.keyStore",
                "javax.net.ssl.keyStorePassword", "javax.net.ssl.trustStore",
                "javax.net.ssl.trustStorePassword"};
        for (String spropkey : spropkeys) {
            sb.append("\n  [").append(spropkey).append("]=").append(System.getProperty(spropkey));
        }

        WebConversation conversation = new WebConversation();
        WebResponse response = null;
        try {
            response = conversation.getResponse(url);
            if (clientAuthMandatory && badClientCertificate) {
                Assert.fail("expected exception for bad certificate:but got response=" + response + sb);
            } else {
                Assert.assertNotNull("response" + sb, response);
                logger.info(response);

                String respUrl = response.getURL().toString();

                Assert.assertTrue("URL should start with '" + baseHttpsUrl
                                  + "' ... but was '" + respUrl + "'",
                                  respUrl.startsWith(baseHttpsUrl));
                Assert.assertEquals("Title for response page" + sb, expectedTitle,
                                    response.getTitle().trim());
            }


        } catch (IOException se) {
            if (clientAuthMandatory && se instanceof SocketException) {
                logger.debug("expected exception" + sb, se);
                Throwable t = se.getCause();
                while (t instanceof SocketException) {
                    t = t.getCause();
                }

                if (t != null) {
                    logger.debug("root cause exception" + sb, t);
                }
            } else {
                logger.fatal("unexpected exception:" + sb, se);
                throw new RuntimeException("unexpected exception" + sb, se);
            }

        }
    }

}
