package com.redblackit.web.controller;

import com.redblackit.version.CompositeVersionInfo;
import com.redblackit.version.CompositeVersionInfoMap;

public class VersionControllerTestBase {

    protected static final String CONFIGURATION_VERSION0_KEY  = "cversion0";
    protected static final String CONFIGURATION_VERSION0      = "c0";
    protected static final String IMPLEMENTATION_VERSION0_KEY = "iversion0";
    protected static final String IMPLEMENTATION_VERSION0     = "i0.0";
    protected static final String IMPLEMENTATION_VENDOR0_KEY  = "ivendor0";
    protected static final String IMPLEMENTATION_VENDOR0      = "Vendor0";
    protected static final String IMPLEMENTATION_TITLE0_KEY   = "ititle0";
    protected static final String IMPLEMENTATION_TITLE0       = "Implementation Title0";
    protected static final String LEVEL0_INFO1_KEY            = "vinfo0";

    protected static final String CONFIGURATION_VERSION1_KEY  = "cversion1";
    protected static final String CONFIGURATION_VERSION1      = "c1";
    protected static final String IMPLEMENTATION_VERSION1_KEY = "iversion1";
    protected static final String IMPLEMENTATION_VERSION1     = "i1.0";
    protected static final String LEVEL1_INFO20_KEY           = "vinfo1.0";
    protected static final String LEVEL1_INFO21_KEY           = "vinfo1.1";

    protected static final String CONFIGURATION_VERSION20_KEY  = "cversion2.0";
    protected static final String CONFIGURATION_VERSION20      = "c2a";
    protected static final String IMPLEMENTATION_VERSION20_KEY = "iversion2.0";
    protected static final String IMPLEMENTATION_VERSION20     = "i2.1";
    protected static final String CONFIGURATION_VERSION21_KEY  = "cversion2.1";
    protected static final String CONFIGURATION_VERSION21      = "c2b";
    protected static final String IMPLEMENTATION_VERSION21_KEY = "iversion2.1";
    protected static final String IMPLEMENTATION_VERSION21     = "i3.1.0";

    private CompositeVersionInfo expectedCompositeVersionInfo;
    private CompositeVersionInfo actualCompositeVersionInfo;

    public VersionControllerTestBase() {
        super();
    }


    /**
     * Create expectedCompositeVersionInfo and actualCompositeVersionInfo at level 0
     */
    protected void createExpectedAndActualCVI0() {
        expectedCompositeVersionInfo = setupCVI0();
        actualCompositeVersionInfo = setupCVI0();
    }

    /**
     * Create expectedCompositeVersionInfo at level 1
     */
    protected void setupExpectedCVI1() {
        setupCVI1(getExpectedCompositeVersionInfo());
    }

    /**
     * Create actualCompositeVersionInfo at level 1
     */
    protected void setupActualCVI1() {
        setupCVI1(getActualCompositeVersionInfo());
    }

    /**
     * Create expectedCompositeVersionInfo at level 2.0
     */
    protected void setupExpectedCVI20() {
        setupCVI20(getExpectedCompositeVersionInfo());
    }

    /**
     * Create actualCompositeVersionInfo at level 2.0
     */
    protected void setupActualCVI20() {
        setupCVI20(getActualCompositeVersionInfo());
    }

    /**
     * Create expectedCompositeVersionInfo at level 2.1
     */
    protected void setupExpectedCVI21() {
        setupCVI21(getExpectedCompositeVersionInfo());
    }

    /**
     * Create actualCompositeVersionInfo at level 2.1
     */
    protected void setupActualCVI21() {
        setupCVI21(getActualCompositeVersionInfo());
    }


    /**
     * Set-up all actual
     */
    protected void setupActualCVI1_2All() {
        setupActualCVI1();
        setupActualCVI20();
        setupActualCVI21();
    }


    /**
     * Set-up all expected
     */
    protected void setupExpectedCVI1_2All() {
        setupExpectedCVI1();
        setupExpectedCVI20();
        setupExpectedCVI21();
    }

    /**
     * @return the root expectedCompositeVersionInfo object
     */
    protected CompositeVersionInfo getExpectedCompositeVersionInfo() {
        return expectedCompositeVersionInfo;
    }

    /**
     * @return the root actualCompositeVersionInfo object
     */
    protected CompositeVersionInfo getActualCompositeVersionInfo() {
        return actualCompositeVersionInfo;
    }

    /**
     * Create and fill in CompositeVersionInfoMap at level 0 i.e. string map only
     *
     * @return compositeVersionInfo
     */
    private CompositeVersionInfo setupCVI0() {
        CompositeVersionInfo compositeVersionInfo = new CompositeVersionInfoMap();

        compositeVersionInfo.setVersionForKeys(CONFIGURATION_VERSION0, CONFIGURATION_VERSION0_KEY);
        compositeVersionInfo.setVersionForKeys(IMPLEMENTATION_VERSION0, IMPLEMENTATION_VERSION0_KEY);
        compositeVersionInfo.setVersionForKeys(IMPLEMENTATION_VENDOR0, IMPLEMENTATION_VENDOR0_KEY);
        compositeVersionInfo.setVersionForKeys(IMPLEMENTATION_TITLE0, IMPLEMENTATION_TITLE0_KEY);

        return compositeVersionInfo;
    }

    /**
     * Create and fill in CompositeVersionInfoMap at level 1 i.e.
     * <ul>
     * <li>string map at level 1</li>
     * <li>level 1 composite version info in supplied level 0 version info map</li>
     * </ul>
     *
     * @param compositeVersionInfo
     */
    private void setupCVI1(CompositeVersionInfo compositeVersionInfo) {
        compositeVersionInfo.setVersionForKeys(CONFIGURATION_VERSION1, LEVEL0_INFO1_KEY, CONFIGURATION_VERSION1_KEY);
        compositeVersionInfo.setVersionForKeys(IMPLEMENTATION_VERSION1, LEVEL0_INFO1_KEY, IMPLEMENTATION_VERSION1_KEY);
    }


    /**
     * Create and fill in CompositeVersionInfoMap at level 2.0 i.e.
     * <ul>
     * <li>string map at level 2.0</li>
     * <li>level 2.0 composite version info in level 1 version info map for supplied level 0 CVI</li>
     * </ul>
     *
     * @param compositeVersionInfo
     */
    private void setupCVI20(CompositeVersionInfo compositeVersionInfo) {
        compositeVersionInfo.setVersionForKeys(CONFIGURATION_VERSION20, LEVEL0_INFO1_KEY, LEVEL1_INFO20_KEY, CONFIGURATION_VERSION20_KEY);
        compositeVersionInfo.setVersionForKeys(IMPLEMENTATION_VERSION20, LEVEL0_INFO1_KEY, LEVEL1_INFO20_KEY, IMPLEMENTATION_VERSION20_KEY);
    }

    /**
     * Create and fill in CompositeVersionInfoMap at level 2.1 i.e.
     * <ul>
     * <li>string map at level 2.1</li>
     * <li>level 2.1 composite version info in level 1 version info map for supplied level 0 CVI</li>
     * </ul>
     *
     * @param compositeVersionInfo
     */
    private void setupCVI21(CompositeVersionInfo compositeVersionInfo) {
        compositeVersionInfo.setVersionForKeys(CONFIGURATION_VERSION21, LEVEL0_INFO1_KEY, LEVEL1_INFO21_KEY, CONFIGURATION_VERSION21_KEY);
        compositeVersionInfo.setVersionForKeys(IMPLEMENTATION_VERSION21, LEVEL0_INFO1_KEY, LEVEL1_INFO21_KEY, IMPLEMENTATION_VERSION21_KEY);
    }
}