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
package org.kawanfw.sql.servlet.injection.properties;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.server.auth.crypto.DefaultPropertiesPasswordManager;
import org.kawanfw.sql.api.server.auth.crypto.PropertiesPasswordManager;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * Calls a concrete PropertiesPasswordManagerLoader.getPassword(database) if defined in aceql-server.properties.
 * @author Nicolas de Pomereu
 *
 */
public class PropertiesPasswordManagerLoader {

    /** Debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(PropertiesPasswordManagerLoader.class);
    
    /**
     * Gets the password from PropertiesPasswordManager, if a concrete implementation is defined in properties
     * @param properties
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public static char [] getPassword(Properties properties) throws IOException, SQLException {
	
	Objects.requireNonNull(properties, "properties cannot be null!");
	
	String propertiesPasswordManagerClassName = properties.getProperty("propertiesPasswordManagerClassName");
	
	if (propertiesPasswordManagerClassName == null || propertiesPasswordManagerClassName.trim().isEmpty()) {
	    propertiesPasswordManagerClassName = DefaultPropertiesPasswordManager.class.getName();
	}
	
	debug("Before Class<?> c = Class.forName(propertiesPasswordManagerClassName);");
	
	PropertiesPasswordManager propertiesPasswordManager = null;
	
	// Load it, and get the password
	try {
	    Class<?> c = Class.forName(propertiesPasswordManagerClassName);
	    Constructor<?> constructor = c.getConstructor();
	    propertiesPasswordManager = (PropertiesPasswordManager) constructor.newInstance();
	    debug("After (PropertiesPasswordManager) constructor.newInstance()");
	} catch (Exception e) {
	    String initErrrorMesage = "Impossible to load PropertiesPasswordManager concrete class: " + propertiesPasswordManagerClassName;
	    e.printStackTrace();
	    throw new DatabaseConfigurationException(initErrrorMesage);
	} 
	
	char [] password = propertiesPasswordManager.getPassword();
	
	if (password == null) {
	    debug("password is null!");   	    
	}
	else {
	    debug("password: " + new String (password));    
	}

	return password;
    }

    /**
     * Print debug info
     *
     * @param s
     */

    private static void debug(String s) {
	if (DEBUG)
	    System.out.println(new Date() + " "  + PropertiesPasswordManagerLoader.class.getSimpleName() + " " + s);
    }
}
