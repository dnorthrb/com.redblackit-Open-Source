/**
 * Copyright Red-Black IT Limited 2010
 */
package com.redblackit.version;

/**
 * @author dnorth
 *
 */
public interface VersionInfo
{
    /**
     * Get component title
     *
     * @return title
     */
    String getImplementationTitle();

    /**
     * Get component copyright statement
     *
     * @return implementation version
     */
    String getImplementationVendor();

    /**
     * Get component implementation version
     *
     * @return implementation version
     */
    String getImplementationVersion();

    /**
     * Get component configuration version
     *
     * @return implementation version
     */
    String getConfigurationVersion();

    /**
     * Get version string
     *
     * @return formatted version string
     */
    String getVersionString();

}
