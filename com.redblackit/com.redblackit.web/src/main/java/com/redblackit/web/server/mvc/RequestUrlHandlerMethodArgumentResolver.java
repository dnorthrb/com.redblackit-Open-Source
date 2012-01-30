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

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.redblackit.web.server.mvc.annotation.RequestUrl;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URL;

/**
 * HandlerMethodArgumentResolver implementation for parameters annotated with @RequestUrl
 * ( {@link RequestUrl}). This can be configured as a custom WebArgumentResolver
 * in the Spring RequestMappingHandlerAdapter to include support for @MVC
 * methods specifying such parameters, using the new 3.1 <code>&lt;mvc:argument-resolvers&gt;</code>
 * sub-element of <code>&lt;mvc:annotation-driven&gt;</code>.
 *
 * @author djnorth
 */
public class RequestUrlHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * Resolve a requestURL argument if we can.
     * <ul>
     * <li>If the parameter is of type UrlChildLocation it is assigned an instance created from the request URL</li>
     * <li>If the parameter annotated with @RequestUrl, and of an
     * appropriate type, the requestURL is returned as the parameter value</li>
     * </ul>
     *
     * @param methodParameter
     * @param mavContainer         (unused)
     * @param webRequest
     * @param webDataBinderFactory (unused)
     * @return returnValue
     * @see HandlerMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory webDataBinderFactory) {

        Object returnValue = null;
        if (supportsParameter(methodParameter)) {
            Class<?> paramType = methodParameter.getParameterType();

            if (UrlChildLocation.class.isAssignableFrom(paramType)) {
                returnValue = new UrlChildLocation(webRequest.getNativeRequest(
                        HttpServletRequest.class));
            } else {
                StringBuffer urlsb = webRequest.getNativeRequest(
                        HttpServletRequest.class).getRequestURL();
                if (String.class.isAssignableFrom(paramType)) {
                    returnValue = urlsb.toString();
                } else if (StringBuffer.class.isAssignableFrom(paramType)) {
                    returnValue = urlsb;
                } else if (StringBuilder.class.isAssignableFrom(paramType)) {
                    returnValue = new StringBuilder(urlsb);
                } else {

                    try {
                        if (URL.class.isAssignableFrom(paramType)) {
                            returnValue = new URL(urlsb.toString());
                        } else if (URI.class.isAssignableFrom(paramType)) {
                            returnValue = new URI(urlsb.toString());
                        }

                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }

        }

        return returnValue;
    }

    /**
     * Check for parameter type support. Note if the annotation is present, we
     * throw an exception if the annotation is applied to an invalid type.
     *
     * @param methodParameter
     * @return true if supported, false if not
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        final Class<?> paramType = methodParameter.getParameterType();

        boolean isSupported = UrlChildLocation.class.isAssignableFrom(paramType);

        if (!isSupported) {
            if (methodParameter.hasParameterAnnotation(RequestUrl.class)) {
                isSupported = String.class.isAssignableFrom(paramType)
                        || StringBuffer.class.isAssignableFrom(paramType)
                        || StringBuilder.class.isAssignableFrom(paramType)
                        || URL.class.isAssignableFrom(paramType)
                        || URI.class.isAssignableFrom(paramType);

                if (!isSupported) {
                    throw new IllegalStateException(
                            "methodParameter["
                                    + methodParameter.getParameterIndex()
                                    + "] type="
                                    + paramType
                                    + ":not compatible with @RequestUrl annotation:must be String, StringBuffer, StringBuilder, URL or URI");
                }
            }
        }

        return isSupported;
    }
}
