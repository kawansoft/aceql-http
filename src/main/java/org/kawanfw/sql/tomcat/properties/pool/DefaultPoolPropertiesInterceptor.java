/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.tomcat.properties.pool;
/**
 * @author Nicolas de Pomereu
 *
 */

public class DefaultPoolPropertiesInterceptor implements PoolPropertiesInterceptor {

    @Override
    public String interceptValue(String theMethod, final String propertyValue) {
	if (theMethod == null || propertyValue == null) {
	    return propertyValue;
	}
	
	// Not clean to update passed parameter
	String propertyValueUpdated = propertyValue;
	
	if (theMethod.equals("setMaxIdle")) {
	    int maxAllowedValue = 125;
	    propertyValueUpdated = getMaxAllowedValue(propertyValueUpdated, maxAllowedValue);
	}
	else if (theMethod.equals("setMaxActive")) {
	    int maxAllowedValue = 125;
	    propertyValueUpdated = getMaxAllowedValue(propertyValueUpdated, maxAllowedValue);
	}
	return propertyValueUpdated;

    }

    /**
     * Gets the new allowed value
     * @param propertyValue
     * @param maxAllowedValue
     */
    public String getMaxAllowedValue(String propertyValue, int maxAllowedValue) {
	int value;
	try {
	    value = Integer.parseInt(propertyValue);
	    if (value > maxAllowedValue) {
		return "" + maxAllowedValue;
	    }
	    return propertyValue;
	} catch (NumberFormatException e) {
	    return propertyValue;
	}
    }

}
