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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.server.auth.crypto.PropertiesPasswordManager;
import org.kawanfw.sql.tomcat.util.LinkedProperties;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.version.EditionUtil;

/**
 * Methods for properties and jasypt encrypted properties loading.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class PropertiesFileUtil {

    /** Debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(PropertiesFileUtil.class);
    
    /**
     * Returns the Properties extracted from a file.
     *
     * @param file the file containing the properties
     * @return the Properties extracted from the file
     *
     * @throws IOException
     * @throws DatabaseConfigurationException
     * @throws SQLException 
     */
    public static Properties getProperties(File file) throws IOException {
	
	Properties properties = commonsGetProperties(file);
	
	debug("Before EditionUtil.isCommunityEdition()");
	if (EditionUtil.isCommunityEdition()) {
	    return properties;
	}
	debug("After EditionUtil.isCommunityEdition()");
	debug("Properties file: " + file);
	
	char[] password = null;
	try {
	    password = PropertiesPasswordManagerLoader.getPassword(properties);
	} catch (Exception e) {
	    throw new DatabaseConfigurationException(e.getMessage());
	}
	
	debug("Password: " + new String(password));
		
	try {
	    Class<?> c = Class.forName("org.kawanfw.sql.pro.reflection.builders.ProEditionPropertiesDecryptor");
	    Constructor<?> constructor = c.getConstructor();
	    PropertiesDecryptor propertiesDecryptor = (PropertiesDecryptor) constructor.newInstance();
	    properties =  propertiesDecryptor.decrypt(properties, password);
	    
	    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
		String key = (String) entry.getKey();
		String value = (String) entry.getValue();
		if (key.contains("password")) {
		    debug(" In getProperties: --> key / value: " + key + " / " + value);
		}
	    }
	    
	    return properties;
	} catch (Exception e) {
	    e.printStackTrace(System.out);
	    throw new IOException("Can not load ProEditionPropertiesDecryptor", e);
	} 
    }
    
    public static char [] getPassword(Properties properties) throws IOException, SQLException {
	
	Objects.requireNonNull(properties, "properties cannot be null!");
	
	String propertiesPasswordManagerClassName = properties.getProperty("propertiesPasswordManagerClassName");
	
	if (propertiesPasswordManagerClassName == null || propertiesPasswordManagerClassName.isEmpty()) {
	    return null;
	}
	
	PropertiesPasswordManager propertiesPasswordManager = null;
	
	// Load it, and get the password
	try {
	    Class<?> c = Class.forName(propertiesPasswordManagerClassName);
	    Constructor<?> constructor = c.getConstructor();
	    propertiesPasswordManager = (PropertiesPasswordManager) constructor.newInstance();
	} catch (Exception e) {
	    String initErrrorMesage = "Impossible to load PropertiesPasswordManager concrete class: " + propertiesPasswordManagerClassName;
	    e.printStackTrace();
	    throw new DatabaseConfigurationException(initErrrorMesage);
	} 
	
	return propertiesPasswordManager.getPassword();
    }
    
    /**
     * Return the load Properties for the passed file
     * @param file
     * @return
     * @throws IllegalArgumentException
     * @throws DatabaseConfigurationException
     * @throws IOException
     * @throws FileNotFoundException
     */
    private static Properties commonsGetProperties(File file)
	    throws IllegalArgumentException, DatabaseConfigurationException, IOException, FileNotFoundException {
	if (file == null) {
	    throw new IllegalArgumentException("file can not be null!");
	}

	if (!file.exists()) {
	    throw new DatabaseConfigurationException("properties file not found: " + file);
	}

	// Get the properties with order of position in file:
	Set<String> linkedProperties = LinkedProperties.getLinkedPropertiesName(file);

	// Create the ordered properties Properties properties;
	Properties properties = new Properties();
	try (InputStream in = new FileInputStream(file);) {
	    properties = new LinkedProperties(linkedProperties);
	    properties.load(in);
	}
	
	return properties;
    }

    /**
     * Print debug info
     *
     * @param s
     */

    private static void debug(String s) {
	if (DEBUG)
	    System.out.println(new Date() + " "  + PropertiesFileUtil.class.getSimpleName() + " " + s);
    }
}
