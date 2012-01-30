package com.redblackit.web.server.mvc;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Interceptor that will log requests, responses and outcome, at trace level,
 * using log4j on category equal to the loggerName.
 * <p/>
 * It also provides the means to change the log level used, both by configuration, and JMX. The default level (after
 * construction or reset) is TRACE. This is generally the best level to use! Note that this does not adjust the level
 * on
 * the logger, but rather the level used to log.
 *
 * @author Dominic North
 */
@ManagedResource(description = "logger intercepting MVC requests and responses")
public class LoggingInterceptor implements HandlerInterceptor, InitializingBean {

    /**
     * Default character set
     */
    public static final Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");

    /**
     * Logger we use
     */
    private Logger logger = null;

    /**
     * Our bean name
     */
    private String loggerName = null;

    /**
     * Required log-level used for logging
     */
    private Level overrideLevel = null;

    /**
     * Servlet name
     */
    private String servletName = null;

    /**
     * Maximum payload logging length
     */
    private int maxLoggedBodyLength = Integer.MAX_VALUE;

    /**
     * Initialize servlet name from servlet config
     */
    @Autowired(required = false)
    public void initializeServletName(ServletConfig servletConfig) {
        if (servletConfig != null) {
            servletName = servletConfig.getServletName();
        }
    }

    /**
     * Log before message is handled
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        if (isLoggingAtEffectiveLevel()) {
            final StringBuilder messagesb = startMessageLog("preHandle request", httpServletRequest, handler);
            messagesb.append("\n contextPath=").append(httpServletRequest.getContextPath());
            messagesb.append("\n servletPath=").append(httpServletRequest.getServletPath());
            messagesb.append("\n handlerPath=").append(httpServletRequest.getPathInfo());
            messagesb.append("\n request headers:");

            Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                final String headerName = headerNames.nextElement();
                List<String> headerValues = Collections.list(httpServletRequest.getHeaders(headerName));
                messagesb.append("\n  ").append(headerName.toUpperCase()).append('=').append(headerValues);
            }

            final int contentLength = httpServletRequest.getContentLength();
            final int loggedMessageBodyLength = Math.min(contentLength, getMaxLoggedBodyLength());
            messagesb.append("\n body (").append(contentLength).append(" chars");
            if (loggedMessageBodyLength > 0) {
                Reader bodyReader = httpServletRequest.getReader();
                char[] body = new char[loggedMessageBodyLength];
                int lenread = bodyReader.read(body, 0, body.length);
                if (lenread < contentLength) {
                    messagesb.append(", truncated to ").append(lenread);
                }
                messagesb.append(")\n==>\n").append(body).append("\n<==");
            } else {
                messagesb.append(", omitted)");
            }

            doLogMessage(messagesb);
        }

        return true;
    }

    /**
     * Log after request has been handled, but before view is resolved and rendered
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, ModelAndView modelAndView) throws Exception {
        if (isLoggingAtEffectiveLevel()) {
            final StringBuilder messagesb = startMessageLog("postHandle response to request", httpServletRequest, handler);
            startResponseMessageLog(httpServletResponse, messagesb);

            messagesb.append("\n modelAndView=").append(modelAndView);

            doLogMessage(messagesb);
        }
    }


    /**
     * Log after completion of request, and rendering of response
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param handler
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) throws Exception {
        if (isLoggingAtEffectiveLevel()) {
            final StringBuilder messagesb = startMessageLog("afterCompletion response to request", httpServletRequest, handler);
            startResponseMessageLog(httpServletResponse, messagesb);

            if (e != null) {
                messagesb.append("\n exception=").append(e);
                for (StackTraceElement ste : e.getStackTrace()) {
                    messagesb.append("\n  " + ste);
                }
            }

            doLogMessage(messagesb);
        }
    }


    /**
     * Set the loggerName, which will be used as the category for the interceptor
     *
     * @param loggerName
     */
    public void setLoggerName(String loggerName) {
        Assert.notNull(loggerName, "loggerName should not be null");
        this.loggerName = loggerName;
    }


    /**
     * Ensure we have the loggerName, then create the logger
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (loggerName == null && servletName != null) {
            setLoggerName(servletName);
        }

        Assert.notNull(loggerName, "loggerName must be set if servletName not available");

        logger = Logger.getLogger(loggerName);
        if (logger.getLevel() == null) {
            logger.setLevel(Level.INFO);
        }
    }


    /**
     * Check if we will do logging
     *
     * @return true if we will, false if not
     */
    @ManagedAttribute(description = "derived from effective log level and logger level")
    public boolean isLoggingAtEffectiveLevel() {
        return getEffectiveLevel().isGreaterOrEqual(logger.getLevel());
    }


    /**
     * Ensure we log at level corresponding to logger's configured level, but only set new override level if we are not
     * currently logging
     */
    @ManagedOperation(description = "set override level to current logger level, if not already ge")
    public void forceLoggingAtEffectiveLevel() {
        if (!isLoggingAtEffectiveLevel()) {
            setOverrideLevel(logger.getLevel());
        }
    }


    /**
     * @return effective log level
     */
    @ManagedAttribute(description = "effective log level")
    public Level getEffectiveLevel() {
        return overrideLevel == null ? Level.TRACE : overrideLevel;
    }

    /**
     * @return override log-level
     */
    @ManagedAttribute(description = "current override level - null means default applies i.e. TRACE")
    public Level getOverrideLevel() {
        return overrideLevel;
    }

    /**
     * Set the overrideLevel
     *
     * @param overrideLevel
     */
    @ManagedAttribute(description = "current override level - null means default applies i.e. TRACE")
    public void setOverrideLevel(Level overrideLevel) {
        this.overrideLevel = overrideLevel;
    }

    /**
     * Force back to default level
     */
    @ManagedOperation(description = "clear override level so restoring default level i.e. TRACE")
    public void resetToDefaultLevel() {
        setOverrideLevel(null);
    }

    /**
     * Get maximum length to be logged
     *
     * @return max length
     */
    @ManagedAttribute(description = "maximum length of message body to be logged")
    public int getMaxLoggedBodyLength() {
        return maxLoggedBodyLength;
    }

    /**
     * Set maximum length to be logged
     *
     * @param maxLoggedBodyLength (<0 set to Integer.MAX_LENGTH)
     */
    @ManagedAttribute(description = "maximum length of message body to be logged")
    public void setMaxLoggedBodyLength(int maxLoggedBodyLength) {
        this.maxLoggedBodyLength = (maxLoggedBodyLength < 0 ? Integer.MAX_VALUE : maxLoggedBodyLength);
    }

    /**
     * Get logger name as supplied (if null, servlet name used)
     *
     * @return logger name
     */
    @ManagedAttribute(description = "logger name as supplied (if null, servlet name used)")
    public String getLoggerName() {
        return loggerName;
    }

    /**
     * Get servlet name (supplied from ServletConfig)
     *
     * @return servlet name
     */
    @ManagedAttribute(description = "servlet name (supplied from ServletConfig)")
    public String getServletName() {
        return servletName;
    }

    /**
     * toString dumping out our fields
     *
     * @return string representation of state
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("{loggerName=").append(loggerName);
        sb.append(", servletName=").append(servletName);
        sb.append(", overrideLevel=").append(overrideLevel);
        sb.append(", maxLoggedBodyLength=").append(maxLoggedBodyLength);
        sb.append(", logger.getLevel()=").append(logger.getLevel());
        sb.append('}');
        return sb.toString();
    }

    /**
     * Start message log buffer with request URL
     *
     * @param specificHeading
     * @param httpServletRequest
     * @param handler
     * @return message string builder
     */
    protected StringBuilder startMessageLog(String specificHeading, HttpServletRequest httpServletRequest, Object handler) {
        final StringBuilder messagesb = new StringBuilder();
        if (servletName != null && !servletName.equals(loggerName)) {
            messagesb.append(servletName).append(' ');
        }
        messagesb.append(specificHeading).append(": ");
        messagesb.append(httpServletRequest.getMethod()).append(' ').append(httpServletRequest.getRequestURL());
        final String queryString = httpServletRequest.getQueryString();
        if (queryString != null) {
            messagesb.append('?').append(queryString);
        }
        messagesb.append("\n handler=").append(handler);
        return messagesb;
    }

    /**
     * Start log of httpServletResponse
     *
     * @param httpServletResponse
     * @param messagesb
     */
    protected void startResponseMessageLog(HttpServletResponse httpServletResponse, StringBuilder messagesb) {
        final int statusInt = httpServletResponse.getStatus();
        String statusReason;
        try {
            HttpStatus status = HttpStatus.valueOf(statusInt);
            statusReason = status.getReasonPhrase();
        } catch (Throwable t) {
            statusReason = "*** undefined status code (" + t.getMessage() + ")";
        }
        messagesb.append("\n httpStatus=").append(statusInt).append(' ').append(statusReason);

        messagesb.append("\n response headers:");
        Collection<String> headerNames = httpServletResponse.getHeaderNames();
        for (String headerName : headerNames) {
            Collection<String> headerValues = httpServletResponse.getHeaders(headerName);
            messagesb.append("\n  ").append(headerName.toUpperCase()).append('=').append(headerValues);
        }
    }

    /**
     * Log message from supplied StringBuilder
     *
     * @param messagesb
     */
    protected void doLogMessage(StringBuilder messagesb) {
        if (isLoggingAtEffectiveLevel()) {
            logger.log(getEffectiveLevel(), messagesb);
        }
    }
}
