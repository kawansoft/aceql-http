/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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

	return userHome.isEmpty() || vendorUrl.contains("www.android.com");

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
