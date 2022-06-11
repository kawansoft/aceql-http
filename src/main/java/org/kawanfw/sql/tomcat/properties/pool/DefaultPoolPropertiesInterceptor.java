/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
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
