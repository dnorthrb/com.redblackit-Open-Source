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

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author djnorth
 * 
 *         Factory creating an HttpClient that is configured for x.509.
 */
public class X509HttpClientFactoryBean implements FactoryBean<HttpClient>,
		InitializingBean {

	/**
	 * Logger
	 */
	private Logger logger = Logger.getLogger("web.client");

	/**
	 * KeyStore
	 */
	private String keyStore = System.getProperty("javax.net.ssl.keyStore");

	/**
	 * KeyStore type
	 */
	private String keyStoreType = "jks";

	/**
	 * KeyStore password
	 */
	private String keyStorePassword = System.getProperty(
			"javax.net.ssl.keyStorePassword", "changeit");

	/**
	 * TrustStore for client
	 */
	private String trustStore = System.getProperty("javax.net.ssl.trustStore");

	/**
	 * TrustStore type
	 */
	private String trustStoreType = "jks";

	/**
	 * TrustStore password
	 */
	private String trustStorePassword = System.getProperty(
			"javax.net.ssl.trustStorePassword", "changeit");

	/**
	 * https port
	 */
	private int httpsPort = 8443;

	/**
	 * Optional HttpParams
	 */
	private HttpParams httpParams = null;

	/**
	 * Constructor taking mandatory fields
	 */
	public X509HttpClientFactoryBean() {
	}

	/**
	 * Set keyStore filename (default system property javax.net.ssl.keyStore)
	 * 
	 * @param keyStore
	 *            the keyStore to set
	 */
	public void setKeyStore(String keyStore) {
		this.keyStore = keyStore;
	}

	/**
	 * @return the keyStore
	 */
	public String getKeyStore() {
		return keyStore;
	}

	/**
	 * Set the keyStore type (default "jks")
	 * 
	 * @param keyStoreType
	 *            the keyStoreType to set
	 */
	public void setKeyStoreType(String keyStoreType) {
		this.keyStoreType = keyStoreType;
	}

	/**
	 * @return the keyStoreType
	 */
	public String getKeyStoreType() {
		return keyStoreType;
	}

	/**
	 * Set keyStorePassword (default system property
	 * javax.net.ssl.keyStorePassword, or changeit)
	 * N.B. This must equal the (private) key password
	 * 
	 * @param keyStorePassword
	 *            the keyStorePassword to set
	 */
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	/**
	 * @return the keyStorePassword
	 */
	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	/**
	 * Set trustStore filename (default system property
	 * javax.net.ssl.trustStore)
	 * 
	 * @param trustStore
	 *            the trustStore to set
	 */
	public void setTrustStore(String trustStore) {
		this.trustStore = trustStore;
	}

	/**
	 * @return the trustStore
	 */
	public String getTrustStore() {
		return trustStore;
	}

	/**
	 * Set the trustStore type (default "jks")
	 * 
	 * @param trustStoreType
	 *            the trustStoreType to set
	 */
	public void setTrustStoreType(String trustStoreType) {
		this.trustStoreType = trustStoreType;
	}

	/**
	 * @return the trustStoreType
	 */
	public String getTrustStoreType() {
		return trustStoreType;
	}

	/**
	 * Set trustStorePassword (default system property
	 * javax.net.ssl.trustStorePassword, or changeit)
	 * 
	 * @param trustStorePassword
	 *            the trustStorePassword to set
	 */
	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	/**
	 * @return the trustStorePassword
	 */
	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	/**
	 * Set the https port (default 8443)
	 * 
	 * @param httpsPort
	 *            the httpsPort to set
	 */
	public void setHttpsPort(int httpsPort) {
		this.httpsPort = httpsPort;
	}

	/**
	 * @return the hppsPort
	 */
	public int getHttpsPort() {
		return httpsPort;
	}

	/**
	 * @param httpParams
	 *            the httpParams to set
	 */
	public void setHttpParams(HttpParams httpParams) {
		this.httpParams = httpParams;
	}

	/**
	 * @return the httpParams
	 */
	public HttpParams getHttpParams() {
		return httpParams;
	}

	/**
	 * Ensure we have keystores and passwords defined.
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (getKeyStore() == null || getKeyStore().length() == 0
				|| getKeyStoreType() == null || getKeyStoreType().length() == 0
				|| getKeyStorePassword() == null
				|| getKeyStorePassword().length() == 0
				|| getTrustStore() == null || getTrustStore().length() == 0
				|| getTrustStoreType() == null
				|| getTrustStoreType().length() == 0
				|| getTrustStorePassword() == null
				|| getTrustStorePassword().length() == 0) {
			throw new IllegalArgumentException("Missing key/trust store info:"
					+ this);
		}
	}

	/**
	 * Create the httpClient from the properties set
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public HttpClient getObject() {

		if (logger.isDebugEnabled()) {
			logger.debug("getObject:E:this=" + this);
		}

		try {

			final KeyStore keystore = KeyStore.getInstance(getKeyStoreType());
			InputStream keystoreInput = new FileInputStream(getKeyStore());
			keystore.load(keystoreInput, getKeyStorePassword().toCharArray());

			KeyStore truststore = KeyStore.getInstance(getTrustStoreType());
			InputStream truststoreInput = new FileInputStream(getTrustStore());
			truststore.load(truststoreInput, getTrustStorePassword()
					.toCharArray());

			final SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("https", new SSLSocketFactory(
					keystore, getKeyStorePassword(), truststore),
					getHttpsPort()));

			if (httpParams == null) {
				httpParams = new BasicHttpParams();
			}

			HttpClient httpClient = new DefaultHttpClient(
					new ThreadSafeClientConnManager(httpParams, schemeRegistry),
					httpParams);

			if (logger.isDebugEnabled()) {
				logger.debug("getObject:R:httpClient=" + httpClient);
			}

			return httpClient;

		} catch (Throwable t) {
			throw new RuntimeException(this.toString(), t);
		}
	}

	/**
	 * Return the interface type. This should be sufficient information to
	 * support @Autowire
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return HttpClient.class;
	}

	/**
	 * We don't cache our client
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return false;
	}

	/**
	 * toString
	 */
	public String toString() {
		StringBuffer tos = new StringBuffer(super.toString());
		tos.append(":keyStore=").append(keyStore == null ? null : keyStore);
		tos.append(":keyStoreType=").append(keyStoreType);
		tos.append(":keyStorePassword=").append(keyStorePassword);
		tos.append(":trustStore=").append(
				trustStore == null ? null : trustStore);
		tos.append(":trustStoreType=").append(trustStoreType);
		tos.append(":trustStorePassword=").append(trustStorePassword);
		tos.append(":httpsPort=").append(httpsPort);
		tos.append(":httpParams=").append(httpParams);

		return tos.toString();
	}

}
