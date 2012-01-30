package com.redblackit.web.server.mvc;

import org.apache.log4j.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unit test class for {@link LoggingInterceptor}.
 * There is a separate (small) test class to test for correct treatment of null logger and servlet config.
 *
 * @author Dominic North
 */
@RunWith(Parameterized.class)
public class LoggingInterceptorTest {

    public static final String LOGGER_NAME = "testLoggingInterceptor";

    public static final String SERVLET_NAME = "test-servlet";

    /**
     * Name parameter combinations
     */
    public static final NameParameters[] NAME_PARAMETER_DATA = new NameParameters[]{
            new NameParameters(LOGGER_NAME, null, "^"),
            new NameParameters(null, SERVLET_NAME, "^"),
            new NameParameters(LOGGER_NAME, SERVLET_NAME, "^" + SERVLET_NAME + " ")
    };

    /**
     * Fixed lengths
     */
    public static final Integer[] FIXED_MAX_LOGGED_BODY_LENGTHS = new Integer[]{null, Integer.MAX_VALUE, 0, 1, -1};

    /**
     * Test data method
     */
    @Parameterized.Parameters
    public static List<Object[]> testData() {

        final HttpHeaders requestHeaders0 = new HttpHeaders();
        requestHeaders0.setContentType(MediaType.TEXT_PLAIN);
        requestHeaders0.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));

        final String schemeHttp = "http";
        final String schemeHttps = "https";

        final HttpHeaders responseHeaders0 = new HttpHeaders();
        responseHeaders0.setContentType(MediaType.TEXT_PLAIN);

        final ModelAndView modelAndView0 = new ModelAndView("view0");
        modelAndView0.addObject("attribute0", "attributeValue0");

        /**
         * All name parameter combinations
         */

        /**
         * Test parameter combinations
         */
        RequestResponseParameters[] requestResponseParameterData = {
                new RequestResponseParameters(requestHeaders0,
                        HttpMethod.GET.toString(),
                        schemeHttp,
                        "myhost.com",
                        "8080",
                        "context1",
                        "servlet1",
                        "path1",
                        "entityId=id1&name=name1",
                        "some content which is nice",
                        HttpStatus.OK.value(), responseHeaders0, modelAndView0),
                new RequestResponseParameters(requestHeaders0,
                        HttpMethod.GET.toString(),
                        schemeHttps,
                        "myhost.com",
                        "8443",
                        "context2",
                        "servlet2",
                        "path2/entities/id1/names/name1",
                        null,
                        "a little bit of content",
                        666, responseHeaders0, modelAndView0),
                new RequestResponseParameters(requestHeaders0,
                        HttpMethod.PUT.toString(),
                        schemeHttps,
                        "myhost.com",
                        "8443",
                        "context2",
                        "servlet2",
                        "path2/entities/id1/names/name1",
                        null,
                        "a little bit of content",
                        HttpStatus.NO_CONTENT.value(), responseHeaders0, null)
        };

        final List<Object[]> parameterCombinations = new ArrayList<Object[]>();
        for (NameParameters nameParameters : NAME_PARAMETER_DATA) {
            for (RequestResponseParameters requestResponseParameters : requestResponseParameterData) {
                for (Integer fixedMaxLoggedBodyLength : FIXED_MAX_LOGGED_BODY_LENGTHS) {
                    Object[] testData = {nameParameters, requestResponseParameters, fixedMaxLoggedBodyLength};
                    parameterCombinations.add(testData);
                }

                if (requestResponseParameters.getRequestBody() != null && requestResponseParameters.getRequestBody().length() > 1) {
                    Object[] testData = {nameParameters, requestResponseParameters, requestResponseParameters.getRequestBody().length() - 1};
                    parameterCombinations.add(testData);
                }
            }
        }

        return parameterCombinations;
    }

    /**
     * LoggingInterceptor under test
     */
    private LoggingInterceptor loggingInterceptor;

    /**
     * Logger
     */
    private Logger logger;

    /**
     * Appender (Mock)
     */
    private TestAppender appender;

    /**
     * ServletConfig (mock)
     */
    private ServletConfig servletConfig;

    /**
     * HttpServletRequest (Mock)
     */
    private HttpServletRequest request;

    /**
     * HttpServletRequest (Mock)
     */
    private HttpServletResponse response;

    /**
     * Dummy handler (not called)
     */
    private TestController handler;

    /**
     * Name combination parameters
     */
    private final NameParameters nameParameters;

    /**
     * Request/Response parameters
     */
    private final RequestResponseParameters requestResponseParameters;

    /**
     * Maximum logged body length parameter
     */
    private final Integer maxLoggedBodyLength;

    /**
     * Expected maxLoggedBodyLength in interceptor
     */
    private final int expectedMaxLoggedBodyLength;

    /**
     * Constructor taking test parameters
     *
     * @param nameParameters
     * @param requestResponseParameters
     * @param maxLoggedBodyLength
     */
    public LoggingInterceptorTest(NameParameters nameParameters, RequestResponseParameters requestResponseParameters, Integer maxLoggedBodyLength) {
        this.nameParameters = nameParameters;
        this.requestResponseParameters = requestResponseParameters;
        this.maxLoggedBodyLength = maxLoggedBodyLength;
        if (maxLoggedBodyLength == null || maxLoggedBodyLength.intValue() < 0) {
            expectedMaxLoggedBodyLength = Integer.MAX_VALUE;
        } else {
            expectedMaxLoggedBodyLength = maxLoggedBodyLength.intValue();
        }
    }

    /**
     * Set up interceptor with logger, using our appender.
     */
    @Before
    public void setupInterceptor() throws Exception {
        loggingInterceptor = new LoggingInterceptor();
        String expectedLoggerName = null;
        if (nameParameters.getLoggerName() != null) {
            expectedLoggerName = nameParameters.getLoggerName();
            loggingInterceptor.setLoggerName(nameParameters.getLoggerName());
        }

        if (nameParameters.getServletName() != null) {
            if (nameParameters.getLoggerName() == null) {
                expectedLoggerName = nameParameters.getServletName();
            }
            servletConfig = new MockServletConfig(nameParameters.getServletName());
        }

        Assert.assertNotNull(expectedLoggerName);

        loggingInterceptor.initializeServletName(servletConfig);
        loggingInterceptor.afterPropertiesSet();
        if (maxLoggedBodyLength != null) {
            loggingInterceptor.setMaxLoggedBodyLength(maxLoggedBodyLength.intValue());
        }

        request = EasyMock.createMock(HttpServletRequest.class);
        response = EasyMock.createMock(HttpServletResponse.class);

        appender = new TestAppender();

        logger = Logger.getLogger(expectedLoggerName);

        logger.addAppender(appender);

        handler = new TestController();
    }


    /**
     * Ensure maxBodyLength has been set correctly
     */
    @Test
    public void testMaxLoggedBodyLengthSet() {
        Assert.assertEquals(assertMsg("maxLoggedBodyLength"), expectedMaxLoggedBodyLength, loggingInterceptor.getMaxLoggedBodyLength());
    }

    /**
     * Test preHandle logging with no override level, logger set to DEBUG
     */
    @Test
    public void testPreHandleLogDEBUG() throws Exception {
        logger.setLevel(Level.DEBUG);
        verifyPreHandleNoLogging();
    }


    /**
     * Test preHandle logging with explicit set override level DEBUG, logger set to DEBUG, unlimited max length
     */
    @Test
    public void testPreHandleLogDEBUGOverrideDEBUG() throws Exception {
        logger.setLevel(Level.DEBUG);
        loggingInterceptor.setOverrideLevel(Level.DEBUG);
        verifyPreHandleWithLogging();
    }


    /**
     * Test preHandle logging with forceloggingAtEffectiveLevel, logger set to DEBUG, unlimited max length
     */
    @Test
    public void testPreHandleLogDEBUGForceLogging() throws Exception {
        logger.setLevel(Level.DEBUG);
        loggingInterceptor.forceLoggingAtEffectiveLevel();
        verifyPreHandleWithLogging();
    }


    /**
     * Test postHandle logging with no override level, logger set to DEBUG
     */
    @Test
    public void testPostHandleLogDEBUG() throws Exception {
        logger.setLevel(Level.DEBUG);
        verifyPostHandleNoLogging();
    }


    /**
     * Test postHandle logging with explicit set override level DEBUG, logger set to DEBUG, unlimited max length
     */
    @Test
    public void testPostHandleLogDEBUGOverrideDEBUG() throws Exception {
        logger.setLevel(Level.DEBUG);
        loggingInterceptor.setOverrideLevel(Level.DEBUG);
        verifyPostHandleWithLogging();
    }


    /**
     * Test postHandle logging with forceloggingAtEffectiveLevel, logger set to DEBUG, unlimited max length
     */
    @Test
    public void testPostHandleLogDEBUGForceLogging() throws Exception {
        logger.setLevel(Level.DEBUG);
        loggingInterceptor.forceLoggingAtEffectiveLevel();
        verifyPostHandleWithLogging();
    }


    /**
     * Test afterCompletion logging with no override level, logger set to DEBUG
     */
    @Test
    public void testAfterCompletionNoExceptionLogDEBUG() throws Exception {
        logger.setLevel(Level.DEBUG);
        verifyAfterCompletionNoExceptionNoLogging();
    }


    /**
     * Test afterCompletion logging with no override level, logger set to DEBUG
     */
    @Test
    public void testAfterCompletionWithExceptionLogDEBUG() throws Exception {
        logger.setLevel(Level.DEBUG);
        verifyAfterCompletionWithExceptionNoLogging();
    }


    /**
     * Test afterCompletion logging with explicit set override level DEBUG, logger set to DEBUG, unlimited max length, no exception
     */
    @Test
    public void testAfterCompletionNoExceptionLogDEBUGOverrideDEBUG() throws Exception {
        logger.setLevel(Level.DEBUG);
        loggingInterceptor.setOverrideLevel(Level.DEBUG);
        verifyAfterCompletionWithLogging(null);
    }


    /**
     * Test afterCompletion logging with forceloggingAtEffectiveLevel, logger set to DEBUG, unlimited max length, no exception
     */
    @Test
    public void testAfterCompletionNoExceptionLogDEBUGForceLogging() throws Exception {
        logger.setLevel(Level.DEBUG);
        loggingInterceptor.forceLoggingAtEffectiveLevel();
        verifyAfterCompletionWithLogging(null);
    }


    /**
     * Test afterCompletion logging with explicit set override level DEBUG, logger set to DEBUG, unlimited max length, with exception
     */
    @Test
    public void testAfterCompletionWithExceptionLogDEBUGOverrideDEBUG() throws Exception {
        logger.setLevel(Level.DEBUG);
        loggingInterceptor.setOverrideLevel(Level.DEBUG);
        verifyAfterCompletionWithLogging(new IllegalStateException("test message 1"));
    }


    /**
     * Test afterCompletion logging with forceloggingAtEffectiveLevel, logger set to DEBUG, unlimited max length, with exception
     */
    @Test
    public void testAfterCompletionWithExceptionLogDEBUGForceLogging() throws Exception {
        logger.setLevel(Level.DEBUG);
        loggingInterceptor.forceLoggingAtEffectiveLevel();
        verifyAfterCompletionWithLogging(new RuntimeException("test message 2", new IllegalArgumentException("test message 3")));
    }


    /**
     * Helper to run test base on supplied parameters for preHandle when no logging expected
     */
    protected void verifyPreHandleNoLogging() throws Exception {
        startTestNoLogging();

        boolean preHandleRet = loggingInterceptor.preHandle(request, response, handler);

        Assert.assertTrue("preHandle should return true", preHandleRet);
        EasyMock.verify(request, response);
    }


    /**
     * Helper to run test base on supplied parameters for preHandle when logging should occur
     */
    protected void verifyPreHandleWithLogging() throws Exception {

        final StringBuffer url = startTestWithLogging();

        final int bodyLength = requestResponseParameters.getRequestBody().length();
        EasyMock.expect(request.getContentLength()).andReturn(bodyLength);

        EasyMock.expect(request.getContextPath()).andReturn(requestResponseParameters.getContext());
        EasyMock.expect(request.getServletPath()).andReturn(requestResponseParameters.getServlet());
        EasyMock.expect(request.getPathInfo()).andReturn(requestResponseParameters.getHandlerPath());

        Enumeration<String> headerNames = Collections.enumeration(requestResponseParameters.getRequestHeaders().keySet());
        EasyMock.expect(request.getHeaderNames()).andReturn(headerNames);
        for (String header : requestResponseParameters.getRequestHeaders().keySet()) {
            EasyMock.expect(request.getHeaders(header))
                    .andReturn(Collections.enumeration(requestResponseParameters.getRequestHeaders().get(header)));
        }

        final int expectedBodyLength = Math.min(expectedMaxLoggedBodyLength, bodyLength);
        if (expectedBodyLength > 0) {
            EasyMock.expect(request.getReader()).andReturn(new BufferedReader(new StringReader(requestResponseParameters.getRequestBody())));
        }

        EasyMock.replay(request, response);

        boolean preHandleRet = loggingInterceptor.preHandle(request, response, handler);

        Assert.assertTrue("preHandle should return true", preHandleRet);

        String logMessage = verifyLogRequestUrl(url, "preHandle");

        assertMsgMatches(logMessage, "handler", handler, "^ handler=", "$");
        assertMsgMatches(logMessage, "contextPath", requestResponseParameters.getContext(), "^ contextPath=", "$");
        assertMsgMatches(logMessage, "servletPath", requestResponseParameters.getServlet(), "^ servletPath=", "$");
        assertMsgMatches(logMessage, "handlerPath", requestResponseParameters.getHandlerPath(), "^ handlerPath=", "$");

        verifyLoggedHeaders(logMessage, "^ request ", requestResponseParameters.getRequestHeaders());

        StringBuilder bodyRegexPfx = new StringBuilder("^ body ").append(Pattern.quote("("));
        StringBuilder bodyRegexSfx = new StringBuilder(" chars");
        if (expectedBodyLength == 0) {
            bodyRegexSfx.append(", omitted");
        } else if (bodyLength > expectedBodyLength) {
            bodyRegexSfx.append(", truncated to ").append(expectedBodyLength);
        }
        bodyRegexSfx.append(Pattern.quote(")")).append("$");
        assertMsgMatches(logMessage, "body heading", Integer.valueOf(bodyLength), bodyRegexPfx.toString(), bodyRegexSfx.toString());
        if (expectedBodyLength > 0) {
            assertMsgMatches(logMessage, "body",
                    requestResponseParameters.getRequestBody().substring(0, expectedBodyLength),
                    "^==>\n", "\n<==$");
        }

        EasyMock.verify(request, response);
    }


    /**
     * Helper to run test base on supplied parameters for postHandle when no logging expected
     */
    protected void verifyPostHandleNoLogging() throws Exception {
        startTestNoLogging();

        loggingInterceptor.postHandle(request, response, handler, requestResponseParameters.getModelAndView());

        EasyMock.verify(request, response);
    }


    /**
     * Helper to run test base on supplied parameters for postHandle when logging should occur
     */
    protected void verifyPostHandleWithLogging() throws Exception {

        final StringBuffer url = startResponseTestWithLogging();

        loggingInterceptor.postHandle(request, response, handler, requestResponseParameters.getModelAndView());

        String logMessage = verifyLogRequestUrl(url, "postHandle response to");

        verifyLoggedResponseContent(logMessage);

        EasyMock.verify(request, response);
    }


    /**
     * Helper to run test base on supplied parameters for afterCompletion when no logging expected, and no exception provided
     */
    protected void verifyAfterCompletionNoExceptionNoLogging() throws Exception {
        startTestNoLogging();

        loggingInterceptor.afterCompletion(request, response, handler, null);

        EasyMock.verify(request, response);
    }


    /**
     * Helper to run test base on supplied parameters for afterCompletion when no logging expected, and an exception is provided
     */
    protected void verifyAfterCompletionWithExceptionNoLogging() throws Exception {
        startTestNoLogging();

        loggingInterceptor.afterCompletion(request, response, handler, new NullPointerException("test exception"));

        EasyMock.verify(request, response);
    }


    /**
     * Helper to run test base on supplied parameters for afterCompletion witgh supplied exception when logging should occur
     *
     * @param exception
     */
    protected void verifyAfterCompletionWithLogging(Exception exception) throws Exception {

        final StringBuffer url = startResponseTestWithLogging();

        loggingInterceptor.afterCompletion(request, response, handler, exception);

        String logMessage = verifyLogRequestUrl(url, "afterCompletion response to");

        verifyLoggedResponseContent(logMessage);

        if (exception != null) {
            assertMsgMatches(logMessage, "exception", exception, "^ exception=", "$");
            StackTraceElement[] stackTrace = exception.getStackTrace();
            for (int i = 0; i < stackTrace.length; ++i) {
                assertMsgMatches(logMessage, "stackTrace[" + i + "]", stackTrace[i], "^\\s+", "$");
            }
        }

        EasyMock.verify(request, response);
    }


    /**
     * Helper for starting tests on response
     *
     * @return
     */
    protected StringBuffer startResponseTestWithLogging() {
        final StringBuffer url = startTestWithLogging();

        EasyMock.expect(response.getStatus()).andReturn(requestResponseParameters.getStatus());

        Set<String> headerNames = requestResponseParameters.getResponseHeaders().keySet();
        EasyMock.expect(response.getHeaderNames()).andReturn(headerNames);
        for (String header : headerNames) {
            EasyMock.expect(response.getHeaders(header))
                    .andReturn(requestResponseParameters.getResponseHeaders().get(header));
        }

        EasyMock.replay(request, response);
        return url;
    }


    /**
     * Helper to verify logging response content
     *
     * @param logMessage
     */
    protected void verifyLoggedResponseContent(String logMessage) {
        final int statusInt = requestResponseParameters.getStatus();
        StringBuilder statusReasonRegexSfx = new StringBuilder(' ');
        try {
            HttpStatus status = HttpStatus.valueOf(statusInt);
            statusReasonRegexSfx.append(' ').append(status.getReasonPhrase());
        } catch (Throwable t) {
            statusReasonRegexSfx.append(" *** undefined status code (").append(t.getMessage()).append(')');
        }

        assertMsgMatches(logMessage, "httpStatus",
                requestResponseParameters.getStatus(), "^ httpStatus=",
                Pattern.quote(statusReasonRegexSfx.toString()) + "$");

        verifyLoggedHeaders(logMessage, "^ response ", requestResponseParameters.getResponseHeaders());
    }


    /**
     * Common start to no logging tests
     */
    protected void startTestNoLogging() {
        Assert.assertFalse(assertMsg("should not be able to log"), loggingInterceptor.isLoggingAtEffectiveLevel());

        EasyMock.replay(request, response);
    }


    /**
     * Common start to test where logging should occur
     *
     * @return
     */
    protected StringBuffer startTestWithLogging() {
        Assert.assertTrue(assertMsg("should be able to log"), loggingInterceptor.isLoggingAtEffectiveLevel());

        final StringBuffer url = new StringBuffer();
        url.append(requestResponseParameters.getScheme()).append("://")
                .append(requestResponseParameters.getHostname()).append(':').append(requestResponseParameters.getPort())
                .append('/').append(requestResponseParameters.getContext())
                .append('/').append(requestResponseParameters.getServlet())
                .append('/').append(requestResponseParameters.getHandlerPath());

        EasyMock.expect(request.getMethod()).andReturn(requestResponseParameters.getMethod());
        EasyMock.expect(request.getRequestURL()).andReturn(url);
        EasyMock.expect(request.getQueryString()).andReturn(requestResponseParameters.getQueryString());
        return url;
    }


    /**
     * Common assertions on request URL
     *
     * @param url           StringBuffer
     * @param requestPrefix
     * @return full log message for remaining assertions
     */
    protected String verifyLogRequestUrl(StringBuffer url, String requestPrefix) {
        List<LoggingEvent> loggedEvents = appender.getLoggingEvents();
        Assert.assertEquals(assertMsg("one logging event should be added"), 1, loggedEvents.size());

        String logMessage = loggedEvents.get(0).getMessage().toString();

        assertMsgMatches(logMessage, "method", requestResponseParameters.getMethod(), nameParameters.getMethodRegexPrefix() + requestPrefix + " request: ", " http");
        if (requestResponseParameters.getQueryString() == null) {
            assertMsgMatches(logMessage, "url", url, " ", "$");
        } else {
            assertMsgMatches(logMessage, "url", url, " ", Pattern.quote("?"));
            assertMsgMatches(logMessage, "queryString", requestResponseParameters.getQueryString(), Pattern.quote("?"), "$");
        }
        assertMsgMatches(logMessage, "handler", handler, "^ handler=", "$");
        return logMessage;
    }


    /**
     * Helper verifying headers
     *
     * @param logMessage
     * @param specificHeaderHeadingPrefix
     * @param headers
     */
    protected void verifyLoggedHeaders(String logMessage, String specificHeaderHeadingPrefix, HttpHeaders headers) {
        assertMsgMatches(logMessage, "headers heading", "", specificHeaderHeadingPrefix + "headers:", "$");

        for (String header : headers.keySet()) {
            List<String> headerValue = headers.get(header);
            final String headerUC = header.toUpperCase();
            assertMsgMatches(logMessage, "requestHeaders[" + headerUC + "]", headerValue, "^  " + headerUC + "=", "$");
        }
    }


    /**
     * Assert message content matches regex
     *
     * @param logMessage
     * @param regexPfx
     * @param content
     * @param regexSfx
     * @return offset after match
     */
    protected void assertMsgMatches(String logMessage, String contentName, Object content, String regexPfx, String regexSfx) {
        StringBuilder sb = new StringBuilder(regexPfx).append(Pattern.quote(content.toString())).append(regexSfx);
        final String regex = sb.toString();
        final Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE + Pattern.MULTILINE).matcher(logMessage);

        sb = new StringBuilder(contentName).append('=').append(content)
                .append(":message '").append(logMessage)
                .append("' should match regex '")
                .append(regex).append("'");
        Assert.assertTrue(assertMsg(sb.toString()), matcher.find());
    }

    /**
     * Helper to create assertMsg
     */
    protected String assertMsg(String message) {
        final StringBuilder msgsb = new StringBuilder(message);
        msgsb.append("\n loggingInterceptor=").append(loggingInterceptor);
        msgsb.append("\n nameParameters=").append(nameParameters);
        msgsb.append("\n requestResponseParameters=").append(requestResponseParameters);
        msgsb.append("\n maxLoggedBodyLength=").append(maxLoggedBodyLength);
        return msgsb.toString();
    }

    /**
     * Stub appender (equals not there for logging events etc)
     */
    protected class TestAppender implements Appender {

        /**
         * Name
         */
        private String name;

        /**
         * LoggingEvent last appended
         */
        private List<LoggingEvent> loggingEvents = new ArrayList<LoggingEvent>();

        /**
         * Add a filter to the end of the filter list.
         *
         * @since 0.9.0
         */
        @Override
        public void addFilter(Filter newFilter) {
            throw new UnsupportedOperationException("filter=" + newFilter);
        }

        /**
         * Returns the head Filter. The Filters are organized in a linked list
         * and so all Filters on this Appender are available through the result.
         *
         * @return the head Filter or null, if no Filters are present
         * @since 1.1
         */
        @Override
        public Filter getFilter() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        /**
         * Clear the list of filters by removing all the filters in it.
         *
         * @since 0.9.0
         */
        @Override
        public void clearFilters() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        /**
         * Release any resources allocated within the appender such as file
         * handles, network connections, etc.
         * <p/>
         * <p>It is a programming error to append to a closed appender.
         *
         * @since 0.8.4
         */
        @Override
        public void close() {
            loggingEvents.clear();
        }

        /**
         * Log in <code>Appender</code> specific way. When appropriate,
         * Loggers will call the <code>doAppend</code> method of appender
         * implementations in order to log.
         */
        @Override
        public void doAppend(LoggingEvent event) {
            loggingEvents.add(event);
        }

        /**
         * Get the name of this appender.
         *
         * @return name, may be null.
         */
        @Override
        public String getName() {
            return name;  //To change body of implemented methods use File | Settings | File Templates.
        }

        /**
         * Set the {@link org.apache.log4j.spi.ErrorHandler} for this appender.
         *
         * @since 0.9.0
         */
        @Override
        public void setErrorHandler(ErrorHandler errorHandler) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        /**
         * Returns the {@link org.apache.log4j.spi.ErrorHandler} for this appender.
         *
         * @since 1.1
         */
        @Override
        public ErrorHandler getErrorHandler() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        /**
         * Set the {@link org.apache.log4j.Layout} for this appender.
         *
         * @since 0.8.1
         */
        @Override
        public void setLayout(Layout layout) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        /**
         * Returns this appenders layout.
         *
         * @since 1.1
         */
        @Override
        public Layout getLayout() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        /**
         * Set the name of this appender. The name is used by other
         * components to identify this appender.
         *
         * @since 0.8.1
         */
        @Override
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Configurators call this method to determine if the appender
         * requires a layout. If this method returns <code>true</code>,
         * meaning that layout is required, then the configurator will
         * configure an layout using the configuration information at its
         * disposal.  If this method returns <code>false</code>, meaning that
         * a layout is not required, then layout configuration will be
         * skipped even if there is available layout configuration
         * information at the disposal of the configurator..
         * <p/>
         * <p>In the rather exceptional case, where the appender
         * implementation admits a layout but can also work without it, then
         * the appender should return <code>true</code>.
         *
         * @since 0.8.4
         */
        @Override
        public boolean requiresLayout() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }


        /**
         * Get our events
         *
         * @return event list
         */
        public List<LoggingEvent> getLoggingEvents() {
            return loggingEvents;
        }
    }


    /**
     * Parameter class for logger name and servlet name combinations
     */
    protected static class NameParameters {
        private final String loggerName;
        private final String servletName;
        private final String methodRegexPrefix;

        /**
         * Constructor taking all parameters
         *
         * @param loggerName
         * @param servletName
         * @param methodRegexPrefix
         */
        public NameParameters(String loggerName, String servletName, String methodRegexPrefix) {
            this.loggerName = loggerName;
            this.servletName = servletName;
            this.methodRegexPrefix = methodRegexPrefix;
        }

        public String getLoggerName() {
            return loggerName;
        }

        public String getServletName() {
            return servletName;
        }

        public String getMethodRegexPrefix() {
            return methodRegexPrefix;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("NameParameters");
            sb.append("{loggerName='").append(loggerName).append('\'');
            sb.append(", servletName='").append(servletName).append('\'');
            sb.append(", methodRegexPrefix='").append(methodRegexPrefix).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }


    /**
     * Parameter class for request and response content
     */
    protected static class RequestResponseParameters {
        private final HttpHeaders requestHeaders;
        private final String method;
        private final String scheme;
        private final String hostname;
        private final String port;
        private final String context;
        private final String servlet;
        private final String handlerPath;
        private final String queryString;
        private final String requestBody;
        private final int status;
        private final HttpHeaders responseHeaders;
        private final ModelAndView modelAndView;

        /**
         * @param requestHeaders
         * @param method
         * @param scheme
         * @param hostname
         * @param port
         * @param context
         * @param servlet
         * @param handlerPath
         * @param queryString
         * @param requestBody
         * @param status
         * @param responseHeaders
         * @param modelAndView
         */
        public RequestResponseParameters(HttpHeaders requestHeaders, String method, String scheme, String hostname, String port, String context, String servlet, String handlerPath, String queryString, String requestBody, int status, HttpHeaders responseHeaders, ModelAndView modelAndView) {
            this.method = method;
            this.scheme = scheme;
            this.hostname = hostname;
            this.port = port;
            this.context = context;
            this.servlet = servlet;
            this.handlerPath = handlerPath;
            this.queryString = queryString;
            this.requestBody = requestBody;
            this.status = status;

            this.requestHeaders = new HttpHeaders();
            for (String header : requestHeaders.keySet()) {
                this.requestHeaders.put(header, requestHeaders.get(header));
            }

            if (requestBody == null) {
                this.requestHeaders.setContentLength(0);
            } else {
                this.requestHeaders.setContentLength(requestBody.length());
            }

            this.responseHeaders = new HttpHeaders();
            for (String header : responseHeaders.keySet()) {
                this.responseHeaders.put(header, responseHeaders.get(header));
            }

            if (modelAndView == null) {
                this.modelAndView = null;
            } else {
                this.modelAndView = new ModelAndView(modelAndView.getViewName());
                this.modelAndView.addAllObjects(modelAndView.getModel());
            }
        }

        public HttpHeaders getRequestHeaders() {
            return requestHeaders;
        }

        public String getMethod() {
            return method;
        }

        public String getScheme() {
            return scheme;
        }

        public String getHostname() {
            return hostname;
        }

        public String getPort() {
            return port;
        }

        public String getContext() {
            return context;
        }

        public String getServlet() {
            return servlet;
        }

        public String getHandlerPath() {
            return handlerPath;
        }

        public String getQueryString() {
            return queryString;
        }

        public String getRequestBody() {
            return requestBody;
        }

        public int getStatus() {
            return status;
        }

        public HttpHeaders getResponseHeaders() {
            return responseHeaders;
        }

        public ModelAndView getModelAndView() {
            return modelAndView;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("RequestResponseParameters");
            sb.append("{requestHeaders=").append(requestHeaders);
            sb.append(", method='").append(method).append('\'');
            sb.append(", scheme='").append(scheme).append('\'');
            sb.append(", hostname='").append(hostname).append('\'');
            sb.append(", port='").append(port).append('\'');
            sb.append(", context='").append(context).append('\'');
            sb.append(", servlet='").append(servlet).append('\'');
            sb.append(", handlerPath='").append(handlerPath).append('\'');
            sb.append(", queryString='").append(queryString).append('\'');
            sb.append(", requestBody='").append(requestBody).append('\'');
            sb.append(", status='").append(status).append('\'');
            sb.append(", responseHeaders=").append(responseHeaders);
            sb.append(", modelAndView=").append(modelAndView);
            sb.append('}');
            return sb.toString();
        }
    }

    /**
     * Test controller for handler
     */
    @Controller
    protected class TestController {

        /**
         * test method
         */
        @RequestMapping("/test")
        public String testMethod(Model model) {
            return "view0";
        }
    }
}