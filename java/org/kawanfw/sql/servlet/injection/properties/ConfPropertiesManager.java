/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.injection.properties;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.injection.properties.ConfProperties.ConfPropertiesBuilder;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;
import org.kawanfw.sql.tomcat.TomcatStarterUtilProperties;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;

/**
 * Create a ConfProperties from the passed properties.
 * 
 * @author Nicolas de Pomereu
 *
 */

public class ConfPropertiesManager {

    private static boolean DEBUG = FrameworkDebug.isSet(ConfPropertiesManager.class);

    private Properties properties;

    /**
     * Constructor
     * 
     * @param properties
     */
    public ConfPropertiesManager(Properties properties) {
	this.properties = Objects.requireNonNull(properties, "properties cannot be null!");
    }

    /**
     * Create the ConfProperties instance created from the Properties.
     * 
     * @return the ConfProperties instance created from the Properties.
     * @throws IOException
     */
    public ConfProperties createConfProperties() throws IOException, SQLException {

	ConfPropertiesBuilder confPropertiesBuilder = new ConfPropertiesBuilder();

	//ServletAceQLCallNameGetter servletAceQLCallNameGetter = AceQLServletCallNameGetterCreator.createInstance();
	//String aceQLManagerServletCallName = servletAceQLCallNameGetter.getName();

	AdvancedServletAceQLCallNameGetter advancedServletAceQLCallNameGetter = new AdvancedServletAceQLCallNameGetter();
	String aceQLManagerServletCallName = advancedServletAceQLCallNameGetter.getName();
	
	debug("aceQLManagerServletCallName: " + aceQLManagerServletCallName);

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
	Map<String, Set<String>> sqlFirewallClassNamesMap = new ConcurrentHashMap<>();
	Map<String, Set<String>> sqlFirewallTriggerClassNamesMap = new ConcurrentHashMap<>();
	Map<String, Set<String>> updateListenerClassNamesMap = new ConcurrentHashMap<>();
	Map<String, OperationalMode> operationalModeMap = new LinkedHashMap<>();
	
	buildObjectsPerDatabase(databases, databaseConfiguratorClassNameMap, sqlFirewallClassNamesMap,
		sqlFirewallTriggerClassNamesMap, updateListenerClassNamesMap, operationalModeMap);

	confPropertiesBuilder.databaseConfiguratorClassNameMap(databaseConfiguratorClassNameMap);
	confPropertiesBuilder.sqlFirewallManagerClassNamesMap(sqlFirewallClassNamesMap);
	confPropertiesBuilder.sqlFirewallTriggerClassNamesMap(sqlFirewallTriggerClassNamesMap);
	confPropertiesBuilder.operationalModeMap(operationalModeMap);

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

    private void debug(String s) {
	if (DEBUG)
	    System.out.println(this.getClass().getSimpleName() + " " + new Date() + " " + s);
    }

    /**
     * @param databases
     * @param databaseConfiguratorClassNameMap
     * @param sqlFirewallClassNamesMap
     * @param sqlFirewallTriggerClassNamesMap
     * @param updateListenerClassNamesMap
     * @param operationalModeMap
     */
    public void buildObjectsPerDatabase(Set<String> databases, Map<String, String> databaseConfiguratorClassNameMap,
	    Map<String, Set<String>> sqlFirewallClassNamesMap,
	    Map<String, Set<String>> sqlFirewallTriggerClassNamesMap,
	    Map<String, Set<String>> updateListenerClassNamesMap, Map<String, OperationalMode> operationalModeMap) {
	
	for (String database : databases) {

	    // Set the configurator to use for this database
	    String databaseConfiguratorClassName = TomcatStarterUtil.trimSafe(
		    properties.getProperty(database + "." + ServerSqlManager.DATABASE_CONFIGURATOR_CLASS_NAME));

	    if (databaseConfiguratorClassName != null && !databaseConfiguratorClassName.isEmpty()) {
		databaseConfiguratorClassNameMap.put(database, databaseConfiguratorClassName);
	    }

	    // Set the firewall class names to use for this database
	    String sqlFirewallClassNameArray = TomcatStarterUtil.trimSafe(
		    properties.getProperty(database + "." + ServerSqlManager.SQL_FIREWALL_MANAGER_CLASS_NAMES));

	    if (sqlFirewallClassNameArray != null && !sqlFirewallClassNameArray.isEmpty()) {
		List<String> sqlFirewallClassNamesList = TomcatStarterUtilProperties.getList(sqlFirewallClassNameArray);
		Set<String> sqlFirewallClassNames = new LinkedHashSet<>(sqlFirewallClassNamesList);
		sqlFirewallClassNamesMap.put(database, sqlFirewallClassNames);
	    } else {
		sqlFirewallClassNamesMap.put(database, new LinkedHashSet<String>());
	    }
	    
	    String sqlFirewallTriggerClassNameArray = TomcatStarterUtil.trimSafe(
		    properties.getProperty(database + "." + ServerSqlManager.SQL_FIREWALL_TRIGGER_CLASS_NAMES));

	    if (sqlFirewallTriggerClassNameArray != null && !sqlFirewallTriggerClassNameArray.isEmpty()) {
		List<String> sqlFirewallTriggerClassNamesList = TomcatStarterUtilProperties
			.getList(sqlFirewallTriggerClassNameArray);
		Set<String> sqlFirewallTriggerClassNames = new LinkedHashSet<>(sqlFirewallTriggerClassNamesList);
		sqlFirewallTriggerClassNamesMap.put(database, sqlFirewallTriggerClassNames);
	    } else {
		sqlFirewallTriggerClassNamesMap.put(database, new LinkedHashSet<String>());
	    }
	    
	    String updateListenerClassNameArray = TomcatStarterUtil.trimSafe(
		    properties.getProperty(database + "." + ServerSqlManager.UPDATE_LISTENER_MANAGER_CLASS_NAMES));

	    if (updateListenerClassNameArray != null && !updateListenerClassNameArray.isEmpty()) {
		List<String> updateListenerClassNamesList = TomcatStarterUtilProperties
			.getList(updateListenerClassNameArray);
		
		Set<String> updateListenerClassNames = new LinkedHashSet<>(updateListenerClassNamesList);
		updateListenerClassNamesMap.put(database, updateListenerClassNames);
	    } else {
		updateListenerClassNamesMap.put(database, new LinkedHashSet<String>());
	    }
	    
	    String operationalMode = TomcatStarterUtil.trimSafe(
		    properties.getProperty(database + "." + ConfPropertiesUtil.OPERATIONAL_MODE));
	    if (operationalMode == null || operationalMode.isEmpty()) {
		operationalMode = OperationalMode.protecting.toString();
	    }
	    
	    try {
		OperationalMode operationalModeEnum
		  = OperationalMode.valueOf(operationalMode);
		operationalModeMap.put(database, operationalModeEnum);
	    } catch (IllegalArgumentException e) {
		throw new IllegalArgumentException(SqlTag.USER_CONFIGURATION + 
			" the " + database + ".operationalMode property value is invalid: "+ operationalMode + ".  Please correct. " );
	    }
	    
	}
    }

}
