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

package com.redblackit.version;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Version info where most comes from Manifest for this class's JAR or WAR
 * 
 * @author djnorth
 */
@XmlRootElement
public class VersionInfoFromProperties implements VersionInfo {

	/**
	 * Configuration version
	 */
	private Properties versionProperties;

	/**
	 * Default, mainly for JAXB's benefit
	 */
	public VersionInfoFromProperties() {
		this(null);
	}

	/**
	 * Constructor taking attributes
	 * 
	 * @param manifestClass
	 * @param versionProperties
	 */
	public VersionInfoFromProperties(Properties versionProperties) {
		this.versionProperties = versionProperties;
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
	 * This is not in the VersionInfo interface, and is mainly for the benefit of JAXB2
	 * 
	 * @return the versionProperties
	 */
	public Properties getVersionProperties() {
		return versionProperties;
	}

	/**
	 * Get version values from supplied map
	 * 
	 * @return properties
	 * @see com.redblackit.version.VersionInfo#getVersionMap()
	 */
	@Override
	@XmlTransient
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
	 * Get version string
	 * 
	 * @return formatted version string
	 * @see com.redblackit.common.util.VersionInfo#getVersionString()
	 */
	@XmlTransient
	public String getVersionString() {
		StringBuffer vb = new StringBuffer(getClass().getName());

		vb.append("\n versionMap=").append(getVersionMap());

		return vb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VersionInfoFromProperties [versionMap=");
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
		VersionInfoFromProperties other = (VersionInfoFromProperties) obj;
		if (getVersionMap() == null) {
			return (other.getVersionMap() != null);
		}
		return getVersionMap().equals(other.getVersionMap());
	}

}
