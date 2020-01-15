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
package org.kawanfw.sql.tomcat;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;

/**
 * @author Nicolas de Pomereu set or clear System Properties defined in
 *         properties file.
 */
public class SystemPropUpdater {

    private static boolean DEBUG = FrameworkDebug
	    .isSet(SystemPropUpdater.class);

    private Properties properties = null;

    public SystemPropUpdater(Properties properties) {
	this.properties = properties;
    }

    public void update() {
	// Do we have to set special values to the Connector?
	Enumeration<?> enumeration = properties.propertyNames();

	if (enumeration.hasMoreElements()) {
	    System.out.println(
		    SqlTag.SQL_PRODUCT_START + " Setting System Properties:");
	}

	while (enumeration.hasMoreElements()) {
	    String property = (String) enumeration.nextElement();

	    if (property.startsWith("systemSetProperty.")) {

		String theValue = properties.getProperty(property);

		String systemProperty = StringUtils.substringAfter(property,
			".");

		debug("property      : " + property);
		debug("systemProperty: " + systemProperty);

		if (theValue != null && !theValue.isEmpty()) {

		    theValue = theValue.trim();

		    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> "
			    + systemProperty + " = " + theValue);
		    System.setProperty(systemProperty, theValue);
		}
	    }

	    if (property.equals("systemClearProperty")) {

		String theValue = properties.getProperty(property);

		if (theValue != null && !theValue.isEmpty()) {

		    theValue = theValue.trim();

		    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> "
			    + theValue + " clear ");
		    System.clearProperty(theValue);
		}
	    }

	}
    }

    /**
     * debug
     */
    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(s);
	}
    }

}
