/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
