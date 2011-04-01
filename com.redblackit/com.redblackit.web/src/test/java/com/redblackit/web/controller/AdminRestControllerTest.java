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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * @author djnorth
 * 
 */
public class AdminRestControllerTest extends VersionControllerTestBase {
	
	private AdminRestController adminRestController;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		adminRestController = new AdminRestController(getVersionInfo());

	}

	/**
	 * Test about
	 */
	@Test
	public void testGetVersionSummary() {

		String versionString = adminRestController.getVersionSummary();
		Assert.assertEquals(VERSION_STRING, versionString);
	}

	/**
	 * Test about 
	 */
	@Test
	public void testGetVersion() {
		Assert.assertEquals(getVersionInfo(), adminRestController.getVersion());
	}

	/**
	 * Test about head
	 */
	@Test
	public void testGetVersionHead() {
		adminRestController.getVersionHead();
	}

}
