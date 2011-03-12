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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author djnorth
 * 
 *         Test class for HostNetUtils
 */
@RunWith(Parameterized.class)
public class FreePortScannerTest {

	@Parameters
	public static List<Object[]> testParameters() {
		Object[][] parameters = {
				{ 1, HostNetUtils.MAX_PORT, 8080, 1, HostNetUtils.MAX_PORT },
				{ -1, -1, -1, HostNetUtils.MIN_SAFE_PORT, HostNetUtils.MAX_PORT },
				{ 8081, Integer.MAX_VALUE, 8443, 8081, HostNetUtils.MAX_PORT },
				{ 8081, 20000, 8443, 8081, 20000 },
				{ 0, 0, 0, HostNetUtils.MIN_SAFE_PORT, HostNetUtils.MAX_PORT },
				{ -1, -1, 9443, HostNetUtils.MIN_SAFE_PORT, HostNetUtils.MAX_PORT }
		};
		
		return Arrays.asList(parameters);
	}

	private static List<ServerSocket> sSockets = new ArrayList<ServerSocket>();

	/**
	 * Test parameters
	 */
	private int suppliedMinPort;
	private int suppliedMaxPort;
	private int suppliedStartPort;
	private int effectiveMinPort;
	private int effectiveMaxPort;

	/**
	 * Logger
	 */
	private Logger logger = Logger.getLogger("web.server");

	/**
	 * Constructor taking
	 * 
	 * @param suppliedMinPort
	 * @param suppliedMaxPort
	 * @param suppliedStartPort
	 */
	public FreePortScannerTest(int suppliedMinPort, int suppliedMaxPort, int suppliedStartPort, int effectiveMinPort, int effectiveMaxPort) {
		this.suppliedMinPort = suppliedMinPort;
		this.suppliedMaxPort = suppliedMaxPort;
		this.suppliedStartPort = suppliedStartPort;
		this.effectiveMinPort = effectiveMinPort;
		this.effectiveMaxPort = effectiveMaxPort;
		logger.info("Constructed:" + this);
	}

	/**
	 * Test that minimum, maximum and start are respected, and that socket works
	 */
	@Test
	public void testGetFreePortAllParms() throws IOException {
		
		final int freePort = HostNetUtils.getFreePort(suppliedMinPort, suppliedMaxPort, suppliedStartPort);
		verifyFreePort(freePort, this.effectiveMinPort, this.effectiveMaxPort, this.suppliedStartPort);
	}


	/**
	 * Test that call with only port works with expected default limits
	 */
	@Test
	public void testGetFreePortPortOnly() throws IOException {
		
		final int freePort = HostNetUtils.getFreePort(this.suppliedStartPort);
		verifyFreePort(freePort, HostNetUtils.MIN_SAFE_PORT, HostNetUtils.MAX_PORT, this.suppliedStartPort);
	}


	/**
	 * Test that call with only port works with everything defaulted
	 */
	@Test
	public void testGetFreePortAllDefaults() throws IOException {
		
		final int freePort = HostNetUtils.getFreePort();
		verifyFreePort(freePort, HostNetUtils.MIN_SAFE_PORT, HostNetUtils.MAX_PORT, 0);
	}
	
	
	/**
	 * Check we got what expected for given parameters and defaults
	 * 
	 * @param freePort
	 * @throws IOException
	 */
	private void verifyFreePort(final int freePort, final int useMinPort, final int useMaxPort, final int useStartPort) throws IOException {
		final String msg = ":freePort=" + freePort + ":useMinPort=" + useMinPort + ":usemaxPort=" + useMaxPort + ":useStartPort=" + useStartPort + ":" +  this;
		
		Assert.assertTrue("freePort should be gt 0" + msg, freePort > 0);
		Assert.assertTrue("freePort should be ge useMinPort:" + msg,
				freePort >= useMinPort);
		Assert.assertTrue("freePort should be le useMaxPort:" + msg,
				freePort <= useMaxPort);

		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket();
		} catch (IOException ioe) {
			logger.error("Unable to create ServerSocket" + msg);
			throw ioe;
		}

		try {
			InetSocketAddress sa = new InetSocketAddress(freePort);
			serverSocket.bind(sa);
			sSockets.add(serverSocket);
		} catch (Throwable t) {
			try {
				serverSocket.close();
			} catch (IOException ioe) {
				// Ignore
			}
			Assert.fail("Unable to bind ServerSocket" + msg);
		}
	}
	
	
	/**
	 * Clean up sockets
	 */
	@AfterClass
	public static void cleanUpClass()
	{
		for (ServerSocket serverSocket : sSockets)
		{
			try
			{
				serverSocket.close();
			}
			catch (Throwable t)
			{
				// Ignore
			}
		}
	}
	

	/**
	 * toString
	 */
	public String toString() {
		StringBuffer tos = new StringBuffer(super.toString());
		tos.append(":suppliedMinPort=").append(suppliedMinPort);
		tos.append(":suppliedMaxPort=").append(suppliedMaxPort);
		tos.append(":suppliedStartPort=").append(suppliedStartPort);
		tos.append(":effectiveMinPort=").append(effectiveMinPort);
		tos.append(":effectiveMaxPort=").append(effectiveMaxPort);
		tos.append(":sSockets=").append(sSockets);

		return tos.toString();
	}
}
