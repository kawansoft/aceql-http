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

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

/**
 * @author Nicolas de Pomereu
 * 
 *         System utilities
 */
public class FrameworkSystemUtil {

    /**
     * only static methods
     */
    protected FrameworkSystemUtil() {

    }

    /**
     * Returns true if system is Andro�d
     * 
     * @return true if system is Andro�d
     */
    public static boolean isAndroid() {

	String userHome = System.getProperty("user.home");
	String vendorUrl = System.getProperty("java.vendor.url");

	if (userHome.isEmpty() || vendorUrl.contains("www.android.com")) {
	    return true;
	} else {
	    return false;
	}

    }

    /**
     * Get all system properties
     */
    public static Map<String, String> getSystemProperties() {
	Properties p = System.getProperties();
	Enumeration<Object> keys = p.keys();
	List<String> listKeys = new Vector<String>();

	while (keys.hasMoreElements()) {
	    String key = (String) keys.nextElement();
	    listKeys.add(key);
	}

	Collections.sort(listKeys);

	Map<String, String> mapProperties = new LinkedHashMap<String, String>();

	for (int i = 0; i < listKeys.size(); i++) {
	    String key = listKeys.get(i);
	    String value = p.getProperty(key);

	    mapProperties.put(key, value);
	}

	return mapProperties;
    }

}
