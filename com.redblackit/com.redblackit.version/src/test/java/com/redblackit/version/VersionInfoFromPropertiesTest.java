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

package com.redblackit.version;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.xml.bind.JAXB;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class VersionInfoFromPropertiesTest {

	/**
	 * VersionInfo under test
	 */
	private VersionInfo versionInfoUnderTest;

	/**
	 * Expected versionMap
	 */
	private Map<String, String> expectedVersionMap;
	
	/**
	 * File for marshalling
	 */
	private File versionPropsXmlFile;

	/**
	 * Create test data
	 * 
	 * @return
	 */
	@Parameters
	public static List<Object[]> testData() {

		Properties vprops1 = new Properties();
		vprops1.put("configurationVersion", "0.0.1");

		Properties vprops2 = new Properties();
		vprops2.put("configurationVersion", "0.1.Beta");
		vprops2.put("versionDate", "20110107");

		Object[][] data = { { vprops1, "vfp1.xml" }, { vprops2, "vfp2.xml" } };

		List<Object[]> dataList = Arrays.asList(data);

		return dataList;
	}

	/**
	 * Constructor taking instance to test and expected results.
	 * 
	 * @param manifestClass
	 * @param expectedTitle
	 * @param expectedVersion
	 * @param expectedVendor
	 * @param expectedVersionProperties
	 */
	public VersionInfoFromPropertiesTest(Properties expectedVersionProperties, String jaxbFilename ) {

		this.versionInfoUnderTest = new VersionInfoFromProperties(
				expectedVersionProperties);

		this.expectedVersionMap = new TreeMap<String, String>();
		for (Object pname : expectedVersionProperties.keySet()) {
			this.expectedVersionMap.put(pname.toString(),
					expectedVersionProperties.getProperty(pname.toString()));
		}
		
		this.versionPropsXmlFile = new File("target/versionInfoFromProperties-" + jaxbFilename);

	}

	/**
	 * Test get version properties
	 */
	@Test
	public void getVersionProperties() {
		Assert.assertEquals(versionInfoUnderTest.getClass() + ":version maps",
				expectedVersionMap, versionInfoUnderTest.getVersionMap());
	}

	/**
	 * Test get version string
	 */
	@Test
	public void getVersionString() {
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
		VersionInfoFromProperties otherVersionInfo = new VersionInfoFromProperties(
				otherVersionProps);

		Assert.assertFalse("ne null",
				versionInfoUnderTest.equals(otherVersionInfo));

		otherVersionInfo = new VersionInfoFromProperties(otherVersionProps);
		Assert.assertFalse("ne versionInfo with null props",
				versionInfoUnderTest.equals(otherVersionInfo));

		otherVersionProps = new Properties();
		otherVersionInfo.setVersionProperties(otherVersionProps);
		Assert.assertFalse("ne versionInfo with empty props",
				versionInfoUnderTest.equals(otherVersionInfo));

		otherVersionProps.putAll(versionInfoUnderTest.getVersionMap());
		Assert.assertEquals("eq versionInfo with eq props",
				versionInfoUnderTest, otherVersionInfo);

		otherVersionProps.put("extraKey", "extraValue");
		Assert.assertFalse("ne versionInfo with ne props",
				versionInfoUnderTest.equals(otherVersionInfo));

	}
	
	/**
	 * Test JAXB XML marshalling/unmarshalling for annotations in class
	 * What goes in should come out!
	 */
	@Test
	public void testJAXBMarshallUnmarshall()
	{
		JAXB.marshal(versionInfoUnderTest, versionPropsXmlFile);
		VersionInfo unmarshalledVersionInfo = JAXB.unmarshal(versionPropsXmlFile, VersionInfoFromProperties.class);
		Assert.assertEquals("original and unmarshalled versionInfo", versionInfoUnderTest, unmarshalledVersionInfo);
	}

}
