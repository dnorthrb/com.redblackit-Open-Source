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
 */
public class AdminRestControllerTest extends VersionControllerTestBase {

    private AdminRestController adminRestController;


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        createExpectedAndActualCVI0();
        adminRestController = new AdminRestController(getActualCompositeVersionInfo());
    }

    /**
     * Test about
     */
    @Test
    public void testGetVersionSummary() {
        String versionString = adminRestController.getVersionSummary();
    }

    /**
     * Test get version for initial level0 object
     */
    @Test
    public void testGetVersion0() {
        Assert.assertEquals("versionInfo", getExpectedCompositeVersionInfo(), adminRestController.getVersion());
    }

    /**
     * Test get version for level1 objects
     */
    @Test
    public void testGetVersion1() {
        setupActualCVI1();
        setupExpectedCVI1();
        Assert.assertEquals("versionInfo", getExpectedCompositeVersionInfo(), adminRestController.getVersion());
    }

    /**
     * Test get version for level2 objects
     */
    @Test
    public void testGetVersion2() {
        setupActualCVI1_2All();
        setupExpectedCVI1_2All();

        Assert.assertEquals("versionInfo", getExpectedCompositeVersionInfo(), adminRestController.getVersion());
    }

    /**
     * Test about head
     */
    @Test
    public void testGetVersionHead() {
        adminRestController.getVersionHead();
    }

}
