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

package org.kawanfw.sql.servlet.injection.classes;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.ServletConfig;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.api.server.listener.UpdateListener;
import org.kawanfw.sql.servlet.injection.classes.InjectedClasses.InjectedClassesBuilder;
import org.kawanfw.sql.servlet.injection.classes.creator.BlobDownloadConfiguratorCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.BlobUploadConfiguratorCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.DatabaseConfiguratorCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.SessionConfiguratorCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.SqlFirewallsCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.UpdateListenersCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.UserAuthenticatorCreator;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.tomcat.ThreadPoolExecutorCreator;
import org.kawanfw.sql.tomcat.TomcatSqlModeStore;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.util.Tag;
import org.kawanfw.sql.version.VersionWrapper;

public class InjectedClassesManagerNew {

    private static boolean DEBUG = FrameworkDebug.isSet(InjectedClassesManagerNew.class);
    public static String CR_LF = System.getProperty("line.separator");

    /** The Exception thrown at init */
    private Exception exception = null;

    /** The init error message trapped */
    private String initErrrorMesage = null;

    private String classNameToLoad;

    private ServletConfig config;

    /**
     * Constructor.
     *
     * @param config
     */
    public InjectedClassesManagerNew(ServletConfig config) {
	this.config = config;
    }

    /**
     * Created all injected classes instances.
     */
    public void createClasses() {
	classNameToLoad = null;
	try {
	    // Test if we are in Native Tomcat and do specific stuff.
	    if (!TomcatSqlModeStore.isTomcatEmbedded()) {
		NativeTomcatElementsCreator nativeTomcatElementsCreator = new NativeTomcatElementsCreator(config);
		nativeTomcatElementsCreator.create();
	    }

	    File propertiesFile = PropertiesFileStore.get();
	    Properties properties = PropertiesFileUtil.getProperties(propertiesFile);
	    ThreadPoolExecutorCreator threadPoolExecutorCreator = new ThreadPoolExecutorCreator(properties);
	    ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorCreator.create();

	    Set<String> databases = ConfPropertiesStore.get().getDatabaseNames();

	    // Create out InjectedClasses builder
	    InjectedClassesBuilder injectedClassesBuilder = new InjectedClassesBuilder();

	    // Load all the classes and set our InjectedClassesBuilder instance
	    injectedClassesBuilder.threadPoolExecutor(threadPoolExecutor);

	    loadUserAuthenticator(injectedClassesBuilder);
	    loadRequestHeadersAuthenticator(injectedClassesBuilder);

	    Map<String, DatabaseConfigurator> databaseConfigurators = new HashMap<>();
	    Map<String, List<SqlFirewallManager>> sqlFirewallMap = new HashMap<>();
	    Map<String, List<UpdateListener>> updateListenerMap = new HashMap<>();
	    
	    for (String database : databases) {
		DatabaseConfigurator databaseConfigurator = loadDatabaseConfigurators(database);
		List<SqlFirewallManager> sqlFirewalManagers = loadSqlFirewallManagers(database, databaseConfigurator);
		List<UpdateListener> updateListeners = loadUpdateListeners(database, injectedClassesBuilder);
		
		databaseConfigurators.put (database, databaseConfigurator);
		sqlFirewallMap.put (database, sqlFirewalManagers);
		updateListenerMap.put (database, updateListeners);
	    }
	    
	    injectedClassesBuilder.databaseConfigurators(databaseConfigurators);
	    injectedClassesBuilder.sqlFirewallMap(sqlFirewallMap);
	    injectedClassesBuilder.updateListenerMap(updateListenerMap);
	    
	    loadBlobDownloadConfigurator(injectedClassesBuilder);
	    loadBlobUploadConfigurator(injectedClassesBuilder);
	    loadSessionManagerConfigurator(injectedClassesBuilder);

	    // Create the InjectedClasses instance
	    InjectedClasses injectedClasses = injectedClassesBuilder.build();

	    // Store the InjectedClasses instance statically
	    InjectedClassesStore.set(injectedClasses);

	} catch (ClassNotFoundException e) {
	    initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
		    + " Impossible to load (ClassNotFoundException) Configurator class: " + classNameToLoad;
	    exception = e;
	} catch (InstantiationException e) {
	    initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
		    + " Impossible to load (InstantiationException) Configurator class: " + classNameToLoad;
	    exception = e;
	} catch (IllegalAccessException e) {
	    initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
		    + " Impossible to load (IllegalAccessException) Configurator class: " + classNameToLoad;
	    exception = e;
	} catch (DatabaseConfigurationException e) {
	    initErrrorMesage = e.getMessage();
	    exception = e;
	} catch (Exception e) {
	    initErrrorMesage = Tag.PRODUCT_PRODUCT_FAIL + " Please contact support at: support@kawansoft.com";
	    exception = e;
	}

	treatException();
    }

    /**
     *
     */
    private void treatException() {
	if (exception == null) {
	    System.out.println(SqlTag.SQL_PRODUCT_START + " Loaded classes Status: OK.");

	    if (!TomcatSqlModeStore.isTomcatEmbedded()) {
		String runningMessage = SqlTag.SQL_PRODUCT_START + " " + VersionWrapper.getName() + " Start OK.";
		System.out.println(runningMessage);
	    }

	} else {
	    exception.printStackTrace();
	    if (!TomcatSqlModeStore.isTomcatEmbedded()) {
		String errorMessage1 = SqlTag.SQL_PRODUCT_START + "  -> Loaded classes Status: KO.";
		String errorMessage2 = initErrrorMesage;
		String errorMessage3 = ExceptionUtils.getStackTrace(exception);

		System.out.println(errorMessage1);
		System.out.println(errorMessage2);
		System.out.println(errorMessage3);

		System.out.println();
	    }
	}
    }

    /**
     * Loads Session Manager Configurator.
     * 
     * @param injectedClassesBuilder
     *
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    private void loadSessionManagerConfigurator(InjectedClassesBuilder injectedClassesBuilder)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException {
	// Load Configurators for SessionManager
	String sessionManagerConfiguratorClassName = ConfPropertiesStore.get().getSessionConfiguratorClassName();
	classNameToLoad = sessionManagerConfiguratorClassName;
	SessionConfiguratorCreator sessionConfiguratorCreator = new SessionConfiguratorCreator(
		sessionManagerConfiguratorClassName);
	injectedClassesBuilder.sessionConfigurator(sessionConfiguratorCreator.getSessionConfigurator());
	sessionManagerConfiguratorClassName = sessionConfiguratorCreator.getSessionConfiguratorClassName();

	if (!sessionManagerConfiguratorClassName
		.equals(org.kawanfw.sql.api.server.session.DefaultSessionConfigurator.class.getName())) {
	    System.out.println(SqlTag.SQL_PRODUCT_START + " Loading sessionManagerConfiguratorClassName: ");
	    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + sessionManagerConfiguratorClassName);
	}
    }

    /**
     * Loads Blob upload configurator.
     * 
     * @param injectedClassesBuilder
     *
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    private void loadBlobUploadConfigurator(InjectedClassesBuilder injectedClassesBuilder)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException {
	String blobUploadConfiguratorClassName = ConfPropertiesStore.get().getBlobUploadConfiguratorClassName();
	classNameToLoad = blobUploadConfiguratorClassName;
	BlobUploadConfiguratorCreator blobUploadConfiguratorCreator = new BlobUploadConfiguratorCreator(
		blobUploadConfiguratorClassName);
	injectedClassesBuilder.blobUploadConfigurator(blobUploadConfiguratorCreator.getBlobUploadConfigurator());
	blobUploadConfiguratorClassName = blobUploadConfiguratorCreator.getBlobUploadConfiguratorClassName();

	if (!blobUploadConfiguratorClassName
		.equals(org.kawanfw.sql.api.server.blob.DefaultBlobUploadConfigurator.class.getName())) {
	    System.out.println(SqlTag.SQL_PRODUCT_START + " Loading blobUploadConfiguratorClassName: ");
	    System.out.println(SqlTag.SQL_PRODUCT_START + " " + blobUploadConfiguratorClassName);
	}
    }

    /**
     * Loads Blob download configurator.
     * 
     * @param injectedClassesBuilder
     *
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    private void loadBlobDownloadConfigurator(InjectedClassesBuilder injectedClassesBuilder)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException {
	// Load Configurators for Blobs/Clobs
	String blobDownloadConfiguratorClassName = ConfPropertiesStore.get().getBlobDownloadConfiguratorClassName();
	classNameToLoad = blobDownloadConfiguratorClassName;
	BlobDownloadConfiguratorCreator blobDownloadConfiguratorCreator = new BlobDownloadConfiguratorCreator(
		blobDownloadConfiguratorClassName);
	injectedClassesBuilder.blobDownloadConfigurator(blobDownloadConfiguratorCreator.getBlobDownloadConfigurator());
	blobDownloadConfiguratorClassName = blobDownloadConfiguratorCreator.getBlobDownloadConfiguratorClassName();

	if (!blobDownloadConfiguratorClassName
		.equals(org.kawanfw.sql.api.server.blob.DefaultBlobDownloadConfigurator.class.getName())) {
	    System.out.println(SqlTag.SQL_PRODUCT_START + " Loading blobDownloadConfiguratorClassName: ");
	    System.out.println(SqlTag.SQL_PRODUCT_START + " " + blobDownloadConfiguratorClassName);
	}
    }

    /**
     * loads userAuthenticator.
     * 
     * @param injectedClassesBuilder
     *
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private void loadUserAuthenticator(InjectedClassesBuilder injectedClassesBuilder)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	String userAuthenticatorClassName = ConfPropertiesStore.get().getUserAuthenticatorClassName();

	classNameToLoad = userAuthenticatorClassName;
	UserAuthenticatorCreator userAuthenticatorCreator = new UserAuthenticatorCreator(userAuthenticatorClassName);
	injectedClassesBuilder.userAuthenticator(userAuthenticatorCreator.getUserAuthenticator());
	userAuthenticatorClassName = userAuthenticatorCreator.getUserAuthenticatorClassName();

	System.out.println(SqlTag.SQL_PRODUCT_START + " Loading UserAuthenticator class:");
	System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + userAuthenticatorClassName);
    }

    /**
     * loads requestHeadersAuthenticator.
     * 
     * @param injectedClassesBuilder TODO
     *
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException 
     */
    private void loadRequestHeadersAuthenticator(InjectedClassesBuilder injectedClassesBuilder)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {

	String requestHeadersAuthenticatorClassName = ConfPropertiesStore.get()
		.getRequestHeadersAuthenticatorClassName();

	classNameToLoad = requestHeadersAuthenticatorClassName;
	
	RequestHeadersAuthenticatorLoader requestHeadersAuthenticatorLoader = RequestHeadersAuthenticatorLoaderCreator.createInstance();
	requestHeadersAuthenticatorLoader.loadRequestHeadersAuthenticator(injectedClassesBuilder, requestHeadersAuthenticatorClassName);
	
    }

    /**
     * loads the Firewall Managers.
     * 
     * @param database
     * @param databaseConfigurator
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     * @throws IOException
     */
    private List<SqlFirewallManager>  loadSqlFirewallManagers(String database, DatabaseConfigurator databaseConfigurator)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, IOException {

	List<String> sqlFirewallClassNames = ConfPropertiesStore.get().getSqlFirewallClassNames(database);
	classNameToLoad = sqlFirewallClassNames.toString();

	String tagSQLFirewallManager = null;
	if (sqlFirewallClassNames.size() < 2)
	    tagSQLFirewallManager = " SQLFirewallManager class: ";
	else
	    tagSQLFirewallManager = " SQLFirewallManager classes: ";

	System.out.println(SqlTag.SQL_PRODUCT_START + " Loading Database " + database + tagSQLFirewallManager);

	SqlFirewallsCreator sqlFirewallsCreator = new SqlFirewallsCreator(sqlFirewallClassNames, database,
		databaseConfigurator);
	List<SqlFirewallManager> sqlFirewallManagers = sqlFirewallsCreator.getSqlFirewalls();

	sqlFirewallClassNames = sqlFirewallsCreator.getSqlFirewallClassNames();
	classNameToLoad = sqlFirewallClassNames.toString();

	for (String sqlFirewallClassName : sqlFirewallClassNames) {
	    System.out.println(SqlTag.SQL_PRODUCT_START + "   -> " + sqlFirewallClassName);
	}
	
	return sqlFirewallManagers;

    }

    /**
     * Loads the Update Listeners.
     * @param database
     * @param injectedClassesBuilder
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     * @throws IOException
     */
    private List<UpdateListener> loadUpdateListeners(String database, InjectedClassesBuilder injectedClassesBuilder)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, IOException {


	List<String> updateListenerClassNames = ConfPropertiesStore.get().getUpdateListenerClassNames(database);
	classNameToLoad = updateListenerClassNames.toString();

	String tagUpdateListener = null;
	if (updateListenerClassNames.size() < 2)
	    tagUpdateListener = " UpdateListener class: ";
	else
	    tagUpdateListener = " UpdateListener classes: ";

	System.out.println(SqlTag.SQL_PRODUCT_START + " Loading Database " + database + tagUpdateListener);

	Map<String, DatabaseConfigurator> databaseConfigurators = injectedClassesBuilder.getDatabaseConfigurators();

	DatabaseConfigurator databaseConfigurator = databaseConfigurators.get(database);
	UpdateListenersCreator updateListenersCreator = new UpdateListenersCreator(updateListenerClassNames, database,
		databaseConfigurator);
	List<UpdateListener> updateListeners = updateListenersCreator.getUpdateListeners();

	updateListenerClassNames = updateListenersCreator.getUpdateListenerClassNames();
	classNameToLoad = updateListenerClassNames.toString();

	for (String updateListenerClassName : updateListenerClassNames) {
	    System.out.println(SqlTag.SQL_PRODUCT_START + "   -> " + updateListenerClassName);
	}

	return updateListeners;
    }

    /**
     * Loads the database configurators.
     * @param database
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    private DatabaseConfigurator loadDatabaseConfigurators(String database)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException {
	String databaseConfiguratorClassName;

	// WARNING: Database configurator must be loaded prior to firewalls
	// because a getConnection() is used to test SqlFirewallManager

	databaseConfiguratorClassName = ConfPropertiesStore.get().getDatabaseConfiguratorClassName(database);

	debug("databaseConfiguratorClassName    : " + databaseConfiguratorClassName);

	// Check spelling with first letter capitalized
	// HACK NDP
	// TODO LATER
	// if (databaseConfiguratorClassName == null ||
	// databaseConfiguratorClassName.isEmpty()) {
	// String capitalized =
	// StringUtils.capitalize(ServerSqlManager.DATABASE_CONFIGURATOR_CLASS_NAME);
	// databaseConfiguratorClassName =
	// ServletParametersStore.getInitParameter(database, capitalized);
	// }

	// Call the specific DatabaseConfigurator class to use
	classNameToLoad = databaseConfiguratorClassName;
	DatabaseConfiguratorCreator databaseConfiguratorCreator = new DatabaseConfiguratorCreator(
		databaseConfiguratorClassName);
	DatabaseConfigurator databaseConfigurator = databaseConfiguratorCreator.getDatabaseConfigurator();
	databaseConfiguratorClassName = databaseConfiguratorCreator.getDatabaseConfiguratorClassName();

	System.out.println(SqlTag.SQL_PRODUCT_START + " Loading Database " + database + " DatabaseConfigurator class:");
	System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + databaseConfiguratorClassName);

	return databaseConfigurator;
    }

    public Exception getException() {
	return exception;
    }

    public String getInitErrrorMesage() {
	return initErrrorMesage;
    }

    /**
     * Method called by children Servlet for debug purpose Println is done only if
     * class name name is in kawansoft-debug.ini
     */
    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
