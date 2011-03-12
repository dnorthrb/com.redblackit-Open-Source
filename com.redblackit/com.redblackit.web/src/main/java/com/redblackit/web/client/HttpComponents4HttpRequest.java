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
package com.redblackit.web.client;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * {@link org.springframework.http.client.ClientHttpRequest} implementation that uses
 * Apache HttpComponent's HttpClient to execute requests.
 *
 * <p>Based on {@link org.springframework.http.client.CommonsClientHttpRequest}
 *
 * @see HttpComponents4ClientHttpRequestFactory#createRequest(java.net.URI, org.springframework.http.HttpMethod)
 * 
 * @author djnorth
 */
public final class HttpComponents4HttpRequest extends AbstractClientHttpRequest {

	/**
	 * Client we will use
	 */
	private final HttpClient httpClient;

	/**
	 * Request object we're wrapping
	 */
	private final HttpUriRequest httpRequest;

	/**
	 * Construct our wrapper from the supplied client and request.
	 * Given the lack of any protected getter for accessing the request, we stick with the interface here.
	 * 
	 * @param httpClient
	 * @param httpRequest
	 */
	HttpComponents4HttpRequest(HttpClient httpClient, HttpUriRequest httpRequest) {
		this.httpClient = httpClient;
		this.httpRequest = httpRequest;
	}

	/**
	 * Get Spring enum for request method string
	 * 
	 * @return Spring method enum
	 */
	public HttpMethod getMethod() {
		return HttpMethod.valueOf(this.httpRequest.getMethod());
	}
	
	
	/**
	 * Get URI from request
	 * 
	 * @return URI
	 */
	public URI getURI() {
            return this.httpRequest.getURI();
	}

	
	/**
	 * Execute the request using our client, and create the response wrapper from the response
	 * 
	 * @return response wrapper object
	 */
	@Override
	public ClientHttpResponse executeInternal(HttpHeaders headers, byte[] output) throws IOException {
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			String headerName = entry.getKey();
			for (String headerValue : entry.getValue()) {
				httpRequest.addHeader(headerName, headerValue);
			}
		}
		
		if (this.httpRequest instanceof HttpEntityEnclosingRequestBase) {
			HttpEntityEnclosingRequestBase entityEnclosingMethod = (HttpEntityEnclosingRequestBase) this.httpRequest;
			HttpEntity requestEntity = new ByteArrayEntity(output);
			entityEnclosingMethod.setEntity(requestEntity);
		}
 		
        HttpResponse httpResponse = this.httpClient.execute(this.httpRequest);
		return new HttpComponents4HttpResponse(httpResponse);
	}

}
