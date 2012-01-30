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

package com.redblackit.web.server;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import java.io.IOException;

/**
 * @author djnorth
 * 
 *         Helper class for use in embedded server agents, and testing, and
 *         providing common SSL configuration.
 * 
 *         This is only intended to be a very light-weight wrapper, and it is
 *         entirely legitimate to do all the configuration outside, by supplying
 *         both port values <= 0.
 */
public class DefaultEmbeddedJettyServer implements EmbeddedJettyServer {

	private static Logger logger = Logger.getLogger("web.server");

	/**
	 * Our server
	 */
	private Server server;

	/**
	 * Constructor taking key- and trust-store info
	 * 
	 * We've suppressed deprecation warnings given that setPort etc have been
	 * deprecated without corresponding update to Javadoc.
	 * 
	 * @param keyStore
	 * @param keyStorePassword
	 * @param trustStore
	 * @param trustStorePassword
	 *            for private key (defaults to keyStorePassword)
	 */
	@SuppressWarnings("deprecation")
	public DefaultEmbeddedJettyServer(final int httpPort, final int httpsPort,
			final String keyStore, final String keyStorePassword,
			final String trustStore, final String trustStorePassword) {
		
		final String pmsg = ":httpPort=" + httpPort + ":httpsPort=" + httpsPort + ":keyStore=" + keyStore + ":keyStorePassword=" 
						+ trustStorePassword + ":trustStore=" + trustStore + ":trustStorePassword=" + trustStorePassword;
		
		Assert.state(httpPort > 0 || httpsPort > 0, "one of httpPort, httpsPort must be gt 0" + pmsg);

		if (httpsPort > 0) {
			Assert.state(httpPort != httpsPort, "httpPort equals httpsPort=" + httpsPort + pmsg);
			Assert.notNull(keyStore, "keyStore" + pmsg);
			Assert.notNull(keyStorePassword, "keyStorePassword" + pmsg);
			Assert.notNull(trustStore, "trustStore" + pmsg);
			Assert.notNull(trustStorePassword, "trustStorePassword" + pmsg);

			Assert.state(
					!keyStore.equals(trustStore)
							|| (keyStore.equals(trustStore) && keyStorePassword
									.equals(trustStorePassword)), "keyStore = trustStore but keyStorePassword != trustStorePassword" + pmsg);
		}
		else
		{
			if (keyStore != null || keyStorePassword != null || trustStore != null || trustStorePassword != null)
			{
				logger.warn("httpsPort=" + httpsPort + " <= 0, but keyInfo supplied" + pmsg);
			}
		}

		setServer(new Server());

		if (httpPort > 0) {
			Connector connector = new SelectChannelConnector();
			connector.setPort(httpPort);
			getServer().addConnector(connector);
		}

		if (httpsPort > 0) {
			SslConnector sslConnector = new SslSelectChannelConnector();
			sslConnector.setPort(httpsPort);
			sslConnector.setKeystore(keyStore);
			sslConnector.setPassword(keyStorePassword);
			sslConnector.setKeyPassword(keyStorePassword);
			sslConnector.setTruststore(trustStore);
			sslConnector.setTrustPassword(trustStorePassword);
			sslConnector.setWantClientAuth(true);
			getServer().addConnector(sslConnector);
		}
	}

	/**
	 * Convenience constructor for common key and trust-store.
	 * 
	 * @param keyStore
	 * @param keyStorePassword
	 */
	public DefaultEmbeddedJettyServer(final int httpPort, final int httpsPort,
			final String keyStore, final String keyStorePassword) {

		this(httpPort, httpsPort, keyStore, keyStorePassword, keyStore,
				keyStorePassword);
	}

	/**
	 * Get our constructed server
	 * 
	 * @return the server
	 * @see com.redblackit.web.server.EmbeddedJettyServer#getServer()
	 */
	@Override
	public Server getServer() {
		return server;
	}

	/**
	 * Get the (first) httpConnector, or null
	 * 
	 * @return connector
	 * @see com.redblackit.web.server.EmbeddedJettyServer#getHttpConnector()
	 */
	@Override
	public Connector getHttpConnector() {
		for (Connector connector : getServer().getConnectors()) {
			if (!(connector instanceof SslConnector)) {
				return connector;
			}
		}

		return null;
	}

	/**
	 * Get the (first) httpsConnector, or null
	 * 
	 * @see com.redblackit.web.server.EmbeddedJettyServer#getHttpsConnector()
	 */
	@Override
	public SslConnector getHttpsConnector() {
		for (Connector connector : getServer().getConnectors()) {
			if (connector instanceof SslConnector) {
				return (SslConnector) connector;
			}
		}

		return null;
	}

	/**
	 * Start server and wait timeout in seconds for it to be started. If there's
	 * an exception, this will be wrapped as a RuntimeException and re-throw.
	 * 
	 * @param timeoutSecs
	 *            if <=0, then use the default.
	 * @return true if started
	 * @see com.redblackit.web.server.EmbeddedJettyServer#startWait(int)
	 */
	@Override
	public boolean startWait(int timeoutSecs) {

		if (timeoutSecs <= 0) {
			timeoutSecs = DEFAULT_SERVER_TIMEOUT_SECS;
		}
		int waitSecs = 0;

		try {
			getServer().start();
			logger.info("Starting server:" + this);
			while (waitSecs <= timeoutSecs && !getServer().isStarted()) {
				Thread.sleep(1000);
				++waitSecs;
			}

		} catch (Throwable t) {
			final String msg = "server start failed:" + this;
			logger.error(msg, t);
			throw new RuntimeException(msg, t);
		}

		final boolean isStarted = getServer().isStarted();
		if (isStarted) {
			logger.info("Server started after " + waitSecs + " s:" + this);
		} else {
			logger.warn("Server start timed out after " + waitSecs + " s:"
					+ this);
		}
		return isStarted;
	}

	/**
	 * Start server and wait default timeout (2 mins) for it to be started.
	 * 
	 * @return true if started
	 * @see com.redblackit.web.server.EmbeddedJettyServer#startWait()
	 */
	@Override
	public boolean startWait() {
		return startWait(DEFAULT_SERVER_TIMEOUT_SECS);
	}

	/**
	 * Start server, returning immediately
	 * 
	 * @see com.redblackit.web.server.EmbeddedJettyServer#start()
	 */
	@Override
	public void start() {
		try {
			getServer().start();
			logger.info("Starting server:" + this);
		} catch (Throwable t) {
			final String msg = "server start failed:" + this;
			logger.error(msg, t);
			throw new RuntimeException(msg, t);
		}
		
		logger.info("Server start issued:" + this);

	}

	/**
	 * Join the server's thread
	 * 
	 * @see com.redblackit.web.server.EmbeddedJettyServer#joinServer()
	 */
	@Override
	public void joinServer() {
		logger.info("Joining server thread:" + this);
		try {
			getServer().join();
			logger.info("finished:" + this);
		} catch (Throwable t) {
			final String msg = "server join failed:" + this;
			logger.error(msg, t);
			throw new RuntimeException(msg, t);
		}
	}

	/**
	 * Stop server and wait timeout seconds for it to be stopped
	 * 
	 * @param timeoutSecs
	 * @return true if stopped
	 * @see com.redblackit.web.server.EmbeddedJettyServer#stopWait(int)
	 */
	@Override
	public boolean stopWait(int timeoutSecs) {

		if (timeoutSecs <= 0) {
			timeoutSecs = DEFAULT_SERVER_TIMEOUT_SECS;
		}
		int waitSecs = 0;

		try {
			getServer().stop();
			logger.info("Stopping server:" + this);
			while (waitSecs <= timeoutSecs && !getServer().isStopped()) {
				Thread.sleep(1000);
				++waitSecs;
			}

		} catch (Throwable t) {
			final String msg = "server stop failed:" + this;
			logger.error(msg, t);
			throw new RuntimeException(msg, t);
		}

		final boolean isStopped = getServer().isStopped();
		if (isStopped) {
			logger.info("Server stopped after " + waitSecs + " s:" + this);
		} else {
			logger.warn("Server stop timed out after " + waitSecs + " s:"
					+ this);
		}
		return isStopped;
	}

	/**
	 * Stop server and wait default timeout (2 mins) for it to be stopped.
	 * 
	 * @return true if stopped
	 * @see com.redblackit.web.server.EmbeddedJettyServer#stopWait()
	 */
	@Override
	public boolean stopWait() {
		return stopWait(DEFAULT_SERVER_TIMEOUT_SECS);
	}

	/**
	 * Stop server, and return immediately
	 * 
	 * @see com.redblackit.web.server.EmbeddedJettyServer#stop()
	 */
	@Override
	public void stop() {
		logger.info("Stopping server:" + this);
		try {
			getServer().stop();
			logger.info("finished:" + this);
		} catch (Throwable t) {
			final String msg = "server stop failed:" + this;
			logger.error(msg, t);
			throw new RuntimeException(msg, t);
		}
	}

	/**
	 * Stop server, and join it's thread
	 * 
	 * @see com.redblackit.web.server.EmbeddedJettyServer#stopAndJoin()
	 */
	@Override
	public void stopAndJoin() {
		stop();
		joinServer();
	}

	/**
	 * toString
	 */
	public String toString() {
		StringBuffer tos = new StringBuffer(super.toString());

		tos.append(":server=" + server);

		return tos.toString();
	}

	/**
	 * @param server
	 *            the server to set
	 */
	protected void setServer(Server server) {
		this.server = server;
	}

}
