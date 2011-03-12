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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

/**
 * Utility class that finds free BSD ports for use in testing scenario's.
 * 
 * Based on the Spring test class of the same name, but allowing supplied ports
 * to be checked 9and so used) first
 */
public abstract class HostNetUtils {

	public static final int MIN_SAFE_PORT = 1024;

	public static final int MAX_PORT = 65535;

	private static final Random random = new Random();
	
	private static final Logger logger = Logger.getLogger("web.server");

	/**
	 * Returns the number of a free port in the default range.
	 */
	public static int getFreePort() {
		return getFreePort(0, 0, 0);
	}

	/**
	 * Returns the number of a free port in the default range, starting from
	 * supplied value
	 * 
	 * @param startPort
	 *            port value to try first
	 */
	public static int getFreePort(int startPort) {
		return getFreePort(0, 0, startPort);
	}

	/**
	 * Returns the number of a free port in the given range, starting from
	 * supplied value. Note that the supplied value need not be between the min
	 * and max values, but will be ignored if <= 0.
	 * 
	 * @param minPort
	 *            minimum port value (default MIN_SAFE_PORT)
	 * @param maxPort
	 *            maximum port value (default MAX_PORT)
	 * @param startPort
	 *            port value to try first
	 */
	public static int getFreePort(int minPort, int maxPort, int startPort) {
		
		if (minPort <= 0)
		{
			logger.info("defaulting minPort:" + minPort + "->" + MIN_SAFE_PORT);
			minPort = MIN_SAFE_PORT;
		}
		
		if (maxPort <= 0 || maxPort > MAX_PORT)
		{
			logger.info("defaulting maxPort:" + maxPort + "->" + MAX_PORT);
			maxPort = MAX_PORT;
		}
		
		Assert.state(minPort < maxPort, "after defaulting, minPort=" + minPort + " >= maxPort=" + maxPort);

		if (startPort > 0 && isPortAvailable(startPort)) {
			logger.info("Using startPort=" + startPort);
			return startPort;
		}

		int candidatePort;
		int portRange = maxPort - minPort;
		int searchCounter = 0;
		do {
			if (++searchCounter > portRange) {
				throw new IllegalStateException(String.format(
						"There were no ports available in the range %d to %d",
						minPort, maxPort));
			}
			candidatePort = getRandomPort(minPort, portRange);
			logger.info("Trying candidatePort=" + candidatePort);
		} while (!isPortAvailable(candidatePort));

		logger.info("Using candidatePort=" + candidatePort);
		return candidatePort;
	}
	
	/**
	 * Get local hostname
	 * 
	 * @return hostname
	 */
	public static String getLocalHostname() {
		String localhost;
		try {
			localhost = InetAddress.getLocalHost().getHostName();
			logger.debug("localhost=" + localhost);
		} catch (UnknownHostException ex) {
			localhost = "localhost";
			logger.warn("couldn't get localhost name, defaulting to localhost");
		}

		logger.debug("resulting localhost=" + localhost);
		return localhost;
	}

	/**
	 * Main providing facility to run from command line
	 * 
	 * @param args
	 */
	public void main(String[] args) {
		final String msgpfx = HostNetUtils.getLocalHostname() + "-" + HostNetUtils.class.getName();
		logger.info(msgpfx + " " + Arrays.toString(args));
		try {
			final int startPort = (args.length > 0 ? Integer.valueOf(args[0])
					: -1);
			final int minPort = (args.length > 1 ? Integer.valueOf(args[1])
					: MIN_SAFE_PORT);
			final int maxPort = (args.length > 2 ? Integer.valueOf(args[2])
					: MAX_PORT);
			
			final int freePort = getFreePort(minPort, maxPort, startPort);
			logger.info(msgpfx + ":freePort=" + freePort);

		} catch (Throwable t) {
			logger.fatal(msgpfx + " usage:java " + msgpfx
					+ " [startPort [minPort [maxPort]]]", t);
		}
	}

	/**
	 * Get a rendom port value
	 * 
	 * @param minPort
	 * @param portRange
	 * @return
	 */
	private static int getRandomPort(int minPort, int portRange) {
		return minPort + random.nextInt(portRange);
	}

	/**
	 * Check if port is available
	 * 
	 * @param port
	 * @return
	 */
	private static boolean isPortAvailable(int port) {
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket();
		} catch (IOException ex) {
			throw new IllegalStateException("Unable to create ServerSocket.",
					ex);
		}

		try {
			InetSocketAddress sa = new InetSocketAddress(port);
			serverSocket.bind(sa);
			return true;
		} catch (IOException ex) {
			return false;
		} finally {
			try {
				serverSocket.close();
			} catch (IOException ex) {
				// ignore
			}
		}
	}

}
