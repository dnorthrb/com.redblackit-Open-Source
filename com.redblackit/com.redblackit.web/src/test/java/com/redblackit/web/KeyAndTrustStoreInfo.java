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

import java.io.File;

/**
 * Constants for use with test, for key- and trust-stores
 *
 * @author djnorth
 */
public abstract class KeyAndTrustStoreInfo {

    public static final String CLIENT0_KS_PWD = "client0";
    public static final String CLIENT0_TS_PWD = "client0";
    public static final String CLIENT1_KS_PWD = "client1";
    public static final String CLIENT1_TS_PWD = CLIENT1_KS_PWD;
    public static final String SERVER0_KS_PWD = "server0";
    public static final String SERVER0_TS_PWD = "server0";
    public static final String SERVER1_KS_PWD = "server1";
    public static final String SERVER1_TS_PWD = SERVER1_KS_PWD;

    private static final String CLIENT0_KS = "src/test/resources/keystores/client0-keystore-only.jks";
    private static final String CLIENT0_TS = "src/test/resources/keystores/client0-truststore-only.jks";
    private static final String CLIENT1_KS = "src/test/resources/keystores/client1-keystore-truststore.jks";
    private static final String CLIENT1_TS = CLIENT1_KS;
    private static final String SERVER0_KS = "src/test/resources/keystores/server0-keystore-only.jks";
    private static final String SERVER0_TS = "src/test/resources/keystores/server0-truststore-only.jks";
    private static final String SERVER1_KS = "src/test/resources/keystores/server1-keystore-truststore.jks";
    private static final String SERVER1_TS = SERVER1_KS;

    public static String getClient0Ks() {
        return getAbsoluteFileName(CLIENT0_KS);
    }

    public static String getClient0Ts() {
        return getAbsoluteFileName(CLIENT0_TS);
    }

    public static String getClient1Ks() {
        return getAbsoluteFileName(CLIENT1_KS);
    }

    public static String getClient1Ts() {
        return getAbsoluteFileName(CLIENT1_TS);
    }

    public static String getServer0Ks() {
        return getAbsoluteFileName(SERVER0_KS);
    }

    public static String getServer0Ts() {
        return getAbsoluteFileName(SERVER0_TS);
    }

    public static String getServer1Ks() {
        return getAbsoluteFileName(SERVER1_KS);
    }

    public static String getServer1Ts() {
        return getAbsoluteFileName(SERVER1_TS);
    }

    /**
     * helper adjusting path according to current user.dir, to allow us to work with Maven
     *
     * @param fileName
     */
    private static String getAbsoluteFileName(String fileName) {
        final File userDir = new File(System.getProperty("user.dir"));
        File file = new File(userDir, fileName);
        if (!file.exists()) {
            File parent = new File(userDir, "com.redblackit.web");
            file = new File(parent, fileName);
            if (!file.exists()) {
                throw new IllegalArgumentException("cannot find fileName " + fileName + "in " + userDir + " or " + parent);
            }
        }
        
        return file.getAbsolutePath();
    }
}
