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

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ssl.SslConnector;

/**
 * @author djnorth
 * 
 * Interface for embedded Jetty server wrappers
 */
public interface EmbeddedJettyServer {
	
	/**
	 * Default server start/stop timeout in seconds
	 */
	public static final int DEFAULT_SERVER_TIMEOUT_SECS = 120 ;

	/**
	 * @return the server
	 */
	Server getServer();

	/**
	 * Get the (first) httpConnector, or null
	 * 
	 * @return connector
	 */
	Connector getHttpConnector();

	/**
	 * Get the (first) httpsConnector, or null
	 * 
	 * @return SSL connector
	 */
	SslConnector getHttpsConnector();
	
	/**
	 * Start server and wait timeout seconds for it to be started
	 * 
	 * @param timeoutSecs
	 * @return true if started
	 */
	boolean startWait(int timeoutSecs);
	
	/**
	 * Start server and wait default timeout (2 mins) for it to be started.
	 * 
	 * @return true if started
	 */
	boolean startWait();
	
	/**
	 * Start server, returning immediately
	 */
	void start();
	
	/**
	 * Join the server's thread
	 */
	void joinServer();
	
	/**
	 * Stop server and wait timeout seconds for it to be stopped
	 * 
	 * @param timeoutSecs
	 * @return true if stopped
	 */
	boolean stopWait(int timeoutSecs);
	
	/**
	 * Stop server and wait default timeout (2 mins) for it to be stopped.
	 * 
	 * @return true if stopped
	 */
	boolean stopWait();
	
	/**
	 * Stop server, and return immediately
	 */
	void stop();
	
	/**
	 * Stop server, and join it's thread
	 */
	void stopAndJoin();

}