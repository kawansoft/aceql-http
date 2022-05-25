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
	
	if (propertiesPasswordManagerClassName == null || propertiesPasswordManagerClassName.isEmpty()) {
	    System.err.println("propertiesPasswordManagerClassName is null!");
	    return null;
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
	
	debug("password: " + new String (password));
	
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
