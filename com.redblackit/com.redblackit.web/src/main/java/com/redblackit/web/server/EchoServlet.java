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

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;

/**
 * @author djnorth
 * 
 *         Simple servlet that echos the request back to the client, with
 *         requested URL in the Location header, and status code 200. This can
 *         serve as a test servlet, or a keep-alive/ping for the client to use.
 */
public class EchoServlet extends HttpServlet {

	private Logger logger = Logger.getLogger("web.server");

	/**
	 * Serialization stuff
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Use our common echo
	 * 
	 * @param req
	 * @param resp
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doEcho(req, resp, "GET");
	}

	/**
	 * Use our common echo
	 * 
	 * @param req
	 * @param resp
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doEcho(req, resp, "PUT");
	}

	/**
	 * Use our common echo
	 * 
	 * @param req
	 * @param resp
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doEcho(req, resp, "POST");
	}

	/**
	 * doEcho
	 * 
	 * <ul>
	 * <li>Log method, URL, headers, body</li>
	 * <li>Replicate request headers, except for setting location to received
	 * URL</li>
	 * <li>Replicate request body in response</li>
	 * </ul>
	 * 
	 * @param req
	 * @param resp
	 * @param method
	 */
	@SuppressWarnings("rawtypes")
	private void doEcho(HttpServletRequest req, HttpServletResponse resp,
			String method) throws IOException {
		String reqURI = req.getRequestURI();
		logger.debug(this.getClass().getName() + ":" + method + " - " + reqURI);

		for (Enumeration hdrse = req.getHeaderNames(); hdrse.hasMoreElements();) {
			String headerName = (String) hdrse.nextElement();
			int hnct = 0;
			for (Enumeration hdre = req.getHeaders(headerName); hdre
					.hasMoreElements();) {
				String headerValue = (String) hdre.nextElement();
				logger.debug(this.getClass().getName() + ":  header["
						+ headerName + "," + hnct + "]=" + headerValue);
				if (!headerName.equals("Location")) {
					resp.addHeader(headerName, headerValue);
				}
				hnct++;
			}
			
			if (hnct == 0)
			{
				resp.setHeader(headerName, "");
				logger.info(this.getClass().getName() + ":  header["
						+ headerName + "," + hnct + "]='' (empty)" );
			}
		}

		resp.setHeader("Location", reqURI);
		resp.setStatus(HttpServletResponse.SC_OK);

		if (req.getContentLength() > 0) {
			String body = FileCopyUtils.copyToString(req.getReader());
			logger.debug(this.getClass().getName() + ":  body>>\n" + body
					+ "\nbody<<");
			FileCopyUtils.copy(body, resp.getWriter());
			resp.flushBuffer();
			resp.setContentLength(req.getContentLength());
		}
		else
		{
			logger.debug(this.getClass().getName() + ":  body is empty");
			resp.setContentLength(0);
		}

	}
}
