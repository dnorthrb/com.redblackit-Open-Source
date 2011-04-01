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

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ExtendedModelMap;

import com.redblackit.version.VersionInfo;
import com.redblackit.web.controller.AdminController;
import com.redblackit.web.controller.AdminRestController;

/**
 * @author djnorth
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AdminAppTest {

	@Autowired
	private AdminController adminController;

	@Autowired
	private AdminRestController adminRestController;

	@Autowired
	@Qualifier("versionProperties")
	private Properties versionProps;

	private Map<String, String> expectedVersionMap = null;

	/**
	 * Set up the versionMap
	 */
	@Before
	public void setExpectedVersionMap() {
		if (expectedVersionMap == null) {
			expectedVersionMap = new TreeMap<String, String>();
			for (Object pname : versionProps.keySet()) {
				expectedVersionMap.put(pname.toString(),
						versionProps.getProperty(pname.toString()));
			}
		}
	}

	/**
	 * Test human about service
	 */
	@Test
	public void testAbout() {
		ExtendedModelMap model = new ExtendedModelMap();

		adminController.about(model);
		Object versionInfoObj = model.get("versionInfo");

		Assert.assertNotNull("versionInfo model attribute", versionInfoObj);
		Assert.assertTrue("versionInfo is of type VersionInfo:"
				+ versionInfoObj.getClass(),
				versionInfoObj instanceof VersionInfo);

		VersionInfo versionInfo = (VersionInfo) versionInfoObj;
		Assert.assertEquals(versionProps, versionInfo.getVersionMap());
	}

	/**
	 * Test RESTful about (getVersion) service
	 */
	@Test
	public void testGetVersion() {
		String versionString = adminRestController.getVersionSummary();
		Assert.assertTrue("versionString=" + versionString + ":versionProps="
				+ versionProps,
				versionString.endsWith("versionMap=" + expectedVersionMap));
	}
}
