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

import java.util.*;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Implementation of composite version info based on maps.
 *
 * @author djnorth
 */
public class CompositeVersionInfoMap extends VersionInfoBase implements
        CompositeVersionInfo {

    /**
     * Comparator for our use
     */
    private final ComponentKeyListComparator comparator = new ComponentKeyListComparator();
    /**
     * Key concatenation separator
     */
    private char keyConcatenationSeparator;

    /**
     * Map with version info
     */
    private Map<String, CompositeVersionInfo> versionInfoMap;

    /**
     * Top level version strings
     */
    private Map<String, String> versionStringMap;

    /**
     * Default constructor, using default key concatenation separator
     */
    public CompositeVersionInfoMap() {
        this(CompositeVersionInfo.DEFAULT_KEY_CONCATENATION_SEPARATOR);
    }

    /**
     * Constructor taking key concatenation separator
     *
     * @param keyConcatenationSeparator
     */
    public CompositeVersionInfoMap(char keyConcatenationSeparator) {
        super();
        this.keyConcatenationSeparator = keyConcatenationSeparator;
    }

    /**
     * This returns a flattened map of version information, composing the keys
     * at this level with a specified separation character, using getVersionMap
     * from each VersionInfo. This means that, if a CompositeVersionInfo is
     * present, its own separator will be used for its getVersionMap. This gives
     * the power and responsibility to choose how separators are used through
     * the whole structure.
     * <p/>
     * If there is no version info, we return null.
     *
     * @return map as above.
     * @see com.redblackit.version.VersionInfo#getVersionMap()
     */
    @Override
    @JsonIgnore
    public Map<String, String> getVersionMap() {

        Map<String, String> versionMap0 = null;
        if (versionStringMap == null) {
            versionMap0 = new TreeMap<String, String>();
        } else {
            versionMap0 = getVersionStringMap();
        }
        if (versionInfoMap != null) {
            for (String key0 : versionInfoMap.keySet()) {
                Map<String, String> versionMap1 = getVersionInfoForKey(key0)
                        .getVersionMap();
                for (String key1 : versionMap1.keySet()) {
                    versionMap0.put(key0 + getKeyConcatenationSeparator()
                                    + key1, versionMap1.get(key1));
                }
            }
        }

        return versionMap0;
    }

    /**
     * Get composite version info for specified keys, or null if not defined.
     * <p/>
     * The implementation should navigate down the specified composite version
     * info objects until:
     * <ul>
     * <li>it reaches last key in the sequence</li>
     * <li>there is no composite version info object for an intermediate key</li>
     * </ul>
     *
     * @param keys
     * @return compositeVersionInfo object for those keys, or null if none found
     * @see com.redblackit.version.CompositeVersionInfo#getVersionInfoForKeys(java.lang.String[])
     */
    @Override
    public CompositeVersionInfo getVersionInfoForKeys(String... keys) {
        CompositeVersionInfo lastVersionInfo = null;
        if (versionInfoMap != null && keys != null && keys.length > 0) {
            CompositeVersionInfo versionInfo = getVersionInfoForKey(keys[0]);
            if (keys.length == 1 || versionInfo == null) {
                lastVersionInfo = versionInfo;
            } else {
                lastVersionInfo = versionInfo.getVersionInfoForKeys(Arrays
                                                                            .copyOfRange(keys, 1, keys.length));
            }
        }
        return lastVersionInfo;
    }

    /**
     * Set composite version info for keys, replacing any existing object. Any
     * required intermediate version info objects will be created, using our
     * separator character.
     * <p/>
     * A null or 0-length array, or a null key value, or null version info
     * object will cause a NullPointerException
     *
     * @param versionInfo
     * @param keys
     * @see CompositeVersionInfo#setVersionInfoForKeys(CompositeVersionInfo, String...)
     */
    @Override
    public void setVersionInfoForKeys(CompositeVersionInfo versionInfo,
                                      String... keys) {

        if (keys == null || keys.length == 0 || keys[0] == null
            || versionInfo == null) {
            throw new NullPointerException("null or 0-length keys (="
                                           + Arrays.toString(keys) + ") or null versionInfo (="
                                           + versionInfo + ")");
        }

        if (versionInfo == this) {
            throw new IllegalArgumentException("recursive call for key=" + keys
                                               + ":with versionInfo == this:" + this);
        }

        if (this.versionInfoMap == null) {
            this.versionInfoMap = new TreeMap<String, CompositeVersionInfo>();
        }

        if (keys.length == 1) {
            this.versionInfoMap.put(keys[0], versionInfo);
        } else {
            CompositeVersionInfo nextCompositeVersionInfo = this.versionInfoMap
                    .get(keys[0]);
            if (nextCompositeVersionInfo == null) {
                nextCompositeVersionInfo = new CompositeVersionInfoMap(
                        keyConcatenationSeparator);
                this.versionInfoMap.put(keys[0], nextCompositeVersionInfo);
            }

            nextCompositeVersionInfo.setVersionInfoForKeys(versionInfo,
                                                           Arrays.copyOfRange(keys, 1, keys.length));
        }

    }

    /**
     * Get version string for specified keys, or null if not defined.
     * <p/>
     * Given key0, ...., keyN-1, keyN (assuming all objects are found), this is
     * equivalent to calling
     * <code>getVersionInfoForKeys(key0,..., keyN-1).getVersionForKey(keyN)</code>
     *
     * @param keys
     * @return version string for those keys, or null if none found
     * @see com.redblackit.version.CompositeVersionInfo#getVersionForKeys(java.lang.String[])
     */
    @Override
    public String getVersionForKeys(String... keys) {
        String version = null;
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                version = getVersionForKey(keys[0]);
            } else {
                CompositeVersionInfo containingVersionInfo = getVersionInfoForKeys(Arrays
                                                                                           .copyOf(keys, keys.length - 1));
                if (containingVersionInfo != null) {
                    version = containingVersionInfo
                            .getVersionForKeys(keys[keys.length - 1]);
                }
            }

            if (version == null && getLogger().isDebugEnabled()) {
                getLogger().debug(
                        "getVersionForKeys:keys=" + Arrays.toString(keys)
                        + ":no entry found");
            }
        }
        return version;
    }

    /**
     * Set version string for specified keys, replacing any existing value. Any
     * required intermediate version info objects will be created, using our
     * separator character.
     * <p/>
     * A null or 0-length array, or a null key value, or null version string
     * object will cause a NullPointerException
     *
     * @param version
     * @param keys
     * @see com.redblackit.version.CompositeVersionInfo#setVersionForKeys(java.lang.String,
     *      java.lang.String[])
     */
    @Override
    public void setVersionForKeys(String version, String... keys) {
        if (keys == null || keys.length == 0 || keys[0] == null
            || version == null) {
            throw new NullPointerException("null or 0-length keys (="
                                           + Arrays.toString(keys) + ") or null version (=" + version
                                           + ")");
        }

        if (keys.length == 1) {
            if (this.versionStringMap == null) {
                this.versionStringMap = new TreeMap<String, String>();
            }

            this.versionStringMap.put(keys[0], version);

        } else {
            if (this.versionInfoMap == null) {
                this.versionInfoMap = new TreeMap<String, CompositeVersionInfo>();
            }

            CompositeVersionInfo nextCompositeVersionInfo = this.versionInfoMap
                    .get(keys[0]);

            if (nextCompositeVersionInfo == null) {
                nextCompositeVersionInfo = new CompositeVersionInfoMap(
                        keyConcatenationSeparator);
                this.versionInfoMap.put(keys[0], nextCompositeVersionInfo);
            }

            nextCompositeVersionInfo.setVersionForKeys(version,
                                                       Arrays.copyOfRange(keys, 1, keys.length));
        }

    }

    /**
     * Return a copy of the structured map.
     *
     * @return versionInfoMap
     * @see com.redblackit.version.CompositeVersionInfo#getVersionInfoMap()
     */
    @Override
    public Map<String, CompositeVersionInfo> getVersionInfoMap() {
        return (versionInfoMap == null ? null
                                       : new TreeMap<String, CompositeVersionInfo>(versionInfoMap));
    }

    /**
     * Set complete version info map, clearing current map, and copying new
     * entries into a new map. This means that the source map need not be a
     * TreeMap.
     * <p/>
     * This method is suitable for use in Spring configuration.
     *
     * @param versionInfoMap to use
     */
    @JsonDeserialize(contentAs = CompositeVersionInfoMap.class)
    public void setVersionInfoMap(
            Map<String, CompositeVersionInfo> versionInfoMap) {
        if (versionInfoMap == null) {
            this.versionInfoMap = null;
        } else {
            if (this.versionInfoMap == null) {
                this.versionInfoMap = new TreeMap<String, CompositeVersionInfo>();
            } else {
                this.versionInfoMap.clear();
            }

            this.versionInfoMap.putAll(versionInfoMap);
        }
    }

    /**
     * Set top-level version string map, clearing current map, and copying new
     * entries into a new map. This means that the source map need not be a
     * TreeMap.
     * <p/>
     * This method is suitable for use in Spring configuration.
     *
     * @param versionStringMap the versionStringMap to set
     */
    public void setVersionStringMap(Map<String, String> versionStringMap) {
        if (versionStringMap == null) {
            this.versionStringMap = null;
        } else {
            if (this.versionStringMap == null) {
                this.versionStringMap = new TreeMap<String, String>();
            } else {
                this.versionStringMap.clear();
            }

            this.versionStringMap.putAll(versionStringMap);
        }
    }

    /**
     * Get top level version string map
     *
     * @return top-level version string map
     * @see com.redblackit.version.CompositeVersionInfo#getVersionStringMap()
     */
    @Override
    public Map<String, String> getVersionStringMap() {
        return (versionStringMap == null ? null : new TreeMap<String, String>(
                versionStringMap));
    }

    /**
     * Get top level version map with each single key a list of the component keys
     *
     * @return version string map
     * @see CompositeVersionInfo
     */
    @Override
    @JsonIgnore
    public Map<List<String>, String> getVersionComponentMap() {
        Map<List<String>, String> versionComponentMap0 = new TreeMap<List<String>, String>(comparator);
        if (versionStringMap != null) {
            for (String key : versionStringMap.keySet()) {
                versionComponentMap0.put(Collections.singletonList(key), versionStringMap.get(key));
            }
        }

        if (versionInfoMap != null) {
            for (String key : versionInfoMap.keySet()) {
                Map<List<String>, String> versionComponentMap1 = versionInfoMap.get(key).getVersionComponentMap();
                for (List<String> componentKey1 : versionComponentMap1.keySet()) {
                    List<String> componentKey0 = new ArrayList<String>(componentKey1);
                    componentKey0.add(0, key);
                    versionComponentMap0.put(componentKey0, versionComponentMap1.get(componentKey1));
                }
            }
        }

        return versionComponentMap0;
    }

    /**
     * Get the maximum depth of the component version maps.
     * <ul><li>0 corresponds to the top level versionStringMap alone, or with versionInfoMap entries which are
     * themselves entirely empty of version information.</li>
     * <li>-1 implies no version information at all</li></ul>
     *
     * @return depth value
     * @see CompositeVersionInfo
     */
    @Override
    @JsonIgnore
    public int getMaximumComponentVersionDepth() {
        int maxDepth = (versionStringMap == null ? -1 : 0);
        if (versionInfoMap != null) {
            for (String key : versionInfoMap.keySet()) {
                int componentDepth = (versionInfoMap.get(key).getMaximumComponentVersionDepth() + 1);
                if (componentDepth > maxDepth) {
                    maxDepth = componentDepth;
                }
            }
        }

        return maxDepth;
    }

    /**
     * We can change the concatenation character
     *
     * @param keyConcatenationSeparator the keyConcatenationSeparator to set
     */
    public void setKeyConcatenationSeparator(char keyConcatenationSeparator) {
        this.keyConcatenationSeparator = keyConcatenationSeparator;
    }

    /**
     * @return the keyConcatenationSeparator
     */
    public char getKeyConcatenationSeparator() {
        return keyConcatenationSeparator;
    }


    /**
     * Normal toString
     *
     * @see java.lang.Object#toString()     *
     */
    @Override
    public String toString() {
        return toString(0);
    }

    /**
     * toString providing for indentation and so readability
     *
     * @param level
     * @return
     */
    public String toString(int level) {
        char[] pfxcs = new char[level * 2];
        Arrays.fill(pfxcs, ' ');
        String pfx = new String(pfxcs);

        StringBuilder builder = new StringBuilder();
        builder.append("CompositeVersionInfoMap {keyConcatenationSeparator=");
        builder.append(keyConcatenationSeparator);
        builder.append("\n  ").append(pfx).append("versionInfoMap=");
        if (versionInfoMap == null) {
            builder.append(versionInfoMap);
        } else {
            for (String key : versionInfoMap.keySet()) {
                builder.append("\n  ").append(pfx).append("['").append(key).append("']:");
                CompositeVersionInfo cvi = versionInfoMap.get(key);
                if (cvi instanceof CompositeVersionInfoMap) {
                    builder.append(((CompositeVersionInfoMap) cvi).toString(level + 1));
                } else {
                    builder.append(cvi);
                }
            }
        }
        builder.append("\n  ").append(pfx).append("versionStringMap=");
        builder.append(versionStringMap);
        builder.append('\n').append(pfx).append('}');
        return builder.toString();
    }

    /*
      * (non-Javadoc)
      *
      * @see java.lang.Object#hashCode()
      */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + keyConcatenationSeparator;
        result = prime * result
                 + ((versionInfoMap == null) ? 0 : versionInfoMap.hashCode());
        result = prime
                 * result
                 + ((versionStringMap == null) ? 0 : versionStringMap.hashCode());
        return result;
    }

    /*
      * (non-Javadoc)
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
        CompositeVersionInfoMap other = (CompositeVersionInfoMap) obj;
        if (keyConcatenationSeparator != other.keyConcatenationSeparator) {
            return false;
        }
        if (versionInfoMap == null) {
            if (other.versionInfoMap != null) {
                return false;
            }
        } else if (!versionInfoMap.equals(other.versionInfoMap)) {
            return false;
        }
        if (versionStringMap == null) {
            if (other.versionStringMap != null) {
                return false;
            }
        } else if (!versionStringMap.equals(other.versionStringMap)) {
            return false;
        }
        return true;
    }

    /**
     * Return the CompositeVersionInfo for a specific key, or null if there is
     * none
     *
     * @param key
     * @return versionInfo
     */
    protected CompositeVersionInfo getVersionInfoForKey(String key) {
        return (versionInfoMap == null || key == null ? null : versionInfoMap
                .get(key));
    }

    /**
     * Get version string for supplied key, or null if not defined
     *
     * @param key
     * @return version string
     */
    protected String getVersionForKey(String key) {
        return (versionStringMap == null || key == null ? null
                                                        : versionStringMap.get(key));
    }

    /**
     * Comparator for key lists
     */
    public class ComponentKeyListComparator implements Comparator<List<String>> {

        /**
         * Compare using string value of lists
         *
         * @param keys0
         * @param keys1
         * @return comparison result
         * @see Comparator#compare(Object, Object)
         */
        @Override
        public int compare(List<String> keys0, List<String> keys1) {
            return keys0.toString().compareTo(keys1.toString());
        }
    }
}
