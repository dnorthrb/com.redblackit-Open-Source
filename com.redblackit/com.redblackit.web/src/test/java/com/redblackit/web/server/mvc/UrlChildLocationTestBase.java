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

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit tests for UrlChildLocation
 *
 * @author djnorth
 */
@RunWith(Parameterized.class)
public abstract class UrlChildLocationTestBase {

    @Parameters
    public static List<Object[]> getParameters() {
        final String purl0NoPort = "http://myhost.com/orders";
        final String purl1Port8080 = "http://myhost.com:8080/accounts";
        final String ctxRelUrl = "/accounts";
        final String pthRelUrl = "accounts";
        final String sChildid0 = "o001";
        final Integer iChildid1 = 123;

        Object[][] parameters = {
                {new TestParameters(purl0NoPort, sChildid0,
                        purl0NoPort + '/' + sChildid0)},
                {new TestParameters(purl0NoPort, iChildid1,
                        purl0NoPort + '/' + iChildid1)},
                {new TestParameters(purl0NoPort, null, purl0NoPort)},
                {new TestParameters(purl0NoPort + '/', sChildid0,
                        purl0NoPort + '/' + sChildid0)},
                {new TestParameters(purl0NoPort + '/', iChildid1,
                        purl0NoPort + '/' + iChildid1)},
                {new TestParameters(purl0NoPort + '/', null, purl0NoPort + '/')},
                {new TestParameters(ctxRelUrl, sChildid0,
                        ctxRelUrl + '/' + sChildid0)},
                {new TestParameters(ctxRelUrl, iChildid1,
                        ctxRelUrl + '/' + iChildid1)},
                {new TestParameters(ctxRelUrl, null, ctxRelUrl)},
                {new TestParameters(pthRelUrl, sChildid0,
                        pthRelUrl + '/' + sChildid0)},
                {new TestParameters(pthRelUrl, iChildid1,
                        pthRelUrl + '/' + iChildid1)},
                {new TestParameters(pthRelUrl, null, pthRelUrl)},
                {new TestParameters(purl1Port8080, sChildid0,
                        purl1Port8080 + '/' + sChildid0)},
                {new TestParameters(purl1Port8080, iChildid1,
                        purl1Port8080 + '/' + iChildid1)},
                {new TestParameters(purl1Port8080, null, purl1Port8080)},
                {new TestParameters(purl1Port8080 + '/',
                        sChildid0, purl1Port8080 + '/' + sChildid0)},
                {new TestParameters(purl1Port8080 + '/',
                        iChildid1, purl1Port8080 + '/' + iChildid1)},
                {new TestParameters(purl1Port8080 + '/', null,
                        purl1Port8080 + '/')}};

        return Arrays.asList(parameters);
    }

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger("web.server");

    /**
     * Test parameters
     */
    private final TestParameters testParameters;

    /**
     * Our helper under test
     */
    private UrlChildLocation urlChildLocationUnderTest;

    /**
     * Constructor taking test parameters
     *
     * @param testParameters
     */
    public UrlChildLocationTestBase(TestParameters testParameters) {
        this.testParameters = testParameters;
    }

    /**
     * Test getRequestUrl gives what we supply in constructor
     * <p/>
     * Test method for
     * {@link UrlChildLocation#getRequestUrl()} .
     */
    @Test
    public void testSetAndGetRequestUrl() {
        Assert.assertEquals(assertMsg("getRequestUrl and supplied requestUrl"),
                testParameters.getRequestUrl(), getUrlChildLocationUnderTest()
                .getRequestUrl());
    }

    /**
     * Test getChildId gives what we supply to setChildId
     * <p/>
     * Test method for
     * {@link UrlChildLocation#getChildId()} .
     */
    @Test
    public void testSetAndGetChildId() {
        getUrlChildLocationUnderTest().setChildId(testParameters.getChildId());
        Assert.assertEquals(assertMsg("getChildId and supplied childId"),
                testParameters.getChildId(), getUrlChildLocationUnderTest()
                .getChildId());
    }

    /**
     * Ensure childUrl created correctly with no childId
     * <p/>
     * Test method for
     * {@link UrlChildLocation#createLocationUrl()}
     * .
     */
    @Test
    public void testCreateChildUrlWithoutSetChildId() {
         String actualChildUrl = getUrlChildLocationUnderTest()
                .createLocationUrl();
        Assert.assertEquals(assertMsg("no childUrl"),
                testParameters.getRequestUrl(), actualChildUrl);
    }

    /**
     * Ensure childUrl created correctly with supplied childId
     * <p/>
     * Test method for
     * {@link UrlChildLocation#createLocationUrl()}
     * .
     */
    @Test
    public void testCreateChildUrlAfterSetChildId() {
        Object childId = testParameters.getChildId();
        getUrlChildLocationUnderTest().setChildId(childId);
        String actualChildUrl = getUrlChildLocationUnderTest()
                .createLocationUrl();
        Assert.assertEquals(assertMsg("childUrl"),
                testParameters.getExpectedChildUrl(), actualChildUrl);
    }

    /**
     * Ensure equals works correctly
     * <p/>
     * Test method for
     * {@link UrlChildLocation#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject() {
        Assert.assertTrue("eq same object", getUrlChildLocationUnderTest()
                .equals(getUrlChildLocationUnderTest()));
        UrlChildLocation equalOther = new UrlChildLocation(
                testParameters.getRequestUrl());
        Assert.assertTrue("eq different object with same URL",
                getUrlChildLocationUnderTest().equals(equalOther));

        Assert.assertFalse("ne null",
                getUrlChildLocationUnderTest().equals(null));
        Assert.assertFalse("ne String URL", getUrlChildLocationUnderTest()
                .equals(testParameters.getRequestUrl()));
        Assert.assertFalse("ne object of different type",
                getUrlChildLocationUnderTest().equals(testParameters));

        UrlChildLocation unequalOther = new UrlChildLocation(
                testParameters.getRequestUrl() + "different");
        Assert.assertFalse("ne object with different URL",
                getUrlChildLocationUnderTest().equals(testParameters));
    }

    /**
     * Get object state, including parameters
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getName());
        builder.append(" [testParameters=");
        builder.append(testParameters);
        builder.append(", urlChildLocationUnderTest=");
        builder.append(getUrlChildLocationUnderTest());
        builder.append("]");
        return builder.toString();
    }

    /**
     * Helper to add context to assertion message
     *
     * @param msg to enhance
     * @return result
     */
    protected String assertMsg(String msg) {
        StringBuilder sb = new StringBuilder(msg);
        sb.append(':').append(this);
        return sb.toString();
    }

    /**
     * @param urlChildLocationUnderTest the urlChildLocationUnderTest to set
     */
    protected void setUrlChildLocationUnderTest(
            UrlChildLocation urlChildLocationUnderTest) {
        this.urlChildLocationUnderTest = urlChildLocationUnderTest;
    }

    /**
     * @return the urlChildLocationUnderTest
     */
    protected UrlChildLocation getUrlChildLocationUnderTest() {
        return urlChildLocationUnderTest;
    }

    /**
     * @return the logger
     */
    protected Logger getLogger() {
        return logger;
    }

    protected static class TestParameters {

        /**
         * Equivalent string url
         */
        private final String requestUrl;

        /**
         * Child ID
         */
        private final Object childId;

        /**
         * Expected child URL string
         */
        private final String expectedChildUrl;

        /**
         * @param requestUrl
         * @param childId
         * @param expectedChildUrl
         */
        public TestParameters(String requestUrl, Object childId,
                              String expectedChildUrl) {
            super();
            this.requestUrl = requestUrl;
            this.childId = childId;
            this.expectedChildUrl = expectedChildUrl;
        }

        /**
         * @return the expectedChildUrl
         */
        public String getExpectedChildUrl() {
            return expectedChildUrl;
        }

        /**
         * @return the requestUrl
         */
        public String getRequestUrl() {
            return requestUrl;
        }

        /**
         * @return the childId
         */
        public Object getChildId() {
            return childId;
        }

        /*
           * (non-Javadoc)
           *
           * @see java.lang.Object#toString()
           */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("RequestUrlHelperParameters [requestUrl=");
            builder.append(requestUrl);
            builder.append(", childId=");
            builder.append(childId);
            builder.append(", expectedChildUrl=");
            builder.append(expectedChildUrl);
            builder.append("]");
            return builder.toString();
        }

    }
}
