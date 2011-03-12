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

package com.redblackit.web.controller;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import com.redblackit.version.VersionInfo;
import com.redblackit.web.controller.AdminController;

/**
 * @author djnorth
 * 
 */
public class AdminControllerTest {
	private static final String VERSION_STRING = "Version String";

	private static final String CONFIGURATION_VERSION = "Configuration Version";

	private static final String IMPLEMENTATION_VERSION = "Implementation Version";

	private static final String IMPLEMENTATION_VENDOR = "Implementation Vendor";

	private static final String IMPLEMENTATION_TITLE = "Implementation Title";

	private Properties versionProperties;

	private VersionInfo versionInfo;

	private AdminController adminController;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		versionProperties = new Properties();
		versionProperties.put("implementationVersion", IMPLEMENTATION_VERSION);
		versionProperties.put("implementationTitle", IMPLEMENTATION_TITLE);
		versionProperties.put("implementationVendor", IMPLEMENTATION_VENDOR);
		versionProperties.put("configurationVersion", CONFIGURATION_VERSION);

		versionInfo = new VersionInfo() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.redblackit.version.VersionInfo#getImplementationVersion()
			 */
			@Override
			public Properties getVersionProperties() {
				return versionProperties;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.redblackit.version.VersionInfo#getVersionString()
			 */
			@Override
			public String getVersionString() {
				return VERSION_STRING;
			}

		};

		adminController = new AdminController(versionInfo);

	}

	/**
	 * Test about
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
		
		VersionInfo returnedVersionInfo = (VersionInfo) versionInfoObj;
		Assert.assertEquals(versionProperties,
				returnedVersionInfo.getVersionProperties());
		Assert.assertEquals(VERSION_STRING,
				returnedVersionInfo.getVersionString());
	}

}
