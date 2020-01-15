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
package org.kawanfw.sql.tomcat;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Class to store statically the parameter names & values for the Server SQL
 * Manager Database
 *
 * @author Nicolas de Pomereu
 *
 */
public class ServletParametersStore {

    /** The database names */
    private static Set<String> databaseSet = null;

    /** The (Database name, DataSource) Map */
    private static Map<String, Set<InitParamNameValuePair>> initParameterseMap = new ConcurrentHashMap<String, Set<InitParamNameValuePair>>();

    private static String servletName = null;

    private static String blobDownloadConfiguratorClassName = null;
    private static String blobUploadConfiguratorClassName = null;

    private static String sessionConfiguratorClassName = null;
    private static String jwtSessionConfiguratorSecretValue = null;
    private static Set<String> userServlets = new HashSet<>();

    private static Map<String, List<String>> sqlFirewallClassNamesMap = new ConcurrentHashMap<>();

    /**
     * no instantiation
     */
    private ServletParametersStore() {

    }

    public static void init() {
	databaseSet = null;
	initParameterseMap = new ConcurrentHashMap<>();
	servletName = null;
	blobDownloadConfiguratorClassName = null;
	blobUploadConfiguratorClassName = null;
	sessionConfiguratorClassName = null;
	jwtSessionConfiguratorSecretValue = null;
	userServlets = new HashSet<>();

    }

    /**
     * Gets the servlet name that will be the /servlet name starting path
     *
     * @return
     */
    public static String getServletName() {
	return servletName;
    }

    /**
     * Sets the servlet name that will the /servlet name starting path
     *
     * @param servletName
     */
    public static void setServletName(String servletName) {
	ServletParametersStore.servletName = servletName;
    }

    /**
     * Stores an init parameter name and value for a specified database.
     *
     * @param database               the database to store the parameter name and
     *                               value for
     * @param InitParamNameValuePair the init paramater name and value
     */
    public static void setInitParameter(String database, InitParamNameValuePair initParamNameValuePair) {
	Set<InitParamNameValuePair> initParamNameValuePairSet = initParameterseMap.get(database);

	if (initParamNameValuePairSet == null) {
	    initParamNameValuePairSet = new TreeSet<InitParamNameValuePair>();
	}

	initParamNameValuePairSet.add(initParamNameValuePair);
	initParameterseMap.put(database, initParamNameValuePairSet);

    }

    /**
     * Returns an init paramater value for a (servlet name, init parameter couple)
     *
     * @param database      the database name to return the init parameter for
     * @param initParamName the init parameter name
     * @return the init parameter value
     */
    public static String getInitParameter(String database, String initParamName) {
	Set<InitParamNameValuePair> initParamNameValuePairSet = initParameterseMap.get(database);

	if (initParamNameValuePairSet == null) {
	    return null;
	}

	for (InitParamNameValuePair initParamNameValuePair : initParamNameValuePairSet) {
	    if (initParamNameValuePair.getName().equals(initParamName)) {
		return initParamNameValuePair.getValue();
	    }
	}

	return null;

    }

    /**
     * Store the database names
     *
     * @param databases the databases to store
     */
    public static void setDatabaseNames(Set<String> databases) {
	databaseSet = databases;
    }

    /**
     * Returns the Set of database names
     *
     * @return
     */
    public static Set<String> getDatabaseNames() {
	return databaseSet;
    }

    /**
     * Returns the BlobDownloadConfigurator class name
     *
     * @return the BlobDownloadConfigurator class name
     */
    public static String getBlobDownloadConfiguratorClassName() {
	return blobDownloadConfiguratorClassName;
    }

    /**
     * Sets the BlobDownloadConfigurator class name
     *
     * @param blobDownloadConfiguratorClassName BlobDownloadConfigurator class name
     */
    public static void setBlobDownloadConfiguratorClassName(String blobDownloadConfiguratorClassName) {
	ServletParametersStore.blobDownloadConfiguratorClassName = blobDownloadConfiguratorClassName;
    }

    /**
     * Returns the BlobUploadConfigurator class name
     *
     * @return the BlobUploadConfigurator class name
     */
    public static String getBlobUploadConfiguratorClassName() {
	return blobUploadConfiguratorClassName;
    }

    /**
     * Sets the BlobUploadConfigurator class name
     *
     * @param blobUploadConfiguratorClassName BlobUploadConfigurator class name
     */
    public static void setBlobUploadConfiguratorClassName(String blobUploadConfiguratorClassName) {
	ServletParametersStore.blobUploadConfiguratorClassName = blobUploadConfiguratorClassName;
    }

    /**
     * Sets the SessionConfigurator class name
     *
     * @param SessionConfigurator class name
     */
    public static void setSessionConfiguratorClassName(String sessionConfiguratorClassName) {
	ServletParametersStore.sessionConfiguratorClassName = sessionConfiguratorClassName;
    }

    /**
     * @return the SessionConfigurator class name
     */
    public static String getSessionConfiguratorClassName() {
	return sessionConfiguratorClassName;
    }

    /**
     *
     * @param jwtSessionConfiguratorSecretValue
     */
    public static void setJwtSessionConfiguratorSecretValue(String jwtSessionConfiguratorSecretValue) {
	ServletParametersStore.jwtSessionConfiguratorSecretValue = jwtSessionConfiguratorSecretValue;

    }

    /**
     * @return the jwtSessionConfiguratorSecretValue
     */
    public static String getJwtSessionConfiguratorSecretValue() {
	return jwtSessionConfiguratorSecretValue;
    }

    /**
     * @return the userServlets
     */
    public static Set<String> getUserServlets() {
	return userServlets;
    }

    /**
     * @param userServlets the userServlets to set
     */
    public static void setUserServlets(Set<String> theUserServlets) {
	ServletParametersStore.userServlets = theUserServlets;
    }

    public static List<String> getSqlFirewallClassNames(String database) {
	return sqlFirewallClassNamesMap.get(database);
    }

    public static void setSqlFirewallClassNames(String database, List<String> sqlFirewallClassNames) {
	sqlFirewallClassNamesMap.put(database, sqlFirewallClassNames);
    }


}
