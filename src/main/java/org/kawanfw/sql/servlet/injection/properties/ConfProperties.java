/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.injection.properties;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * All decoded Properties ready to use.
 * @author Nicolas de Pomereu
 *
 */

public class ConfProperties {

    /** The database names */
    private Set<String> databaseSet = null;

    /** The (Database name, databaseConfiguratorClassName) Map */
    private Map<String, String> databaseConfiguratorClassNameMap = new ConcurrentHashMap<>();

    private String servletCallName = null;

    private String blobDownloadConfiguratorClassName = null;
    private String blobUploadConfiguratorClassName = null;

    private String userAuthenticatorClassName = null;
    private String requestHeadersAuthenticatorClassName = null;

    private String sessionConfiguratorClassName = null;
    private String jwtSessionConfiguratorSecretValue = null;
    private Set<String> userServlets = new HashSet<>();

    private Map<String, Set<String>> sqlFirewallManagerClassNamesMap = new ConcurrentHashMap<>();
    private boolean statelessMode;
    
    private Map<String, Set<String>> sqlFirewallTriggerClassNamesMap = new ConcurrentHashMap<>(); 
    private Map<String, Set<String>> updateListenerClassNamesMap = new ConcurrentHashMap<>();

    private String loggerCreatorClassName = null;
    private boolean displayLoggerElementsAtStartup;

    
    private ConfProperties(ConfPropertiesBuilder confPropertiesBuilder) {
	this.databaseSet = confPropertiesBuilder.databaseSet;
	this.databaseConfiguratorClassNameMap = confPropertiesBuilder.databaseConfiguratorClassNameMap;

	this.servletCallName = confPropertiesBuilder.servletCallName;
	this.loggerCreatorClassName = confPropertiesBuilder.loggerCreatorClassName;
	this.displayLoggerElementsAtStartup = confPropertiesBuilder.displayLoggerElementsAtStartup;
	
	this.blobDownloadConfiguratorClassName = confPropertiesBuilder.blobDownloadConfiguratorClassName;
	this.blobUploadConfiguratorClassName = confPropertiesBuilder.blobUploadConfiguratorClassName;

	this.userAuthenticatorClassName = confPropertiesBuilder.userAuthenticatorClassName;
	this.requestHeadersAuthenticatorClassName = confPropertiesBuilder.requestHeadersAuthenticatorClassName;

	this.sessionConfiguratorClassName = confPropertiesBuilder.sessionConfiguratorClassName;
	this.jwtSessionConfiguratorSecretValue = confPropertiesBuilder.jwtSessionConfiguratorSecretValue;
	this.userServlets = confPropertiesBuilder.userServlets;

	this.sqlFirewallManagerClassNamesMap = confPropertiesBuilder.sqlFirewallManagerClassNamesMap;
	this.statelessMode = confPropertiesBuilder.statelessMode;
	
	this.sqlFirewallTriggerClassNamesMap = confPropertiesBuilder.sqlFirewallTriggerClassNamesMap;
	
	this.updateListenerClassNamesMap = confPropertiesBuilder.updateListenerClassNamesMap;
    }

    /**
     * @return the databaseSet
     */
    public Set<String> getDatabaseNames() {
	return databaseSet;
    }

    /**
     * @return the databaseConfiguratorClassNameMap
     */
    @SuppressWarnings("unused")
    private Map<String, String> getDatabaseConfiguratorClassNameMap() {
	return databaseConfiguratorClassNameMap;
    }
    
    /**
     * Returns the Database configurator class name for the specified database
     * @param database
     * @return the Database configurator class name for the specified database
     */
    public String getDatabaseConfiguratorClassName(String database) {
	return databaseConfiguratorClassNameMap.get(database);
    }

 
    /**
     * @return the servletCallName
     */
    public String getServletCallName() {
	return servletCallName;
    }

    /**
     * @return the loggerCreatorClassName
     */
    public String getLoggerCreatorClassName() {
        return loggerCreatorClassName;
    }

    
    /**
     * @return the displayLoggerElementsAtStartup
     */
    public boolean isDisplayLoggerElementsAtStartup() {
        return displayLoggerElementsAtStartup;
    }

    /**
     * @return the blobDownloadConfiguratorClassName
     */
    public String getBlobDownloadConfiguratorClassName() {
	return blobDownloadConfiguratorClassName;
    }

    /**
     * @return the blobUploadConfiguratorClassName
     */
    public String getBlobUploadConfiguratorClassName() {
	return blobUploadConfiguratorClassName;
    }

    /**
     * @return the userAuthenticatorClassName
     */
    public String getUserAuthenticatorClassName() {
	return userAuthenticatorClassName;
    }

    /**
     * @return the requestHeadersAuthenticatorClassName
     */
    public String getRequestHeadersAuthenticatorClassName() {
	return requestHeadersAuthenticatorClassName;
    }

    /**
     * @return the sessionConfiguratorClassName
     */
    public String getSessionConfiguratorClassName() {
	return sessionConfiguratorClassName;
    }

    /**
     * @return the jwtSessionConfiguratorSecretValue
     */
    public String getJwtSessionConfiguratorSecretValue() {
	return jwtSessionConfiguratorSecretValue;
    }

    /**
     * @return the userServlets
     */
    public Set<String> getUserServlets() {
	return userServlets;
    }

    /**
     * @return the sqlFirewallManagerClassNamesMap
     */
    @SuppressWarnings("unused")
    private Map<String, Set<String>> getSqlFirewallManagerClassNamesMap() {
	return sqlFirewallManagerClassNamesMap;
    }

    public Set<String> getSqlFirewallManagerClassNames(String database) {
	return sqlFirewallManagerClassNamesMap.get(database);
    }
    
    
    public Set<String> getSqlFirewallTriggerClassNames(String database) {
	return sqlFirewallTriggerClassNamesMap.get(database);
    }
    
    /**
     * @return the statelessMode
     */
    public boolean isStatelessMode() {
	return statelessMode;
    }

 
    /**
     * @return the sqlFirewallTriggerClassNamesMap
     */
    @SuppressWarnings("unused")
    private Map<String, Set<String>> getSqlFirewallTriggerClassNamesMap() {
        return sqlFirewallTriggerClassNamesMap;
    }

    /**
     * @return the updateListenerClassNamesMap
     */
    @SuppressWarnings("unused")
    private Map<String, Set<String>> getUpdateListenerClassNamesMap() {
        return updateListenerClassNamesMap;
    }

    public Set<String> getUpdateListenerClassNames(String database) {
	return updateListenerClassNamesMap.get(database);
    }

    public static class ConfPropertiesBuilder {
	/** The database names */
	private Set<String> databaseSet = null;

	/** The (Database name, databaseConfiguratorClassName) Map */
	private Map<String, String> databaseConfiguratorClassNameMap = new ConcurrentHashMap<>();

	private String servletCallName = null;
	private String loggerCreatorClassName;
	private boolean displayLoggerElementsAtStartup;
	
	private String blobDownloadConfiguratorClassName = null;
	private String blobUploadConfiguratorClassName = null;

	private String userAuthenticatorClassName = null;
	private String requestHeadersAuthenticatorClassName = null;

	private String sessionConfiguratorClassName = null;
	private String jwtSessionConfiguratorSecretValue = null;
	private Set<String> userServlets = new HashSet<>();

	private Map<String, Set<String>> sqlFirewallManagerClassNamesMap = new ConcurrentHashMap<>();
	private boolean statelessMode;
	
	private Map<String, Set<String>> sqlFirewallTriggerClassNamesMap = new ConcurrentHashMap<>(); 
	private Map<String, Set<String>> updateListenerClassNamesMap = new ConcurrentHashMap<>();


	public ConfPropertiesBuilder databaseSet(Set<String> databaseSet) {
	    this.databaseSet = databaseSet;
	    return this;
	}

	public ConfPropertiesBuilder databaseConfiguratorClassNameMap(
		Map<String, String> databaseConfiguratorClassNameMap) {
	    this.databaseConfiguratorClassNameMap = databaseConfiguratorClassNameMap;
	    return this;
	}

	public ConfPropertiesBuilder servletCallName(String servletCallName) {
	    this.servletCallName = servletCallName;
	    return this;
	}
	
	public ConfPropertiesBuilder loggerCreatorClassName(String loggerCreatorClassName) {
	    this.loggerCreatorClassName = loggerCreatorClassName;
	    return this;
	}
	
	public ConfPropertiesBuilder displayLoggerElementsAtStartup(boolean displayLoggerElementsAtStartup) {
	    this.displayLoggerElementsAtStartup = displayLoggerElementsAtStartup;
	    return this;
	}

	public ConfPropertiesBuilder blobDownloadConfiguratorClassName(String blobDownloadConfiguratorClassName) {
	    this.blobDownloadConfiguratorClassName = blobDownloadConfiguratorClassName;
	    return this;
	}

	public ConfPropertiesBuilder blobUploadConfiguratorClassName(String blobUploadConfiguratorClassName) {
	    this.blobUploadConfiguratorClassName = blobUploadConfiguratorClassName;
	    return this;
	}

	public ConfPropertiesBuilder userAuthenticatorClassName(String userAuthenticatorClassName) {
	    this.userAuthenticatorClassName = userAuthenticatorClassName;
	    return this;
	}

	public ConfPropertiesBuilder requestHeadersAuthenticatorClassName(String requestHeadersAuthenticatorClassName) {
	    this.requestHeadersAuthenticatorClassName = requestHeadersAuthenticatorClassName;
	    return this;
	}

	public ConfPropertiesBuilder sessionConfiguratorClassName(String sessionConfiguratorClassName) {
	    this.sessionConfiguratorClassName = sessionConfiguratorClassName;
	    return this;
	}

	public ConfPropertiesBuilder jwtSessionConfiguratorSecretValue(String jwtSessionConfiguratorSecretValue) {
	    this.jwtSessionConfiguratorSecretValue = jwtSessionConfiguratorSecretValue;
	    return this;
	}

	public ConfPropertiesBuilder userServlets(Set<String> userServlets) {
	    this.userServlets = userServlets;
	    return this;
	}

	public ConfPropertiesBuilder sqlFirewallManagerClassNamesMap(Map<String, Set<String>> sqlFirewallManagerClassNamesMap) {
	    this.sqlFirewallManagerClassNamesMap = sqlFirewallManagerClassNamesMap;
	    return this;
	}
	

	public ConfPropertiesBuilder sqlFirewallTriggerClassNamesMap(Map<String, Set<String>> sqlFirewallTriggerClassNamesMap) {
	    this.sqlFirewallTriggerClassNamesMap = sqlFirewallTriggerClassNamesMap;
	    return this;
	}

	public ConfPropertiesBuilder updateListenerClassNamesMap(Map<String, Set<String>> updateListenerClassNamesMap) {
	    this.updateListenerClassNamesMap = updateListenerClassNamesMap;
	    return this;
	}
	
	public ConfPropertiesBuilder statelessMode(boolean statelessMode) {
	    this.statelessMode = statelessMode;
	    return this;
	}

	
	// Return the finally constructed User object
	public ConfProperties build() {
	    ConfProperties confProperties = new ConfProperties(this);
	    //validateUserObject(confProperties);
	    return confProperties;
	}

	@SuppressWarnings("unused")
	private void validateUserObject(ConfProperties confProperties) {
	    // FUTURE USAGE
	    // Do some basic validations to check
	    // if user object does not break any assumption of system
	}





    }

}
