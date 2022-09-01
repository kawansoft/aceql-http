/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * Utility class to build a defensive copy of a Map<K, V>, backed by a Hashmap,
 * Hashtable , LinkedHashMap or TreeMap.
 *
 * @author Nicolas de Pomereu
 *
 */
public class MapCopier<K, V> {

    /**
     * Copy a Map<K, V> to another Map
     *
     * @param map
     *            the map to copy
     * @return the copied map
     */
    public Map<K, V> copy(Map<K, V> map) {
	Map<K, V> mapCopy = null;

	if (map instanceof HashMap) {
	    mapCopy = new HashMap<K, V>(map);
	} else if (map instanceof Hashtable) {
	    mapCopy = new Hashtable<K, V>(map);
	} else if (map instanceof LinkedHashMap) {
	    mapCopy = new LinkedHashMap<K, V>(map);
	} else if (map instanceof TreeMap) {
	    mapCopy = new TreeMap<K, V>(map);
	} else {
	    throw new IllegalArgumentException(
		    "copy implementation not supported for Map class: "
			    + map.getClass());
	}

	/*
	 * Set<K> keys = map.keySet();
	 *
	 * for (Iterator<K> iterator = keys.iterator(); iterator.hasNext();) { K
	 * key = (K) iterator.next(); V value = map.get(key);
	 *
	 * mapCopy.put(key, value); }
	 */

	return mapCopy;
    }

}
