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


package com.redblackit.web;

/**
 * @author djnorth
 *
 * Constants for use with test, for key- and trust-stores
 */
public interface KeyAndTrustStoreInfo {

	public static final String CLIENT0_KS_PWD = "client0";
	public static final String CLIENT0_KS = "src/test/resources/client0-keystore-only.jks";
	
	public static final String CLIENT0_TS_PWD = "client0";
	public static final String CLIENT0_TS = "src/test/resources/client0-truststore-only.jks";
	
	public static final String CLIENT1_KS_PWD = "client1";
	public static final String CLIENT1_KS = "src/test/resources/client1-keystore-truststore.jks";
	
	public static final String CLIENT1_TS_PWD = CLIENT1_KS_PWD;
	public static final String CLIENT1_TS = CLIENT1_KS;
	
	public static final String SERVER0_KS_PWD = "server0";
	public static final String SERVER0_KS = "src/test/resources/server0-keystore-only.jks";
	
	public static final String SERVER0_TS_PWD = "server0";
	public static final String SERVER0_TS = "src/test/resources/server0-truststore-only.jks";
	
	public static final String SERVER1_KS_PWD = "server1";
	public static final String SERVER1_KS = "src/test/resources/server1-keystore-truststore.jks";
	
	public static final String SERVER1_TS_PWD = SERVER1_KS_PWD;
	public static final String SERVER1_TS = SERVER1_KS;

}
