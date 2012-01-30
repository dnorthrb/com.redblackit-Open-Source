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


import com.redblackit.version.CompositeVersionInfo;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

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
        createExpectedAndActualCVI0();
		adminController = new AdminController(getActualCompositeVersionInfo());
	}

	/**
	 * Test about level 0 only
	 */
	@Test
	public void testAbout0() {
        doTestAbout();
	}

    /**
     * Test about level 1 only
     */
    @Test
    public void testAbout1() {
        setupActualCVI1();
        setupExpectedCVI1();

        doTestAbout();
    }

    /**
     * Test about level 2
     */
    @Test
    public void testAbout2() {
        setupActualCVI1_2All();
        setupExpectedCVI1_2All();

        doTestAbout();
    }

    /**
     * Helper method to run tests using about
     */
    private void doTestAbout() {
        ExtendedModelMap model = new ExtendedModelMap();

        adminController.about(model);

        Object versionInfoObj = model.get("versionInfo");
        Assert.assertNotNull("versionInfo model attribute", versionInfoObj);
        Assert.assertTrue("versionInfo is of type CompositeVersionInfo:"
                + versionInfoObj.getClass(),
                versionInfoObj instanceof CompositeVersionInfo);

        CompositeVersionInfo returnedVersionInfo = (CompositeVersionInfo) versionInfoObj;
        Assert.assertEquals("returned versionInfo", getExpectedCompositeVersionInfo(), returnedVersionInfo);
    }

}
