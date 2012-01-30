/*
 * Copyright 2002-2011 the original author or authors, or Red-Black IT Ltd, as appropriate.
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

package com.redblackit.version;

import java.util.List;
import java.util.Map;

/**
 * Sub-interface supporting a hierarchical and recursive composite version
 * structure, with keys for component CompositeVersionInfo objects as well as
 * the inherited simple strings.
 * 
 * @author djnorth
 */
public interface CompositeVersionInfo extends VersionInfo {

	/**
	 * Constant for default key concatenation separator
	 */
	public static final char DEFAULT_KEY_CONCATENATION_SEPARATOR = '.';

	/**
	 * Get composite version info for specified keys, or null if not defined.
	 * 
	 * The implementation should navigate down the specified composite version
	 * info objects until:
	 * <ul>
	 * <li>it reaches last key in the sequence</li>
	 * <li>there is no composite version info object for an intermediate key</li>
	 * </ul>
	 * 
	 * @param keys
	 * @return compositeVersionInfo object for those keys, or null if none found
	 */
	CompositeVersionInfo getVersionInfoForKeys(String... keys);

	/**
	 * Set composite version info for keys, replacing any existing
	 * object. Any required intermediate version info objects will be created,
	 * using our separator character.
	 * 
	 * A null or 0-length array, or a null key value, or null versionInfo
	 * object will cause a NullPointerException
	 * 
	 * @param versionInfo
	 * @param keys
	 */
	void setVersionInfoForKeys(CompositeVersionInfo versionInfo, String... keys);

	/**
	 * Get version string for specified keys, or null if not defined.
	 * 
	 * Given key0, ...., keyN-1, keyN (assuming all objects are found), this is
	 * equivalent to calling
	 * <code>getVersionInfoForKeys(key0,..., keyN-1).getVersionForKey(keyN)</code>
	 * 
	 * @param keys
	 * @return version string for those keys, or null if none found
	 */
	String getVersionForKeys(String... keys);
	
	/**
	 * Set version string for specified keys, replacing any existing
	 * value. Any required intermediate version info objects will be created,
	 * using our separator character.
	 * 
	 * A null or 0-length array, or a null key value, or null version string
	 * object will cause a NullPointerException
	 * 
	 * @param version
	 * @param keys
	 */
	void setVersionForKeys(String version, String... keys);

	/**
	 * Get full version info structure
	 * 
	 * @return map of versionInfo maps
	 */
	Map<String, CompositeVersionInfo> getVersionInfoMap();

	/**
	 * Get top level version string map
	 * 
	 * @return version string map
	 */
	Map<String, String> getVersionStringMap();

    /**
     * Get top level version map with each single key a list of the component keys
     *
     * @return version string map
     */
    Map<List<String>, String> getVersionComponentMap();

    /**
     * Get the maximum depth of the component version maps. 0 corresponds to the top level versionStringMap alone.
     *
     * @return depth value
     */
    int getMaximumComponentVersionDepth();

}
