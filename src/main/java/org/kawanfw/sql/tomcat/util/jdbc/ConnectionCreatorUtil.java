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
package org.kawanfw.sql.tomcat.util.jdbc;

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
    public static List<JdbcInstanceInfo> createInstances(Properties properties) {
	if (properties == null) {
	    throw new IllegalArgumentException("properties is null");
	}

	List<JdbcInstanceInfo> instances = new ArrayList<>();

	Set<String> databases = TomcatStarterUtil.getDatabaseNames(properties);
	
	//TomcatStarterUtil.testDatabasesLimit(databases);
	
	for (String database : databases) {
	    String driverClassName = properties.getProperty(database + "." + "driverClassName");
	    String url = properties.getProperty(database + "." + "url");
	    String username = properties.getProperty(database + "." + "username");

	    ConnectionCreatorUtil.checkParameters(database, driverClassName, url, username);
	    JdbcInstanceInfo jdbcInstanceInfo = new JdbcInstanceInfo(driverClassName, url, username);
	    instances.add(jdbcInstanceInfo);
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
