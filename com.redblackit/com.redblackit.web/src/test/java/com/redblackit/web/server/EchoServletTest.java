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

package com.redblackit.web.server;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author djnorth
 * 
 *         Tests for EchoServlet
 */
@RunWith(Parameterized.class)
public class EchoServletTest {

	@Parameters
	public static List<Object[]> getParameters() {
		Object[][] parameters = {

				{
						"http://www.redblackit.com",
						new String[][] { { "Content-Type", "text/plain" },
								{ "Accept", "text/xml" },
								{ "X-SPECIAL", "XS-0", "XS-1" } }, "body0" },

				{
						"http://localhost:8080",
						new String[][] { { "ETAG", "000100100" },
								{ "Accept", "application/json" } }, null },

		{
				"https://" + HostNetUtils.getLocalHostname() + ":8443",
				new String[][] { { "No-Cache" },
						{ "Accept", "application/xml" },
						{ "X-SPECIAL", "XS-0", "XS-1" } },
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
						+ "<beans xmlns=\"http://www.springframework.org/schema/beans\"\n"
						+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:util=\"http://www.springframework.org/schema/util\n"
						+ "http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-3.0.xsd\">\n\n"
						+ "<util:properties id=\"testProperties\" location=\"classpath:/com/redblackit/war/test.properties\" />\n\n</beans>" } };

		return Arrays.asList(parameters);
	}

	/**
	 * Logger
	 */
	private Logger logger = Logger.getLogger("web.server");

	/**
	 * URL
	 */
	private String requestURI;

	/**
	 * Headers
	 */
	private Map<String, List<Object>> headersMap;

	/**
	 * Body content
	 */
	private String body;

	/**
	 * Servlet under test
	 */
	private EchoServlet echoServlet;

	/**
	 * Constructor taking test values
	 * 
	 * @param requestURI
	 * @param headers
	 * @param body
	 */
	public EchoServletTest(String requestURI, String[][] headers, String body) {

		this.echoServlet = new EchoServlet();

		this.requestURI = requestURI;
		this.headersMap = new TreeMap<String, List<Object>>();
		if (headers != null && headers.length > 0) {
			logger.debug("<init>:headers=" + Arrays.deepToString(headers));
			int hi = 0;
			for (String[] header : headers) {
				logger.debug("<init>:header[" + hi + "]="
						+ Arrays.toString(header));
				if (header != null && header.length > 0) {
					Object[] values;
					if (header.length > 1) {
						values = Arrays.copyOfRange(header, 1, header.length);
						logger.debug("<init>:header[" + hi + "].values="
								+ Arrays.toString(values));
					} else {
						values = new Object[] { "" };
						logger.debug("<init>:header[" + hi + "].values="
								+ Arrays.toString(values) + "(empty)");
					}
					headersMap.put(header[0], Arrays.asList(values));
				}
			}
		}

		this.body = body;
	}

	/**
	 * test GET
	 */
	@Test
	public void testEchoGet() throws Exception {
		doTest("GET", false);
	}

	/**
	 * test POST
	 */
	@Test
	public void testEchoPost() throws Exception {
		doTest("POST", true);
	}

	/**
	 * test PUT
	 */
	@Test
	public void testEchoPut() throws Exception {
		doTest("PUT", true);
	}

	/**
	 * test DELETE
	 */
	@Test
	public void testEchoDelete() throws Exception {
		doTest("DELETE", false);
	}

	/**
	 * test HEAD
	 */
	@Test
	public void testEchoHead() throws Exception {
		doTest("HEAD", false);
	}

	/**
	 * toString
	 */
	public String toString() {
		StringBuffer tos = new StringBuffer(super.toString());

		tos.append(":requestURI=").append(requestURI);
		tos.append(":headersMap=").append(headersMap);
		tos.append(":body=").append(body);
		tos.append(":echoServlet=").append(echoServlet);

		return tos.toString();
	}

	/**
	 * Do test
	 * 
	 * @param method
	 * @param hasBody if content should be expected
	 */
	private void doTest(String method, boolean hasBody) throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(method);
		request.setRequestURI(this.requestURI);

		final String msg = "doTest:" + method + ":hasBody=" + hasBody;
		logger.debug(msg + ":this=" + this);

		for (String headerName : headersMap.keySet()) {
			List<Object> values = headersMap.get(headerName);
			request.addHeader(headerName, values);
			Enumeration<String> headerValues = request.getHeaders(headerName);
			int hi = 0;
			while (headerValues.hasMoreElements()) {
				logger.debug(msg + "request:header[" + headerName + "," + hi
						+ "]=" + headerValues.nextElement());
				++hi;
			}

			Assert.assertTrue(msg + "TEST ERROR:request:header[" + headerName + "]="
					+ values + ":shouldn't be empty (" + values.getClass() + ")", hi > 0);

		}

		int expectedContentLength = 0;
		if (hasBody && body != null && body.length() > 0) {
			request.setContent(body.getBytes());
			expectedContentLength = request.getContentLength();
		}

		MockHttpServletResponse response = new MockHttpServletResponse();
		echoServlet.service(request, response);

		String responseBody = response.getContentAsString();

		Assert.assertEquals("response code:" + response, HttpServletResponse.SC_OK,
				response.getStatus());
		Assert.assertEquals("requestURI and Location", requestURI,
				response.getHeader("Location"));

		Map<String, List<Object>> responseHeadersMap = new TreeMap<String, List<Object>>();
		for (String headerName : response.getHeaderNames()) {
			List<Object> values = response.getHeaders(headerName);
			int hi = 0;
			for (Object value : values) {
				logger.debug(msg + ":response:header[" + headerName + "," + hi
						+ "]=" + value);
				++hi;
			}

			if (hi == 0) {
				logger.debug(msg + ":response:header[" + headerName + "]="
						+ values + ":is empty (" + values.getClass() + ")");
				values = Arrays.asList(new Object[] { "" });
			}

			if (!headerName.equals("Location")) {
				responseHeadersMap.put(headerName, values);
			}
		}

		Assert.assertEquals("headers (excluding Location)", headersMap,
				responseHeadersMap);
		if (hasBody)
		{
			Assert.assertEquals("body", (body == null ? "" : body), responseBody);
		}
		else
		{
			Assert.assertEquals("body", "", responseBody);
		}
		
		Assert.assertEquals("contentLength", expectedContentLength,
				response.getContentLength());

	}
}
