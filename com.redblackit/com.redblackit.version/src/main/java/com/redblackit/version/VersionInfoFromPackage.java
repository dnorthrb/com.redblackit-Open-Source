/**
 * Copyright Red-Black IT Limited 2010
 */
package com.redblackit.version;

/**
 * Version info where most comes from Manifest for this class's JAR or WAR
 * 
 * @author dnorth
 */
public class VersionInfoFromPackage implements VersionInfo {

	/**
	 * Set the class from whose JAR file we get manifest info. The default is
	 * this class.
	 * 
	 * @param manifestClass
	 *            the manifestClass to set
	 */
	public void setManifestClass(Class<? extends Object> manifestClass) {
		this.manifestClass = manifestClass;
	}

	/**
	 * @return the manifestClass
	 */
	public Class<? extends Object> getManifestClass() {
		return (manifestClass == null ? this.getClass() : manifestClass);
	}

	/**
	 * Get component title
	 * 
	 * @return title
	 * @see com.redblackit.common.util.VersionInfo#getImplementationTitle()
	 */
	public String getImplementationTitle() {
		return getManifestClass().getPackage().getImplementationTitle();
	}

	/**
	 * Get component vendor
	 * 
	 * @return implementation version
	 * @see com.redblackit.common.util.VersionInfo#getImplementationVendor()
	 */
	public String getImplementationVendor() {
		return getManifestClass().getPackage().getImplementationVendor();
	}

	/**
	 * Get component implementation version
	 * 
	 * @return implementation version
	 * @see com.redblackit.common.util.VersionInfo#getImplementationVersion()
	 */
	public String getImplementationVersion() {
		return getManifestClass().getPackage().getImplementationVersion();
	}

	/**
	 * @param configurationVersion
	 *            the version to set
	 */
	public void setConfigurationVersion(String configurationVersion) {
		this.configurationVersion = configurationVersion;
	}

	/**
	 * Get component configuration version
	 * 
	 * @return implementation version
	 * @see com.redblackit.common.util.VersionInfo#getConfigurationVersion()
	 */
	public String getConfigurationVersion() {
		return configurationVersion;
	}

	/**
	 * Get version string
	 * 
	 * @return formatted version string
	 * @see com.redblackit.common.util.VersionInfo#getVersionString()
	 */
	public String getVersionString() {
		StringBuffer vb = new StringBuffer(getClass().getName());
		vb.append("\n implementationTitle=" + getImplementationTitle());
		vb.append("\n implementationVendor=" + getImplementationVendor());
		vb.append("\n implementationVersion=" + getImplementationVersion());

		if (getConfigurationVersion() != null) {
			vb.append("\n configurationVersion=" + getConfigurationVersion());
		}

		return vb.toString();
	}

	/**
	 * Class to use for manifest information
	 */
	private Class<? extends Object> manifestClass = null;

	/**
	 * Configuration version
	 */
	private String configurationVersion = null;
}
