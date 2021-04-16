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
package org.kawanfw.sql.tomcat.jdbc.passwords;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;
import org.kawanfw.sql.util.SqlTag;

/**
 * Utility methods to create Connections.
 * @author Nicolas de Pomereu
 *
 */
public class ConnectionCreatorUtil {

    /**
     * Creates the list of triplets (driverClassName, url, username)
     * 
     * @param properties
     * @return the list of triplets (driverClassName, url, username)
     */
    public static List<DriverInstanceInfo> createInstances(Properties properties) {
	if (properties == null) {
	    throw new IllegalArgumentException("properties is null");
	}

	List<DriverInstanceInfo> instances = new ArrayList<>();

	Set<String> databases = TomcatStarterUtil.getDatabaseNames(properties);
	for (String database : databases) {
	    String driverClassName = properties.getProperty(database + "." + "driverClassName");
	    String url = properties.getProperty(database + "." + "url");
	    String username = properties.getProperty(database + "." + "username");

	    ConnectionCreatorUtil.checkParameters(database, driverClassName, url, username);
	    DriverInstanceInfo driverInstanceInfo = new DriverInstanceInfo(driverClassName, url, username);
	    instances.add(driverInstanceInfo);
	}

	return instances;
    }

    /**
     * Check that the properties are set for the database name
     * 
     * @param database
     * @param driverClassName
     * @param url
     * @param username
     * @throws DatabaseConfigurationException
     */
    static void checkParameters(String database, String driverClassName, String url, String username)
	    throws DatabaseConfigurationException {

	if (driverClassName == null || driverClassName.isEmpty()) {
	    throw new DatabaseConfigurationException(
		    "the driverClassName property is not set in properties file for database " + database + ". "
			    + SqlTag.PLEASE_CORRECT);
	}

	if ((url == null) || url.isEmpty()) {
	    throw new DatabaseConfigurationException(
		    "the url property is not set in properties file for driverClassName " + driverClassName + ". "
			    + SqlTag.PLEASE_CORRECT);
	}

	if ((username == null) || username.isEmpty()) {
	    throw new DatabaseConfigurationException(
		    "the username property is not set in properties file for driverClassName " + driverClassName + ". "
			    + SqlTag.PLEASE_CORRECT);
	}

	//
	// System.out.println(
	// SqlTag.SQL_PRODUCT_START + " Setting Tomcat JDBC Pool attributes for " +
	// database + " database:");
    }

}
