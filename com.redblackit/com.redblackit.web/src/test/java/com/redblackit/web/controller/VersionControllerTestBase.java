package com.redblackit.web.controller;

import java.util.Map;
import java.util.TreeMap;

import com.redblackit.version.VersionInfo;

public class VersionControllerTestBase {

	protected static final String VERSION_STRING = "Version String";
	protected static final String CONFIGURATION_VERSION = "Configuration Version";
	protected static final String IMPLEMENTATION_VERSION = "Implementation Version";
	protected static final String IMPLEMENTATION_VENDOR = "Implementation Vendor";
	protected static final String IMPLEMENTATION_TITLE = "Implementation Title";
	
	private Map<String, String> versionMap;
	private VersionInfo versionInfo;

	public VersionControllerTestBase() {
		super();
		versionMap = new TreeMap<String, String>();
		getVersionMap().put("implementationVersion", IMPLEMENTATION_VERSION);
		getVersionMap().put("implementationTitle", IMPLEMENTATION_TITLE);
		getVersionMap().put("implementationVendor", IMPLEMENTATION_VENDOR);
		getVersionMap().put("configurationVersion", CONFIGURATION_VERSION);

		versionInfo = new VersionInfo() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.redblackit.version.VersionInfo#getImplementationVersion()
			 */
			@Override
			public Map<String, String> getVersionMap() {
				return versionMap;
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
	}

	/**
	 * @return the versionMap
	 */
	protected Map<String, String> getVersionMap() {
		return versionMap;
	}

	/**
	 * @return the versionInfo
	 */
	protected VersionInfo getVersionInfo() {
		return versionInfo;
	}

}