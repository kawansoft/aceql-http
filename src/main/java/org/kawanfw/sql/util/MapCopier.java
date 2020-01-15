/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2020,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.                                
 *                                                                               
 * AceQL HTTP is free software; you can redistribute it and/or                 
 * modify it under the terms of the GNU Lesser General Public                    
 * License as published by the Free Software Foundation; either                  
 * version 2.1 of the License, or (at your option) any later version.            
 *                                                                               
 * AceQL HTTP is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU             
 * Lesser General Public License for more details.                               
 *                                                                               
 * You should have received a copy of the GNU Lesser General Public              
 * License along with this library; if not, write to the Free Software           
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301  USA
 * 
 * Any modifications to this file must keep this entire header
 * intact.
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
     * Protected constructor
     */
    public MapCopier() {

    }

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
