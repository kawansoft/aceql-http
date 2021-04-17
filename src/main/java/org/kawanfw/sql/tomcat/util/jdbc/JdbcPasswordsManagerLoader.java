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

package org.kawanfw.sql.tomcat.util.jdbc;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.server.auth.jdbc.JdbcPasswordsManager;

/**
 * Calls a concrete JdbcPasswordsManager.getPassword(database) if defined inaceql-server.properties.
 * @author Nicolas de Pomereu
 *
 */
public class JdbcPasswordsManagerLoader {


    /**
     * Gets the password for the passed database from JdbcPasswordsManager, if concrete implementation is defined in properties
     * @param database
     * @param properties
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public static char [] getPasswordUsingJdbcPasswordManagers(String database, Properties properties) throws IOException, SQLException {
	Objects.requireNonNull(database, "database cannot be null!");
	Objects.requireNonNull(properties, "properties cannot be null!");
	
	String jdbcPasswordsManagerClassName = properties.getProperty("jdbcPasswordsManagerClassName");
	
	if ((jdbcPasswordsManagerClassName == null) || jdbcPasswordsManagerClassName.isEmpty()) {
	    // No JdbcPasswordManager implementation ==> return null 
	    return null;
	}
	
	JdbcPasswordsManager jdbcPasswordsManager = null;
	
	// Load it, and get the password
	try {
	    Class<?> c = Class.forName(jdbcPasswordsManagerClassName);
	    Constructor<?> constructor = c.getConstructor();
	    jdbcPasswordsManager = (JdbcPasswordsManager) constructor.newInstance();
	} catch (Exception e) {
	    String initErrrorMesage = "Impossible to load JdbcPasswordsManager concrete class: " + jdbcPasswordsManagerClassName;
	    e.printStackTrace();
	    throw new DatabaseConfigurationException(initErrrorMesage);
	} 
	
	char [] password =  jdbcPasswordsManager.getPassword(database);
	return password;    
    }

    /*
    initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
	    + " Impossible to load (ClassNotFoundException) Configurator class: " + classNameToLoad;
     */
}
