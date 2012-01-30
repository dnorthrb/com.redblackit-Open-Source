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

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Version info taking simple properties or map objects
 * 
 * @author djnorth
 */
public class SimpleVersionInfo extends VersionInfoBase {

	/**
	 * Configuration version
	 */
	private Properties versionProperties;

	/**
	 * Default
	 */
	public SimpleVersionInfo() {
		this(null);
	}

	/**
	 * Constructor taking attributes
	 * 
	 * @param manifestClass
	 * @param versionProperties
	 */
	public SimpleVersionInfo(Properties versionProperties) {
		setVersionProperties(versionProperties);
	}

	/**
	 * Set version properties
	 * 
	 * @param versionProperties
	 *            the versionProperties to set
	 */
	public void setVersionProperties(Properties versionProperties) {
		this.versionProperties = versionProperties;
	}

	/**
	 * This is not in the VersionInfo interface, but allows the use of
	 * properties for convenience
	 * 
	 * @return the versionProperties
	 */
	@JsonIgnore
	public Properties getVersionProperties() {
		return versionProperties;
	}

	/**
	 * Set version properties from supplied map
	 * 
	 * @param versionMap
	 */
	public void setVersionMap(Map<String, String> versionMap) {
		if (versionMap == null) {
			versionProperties = null;
		} else {

			if (versionProperties == null) {
				versionProperties = new Properties();
			} else {
				versionProperties.clear();
			}

			versionProperties.putAll(versionMap);
		}
	}

	/**
	 * Get version values from supplied map
	 * 
	 * @return properties
	 * @see com.redblackit.version.VersionInfo#getVersionMap()
	 */
	@Override
	public Map<String, String> getVersionMap() {
		Map<String, String> versionMap = null;
		if (versionProperties != null) {
			versionMap = new TreeMap<String, String>();
			for (Object pname : versionProperties.keySet()) {
				versionMap.put(pname.toString(),
						versionProperties.getProperty(pname.toString()));
			}
		}

		return versionMap;
	}

	/**
	 * toString using the map.toString
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(getClass().getSimpleName());
		builder.append(" [versionMap=");
		builder.append(getVersionMap());
		builder.append("]");
		return builder.toString();
	}

	/**
	 * hashCode from properties
	 * 
	 * @return hashCode
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getVersionMap().hashCode();
	}

	/**
	 * equals from properties
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SimpleVersionInfo other = (SimpleVersionInfo) obj;
		if (getVersionMap() == null) {
			return (other.getVersionMap() == null);
		}
		return getVersionMap().equals(other.getVersionMap());
	}

}
