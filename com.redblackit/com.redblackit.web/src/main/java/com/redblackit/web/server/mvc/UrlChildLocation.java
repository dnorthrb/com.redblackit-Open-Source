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

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;
import org.springframework.web.util.UriTemplate;

/**
 * This class wraps a servlet request URL and provides a helper method to obtain
 * a child URL for a location header. This is particularly intended for use with
 * REST create (POST) methods.
 *
 * @author djnorth
 */
public class UrlChildLocation {

    /**
     * Request URL
     */
    private final String requestUrl;

    /**
     * Current child ID
     */
    private Object childId = null;

    /**
     * Constructor taking URL string
     *
     * @param requestUrl (may not be null)
     */
    public UrlChildLocation(String requestUrl) {
        Assert.notNull(requestUrl, "requestUrl must be non-null");
        this.requestUrl = requestUrl;
    }

    /**
     * Constructor taking URL string and child ID
     *
     * @param requestUrl (may not be null)
     * @param childId
     */
    public UrlChildLocation(String requestUrl, Object childId) {
        Assert.notNull(requestUrl, "requestUrl must be non-null");
        this.requestUrl = requestUrl;
        this.childId = childId;
    }

    /**
     * Constructor taking HttpServletRequest
     *
     * @param request (neither request not its requestURL may be null)
     */
    public UrlChildLocation(HttpServletRequest request) {
        Assert.notNull(request, "request must be non-null");
        StringBuffer rurlsb = request.getRequestURL();
        Assert.notNull(rurlsb, "request.getRequestURL must be non-null");
        this.requestUrl = rurlsb.toString();
    }

    /**
     * Method to return a location URL for child by appending the childId to the parent URl,
     * relying on the childId toString() method. If the supplied childId is
     * null, it returns the parent URL as-is.
     *
     * @return location URL
     */
    public String getLocationUrl() {
        final String locationUrl;
        if (childId == null) {
            locationUrl = requestUrl;
        } else {
            URI childUri = new UriTemplate("{parentURL}/{childId}").expand(
                    (requestUrl.endsWith("/") ? requestUrl.substring(0,
                            requestUrl.length() - 1) : requestUrl), childId);
            locationUrl = childUri.toASCIIString();
        }

        return locationUrl;
    }

    /**
     * Get base URL string value
     *
     * @return string
     */
    public String getRequestUrl() {
        return requestUrl;
    }

    /**
     * Set the child ID
     *
     * @param childId
     */
    public void setChildId(Object childId) {
        this.childId = childId;
    }

    /**
     * @return the childD
     */
    public Object getChildId() {
        return childId;
    }

    /**
     * equals based on all fields
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UrlChildLocation)) return false;

        UrlChildLocation that = (UrlChildLocation) o;

        if (childId != null ? !childId.equals(that.childId) : that.childId != null) return false;
        if (requestUrl != null ? !requestUrl.equals(that.requestUrl) : that.requestUrl != null) return false;

        return true;
    }

    /**
     * hashCode based on URL alone
     * @return
     */
    @Override
    public int hashCode() {
        return requestUrl != null ? requestUrl.hashCode() : 0;
    }

    /**
     * toString including the requestUrl value
     *
     * @return string state
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("UrlChildLocation");
        sb.append("{requestUrl='").append(requestUrl).append('\'');
        sb.append(", childId=").append(childId);
        sb.append('}');
        return sb.toString();
    }
}
