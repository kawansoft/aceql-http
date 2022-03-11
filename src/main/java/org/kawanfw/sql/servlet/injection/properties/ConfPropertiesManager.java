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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.injection.properties.ConfProperties.ConfPropertiesBuilder;
import org.kawanfw.sql.tomcat.ServletAceQLCallNameGetter;
import org.kawanfw.sql.tomcat.AceQLServletCallNameGetterCreator;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;
import org.kawanfw.sql.tomcat.TomcatStarterUtilProperties;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * Create a ConfProperties from the passed properties.
 * @author Nicolas de Pomereu
 *
 */

public class ConfPropertiesManager {

    private static boolean DEBUG = FrameworkDebug.isSet(ConfPropertiesManager.class);
    
    private Properties properties;

    /**
     * Constructor 
     * @param properties
     */
    public ConfPropertiesManager(Properties properties) {
	this.properties = Objects.requireNonNull(properties, "properties cannot be null!");	
    }

    /**
     * Create the ConfProperties instance created from the Properties.
     * @return the ConfProperties instance created from the Properties.
     * @throws IOException 
     */
    public ConfProperties createConfProperties() throws IOException, SQLException {

	ConfPropertiesBuilder confPropertiesBuilder = new ConfPropertiesBuilder();	
	
	ServletAceQLCallNameGetter servletAceQLCallNameGetter = AceQLServletCallNameGetterCreator.createInstance();
	String aceQLManagerServletCallName = servletAceQLCallNameGetter.getName();
	
	confPropertiesBuilder.servletCallName(aceQLManagerServletCallName);
	
	boolean statelessMode = Boolean.parseBoolean(properties.getProperty(ServerSqlManager.STATELESS_MODE, "false"));
	confPropertiesBuilder.statelessMode(statelessMode);
	
	Set<String> databases = TomcatStarterUtil.getDatabaseNames(properties);
	confPropertiesBuilder.databaseSet(databases);

	String userAuthenticatorClassName = TomcatStarterUtil
		.trimSafe(properties.getProperty(ServerSqlManager.USER_AUTHENTICATOR_CLASS_NAME));
	if (userAuthenticatorClassName != null && !userAuthenticatorClassName.isEmpty()) {
	    confPropertiesBuilder.userAuthenticatorClassName(userAuthenticatorClassName);
	}

	String requestHeadersAuthenticatorClassName = TomcatStarterUtil
		.trimSafe(properties.getProperty(ServerSqlManager.REQUEST_HEADERS_AUTHENTICATOR_CLASS_NAME));
	if (requestHeadersAuthenticatorClassName != null && !requestHeadersAuthenticatorClassName.isEmpty()) {
	    confPropertiesBuilder.requestHeadersAuthenticatorClassName(requestHeadersAuthenticatorClassName);
	}

	Map<String, String> databaseConfiguratorClassNameMap = new HashMap<>();
	Map<String, List<String>> sqlFirewallClassNamesMap = new HashMap<>();
	Map<String, String> sqlFirewallTriggerClassNamesMap  = new HashMap<>();
	Map<String, List<String>> updateListenerClassNamesMap = new HashMap<>();
	
	buildObjectsPerDatabase(databases, databaseConfiguratorClassNameMap, sqlFirewallClassNamesMap,
		sqlFirewallTriggerClassNamesMap, updateListenerClassNamesMap);
	
	confPropertiesBuilder.databaseConfiguratorClassNameMap(databaseConfiguratorClassNameMap);
	confPropertiesBuilder.sqlFirewallManagerClassNamesMap(sqlFirewallClassNamesMap);
	confPropertiesBuilder.sqlFirewallTriggerClassNamesMap(sqlFirewallTriggerClassNamesMap);
	
	if (DEBUG) {
	    System.out.println("sqlFirewallTriggerClassNamesMap: " + sqlFirewallTriggerClassNamesMap);
	}
	
	confPropertiesBuilder.updateListenerClassNamesMap(updateListenerClassNamesMap);
	
	String blobDownloadConfiguratorClassName = TomcatStarterUtil
		.trimSafe(properties.getProperty(ServerSqlManager.BLOB_DOWNLOAD_CONFIGURATOR_CLASS_NAME));
	confPropertiesBuilder.blobDownloadConfiguratorClassName(blobDownloadConfiguratorClassName);
	
	String blobUploadConfiguratorClassName = TomcatStarterUtil
		.trimSafe(properties.getProperty(ServerSqlManager.BLOB_UPLOAD_CONFIGURATOR_CLASS_NAME));
	confPropertiesBuilder.blobUploadConfiguratorClassName(blobUploadConfiguratorClassName);
	
	String sessionConfiguratorClassName = TomcatStarterUtil
		.trimSafe(properties.getProperty(ServerSqlManager.SESSION_CONFIGURATOR_CLASS_NAME));
	confPropertiesBuilder.sessionConfiguratorClassName(sessionConfiguratorClassName);

	String jwtSessionConfiguratorSecretValue = TomcatStarterUtil
		.trimSafe(properties.getProperty(ServerSqlManager.JWT_SESSION_CONFIGURATOR_SECRET));	
	confPropertiesBuilder.jwtSessionConfiguratorSecretValue(jwtSessionConfiguratorSecretValue);
	
	ConfProperties confProperties = confPropertiesBuilder.build();
	return confProperties;
	
    }

    /**
     * @param databases
     * @param databaseConfiguratorClassNameMap
     * @param sqlFirewallClassNamesMap
     * @param sqlFirewallTriggerClassNamesMap 
     * @param updateListenerClassNamesMap
     */
    public void buildObjectsPerDatabase(Set<String> databases, Map<String, String> databaseConfiguratorClassNameMap,
	    Map<String, List<String>> sqlFirewallClassNamesMap, Map<String, String> sqlFirewallTriggerClassNamesMap, Map<String, List<String>> updateListenerClassNamesMap) {
	for (String database : databases) {
	    
	    // Set the configurator to use for this database
	    String databaseConfiguratorClassName = TomcatStarterUtil.trimSafe(
		    properties.getProperty(database + "." + ServerSqlManager.DATABASE_CONFIGURATOR_CLASS_NAME));

	    if (databaseConfiguratorClassName != null && !databaseConfiguratorClassName.isEmpty()) {
		databaseConfiguratorClassNameMap.put(database, databaseConfiguratorClassName);
	    }
	    
	    String sqlFirewallTriggerClassName = TomcatStarterUtil.trimSafe(
		    properties.getProperty(database + "." + ServerSqlManager.SQL_FIREWALL_TRIGGER_CLASS_NAME));

	    if (sqlFirewallTriggerClassName != null && !sqlFirewallTriggerClassName.isEmpty()) {
		sqlFirewallTriggerClassNamesMap.put(database, sqlFirewallTriggerClassName);
	    }
	    
	    // Set the firewall class names to use for this database
	    String sqlFirewallClassNameArray = TomcatStarterUtil.trimSafe(
		    properties.getProperty(database + "." + ServerSqlManager.SQL_FIREWALL_MANAGER_CLASS_NAMES));

	    if (sqlFirewallClassNameArray != null && !sqlFirewallClassNameArray.isEmpty()) {
		List<String> sqlFirewallClassNames = TomcatStarterUtilProperties.getList(sqlFirewallClassNameArray);
		sqlFirewallClassNamesMap.put(database, sqlFirewallClassNames );
	    } else {
		sqlFirewallClassNamesMap.put(database, new ArrayList<String>() );
	    }
	  
	    String updateListenerClassNameArray = TomcatStarterUtil.trimSafe(
		    properties.getProperty(database + "." + ServerSqlManager.UPDATE_LISTENER_MANAGER_CLASS_NAMES));

	    if (updateListenerClassNameArray != null && !updateListenerClassNameArray.isEmpty()) {
		List<String> updateListenerClassNames = TomcatStarterUtilProperties.getList(updateListenerClassNameArray);
		updateListenerClassNamesMap.put(database, updateListenerClassNames );
	    } else {
		updateListenerClassNamesMap.put(database, new ArrayList<String>() );
	    }
	}
    }
    

}
