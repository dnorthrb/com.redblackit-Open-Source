package com.redblackit.war;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Initializer that will set the active profile from the init-param spring.profiles.active.
 * Thislooks like a work-around to a bug, which seems to avoid setting a new profile for a second servlet.
 *
 * @author Dominic North
 */
public class ServletProfileInitializer implements ApplicationContextInitializer {

    public static final String SERVLET_NAME_KEY             = "servletName";
    public static final String APPLICATION_CONTEXT_PATH_KEY = "applicationContextPath";
    public static final String ADDITIONAL_SERVLET_PROPERTY_SOURCE_NAME = "additionalServletProperties";
    /**
     * Logger (on web.server)
     */
    private             Logger logger                       = Logger.getLogger(this.getClass());

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

        String servletName = "unknown";
        ConfigurableEnvironment env = configurableApplicationContext.getEnvironment();
        ConfigurableWebApplicationContext wac = null;
        if (configurableApplicationContext instanceof ConfigurableWebApplicationContext) {
            wac = (ConfigurableWebApplicationContext) configurableApplicationContext;
            servletName = wac.getServletConfig().getServletName();
            logger.debug("servletName=" + servletName);
            logger.debug(servletName + ":current activeProfiles=" + Arrays.toString(env.getActiveProfiles()));
            logger.debug(servletName + ":current spring.profiles.active=" + env.getProperty("spring.profiles.active"));
            String activeProfilesParm = wac.getServletConfig().getInitParameter("spring.profiles.active");
            logger.debug(servletName + ":init-parm spring.profiles.active=" + activeProfilesParm);

            String[] activeProfilesFromInit = StringUtils.tokenizeToStringArray(activeProfilesParm, ",");
            env.setActiveProfiles(activeProfilesFromInit);
            logger.info(servletName + ":setting new activeProfiles=" + Arrays.toString(env.getActiveProfiles()));

            Map<String, Object> additionalServletProperties = new HashMap<String, Object>();
            additionalServletProperties.put(SERVLET_NAME_KEY, servletName);
            additionalServletProperties.put(APPLICATION_CONTEXT_PATH_KEY, wac.getServletConfig().getServletContext().getContextPath());

            MapPropertySource additionalServletPropertySource = new MapPropertySource(ADDITIONAL_SERVLET_PROPERTY_SOURCE_NAME, additionalServletProperties);
            MutablePropertySources mutablePropertySources = env.getPropertySources();
            if (mutablePropertySources.contains(ADDITIONAL_SERVLET_PROPERTY_SOURCE_NAME)) {
                mutablePropertySources.replace(ADDITIONAL_SERVLET_PROPERTY_SOURCE_NAME, additionalServletPropertySource);
            } else {
                mutablePropertySources.addLast(additionalServletPropertySource);
            }

        } else {
            logger.warn("configurableApplicationContext (" + configurableApplicationContext + ") not instance of org.springframework.web.context.ConfigurableWebApplicationContext:cannot be initialized");
        }
    }
}
