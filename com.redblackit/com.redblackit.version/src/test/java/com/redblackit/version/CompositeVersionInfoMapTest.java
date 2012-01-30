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

package com.redblackit.version;

import java.util.*;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for CompositeVersionInfoMapTest.
 *
 * @author djnorth
 */
@RunWith(Parameterized.class)
public class CompositeVersionInfoMapTest {

    /**
     * Create test data
     *
     * @return test data as list
     */
    @Parameters
    public static List<Object[]> testData() {

        final Map<String, String> vsa = new HashMap<String, String>();
        vsa.put("vsa0", "valueA");
        vsa.put("vsa1", "valueB");

        final Map<String, String> vsb = new HashMap<String, String>();
        vsb.put("vsb0", "valueC");
        vsb.put("vsb1", "valueD");
        vsb.put("vsb2", "valueE");

        final Map<String, String> vsc = new HashMap<String, String>();
        vsc.put("vsc0", "valueF");
        vsc.put("vsc1", "valueG");

        final Map<String, String> vsd = new HashMap<String, String>();
        vsd.put("vsd0", "valueH");

        final Map<String, String> vse = new HashMap<String, String>();
        vse.put("vse0", "valueI");
        vse.put("vse1", "valueJ");
        vse.put("vse2", "valueK");

        CVIParameters cvip0 = new CVIParameters((char) 0, vsa, null);
        CVIParameters cvip1 = new CVIParameters('.', vsa, null);
        CVIParameters cvip2 = new CVIParameters('-', vsb, null);
        CVIParameters cvip3 = new CVIParameters('#', vsb, null);
        CVIParameters cvip4 = new CVIParameters('_', vsc, null);
        CVIParameters cvip5 = new CVIParameters('&', vsb, null);
        CVIParameters cvip6 = new CVIParameters((char) 0, vsc, null);
        CVIParameters cvip7 = new CVIParameters('#', vsd, null);

        Map<String, CVIParameters> cvpMap0 = new HashMap<String, CVIParameters>();
        cvpMap0.put("vip0.0", cvip1);
        cvpMap0.put("vip0.1", cvip2);

        Map<String, CVIParameters> cvpMap1 = new HashMap<String, CVIParameters>();
        cvpMap1.put("vip1.0", cvip3);
        cvpMap1.put("vip1.1", cvip4);

        CVIParameters cvip8 = new CVIParameters('#', null, cvpMap0);
        CVIParameters cvip9 = new CVIParameters('-', vse, cvpMap1);

        Map<String, CVIParameters> cvpMap2 = new HashMap<String, CVIParameters>();
        cvpMap2.put("vip2.0", cvip8);
        cvpMap2.put("vip2.1", cvip9);

        CVIParameters cvip10 = new CVIParameters('^', vsd, cvpMap2);
        CVIParameters cvipEmpty = new CVIParameters('.', null, null);

        Object[][] data = {
                {cvip0, vsb, null}, {cvip1, vsb, null},
                {cvip2, vsc, null}, {cvip3, vsc, null},
                {cvip4, vsa, null}, {cvip5, vsa, null},
                {cvip6, vsa, null}, {cvip7, vsa, null},
                {cvip8, vsc, null}, {cvip9, vsd, null},
                {cvip0, vsa, cvpMap0}, {cvip1, vsb, cvpMap1},
                {cvip2, vsc, cvpMap2}, {cvip8, vsa, cvpMap0},
                {cvip9, vsa, cvpMap0}, {cvip10, vsa, cvpMap0},
                {cvipEmpty, vsa, cvpMap1}
        };

        List<Object[]> dataList = Arrays.asList(data);

        return dataList;
    }

    /**
     * Logger
     */
    private Logger logger = Logger.getLogger("VersionInfo");

    /**
     * CompositeVersionInfoMap under test
     */
    private CompositeVersionInfoMap compositeVersionInfoMapUnderTest;

    /**
     * Initial CompositeVersionInfoMap parameters
     */
    private CVIParameters initialCviParameters;

    /**
     * Additional versionStringMap
     */
    private Map<String, String> additionalVersionStringMap;

    /**
     * Additional versionInfo map
     */
    private Map<String, CVIParameters> additionalCVIParameterMap;

    /**
     * COnstructor taking test parameters
     *
     * @param initialCviParameters
     * @param additionalVersionStringMap
     * @param additionalCVIParameterMap
     */
    public CompositeVersionInfoMapTest(CVIParameters initialCviParameters,
                                       Map<String, String> additionalVersionStringMap,
                                       Map<String, CVIParameters> additionalCVIParameterMap) {
        super();
        this.initialCviParameters = initialCviParameters;
        this.additionalVersionStringMap = additionalVersionStringMap;
        this.additionalCVIParameterMap = additionalCVIParameterMap;
    }

    /**
     * Create CompositeVersionInfoMap under test
     */
    @Before
    public void setupCompositeVersionInfoMapUnderTest() {
        this.compositeVersionInfoMapUnderTest = initialCviParameters
                .createCompositeVersionInfoMap();
    }

    /**
     * Ensure the versionMap contains entries corresponding to every entry in
     * every component versionMap
     * <p/>
     * Test method for
     * {@link com.redblackit.version.CompositeVersionInfoMap#getVersionMap()}.
     */
    @Test
    public void testGetVersionMap() {
        Map<String, String> actualVersionMap = compositeVersionInfoMapUnderTest
                .getVersionMap();

        Map<String, String> expectedVersionMap = createExpectedVersionMap(initialCviParameters);
        Assert.assertEquals(assertMsg("version maps differ in value:"),
                            expectedVersionMap, actualVersionMap);

    }

    /**
     * Test method for
     * {@link com.redblackit.version.CompositeVersionInfoMap#getVersionString()}
     * .
     */
    @Test
    public void testGetVersionString() {
        final String versionMapToString = compositeVersionInfoMapUnderTest
                .getVersionMap().toString();
        final String actualVersionString = compositeVersionInfoMapUnderTest
                .getVersionString();

        Assert.assertTrue(assertMsg("versionString '" + actualVersionString
                                    + "' ends with versionMap.toString() '" + versionMapToString
                                    + "'"), actualVersionString.endsWith(versionMapToString));
    }

    /**
     * Test method for
     * {@link com.redblackit.version.CompositeVersionInfoMap#getVersionForKey}.
     */
    @Test
    public void testGetVersionForKey() {
        if (initialCviParameters.getVersionStringMap() != null) {
            for (String key : initialCviParameters.getVersionStringMap()
                                                  .keySet()) {
                Assert.assertEquals(assertMsg("VersionString[" + key + "]"),
                                    initialCviParameters.getVersionStringMap().get(key),
                                    compositeVersionInfoMapUnderTest.getVersionForKey(key));
            }
        }

        Assert.assertNull(assertMsg("bad key returns null"),
                          compositeVersionInfoMapUnderTest
                                  .getVersionForKey("non-existent"));

        Assert.assertNull(assertMsg("null  key returns null"),
                          compositeVersionInfoMapUnderTest.getVersionForKey(null));
    }

    /**
     * Check that all the keys give the appropriate Version, and that a
     * non-existent key gives null.
     * <p/>
     * Test method for
     * {@link com.redblackit.version.CompositeVersionInfoMap#getVersionInfoForKeys(java.lang.String[])}
     * .
     */
    @Test
    public void testGetVersionForKeys() {
        doTestGetVersionForKeys(initialCviParameters, null);
    }

    /**
     * Check that object is set at the appropriate Version.
     * <p/>
     * Test method for
     * {@link com.redblackit.version.CompositeVersionInfoMap#setVersionForKeys(java.lang.String, java.lang.String[])}
     * .
     */
    @Test
    public void testSetVersionForKeys() {
        if (additionalVersionStringMap != null) {
            doTestSetVersionForKeys(initialCviParameters.getCviParmMap(), null, true);
            doTestSetVersionForKeys(initialCviParameters.getCviParmMap(), null, false);
        }
    }

    /**
     * Check that all the keys give the appropriate VersionInfo, and that a
     * non-existent key gives null.
     * <p/>
     * Test method for
     * {@link com.redblackit.version.CompositeVersionInfoMap#getVersionInfoForKey(java.lang.String)}
     * .
     */
    @Test
    public void testGetVersionInfoForKey() {

        if (initialCviParameters.getCviParmMap() != null) {
            for (String key : initialCviParameters.getCviParmMap().keySet()) {
                Assert.assertEquals(assertMsg("VersionInfo[" + key + "]"),
                                    initialCviParameters.getCviParmMap().get(key)
                                                        .createCompositeVersionInfoMap(),
                                    compositeVersionInfoMapUnderTest
                                            .getVersionInfoForKey(key));
            }
        }

        Assert.assertNull(assertMsg("bad key returns null"),
                          compositeVersionInfoMapUnderTest
                                  .getVersionInfoForKey("non-existent"));
        Assert.assertNull(assertMsg("null key returns null"),
                          compositeVersionInfoMapUnderTest.getVersionInfoForKey("null"));
    }

    /**
     * Check that all the keys give the appropriate VersionInfo, and that a
     * non-existent key gives null.
     * <p/>
     * Test method for
     * {@link com.redblackit.version.CompositeVersionInfoMap#getVersionInfoForKeys(java.lang.String[])}
     * .
     */
    @Test
    public void testGetVersionInfoForKeys() {
        Map<String, CVIParameters> cviParmMap = initialCviParameters
                .getCviParmMap();
        doTestGetVersionInfoForKeys(cviParmMap, null);
    }

    /**
     * Check that object is set at the appropriate Version.
     * <p/>
     * Test method for
     * {@link com.redblackit.version.CompositeVersionInfoMap#setVersionInfoForKeys(CompositeVersionInfo,
     * java.lang.String[])}
     * .
     */
    @Test
    public void testSetVersionInfoForKeys() {
        if (additionalCVIParameterMap != null) {
            doTestSetVersionInfoForKeys(initialCviParameters.getCviParmMap(),
                                        null, true);
            doTestSetVersionInfoForKeys(initialCviParameters.getCviParmMap(),
                                        null, false);
        }
    }

    /**
     * Check we give what we were given.
     * <p/>
     * Test method for
     * {@link com.redblackit.version.CompositeVersionInfoMap#getVersionInfoMap()}
     * .
     */
    @Test
    public void testGetVersionInfoMap() {
        Map<String, CompositeVersionInfo> expectedVersionInfoMap = CVIParameters
                .createCVIMap(initialCviParameters.getCviParmMap());
        Assert.assertEquals(assertMsg("versionInfoMap"),
                            expectedVersionInfoMap,
                            compositeVersionInfoMapUnderTest.getVersionInfoMap());
    }

    /**
     * Ensure set replaces map.
     * <p/>
     * Test method for
     * {@link com.redblackit.version.CompositeVersionInfoMap#setVersionInfoMap(java.util.Map)}
     * .
     */
    @Test
    public void testSetVersionInfoMap() {
        Map<String, CompositeVersionInfo> additionalVersionInfoMap = CVIParameters
                .createCVIMap(additionalCVIParameterMap);
        compositeVersionInfoMapUnderTest
                .setVersionInfoMap(additionalVersionInfoMap);
        Assert.assertEquals(assertMsg("versionInfoMap"),
                            additionalVersionInfoMap,
                            compositeVersionInfoMapUnderTest.getVersionInfoMap());
    }

    /**
     * Test method for
     * {@link com.redblackit.version.CompositeVersionInfoMap#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject() {
        Assert.assertTrue(assertMsg("eq same object"),
                          compositeVersionInfoMapUnderTest
                                  .equals(compositeVersionInfoMapUnderTest));
        Assert.assertFalse(assertMsg("ne null"),
                           compositeVersionInfoMapUnderTest.equals(null));

        CompositeVersionInfoMap otherMap = initialCviParameters
                .createCompositeVersionInfoMap();
        Assert.assertTrue(assertMsg("eq same map and separator:" + otherMap),
                          compositeVersionInfoMapUnderTest.equals(otherMap));
        if (!(additionalVersionStringMap != null || additionalVersionStringMap
                .equals(initialCviParameters.getVersionStringMap()))) {
            otherMap.setVersionStringMap(additionalVersionStringMap);
            Assert.assertFalse(assertMsg("ne different map, same separator:"
                                         + otherMap),
                               compositeVersionInfoMapUnderTest.equals(otherMap));
            otherMap.setKeyConcatenationSeparator(initialCviParameters
                                                          .getSeparatorChar() == '-' ? '.' : '-');
            Assert.assertFalse(
                    assertMsg("ne different map, different separator:"
                              + otherMap),
                    compositeVersionInfoMapUnderTest.equals(otherMap));
            otherMap.setVersionStringMap(initialCviParameters
                                                 .getVersionStringMap());
        } else {
            otherMap.setKeyConcatenationSeparator(initialCviParameters
                                                          .getSeparatorChar() == '-' ? '.' : '-');
        }

        Assert.assertFalse(assertMsg("ne same map, different separator:"
                                     + otherMap), compositeVersionInfoMapUnderTest.equals(otherMap));
    }


    /**
     * Test getVersionComponentMap, ensuring that each entry corresponds to the individual version obtained by the
     * corresponding keys.
     */
    @Test
    public void testGetVersionComponentMap() {
        Map<List<String>, String> versionComponentMap = compositeVersionInfoMapUnderTest.getVersionComponentMap();

        for (List<String> key : versionComponentMap.keySet()) {
            Assert.assertEquals(assertMsg("string for keys:" + key + ":versionComponentMap=" + versionComponentMap),
                                compositeVersionInfoMapUnderTest.getVersionForKeys(key.toArray(new String[key.size()])),
                                versionComponentMap.get(key));
        }
    }


    /**
     * Test getMaximumComponentVersionDepth against maximum actually found
     *
     * @see com.redblackit.version.CompositeVersionInfoMap#getMaximumComponentVersionDepth()
     */
    public void testGetMaximumComponentVersionDepth() {
        Set<List<String>> versionComponentMapKeys = compositeVersionInfoMapUnderTest.getVersionComponentMap().keySet();
        int expectedMaxDepth = -1;
        for (List<String> key : versionComponentMapKeys) {
            if (key.size() - 1 > expectedMaxDepth) {
                expectedMaxDepth = key.size() - 1;
            }
        }

        Assert.assertEquals(assertMsg("max component depth:" + versionComponentMapKeys),
                            expectedMaxDepth,
                            compositeVersionInfoMapUnderTest.getMaximumComponentVersionDepth());
    }

    /**
     * Test Jackson Json marshalling/unmarshalling for annotations in class What
     * goes in should come out!
     */
    @Test
    public void testJsonMarshallUnmarshall() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String marshalledVersion = mapper
                .writeValueAsString(compositeVersionInfoMapUnderTest);
        logger.trace("marshalledVersion=" + marshalledVersion);

        CompositeVersionInfo unmarshalledVersionInfo = mapper.readValue(
                marshalledVersion, CompositeVersionInfoMap.class);
        Assert.assertEquals("original and unmarshalled versionInfo",
                            compositeVersionInfoMapUnderTest, unmarshalledVersionInfo);
    }

    /*
      * (non-Javadoc)
      *
      * @see java.lang.Object#toString()
      */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CompositeVersionInfoMapTest [compositeVersionInfoMapUnderTest=");
        builder.append(compositeVersionInfoMapUnderTest);
        builder.append(", initialCviParameters=");
        builder.append(initialCviParameters);
        builder.append(", additionalVersionStringMap=");
        builder.append(additionalVersionStringMap);
        builder.append(", additionalCVIParameterMap=");
        builder.append(additionalCVIParameterMap);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Recursively test getVersionForKeys
     *
     * @param cviParms
     * @param prevkeys
     */
    private void doTestGetVersionForKeys(CVIParameters cviParms,
                                         String[] prevkeys) {
        if (cviParms != null) {
            String prevkeymsg = ":prevkeys=" + Arrays.toString(prevkeys);
            String[] keys = (prevkeys == null ? new String[1] : Arrays.copyOf(
                    prevkeys, prevkeys.length + 1));
            String[] badkeys = null;

            Map<String, String> versionStringMap = cviParms
                    .getVersionStringMap();
            if (versionStringMap != null) {
                for (String key : versionStringMap.keySet()) {
                    keys[keys.length - 1] = key;
                    Assert.assertEquals(
                            assertMsg("Version:cviParms=" + cviParms
                                      + prevkeymsg + ":keys="
                                      + Arrays.toString(keys)), versionStringMap
                            .get(key), compositeVersionInfoMapUnderTest
                            .getVersionForKeys(keys));

                    for (int i = 0; i < keys.length - 1; ++i) {
                        badkeys = Arrays.copyOf(keys, keys.length);
                        badkeys[i] = "non-existent";
                        Assert.assertNull(
                                assertMsg("bad keys returns null:cviParms="
                                          + cviParms + prevkeymsg + ":badkeys="
                                          + Arrays.toString(badkeys)),
                                compositeVersionInfoMapUnderTest
                                        .getVersionForKeys(badkeys));
                    }
                }
            }

            badkeys = Arrays.copyOf(keys, keys.length);
            badkeys[badkeys.length - 1] = "non-existent";
            Assert.assertNull(assertMsg("bad keys returns null:badkeys="
                                        + Arrays.toString(badkeys)),
                              compositeVersionInfoMapUnderTest.getVersionForKeys(badkeys));

            Map<String, CVIParameters> cviParmMap = cviParms.getCviParmMap();
            if (cviParmMap != null) {
                for (String cvpkey : cviParmMap.keySet()) {
                    keys[keys.length - 1] = cvpkey;
                    doTestGetVersionForKeys(cviParmMap.get(cvpkey), keys);
                }
            }
        }
    }

    /**
     * Recursively test setVersionForKeys.
     *
     * @param cviParmMap
     * @param prevkeys
     * @param copyCVIUT  true if we should use a copy of
     *                   compositeVersionInfoMapUnderTest, false to accumulate changes
     */
    private void doTestSetVersionForKeys(Map<String, CVIParameters> cviParmMap,
                                         String[] prevkeys, boolean copyCVIUT) {

        if (cviParmMap != null) {
            String prevkeymsg = ":prevkeys=" + Arrays.toString(prevkeys)
                                + ":copyCVIUT=" + copyCVIUT;

            String[] keys;
            if (prevkeys == null) {
                keys = new String[1];
            } else {
                keys = Arrays.copyOf(prevkeys, prevkeys.length + 1);
            }

            CompositeVersionInfoMap cmpVIP = compositeVersionInfoMapUnderTest;
            for (String newKey : additionalVersionStringMap.keySet()) {

                if (copyCVIUT) {
                    cmpVIP = initialCviParameters
                            .createCompositeVersionInfoMap();
                }
                CompositeVersionInfo parentCmpVIP;
                if (prevkeys == null) {
                    parentCmpVIP = cmpVIP;
                } else {
                    parentCmpVIP = cmpVIP.getVersionInfoForKeys(prevkeys);
                }

                String newVersion = additionalVersionStringMap.get(newKey);
                Map<String, String> expectedVSMap = null;
                if (parentCmpVIP != null) {
                    expectedVSMap = parentCmpVIP.getVersionStringMap();
                }

                if (expectedVSMap == null) {
                    expectedVSMap = new TreeMap<String, String>();
                }

                expectedVSMap.put(newKey, newVersion);
                keys[keys.length - 1] = newKey;

                cmpVIP.setVersionForKeys(newVersion, keys);

                if (prevkeys != null) {
                    parentCmpVIP = cmpVIP.getVersionInfoForKeys(prevkeys);
                    Assert.assertNotNull(
                            assertMsg("parent for new version string should now exist"
                                      + prevkeymsg
                                      + ":keys="
                                      + Arrays.toString(keys)
                                      + (copyCVIUT ? ":cmpVIP=" + cmpVIP : "")),
                            parentCmpVIP);
                }

                Map<String, String> actualVSMap = parentCmpVIP
                        .getVersionStringMap();
                Assert.assertEquals(assertMsg("version string map" + prevkeymsg
                                              + ":keys=" + Arrays.toString(keys)
                                              + (copyCVIUT ? ":cmpVIP=" + cmpVIP : "")),
                                    expectedVSMap, actualVSMap);

                for (String cvipKey : cviParmMap.keySet()) {
                    keys[keys.length - 1] = cvipKey;
                    doTestSetVersionForKeys(cviParmMap.get(cvipKey)
                                                      .getCviParmMap(), prevkeys, copyCVIUT);
                }

            }
        }

    }

    /**
     * Recursively test getVersionInfoForKeys
     *
     * @param cviParmMap
     * @param prevkeys
     */
    private void doTestGetVersionInfoForKeys(
            Map<String, CVIParameters> cviParmMap, String[] prevkeys) {
        if (cviParmMap != null) {
            String[] keys = (prevkeys == null ? new String[1] : Arrays.copyOf(
                    prevkeys, prevkeys.length + 1));
            String[] badkeys = null;
            for (String key : cviParmMap.keySet()) {
                keys[keys.length - 1] = key;
                CVIParameters cviParms = cviParmMap.get(key);
                Assert.assertEquals(assertMsg("VersionInfo:cviParms="
                                              + cviParms + ":keys=" + Arrays.toString(keys)),
                                    cviParms.createCompositeVersionInfoMap(),
                                    compositeVersionInfoMapUnderTest
                                            .getVersionInfoForKeys(keys));

                doTestGetVersionInfoForKeys(cviParms.getCviParmMap(), keys);
                for (int i = 0; i < keys.length - 1; ++i) {
                    badkeys = Arrays.copyOf(keys, keys.length);
                    badkeys[i] = "non-existent";
                    Assert.assertNull(
                            assertMsg("bad keys returns null:badkeys="
                                      + Arrays.toString(badkeys)),
                            compositeVersionInfoMapUnderTest
                                    .getVersionInfoForKeys(badkeys));
                }
            }

            badkeys = Arrays.copyOf(keys, keys.length);
            keys[keys.length - 1] = "non-existent";
            Assert.assertNull(
                    assertMsg("bad keys returns null:keys="
                              + Arrays.toString(keys)),
                    compositeVersionInfoMapUnderTest
                            .getVersionInfoForKeys(keys));

        }
    }

    /**
     * Recursively test setVersionInfoForKeys.
     *
     * @param cviParmMap
     * @param prevkeys
     * @param copyCVIUT  true if we should use a copy of
     *                   compositeVersionInfoMapUnderTest, false to accumulate changes
     */
    private void doTestSetVersionInfoForKeys(
            Map<String, CVIParameters> cviParmMap, String[] prevkeys,
            boolean copyCVIUT) {

        if (cviParmMap != null) {
            String prevkeymsg = ":prevkeys=" + Arrays.toString(prevkeys)
                                + ":copyCVIUT=" + copyCVIUT;

            CompositeVersionInfoMap cmpVIP = compositeVersionInfoMapUnderTest;
            String[] keys;
            if (prevkeys == null) {
                keys = new String[1];
            } else {
                keys = Arrays.copyOf(prevkeys, prevkeys.length + 1);
            }

            for (String newKey : additionalCVIParameterMap.keySet()) {

                if (copyCVIUT) {
                    cmpVIP = initialCviParameters
                            .createCompositeVersionInfoMap();
                }

                CompositeVersionInfo parentCmpVIP;
                if (prevkeys == null) {
                    parentCmpVIP = cmpVIP;
                } else {
                    parentCmpVIP = cmpVIP.getVersionInfoForKeys(prevkeys);
                }

                CompositeVersionInfo newCompositeVersionInfo = additionalCVIParameterMap
                        .get(newKey).createCompositeVersionInfoMap();

                Map<String, CompositeVersionInfo> expectedCVIMap = null;
                if (parentCmpVIP != null) {
                    expectedCVIMap = parentCmpVIP.getVersionInfoMap();
                }

                if (expectedCVIMap == null) {
                    expectedCVIMap = new TreeMap<String, CompositeVersionInfo>();
                }

                expectedCVIMap.put(newKey, newCompositeVersionInfo);
                keys[keys.length - 1] = newKey;

                cmpVIP.setVersionInfoForKeys(newCompositeVersionInfo, keys);

                if (prevkeys != null) {
                    parentCmpVIP = cmpVIP.getVersionInfoForKeys(prevkeys);
                    Assert.assertNotNull(
                            assertMsg("parent for new version map should now exist"
                                      + prevkeymsg
                                      + ":keys="
                                      + Arrays.toString(keys)
                                      + (copyCVIUT ? ":cmpVIP=" + cmpVIP : "")),
                            parentCmpVIP);
                }

                Map<String, CompositeVersionInfo> actualCVIMap = parentCmpVIP
                        .getVersionInfoMap();
                Assert.assertEquals(assertMsg("version map" + prevkeymsg
                                              + ":keys=" + Arrays.toString(keys)
                                              + (copyCVIUT ? ":cmpVIP=" + cmpVIP : "")),
                                    expectedCVIMap, actualCVIMap);

            }

            for (String cvipKey : cviParmMap.keySet()) {
                keys[keys.length - 1] = cvipKey;
                doTestSetVersionInfoForKeys(cviParmMap.get(cvipKey)
                                                      .getCviParmMap(), prevkeys, copyCVIUT);
            }

        }
    }

    /**
     * Helper creating expected version map from CVIParms, recursively
     *
     * @param cviParms
     * @return expectedVersionMap
     */
    private Map<String, String> createExpectedVersionMap(CVIParameters cviParms) {

        final char sepChar = (cviParms.getSeparatorChar() == 0 ? CompositeVersionInfo.DEFAULT_KEY_CONCATENATION_SEPARATOR
                                                               : cviParms.getSeparatorChar());
        Map<String, String> expectedVersionMap = new TreeMap<String, String>();
        if (cviParms.getVersionStringMap() != null) {
            expectedVersionMap.putAll(cviParms.getVersionStringMap());
        }

        if (cviParms.getCviParmMap() != null) {
            for (String key : cviParms.getCviParmMap().keySet()) {
                CVIParameters nextCviParms = cviParms.getCviParmMap().get(key);
                Map<String, String> nextVersionMap = createExpectedVersionMap(nextCviParms);
                for (String nextKey : nextVersionMap.keySet()) {
                    expectedVersionMap.put(key + sepChar + nextKey,
                                           nextVersionMap.get(nextKey));
                }
            }
        }

        return expectedVersionMap;
    }

    /**
     * Helper appending test parameters and context to msg
     *
     * @param msg
     */
    private String assertMsg(String msg) {
        return "" + this + "-\n**  " + msg;
    }

    /**
     * Class encapsulating parameters for creating a composite version info, and
     * providing method to create one object
     */
    private static class CVIParameters {
        /**
         * Separator character to use
         */
        private final char separatorChar;

        /**
         * Version string map to use
         */
        private final Map<String, String> versionStringMap;

        /**
         * Parameters for version info map objects
         */
        private final Map<String, CVIParameters> cviParmMap;

        /**
         * @param separatorChar
         * @param versionStringMap
         * @param cviParmMap
         */
        public CVIParameters(char separatorChar,
                             Map<String, String> versionStringMap,
                             Map<String, CVIParameters> cviParmMap) {
            super();
            this.separatorChar = separatorChar;
            this.versionStringMap = versionStringMap;
            this.cviParmMap = cviParmMap;
        }

        /**
         * @return the separatorChar
         */
        public char getSeparatorChar() {
            return separatorChar;
        }

        /**
         * @return the versionStringMap
         */
        public Map<String, String> getVersionStringMap() {
            return versionStringMap;
        }

        /**
         * @return the cviParmMap
         */
        public Map<String, CVIParameters> getCviParmMap() {
            return cviParmMap;
        }

        /**
         * Create composite version info from parameters
         *
         * @return compositeVersionInfo
         */
        public CompositeVersionInfoMap createCompositeVersionInfoMap() {
            CompositeVersionInfoMap compositeVersionInfoMap;
            if (separatorChar == 0) {
                compositeVersionInfoMap = new CompositeVersionInfoMap();
            } else {
                compositeVersionInfoMap = new CompositeVersionInfoMap(
                        separatorChar);
            }
            compositeVersionInfoMap.setVersionStringMap(versionStringMap);

            if (cviParmMap != null) {
                Map<String, CompositeVersionInfo> cviMap = createCVIMap(cviParmMap);
                compositeVersionInfoMap.setVersionInfoMap(cviMap);
            }

            return compositeVersionInfoMap;
        }

        /*
           * (non-Javadoc)
           *
           * @see java.lang.Object#toString()
           */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("CVIParameters [separatorChar=");
            builder.append(separatorChar);
            builder.append(", versionStringMap=");
            builder.append(versionStringMap);
            builder.append(", cviParmMap=");
            builder.append(cviParmMap);
            builder.append("]");
            return builder.toString();
        }

        /*
           * (non-Javadoc)
           *
           * @see java.lang.Object#hashCode()
           */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                     + ((cviParmMap == null) ? 0 : cviParmMap.hashCode());
            result = prime * result + separatorChar;
            result = prime
                     * result
                     + ((versionStringMap == null) ? 0 : versionStringMap
                    .hashCode());
            return result;
        }

        /*
           * (non-Javadoc)
           *
           * @see java.lang.Object#equals(java.lang.Object)
           */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CVIParameters other = (CVIParameters) obj;
            if (cviParmMap == null) {
                if (other.cviParmMap != null) {
                    return false;
                }
            } else if (!cviParmMap.equals(other.cviParmMap)) {
                return false;
            }
            if (separatorChar != other.separatorChar) {
                return false;
            }
            if (versionStringMap == null) {
                if (other.versionStringMap != null) {
                    return false;
                }
            } else if (!versionStringMap.equals(other.versionStringMap)) {
                return false;
            }
            return true;
        }

        /**
         * Helper to create compositeVersionInfo map from supplied CVIParameter
         * map.
         *
         * @param cviParmMap
         */
        public static Map<String, CompositeVersionInfo> createCVIMap(
                Map<String, CVIParameters> cviParmMap) {
            Map<String, CompositeVersionInfo> cviMap = null;
            if (cviParmMap != null) {
                cviMap = new TreeMap<String, CompositeVersionInfo>();
                for (String cviKey : cviParmMap.keySet()) {
                    cviMap.put(cviKey, cviParmMap.get(cviKey)
                                                 .createCompositeVersionInfoMap());
                }
            }
            return cviMap;
        }

    }

}
