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

import java.util.HashSet;
import java.util.List;
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

    private Map<String, List<String>> sqlFirewallClassNamesMap = new ConcurrentHashMap<>();
    private boolean statelessMode;
    
    private Map<String, List<String>> updateListenerClassNamesMap = new ConcurrentHashMap<>();

    
    private ConfProperties(ConfPropertiesBuilder confPropertiesBuilder) {
	this.databaseSet = confPropertiesBuilder.databaseSet;
	this.databaseConfiguratorClassNameMap = confPropertiesBuilder.databaseConfiguratorClassNameMap;

	this.servletCallName = confPropertiesBuilder.servletCallName;

	this.blobDownloadConfiguratorClassName = confPropertiesBuilder.blobDownloadConfiguratorClassName;
	this.blobUploadConfiguratorClassName = confPropertiesBuilder.blobUploadConfiguratorClassName;

	this.userAuthenticatorClassName = confPropertiesBuilder.userAuthenticatorClassName;
	this.requestHeadersAuthenticatorClassName = confPropertiesBuilder.requestHeadersAuthenticatorClassName;

	this.sessionConfiguratorClassName = confPropertiesBuilder.sessionConfiguratorClassName;
	this.jwtSessionConfiguratorSecretValue = confPropertiesBuilder.jwtSessionConfiguratorSecretValue;
	this.userServlets = confPropertiesBuilder.userServlets;

	this.sqlFirewallClassNamesMap = confPropertiesBuilder.sqlFirewallClassNamesMap;
	this.statelessMode = confPropertiesBuilder.statelessMode;
	
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
     * @return the sqlFirewallClassNamesMap
     */
    @SuppressWarnings("unused")
    private Map<String, List<String>> getSqlFirewallClassNamesMap() {
	return sqlFirewallClassNamesMap;
    }

    public List<String> getSqlFirewallClassNames(String database) {
	return sqlFirewallClassNamesMap.get(database);
    }
    
    /**
     * @return the statelessMode
     */
    public boolean isStatelessMode() {
	return statelessMode;
    }

    
    /**
     * @return the updateListenerClassNamesMap
     */
    @SuppressWarnings("unused")
    private Map<String, List<String>> getUpdateListenerClassNamesMap() {
        return updateListenerClassNamesMap;
    }

    public List<String> getUpdateListenerClassNames(String database) {
	return updateListenerClassNamesMap.get(database);
    }

    public static class ConfPropertiesBuilder {
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

	private Map<String, List<String>> sqlFirewallClassNamesMap = new ConcurrentHashMap<>();
	private boolean statelessMode;
	private Map<String, List<String>> updateListenerClassNamesMap = new ConcurrentHashMap<>();
	    
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

	public ConfPropertiesBuilder sqlFirewallClassNamesMap(Map<String, List<String>> sqlFirewallClassNamesMap) {
	    this.sqlFirewallClassNamesMap = sqlFirewallClassNamesMap;
	    return this;
	}

	public ConfPropertiesBuilder statelessMode(boolean statelessMode) {
	    this.statelessMode = statelessMode;
	    return this;
	}

	public ConfPropertiesBuilder updateListenerClassNamesMap(Map<String, List<String>> updateListenerClassNamesMap) {
	    this.updateListenerClassNamesMap = updateListenerClassNamesMap;
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
	    // HACK NDP 
	    // TODO LATER
	    // Do some basic validations to check
	    // if user object does not break any assumption of system
	}
    }

}
