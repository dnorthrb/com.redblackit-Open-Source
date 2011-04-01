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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;

import com.redblackit.web.KeyAndTrustStoreInfo;
import com.redblackit.web.server.DefaultEmbeddedJettyServer;
import com.redblackit.web.server.EchoServlet;
import com.redblackit.web.server.EmbeddedJettyServer;
import com.redblackit.web.server.HostNetUtils;

/**
 * @author djnorth
 * 
 *         Based on Spring test class AbstractHttpRequestFactoryTestCase, but
 *         <ul>
 *         <li>allowing for http and https tests using appropriate http clients</li>
 *         <li>correcting redirect test to allow for transparent re-direction by
 *         HttpClient 4.x.</li>
 *         <li>using EmbeddedJettyServer wrapper class</li>
 *         <li>using EchoServer from com.redblacit.web</li>
 *         </ul>
 */
@RunWith(Parameterized.class)
public class HttpComponents4HttpRequestFactoryTest {

	private static EmbeddedJettyServer jettyServer;

	private static final String hostname = HostNetUtils.getLocalHostname();

	private static int httpPort = HostNetUtils.getFreePort(8080);
	private static int httpsPort = HostNetUtils.getFreePort(8443);

	/**
	 * Parameter method
	 */
	@Parameters
	public static List<Object[]> getParameters() {
		X509HttpClientFactoryBean x509HttpClientFactoryBean = new X509HttpClientFactoryBean();
		x509HttpClientFactoryBean.setHttpsPort(httpsPort);
		x509HttpClientFactoryBean.setKeyStore(KeyAndTrustStoreInfo.CLIENT0_KS);
		x509HttpClientFactoryBean
				.setKeyStorePassword(KeyAndTrustStoreInfo.CLIENT0_KS_PWD);
		x509HttpClientFactoryBean.setKeyStore(KeyAndTrustStoreInfo.CLIENT0_TS);
		x509HttpClientFactoryBean
				.setKeyStorePassword(KeyAndTrustStoreInfo.CLIENT0_TS_PWD);

		Object[][] parameters = {
				{ new DefaultHttpClient(), "http", httpPort },
				{ x509HttpClientFactoryBean.getObject(), "https", httpsPort } };

		return Arrays.asList(parameters);
	}

	private ClientHttpRequestFactory factory;

	private HttpClient httpClient;

	private String baseUrl;

	/**
	 * Start Jetty and deploy test servlets.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void startJettyServer() throws Exception {
		jettyServer = new DefaultEmbeddedJettyServer(httpPort, httpsPort,
				KeyAndTrustStoreInfo.SERVER1_KS,
				KeyAndTrustStoreInfo.SERVER1_KS_PWD);

		ServletContextHandler jettyContext = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		jettyContext.setContextPath("/");
		jettyServer.getServer().setHandler(jettyContext);

		jettyContext.addServlet(new ServletHolder(new EchoServlet()), "/echo");
		jettyContext.addServlet(new ServletHolder(new StatusServlet(200)),
				"/status/ok");
		jettyContext.addServlet(new ServletHolder(new StatusServlet(404)),
				"/status/notfound");
		jettyContext.addServlet(new ServletHolder(new MethodServlet("DELETE")),
				"/methods/delete");
		jettyContext.addServlet(new ServletHolder(new MethodServlet("GET")),
				"/methods/get");
		jettyContext.addServlet(new ServletHolder(new MethodServlet("HEAD")),
				"/methods/head");
		jettyContext.addServlet(
				new ServletHolder(new MethodServlet("OPTIONS")),
				"/methods/options");
		jettyContext.addServlet(new ServletHolder(new MethodServlet("POST")),
				"/methods/post");
		jettyContext.addServlet(new ServletHolder(new MethodServlet("PUT")),
				"/methods/put");
		jettyContext.addServlet(new ServletHolder(new RedirectServlet(
				"/status/ok")), "/redirect");
		jettyServer.startWait();
	}

	/**
	 * Constructor taking the variables for the tests i.e. the HttpClient to
	 * use, scheme and port
	 * 
	 * @param httpClient
	 * @param scheme
	 * @param port
	 */
	public HttpComponents4HttpRequestFactoryTest(HttpClient httpClient,
			String scheme, int port) {
		this.httpClient = httpClient;
		this.baseUrl = scheme + "://" + hostname + ':' + port;
	}

	/**
	 * Create a factory with the correct client
	 */
	@Before
	public final void createFactory() {
		HttpComponents4ClientHttpRequestFactory specificFactory = new HttpComponents4ClientHttpRequestFactory();
		specificFactory.setHttpClient(httpClient);
		factory = specificFactory;
	}

	/**
	 * Stop Jetty, and wait for it to end
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void stopJettyServer() throws Exception {
		if (jettyServer != null) {
			jettyServer.stopWait();
		}
	}

	/**
	 * Check creation of request for method and URI, and processing of simple
	 * HTTP status when page not found
	 * 
	 * @throws Exception
	 */
	@Test
	public void status() throws Exception {
		URI uri = new URI(baseUrl + "/status/notfound");
		ClientHttpRequest request = factory.createRequest(uri, HttpMethod.GET);
		Assert.assertEquals("Invalid HTTP method", HttpMethod.GET,
				request.getMethod());
		Assert.assertEquals("Invalid HTTP URI", uri, request.getURI());
		ClientHttpResponse response = request.execute();
		assertStatusEquals(response, HttpStatus.NOT_FOUND);
	}

	/**
	 * Test GET sending and receiving headers. Also ensure it works when we set the content length.
	 * 
	 * @throws Exception
	 */
	@Test
	public void echoGet() throws Exception {
		echoMethod(HttpMethod.GET, false, false);
	}

	/**
	 * Test PUT sending and receiving body and headers. Also ensure it works when we set the content length.
	 * 
	 * @throws Exception
	 */
	@Test
	public void echoPut() throws Exception {
		echoMethod(HttpMethod.PUT, true, true);
	}

	/**
	 * Test POST sending and receiving body and headers. Also ensure it works when we set the content length.
	 * 
	 * @throws Exception
	 */
	@Test
	public void echoPost() throws Exception {
		echoMethod(HttpMethod.POST, true, true);
	}

	/**
	 * Test DELETE sending and receiving headers. Also ensure it works when we set the content length.
	 * 
	 * @throws Exception
	 */
	@Test
	public void echoDelete() throws Exception {
		echoMethod(HttpMethod.DELETE, false, false);
	}

	/**
	 * Test HEAD sending and receiving headers. Also ensure it works when we set the content length.
	 * 
	 * @throws Exception
	 */
	@Test
	public void echoHead() throws Exception {
		echoMethod(HttpMethod.HEAD, true, false);
	}

	/**
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	private void echoMethod(HttpMethod method, boolean sendBody, boolean recvBody) throws IOException, URISyntaxException,
			UnsupportedEncodingException {
		ClientHttpRequest request = factory.createRequest(new URI(baseUrl
				+ "/echo"), method);
		Assert.assertEquals("Invalid HTTP method", method,
				request.getMethod());
		String headerName = "MyHeader";
		String headerValue1 = "value1";
		request.getHeaders().add(headerName, headerValue1);
		String headerValue2 = "value2";
		request.getHeaders().add(headerName, headerValue2);
		byte[] body = null;
		if (sendBody)
		{
			body = "Hello World".getBytes("UTF-8");
			FileCopyUtils.copy(body, request.getBody());
			request.getHeaders().setContentLength(body.length);
		}
		else
		{
			request.getHeaders().setContentLength(0);
		}
		
		ClientHttpResponse response = request.execute();
		assertStatusEquals(response, HttpStatus.OK);
		Assert.assertTrue("Header not found", response.getHeaders()
				.containsKey(headerName));
		Assert.assertEquals("Header value not found", Arrays.asList(
				headerValue1, headerValue2),
				response.getHeaders().get(headerName));
		byte[] result = FileCopyUtils.copyToByteArray(response.getBody());
		if (recvBody)
		{
			Assert.assertTrue("Invalid body", Arrays.equals(body, result));
		}
		else
		{
			Assert.assertTrue("Invalid body (non-empty)", result.length == 0);
		}
	}

	/**
	 * Check we cannot set body request that has been sent already, and get an
	 * appropriate exception
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalStateException.class)
	public void multipleWrites() throws Exception {
		ClientHttpRequest request = factory.createRequest(new URI(baseUrl
				+ "/echo"), HttpMethod.POST);
		byte[] body = "Hello World".getBytes("UTF-8");
		FileCopyUtils.copy(body, request.getBody());
		ClientHttpResponse response = request.execute();
		try {
			FileCopyUtils.copy(body, request.getBody());
		} finally {
			response.close();
		}
	}

	/**
	 * Check we cannot add headers that has been sent already, and get an
	 * appropriate exception
	 * 
	 * @throws Exception
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void headersAfterExecute() throws Exception {
		ClientHttpRequest request = factory.createRequest(new URI(baseUrl
				+ "/echo"), HttpMethod.POST);
		request.getHeaders().add("MyHeader", "value");
		byte[] body = "Hello World".getBytes("UTF-8");
		FileCopyUtils.copy(body, request.getBody());
		ClientHttpResponse response = request.execute();
		try {
			request.getHeaders().add("MyHeader", "value");
		} finally {
			response.close();
		}
	}

	/**
	 * Check all methods
	 * 
	 * @throws Exception
	 */
	@Test
	public void httpMethods() throws Exception {
		assertHttpMethod("get", HttpMethod.GET);
		assertHttpMethod("head", HttpMethod.HEAD);
		assertHttpMethod("post", HttpMethod.POST);
		assertHttpMethod("put", HttpMethod.PUT);
		assertHttpMethod("options", HttpMethod.OPTIONS);
		assertHttpMethod("delete", HttpMethod.DELETE);
	}

	/**
	 * See above. Use method-matching servlet for supplied method
	 * 
	 * @param path
	 * @param method
	 * @throws Exception
	 */
	private void assertHttpMethod(String path, HttpMethod method)
			throws Exception {
		ClientHttpResponse response = null;
		try {
			ClientHttpRequest request = factory.createRequest(new URI(baseUrl
					+ "/methods/" + path), method);
			response = request.execute();
			Assert.assertEquals("Invalid method", path
					.toUpperCase(Locale.ENGLISH), request.getMethod().name());
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * Check re-direct will happen transparently, for different methods.
	 * 
	 * @throws Exception
	 */
	@Test
	public void redirect() throws Exception {
		redirectWithMethod(HttpMethod.POST);
		redirectWithMethod(HttpMethod.PUT);
		redirectWithMethod(HttpMethod.GET);
	}

	/**
	 * Do redirect test for specific method
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private void redirectWithMethod(HttpMethod method) throws IOException,
			URISyntaxException {
		ClientHttpResponse response = null;
		try {
			ClientHttpRequest request = factory.createRequest(new URI(baseUrl
					+ "/redirect"), method);
			response = request.execute();

			assertStatusEquals(response, HttpStatus.OK);
			Assert.assertNull("Location header not expected", response
					.getHeaders().getLocation());
			Assert.assertEquals("Path is as originally re-directed", baseUrl
					+ "/status/ok", response.getHeaders().get("X-Status-URL")
					.get(0));

		} finally {
			if (response != null) {
				response.close();
				response = null;
			}
		}
	}

	/**
	 * Assert on status value, giving status text on failure
	 * 
	 * @param response
	 * @param statusCode
	 * @throws IOException
	 */
	private void assertStatusEquals(ClientHttpResponse response,
			HttpStatus statusCode) throws IOException {
		Assert.assertEquals("Invalid status code:" + response.getStatusText(),
				statusCode, response.getStatusCode());
	}

	/**
	 * Servlet that sets a given status code, and the URL in a special header
	 * */
	private static class StatusServlet extends GenericServlet {

		/**
		 * For serialization
		 */
		private static final long serialVersionUID = 1L;
		
		private final int sc;

		private StatusServlet(int sc) {
			this.sc = sc;
		}

		@Override
		public void service(ServletRequest req, ServletResponse resp)
				throws ServletException, IOException {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) resp;
			response.setStatus(sc);
			String location = request.getRequestURL().toString();
			response.setHeader("X-Status-URL", location);
		}
	}

	/**
	 * Servlet matching method against URL
	 */
	private static class MethodServlet extends GenericServlet {

		/**
		 * For serialization
		 */
		private static final long serialVersionUID = 1L;
		
		private final String method;

		private MethodServlet(String method) {
			this.method = method;
		}

		@Override
		public void service(ServletRequest req, ServletResponse res)
				throws ServletException, IOException {
			HttpServletRequest httpReq = (HttpServletRequest) req;
			Assert.assertEquals("Invalid HTTP method", method,
					httpReq.getMethod());
		}
	}

	/**
	 * Servlet to re-direct to configured URL
	 */
	private static class RedirectServlet extends GenericServlet {

		/**
		 * For serialization
		 */
		private static final long serialVersionUID = 1L;
		
		private final String location;

		private RedirectServlet(String location) {
			this.location = location;
		}

		@Override
		public void service(ServletRequest req, ServletResponse res)
				throws ServletException, IOException {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			response.setStatus(HttpServletResponse.SC_SEE_OTHER);
			StringBuilder builder = new StringBuilder();
			builder.append(request.getScheme()).append("://");
			builder.append(request.getServerName()).append(':')
					.append(request.getServerPort());
			builder.append(location);
			response.setHeader("Location", builder.toString());
		}
	}

}