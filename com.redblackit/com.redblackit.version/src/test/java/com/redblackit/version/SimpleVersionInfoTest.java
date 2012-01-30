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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import junit.framework.Assert;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SimpleVersionInfoTest {

	/**
	 * VersionInfo under test
	 */
	private SimpleVersionInfo versionInfoUnderTest;

	/**
	 * Properties for construction
	 */
	private Properties expectedVersionPropertiesConstructor;

	/**
	 * Properties for setting
	 */
	private Properties expectedVersionPropertiesSetter;

	/**
	 * Create test data
	 * 
	 * @return test data as list
	 */
	@Parameters
	public static List<Object[]> testData() {

		Properties vprops1 = new Properties();
		vprops1.put("configurationVersion", "0.0.1");

		Properties vprops2 = new Properties();
		vprops2.put("configurationVersion", "0.1.Beta");
		vprops2.put("versionDate", "20110107");

		Properties vprops3 = new Properties();
		vprops3.put("extraVersionInfo", "M1");

		Properties vprops4 = new Properties();
		vprops3.put("author", "Dominic North");

		Object[][] data = { { vprops1, vprops3 }, { vprops2, vprops4 },
				{ vprops3, null }, { null, vprops1 }, { null, null } };

		List<Object[]> dataList = Arrays.asList(data);

		return dataList;
	}

	/**
	 * Constructor taking instance to test and expected results.
	 * 
	 * @param expectedVersionPropertiesConstructor
	 * @param expectedVersionPropertiesSetter
	 */
	public SimpleVersionInfoTest(
			Properties expectedVersionPropertiesConstructor,
			Properties expectedVersionPropertiesSetter) {

		this.versionInfoUnderTest = new SimpleVersionInfo(
				expectedVersionPropertiesConstructor);

		this.expectedVersionPropertiesConstructor = expectedVersionPropertiesConstructor;
		this.expectedVersionPropertiesSetter = expectedVersionPropertiesSetter;

	}

	/**
	 * Test get version properties
	 */
	@Test
	public void getVersionProperties() {
		Assert.assertEquals(versionInfoUnderTest.getClass()
				+ ":version properties", expectedVersionPropertiesConstructor,
				versionInfoUnderTest.getVersionProperties());
	}

	/**
	 * Test set version map
	 */
	@Test
	public void setVersionMap() {
		Map<String, String> newVersionMap = null;
		if (expectedVersionPropertiesSetter != null) {
			newVersionMap = new TreeMap<String, String>();
			addPropertiesToExpectedVersionMap(newVersionMap,
					expectedVersionPropertiesSetter);
		}
		versionInfoUnderTest.setVersionMap(newVersionMap);
		Assert.assertEquals(versionInfoUnderTest.getClass()
				+ ":version properties", expectedVersionPropertiesSetter,
				versionInfoUnderTest.getVersionProperties());
	}

	/**
	 * Test get version map
	 */
	@Test
	public void getVersionMap() {
		Map<String, String> expectedVersionMap = null;
		if (expectedVersionPropertiesConstructor != null) {
			expectedVersionMap = new TreeMap<String, String>();
			addPropertiesToExpectedVersionMap(expectedVersionMap,
					expectedVersionPropertiesConstructor);
		}
		Assert.assertEquals(versionInfoUnderTest.getClass() + ":version map",
				expectedVersionMap, versionInfoUnderTest.getVersionMap());
	}

	/**
	 * Test set version properties
	 */
	@Test
	public void setVersionProperties() {
		Map<String, String> expectedVersionMap = null;
		if (expectedVersionPropertiesSetter != null) {
			expectedVersionMap = new TreeMap<String, String>();
			addPropertiesToExpectedVersionMap(expectedVersionMap,
					expectedVersionPropertiesSetter);
		}
		versionInfoUnderTest
				.setVersionProperties(expectedVersionPropertiesSetter);
		Assert.assertEquals(versionInfoUnderTest.getClass() + ":version maps",
				expectedVersionMap, versionInfoUnderTest.getVersionMap());
	}

	/**
	 * Test get version string
	 */
	@Test
	public void getVersionString() {
		Map<String, String> expectedVersionMap = null;
		if (expectedVersionPropertiesConstructor != null) {
			expectedVersionMap = new TreeMap<String, String>();
			addPropertiesToExpectedVersionMap(expectedVersionMap,
					expectedVersionPropertiesConstructor);
		}
		final String versionString = versionInfoUnderTest.getVersionString();
		Assert.assertTrue(versionInfoUnderTest.getClass() + ":version string="
				+ versionString + ":expectedVersionMap=" + expectedVersionMap,
				versionString.endsWith("versionMap=" + expectedVersionMap));
	}

	/**
	 * Test equals
	 */
	@Test
	public void testEquals() {
		Assert.assertEquals("indentical objects", versionInfoUnderTest,
				versionInfoUnderTest);

		Properties otherVersionProps = null;
		SimpleVersionInfo otherVersionInfo = null;

		Assert.assertFalse("ne null",
				versionInfoUnderTest.equals(otherVersionInfo));

		otherVersionInfo = new SimpleVersionInfo(otherVersionProps);
		Assert.assertEquals("otherVersionInfo with null props",
				(expectedVersionPropertiesConstructor == null),
				versionInfoUnderTest.equals(otherVersionInfo));

		otherVersionProps = new Properties();
		otherVersionInfo.setVersionProperties(otherVersionProps);
		Assert.assertEquals(
				"otherVersionInfo with empty props",
				(expectedVersionPropertiesConstructor != null && expectedVersionPropertiesConstructor
						.isEmpty()), versionInfoUnderTest
						.equals(otherVersionInfo));

		if (expectedVersionPropertiesConstructor != null) {
			otherVersionProps.putAll(versionInfoUnderTest.getVersionMap());
			Assert.assertEquals("eq versionInfo with eq props",
					versionInfoUnderTest, otherVersionInfo);
		}

		otherVersionProps.put("extraKey", "extraValue");
		Assert.assertFalse("ne versionInfo with ne props",
				versionInfoUnderTest.equals(otherVersionInfo));

	}

	/**
	 * Test Jackson Json marshalling/unmarshalling for annotations in class What
	 * goes in should come out!
	 */
	@Test
	public void testJsonMarshallUnmarshall() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String marshalledVersion = mapper
				.writeValueAsString(versionInfoUnderTest);
		VersionInfo unmarshalledVersionInfo = mapper.readValue(
				marshalledVersion, SimpleVersionInfo.class);
		Assert.assertEquals("original and unmarshalled versionInfo",
				versionInfoUnderTest, unmarshalledVersionInfo);
	}

	/**
	 * Add supplied properties to supplied map
	 * 
	 * @param expectedVersionMap
	 * @param versionProperties
	 */
	private void addPropertiesToExpectedVersionMap(
			Map<String, String> expectedVersionMap, Properties versionProperties) {
		for (Object pname : versionProperties.keySet()) {
			expectedVersionMap.put(pname.toString(),
					versionProperties.getProperty(pname.toString()));
		}
	}

}
