package com.redblackit.version;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Abstract base class for VersionInfo implementations, giving consistent
 * getVersionString implementation.
 * 
 * @author djnorth
 */
public abstract class VersionInfoBase implements VersionInfo {
	
	/**
	 * logger
	 */
	private final Logger logger = Logger.getLogger("VersionInfo");

	/**
	 * Get version string
	 * 
	 * @return formatted version string
	 * @see VersionInfo
	 */
	@JsonIgnore
	public String getVersionString() {
		StringBuffer vb = new StringBuffer(getClass().getName());

		vb.append("\n versionMap=").append(getVersionMap());

		return vb.toString();
	}

	/**
	 * @return the logger
	 */
	protected Logger getLogger() {
		return logger;
	}

}