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

import java.util.Map;
import java.util.TreeMap;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import com.redblackit.version.VersionInfo;

/**
 * @author djnorth
 * 
 */
public class AdminControllerTest extends VersionControllerTestBase {
	private AdminController adminController;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		adminController = new AdminController(getVersionInfo());

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
		Assert.assertEquals(getVersionMap(),
				returnedVersionInfo.getVersionMap());
		Assert.assertEquals(VERSION_STRING,
				returnedVersionInfo.getVersionString());
	}

}
