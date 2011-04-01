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

package com.redblackit.web.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author djnorth
 * 
 */
@RunWith(Parameterized.class)
public class RestTemplateTestHelperTest {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger("web.client");

	/**
	 * Jetty Server
	 */
	private static Server server = new Server(6080);

	/**
	 * Base url
	 */
	private static final String BASE_URL = "http://localhost:6080";

	/**
	 * Method returning parameters, which also sets up the servlets to use
	 */
	@Parameters
	public static List<Object[]> getParameters() throws Exception {
		final String[] methods = { "GET", "POST", "PUT", "DELETE", "HEAD",
				"OPTIONS" };
		final HttpStatus[][] codes = {
				{ HttpStatus.INTERNAL_SERVER_ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR },
				{ HttpStatus.NOT_FOUND, HttpStatus.FORBIDDEN,
						HttpStatus.CONFLICT, HttpStatus.INTERNAL_SERVER_ERROR,
						HttpStatus.METHOD_NOT_ALLOWED,
						HttpStatus.PRECONDITION_FAILED },
				{ HttpStatus.NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.NOT_FOUND,
						HttpStatus.NOT_FOUND } };

		ServletContextHandler jettyContext = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		jettyContext.setContextPath("/");
		server.setHandler(jettyContext);

		List<Object[]> parameters = new ArrayList<Object[]>();

		for (int i = 0; i < codes.length; ++i) {
			String url = "/test" + i;
			Map<String, HttpStatus> mcmap = new HashMap<String, HttpStatus>();
			for (int j = 0; j < methods.length; ++j) {
				mcmap.put(methods[j], codes[i][j]);
			}

			jettyContext.addServlet(new ServletHolder(new StatusCodeServlet(
					mcmap)), url);
			parameters.add(new Object[] { BASE_URL + url, mcmap });
		}

		server.start();
		int i = 0;
		while (!server.isStarted() && i < 20) {
			Thread.sleep(200);
			++i;
		}

		if (!server.isStarted()) {
			Assert.fail("server not started");
		}
		return parameters;
	}

	/**
	 * Url
	 */
	private String url;

	/**
	 * Map of expected method -> status code entries
	 */
	private Map<String, HttpStatus> expectedMethodStatusCodes;

	/**
	 * RestTemplateTestHelper
	 */
	private RestTemplateTestHelper restTemplateTestHelper;

	/**
	 * @param url
	 * @param expectedMethodStatusCodes
	 */
	public RestTemplateTestHelperTest(String url,
			Map<String, HttpStatus> expectedMethodStatusCodes) {
		this.url = url;
		this.expectedMethodStatusCodes = expectedMethodStatusCodes;
		this.restTemplateTestHelper = new RestTemplateTestHelper(
				new RestTemplate(), logger);
	}

	/**
	 * Test method checking matching code correctly processed for
	 * {@link com.redblackit.web.test.RestTemplateTestHelper#doGetForHttpStatusCodeException(java.lang.String, java.lang.Object[], java.lang.String, org.springframework.http.HttpStatus)}
	 * .
	 */
	@Test
	public void testDoGetForHttpClientErrorExceptionMatchStatusCode() {
		restTemplateTestHelper.doGetForHttpStatusCodeException(url, null,
				"MatchStatusCode", expectedMethodStatusCodes.get("GET"));
	}

	/**
	 * Test method checking non-matching error code correctly processed for
	 * {@link com.redblackit.web.test.RestTemplateTestHelper#doGetForHttpStatusCodeException(java.lang.String, java.lang.Object[], java.lang.String, org.springframework.http.HttpStatus)}
	 * .
	 */
	@Test(expected = AssertionError.class)
	public void testDoGetForHttpClientErrorExceptionNonMatchStatusCode() {
		HttpStatus wrongStatusCode = getNonMatchingErrorStatusCode(expectedMethodStatusCodes
				.get("GET"));
		restTemplateTestHelper.doGetForHttpStatusCodeException(url, null,
				"NonMatchStatusCode", wrongStatusCode);
	}

	/**
	 * Test method checking matching code correctly processed for
	 * {@link com.redblackit.web.test.RestTemplateTestHelper#doPostForHttpStatusCodeException(java.lang.String, java.lang.Object, java.lang.Object[], java.lang.String, org.springframework.http.HttpStatus)}
	 * .
	 */
	@Test
	public void testDoPostForHttpClientErrorExceptionMatchStatusCode() {
		restTemplateTestHelper.doPostForHttpStatusCodeException(url, "Body",
				null, "MatchStatusCode", expectedMethodStatusCodes.get("POST"));
	}

	/**
	 * Test method checking non-matching code correctly processed for
	 * {@link com.redblackit.web.test.RestTemplateTestHelper#doPostForHttpStatusCodeException(java.lang.String, java.lang.Object, java.lang.Object[], java.lang.String, org.springframework.http.HttpStatus)}
	 * .
	 */
	@Test(expected = AssertionError.class)
	public void testDoPostForHttpClientErrorExceptionNonMatchStatusCode() {
		HttpStatus wrongStatusCode = getNonMatchingErrorStatusCode(expectedMethodStatusCodes
				.get("POST"));
		restTemplateTestHelper.doPostForHttpStatusCodeException(url, "Body",
				null, "NonMatchStatusCode", wrongStatusCode);
	}

	/**
	 * Test method checking matching code correctly processed for
	 * {@link com.redblackit.web.test.RestTemplateTestHelper#doPutForHttpStatusCodeException(java.lang.String, java.lang.Object, java.lang.Object[], java.lang.String, org.springframework.http.HttpStatus)}
	 * .
	 */
	@Test
	public void testDoPutForHttpClientErrorExceptionMatchStatusCode() {
		restTemplateTestHelper.doPutForHttpStatusCodeException(url, "Body",
				null, "MatchStatusCode", expectedMethodStatusCodes.get("PUT"));
	}

	/**
	 * Test method checking non-matching code correctly processed for
	 * {@link com.redblackit.web.test.RestTemplateTestHelper#doPutForHttpStatusCodeException(java.lang.String, java.lang.Object, java.lang.Object[], java.lang.String, org.springframework.http.HttpStatus)}
	 * .
	 */
	@Test(expected = AssertionError.class)
	public void testDoPutForHttpClientErrorExceptionNonMatchStatusCode() {
		HttpStatus wrongStatusCode = getNonMatchingErrorStatusCode(expectedMethodStatusCodes
				.get("PUT"));
		restTemplateTestHelper.doPutForHttpStatusCodeException(url, "Body",
				null, "NonMatchStatusCode", wrongStatusCode);
	}

	/**
	 * Test method checking matching code correctly processed for
	 * {@link com.redblackit.web.test.RestTemplateTestHelper#doDeleteForHttpStatusCodeException(java.lang.String, java.lang.Object[], java.lang.String, org.springframework.http.HttpStatus)}
	 * .
	 */
	@Test
	public void testDoDeleteForHttpClientErrorExceptionMatchStatusCode() {
		restTemplateTestHelper.doDeleteForHttpStatusCodeException(url, null,
				"MatchStatusCode", expectedMethodStatusCodes.get("DELETE"));
	}

	/**
	 * Test method checking non-matching code correctly processed for
	 * {@link com.redblackit.web.test.RestTemplateTestHelper#doDeleteForHttpStatusCodeException(java.lang.String, java.lang.Object[], java.lang.String, org.springframework.http.HttpStatus)}
	 * .
	 */
	@Test(expected = AssertionError.class)
	public void testDoDeleteForHttpClientErrorExceptionNonMatchStatusCode() {
		HttpStatus wrongStatusCode = getNonMatchingErrorStatusCode(expectedMethodStatusCodes
				.get("DELETE"));
		restTemplateTestHelper.doDeleteForHttpStatusCodeException(url, null,
				"NonMatchStatusCode", wrongStatusCode);
	}

	/**
	 * Test method checking matching code correctly processed for
	 * {@link com.redblackit.web.test.RestTemplateTestHelper#doHeadForHttpStatusCodeException(java.lang.String, java.lang.Object[], java.lang.String, org.springframework.http.HttpStatus)}
	 * . Currently investigating why we get an IOException for HEAD. It looks
	 * like a bug in the old HttpClient, so we'll disable this test for now.
	 * HEAD is tested in the war tests.
	 */
//	@Test
//	public void testDoHeadForHttpClientErrorExceptionMatchStatusCode()
//			throws Throwable {
//		HttpStatus expectedStatusCode = expectedMethodStatusCodes.get("HEAD");
//		restTemplateTestHelper.doHeadForHttpStatusCodeException(url, null,
//				"MatchStatusCode", expectedStatusCode);
//	}

	/**
	 * Test method checking matching code correctly processed for
	 * {@link com.redblackit.web.test.RestTemplateTestHelper#doOptionsForHttpStatusCodeException(java.lang.String, java.lang.Object[], java.lang.String, org.springframework.http.HttpStatus)}
	 * .
	 */
	@Test
	public void testDoOptionsForHttpClientErrorExceptionMatchStatusCode() {
		restTemplateTestHelper.doOptionsForHttpStatusCodeException(url, null,
				"MatchStatusCode", expectedMethodStatusCodes.get("OPTIONS"));
	}

	/**
	 * Test method checking non-matching code correctly processed for
	 * {@link com.redblackit.web.test.RestTemplateTestHelper#doOptionsForHttpStatusCodeException(java.lang.String, java.lang.Object[], java.lang.String, org.springframework.http.HttpStatus)}
	 * .
	 */
	@Test(expected = AssertionError.class)
	public void testDoOptionsForHttpClientErrorExceptionNonMatchStatusCode() {
		HttpStatus wrongStatusCode = getNonMatchingErrorStatusCode(expectedMethodStatusCodes
				.get("OPTIONS"));
		restTemplateTestHelper.doOptionsForHttpStatusCodeException(url, null,
				"NonMatchStatusCode", wrongStatusCode);
	}

	/**
	 * Find a status code (not 2xx) which does not equal supplied code
	 * 
	 * @param expectedStatusCode
	 * @return different statusCode
	 */
	private HttpStatus getNonMatchingErrorStatusCode(
			HttpStatus expectedStatusCode) {
		return (expectedStatusCode.equals(HttpStatus.FORBIDDEN) ? HttpStatus.INTERNAL_SERVER_ERROR
				: HttpStatus.FORBIDDEN);
	}

	/**
	 * Status servlet class
	 */
	private static class StatusCodeServlet extends GenericServlet {
		/**
		 * Map of methods to response status codes
		 */
		private final Map<String, HttpStatus> methodStatusCodeMap;

		/**
		 * @param methodStatusCodeMap
		 */
		public StatusCodeServlet(Map<String, HttpStatus> methodStatusCodeMap) {
			this.methodStatusCodeMap = methodStatusCodeMap;
		}

		/**
		 * We return a response with the specified status code for the method.
		 * 
		 * @see javax.servlet.GenericServlet#service(javax.servlet.ServletRequest,
		 *      javax.servlet.ServletResponse)
		 */
		@Override
		public void service(ServletRequest req, ServletResponse res)
				throws ServletException, IOException {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			HttpStatus statusCode = methodStatusCodeMap
					.get(request.getMethod());
			response.setStatus(statusCode.value());
			String location = request.getRequestURL().toString();
			response.setHeader("Location", location);
			if (req.getContentLength() > 0) {
				String body = FileCopyUtils.copyToString(req.getReader());
				logger.debug(this.getClass().getName() + ":  body>>\n" + body
						+ "\nbody<<");
				FileCopyUtils.copy(body, res.getWriter());
				res.flushBuffer();
				res.setContentLength(req.getContentLength());
			} else {
				logger.debug(this.getClass().getName() + ":  body is empty");
				res.flushBuffer();
				res.setContentLength(0);
			}
		}

	};
}
