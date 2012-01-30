package com.redblackit.web.server.mvc;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Dominic North
 */
public class LoggingInterceptorNullLoggerAndServletNameTest {

    /**
     * Test for exception if neither loggerName nor servletConfig is provided
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAssertionNoneProvided() throws Exception {
        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
        loggingInterceptor.afterPropertiesSet();
    }

    /**
     * Test for exception if null loggerName is set
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAssertionSetNullLoggerName() throws Exception {
        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
        loggingInterceptor.setLoggerName(null);
    }

    /**
     * Test for no exception if null servletConfig is provided
     */
    @Test
    public void testNoAssertionSetNullServletConfig() throws Exception {
        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
        loggingInterceptor.initializeServletName(null);
    }

    /**
     * Test for exception if no loggerName provided, null servletConfig set
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAssertionSetNullServletConfigNoLoggerNameProvided() throws Exception {
        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
        loggingInterceptor.initializeServletName(null);
        loggingInterceptor.afterPropertiesSet();
    }
}
