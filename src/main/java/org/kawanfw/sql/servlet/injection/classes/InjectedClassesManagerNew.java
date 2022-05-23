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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.ServletException;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.api.server.firewall.trigger.SqlFirewallTrigger;
import org.kawanfw.sql.api.server.listener.UpdateListener;
import org.kawanfw.sql.api.server.session.JwtSessionConfigurator;
import org.kawanfw.sql.servlet.AceQLLicenseFileLoader;
import org.kawanfw.sql.servlet.injection.classes.InjectedClasses.InjectedClassesBuilder;
import org.kawanfw.sql.servlet.injection.classes.blob.BlobDownloadConfiguratorClassNameBuilder;
import org.kawanfw.sql.servlet.injection.classes.blob.BlobDownloadConfiguratorClassNameBuilderCreator;
import org.kawanfw.sql.servlet.injection.classes.blob.BlobUploadConfiguratorClassNameBuilder;
import org.kawanfw.sql.servlet.injection.classes.blob.BlobUploadConfiguratorClassNameBuilderCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.BlobDownloadConfiguratorCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.BlobUploadConfiguratorCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.DatabaseConfiguratorCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.SessionConfiguratorCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.SqlFirewallsCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.UserAuthenticatorCreator;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesStore;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesUtil;
import org.kawanfw.sql.tomcat.TomcatSqlModeStore;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;
import org.kawanfw.sql.tomcat.properties.threadpool.ThreadPoolExecutorBuilder;
import org.kawanfw.sql.tomcat.properties.threadpool.ThreadPoolExecutorBuilderCreator;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.util.Tag;

public class InjectedClassesManagerNew {

    private static boolean DEBUG = FrameworkDebug.isSet(InjectedClassesManagerNew.class);
    public static String CR_LF = System.getProperty("line.separator");

    private String classNameToLoad;

    /**
     * Created all injected classes instances.
     * 
     * @param propertiesFileStr
     * @param licenseFileStr
     * @throws ServletException
     * @throws IOException
     */
    public void createClasses(String propertiesFileStr, String licenseFileStr) throws ServletException, IOException {
	classNameToLoad = null;
	try {
	    // Test if we are in Native Tomcat and do specific stuff.
	    if (!TomcatSqlModeStore.isTomcatEmbedded()) {

		if (propertiesFileStr == null) {
		    throw new NullPointerException("The init param \"properties\" has no been defined in web.xml!");
		}

		if (licenseFileStr != null) {
		    File licenseFile = new File(licenseFileStr);
		    if (!licenseFile.exists()) {
			throw new FileNotFoundException(
				"The file defined by the  web.xml init param \"licenseFile\" does not exist:"
					+ licenseFile);
		    }
		    AceQLLicenseFileLoader.setAceqlLicenseFile(licenseFile);
		}

		NativeTomcatElementsBuilder nativeTomcatElementsBuilder = NativeTomcatElementsBuilderCreator
			.createInstance();
		nativeTomcatElementsBuilder.create(propertiesFileStr);
	    }

	    CommunityValidator communityValidator = new CommunityValidator(propertiesFileStr);
	    communityValidator.validate();

	    Set<String> databases = ConfPropertiesStore.get().getDatabaseNames();

	    TomcatStarterUtil.testDatabasesLimit(databases);

	    // Create out InjectedClasses builder
	    InjectedClassesBuilder injectedClassesBuilder = new InjectedClassesBuilder();

	    // Ouf first loader is for authentication
	    loadUserAuthenticator(injectedClassesBuilder);

	    // All elements that depend on database
	    loadPerDatabase(databases, injectedClassesBuilder);

	    loadRequestHeadersAuthenticator(injectedClassesBuilder);

	    ThreadPoolExecutorBuilder threadPoolExecutorBuilder = ThreadPoolExecutorBuilderCreator.createInstance();
	    ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorBuilder.build();
	    injectedClassesBuilder.threadPoolExecutor(threadPoolExecutor);

	    loadBlobDownloadConfigurator(injectedClassesBuilder);
	    loadBlobUploadConfigurator(injectedClassesBuilder);

	    loadSessionManagerConfigurator(injectedClassesBuilder);

	    // Create the InjectedClasses instance
	    InjectedClasses injectedClasses = injectedClassesBuilder.build();

	    // Store the InjectedClasses instance statically
	    InjectedClassesStore.set(injectedClasses);

	} catch (ClassNotFoundException exception) {
	    String initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
		    + " Impossible to load (ClassNotFoundException) Configurator class: " + classNameToLoad;
	    throw new IOException(initErrrorMesage, exception);

	} catch (InstantiationException exception) {
	    String initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
		    + " Impossible to load (InstantiationException) Configurator class: " + classNameToLoad;
	    throw new IOException(initErrrorMesage, exception);
	} catch (IllegalAccessException exception) {
	    String initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
		    + " Impossible to load (IllegalAccessException) Configurator class: " + classNameToLoad;
	    throw new IOException(initErrrorMesage, exception);
	} catch (DatabaseConfigurationException exception) {
	    String initErrrorMesage = exception.getMessage();
	    throw new IOException(initErrrorMesage, exception);
	} catch (Exception exception) {
	    String initErrrorMesage = Tag.RUNNING_PRODUCT + " " + exception.getMessage();
	    throw new IOException(initErrrorMesage);

	}

	// treatException();
    }

    /**
     * Loads elements that depend on databases.
     * 
     * @param databases
     * @param injectedClassesBuilder
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
    public void loadPerDatabase(Set<String> databases, InjectedClassesBuilder injectedClassesBuilder)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, IOException {
	// Load all the classes and set our InjectedClassesBuilder instance
	Map<String, List<SqlFirewallManager>> sqlFirewallManagerMap = new HashMap<>();
	Map<String, DatabaseConfigurator> databaseConfigurators = new HashMap<>();
	Map<String, List<SqlFirewallTrigger>> sqlFirewallTriggerMap = new HashMap<>();
	Map<String, List<UpdateListener>> updateListenerMap = new HashMap<>();

	for (String database : databases) {
	    List<SqlFirewallManager> sqlFirewalManagers = loadSqlFirewallManagers(database);
	    sqlFirewallManagerMap.put(database, sqlFirewalManagers);

	    DatabaseConfigurator databaseConfigurator = loadDatabaseConfigurator(database);
	    databaseConfigurators.put(database, databaseConfigurator);

	    List<SqlFirewallTrigger> sqlFirewallTriggers = loadSqlFirewallTriggers(database, injectedClassesBuilder);
	    sqlFirewallTriggerMap.put(database, sqlFirewallTriggers);

	    List<UpdateListener> updateListeners = loadUpdateListeners(database, injectedClassesBuilder);
	    updateListenerMap.put(database, updateListeners);
	}

	// Final injection for databases
	injectedClassesBuilder.sqlFirewallManagerMap(sqlFirewallManagerMap);
	injectedClassesBuilder.databaseConfigurators(databaseConfigurators);
	injectedClassesBuilder.updateListenerMap(updateListenerMap);
	injectedClassesBuilder.sqlFirewallTriggerMap(sqlFirewallTriggerMap);
    }

    /**
     *
     */
    @SuppressWarnings("unused")
    private void treatException() {
	/*
	 * if (exception == null) { System.out.println(SqlTag.SQL_PRODUCT_START +
	 * " Loaded classes Status: OK.");
	 * 
	 * if (!TomcatSqlModeStore.isTomcatEmbedded()) { String runningMessage =
	 * SqlTag.SQL_PRODUCT_START + " " + VersionWrapper.getName() + " Start OK.";
	 * System.out.println(runningMessage); }
	 * 
	 * } else { exception.printStackTrace(); if
	 * (!TomcatSqlModeStore.isTomcatEmbedded()) { String errorMessage1 =
	 * SqlTag.SQL_PRODUCT_START + "  -> Loaded classes Status: KO."; String
	 * errorMessage2 = initErrrorMesage; String errorMessage3 =
	 * ExceptionUtils.getStackTrace(exception);
	 * 
	 * System.out.println(errorMessage1); System.out.println(errorMessage2);
	 * System.out.println(errorMessage3);
	 * 
	 * System.out.println(); } }
	 */
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
     * @throws SQLException
     */
    private void loadSessionManagerConfigurator(InjectedClassesBuilder injectedClassesBuilder)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException, SQLException {
	// Load Configurators for SessionManager
	// String sessionManagerConfiguratorClassName =
	// ConfPropertiesStore.get().getSessionConfiguratorClassName();

	SessionConfiguratorClassNameBuilder sessionConfiguratorClassNameBuilder = SessionConfiguratorClassNameBuilderCreator
		.createInstance();
	String sessionConfiguratorClassName = sessionConfiguratorClassNameBuilder.getClassName();

	if (ConfPropertiesUtil.isStatelessMode()
		&& !sessionConfiguratorClassName.endsWith(JwtSessionConfigurator.class.getSimpleName())) {
	    throw new SQLException(SqlTag.USER_CONFIGURATION
		    + " Stateless mode is incompatible with DefaultSessionConfigurator implementation. "
		    + "Please use a JwtSessionConfigurator or equivalent in stateless mode.");
	}

	classNameToLoad = sessionConfiguratorClassName;
	SessionConfiguratorCreator sessionConfiguratorCreator = new SessionConfiguratorCreator(
		sessionConfiguratorClassName);
	injectedClassesBuilder.sessionConfigurator(sessionConfiguratorCreator.getSessionConfigurator());
	sessionConfiguratorClassName = sessionConfiguratorCreator.getSessionConfiguratorClassName();

	if (!sessionConfiguratorClassName
		.equals(org.kawanfw.sql.api.server.session.DefaultSessionConfigurator.class.getName())) {
	    System.out.println(SqlTag.SQL_PRODUCT_START + " Loading sessionManagerConfiguratorClassName: ");
	    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + sessionConfiguratorClassName);
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
     * @throws SQLException
     */
    private void loadBlobUploadConfigurator(InjectedClassesBuilder injectedClassesBuilder)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException, SQLException {

	BlobUploadConfiguratorClassNameBuilder blobUploadConfiguratorClassNameBuilder = BlobUploadConfiguratorClassNameBuilderCreator
		.createInstance();
	String blobUploadConfiguratorClassName = blobUploadConfiguratorClassNameBuilder.getClassName();

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
     * @throws SQLException
     */
    private void loadBlobDownloadConfigurator(InjectedClassesBuilder injectedClassesBuilder)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException, SQLException {
	// Load Configurators for Blobs/Clobs

	BlobDownloadConfiguratorClassNameBuilder blobDownloadConfiguratorClassNameBuilder = BlobDownloadConfiguratorClassNameBuilderCreator
		.createInstance();
	String blobDownloadConfiguratorClassName = blobDownloadConfiguratorClassNameBuilder.getClassName();

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
     * Loads requestHeadersAuthenticator.
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
     * @throws SQLException
     */
    private void loadRequestHeadersAuthenticator(InjectedClassesBuilder injectedClassesBuilder)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {

	String requestHeadersAuthenticatorClassName = ConfPropertiesStore.get()
		.getRequestHeadersAuthenticatorClassName();

	classNameToLoad = requestHeadersAuthenticatorClassName;

	RequestHeadersAuthenticatorLoader requestHeadersAuthenticatorLoader = RequestHeadersAuthenticatorLoaderCreator
		.createInstance();
	requestHeadersAuthenticatorLoader.loadRequestHeadersAuthenticator(injectedClassesBuilder,
		requestHeadersAuthenticatorClassName);

    }

    /**
     * Loads the Update Listeners.
     * 
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

	UpdateListenersLoader updateListenersLoader = UpdateListenersLoaderCreator.createInstance();
	List<UpdateListener> updateListeners = updateListenersLoader.loadUpdateListeners(database,
		injectedClassesBuilder, updateListenerClassNames);

	// Update class name(s) to load
	classNameToLoad = updateListenersLoader.getClassNameToLoad();

	return updateListeners;
    }

    /**
     * Loads the SQL Firewall Triggers
     * 
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
    private List<SqlFirewallTrigger> loadSqlFirewallTriggers(String database,
	    InjectedClassesBuilder injectedClassesBuilder)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, IOException {

	List<String> sqlFirewallTriggerClassNames = ConfPropertiesStore.get().getSqlFirewallTriggerClassNames(database);
	classNameToLoad = sqlFirewallTriggerClassNames.toString();

	SqlFirewallTriggersLoader sqlFirewallTriggersLoader = SqlFirewallTriggersLoaderCreator.createInstance();
	List<SqlFirewallTrigger> sqlFirewallTriggers = sqlFirewallTriggersLoader.loadSqlFirewallTriggers(database,
		injectedClassesBuilder, sqlFirewallTriggerClassNames);

	// Update class name(s) to load
	classNameToLoad = sqlFirewallTriggersLoader.getClassNameToLoad();

	return sqlFirewallTriggers;
    }

    /**
     * loads the Firewall Managers.
     * 
     * @param database
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
    private List<SqlFirewallManager> loadSqlFirewallManagers(String database)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, IOException {

	List<String> sqlFirewallClassNames = ConfPropertiesStore.get().getSqlFirewallManagerClassNames(database);
	classNameToLoad = sqlFirewallClassNames.toString();

	debug("==> sqlFirewallClassNames: " + sqlFirewallClassNames);

	String tagSQLFirewallManager = null;
	if (sqlFirewallClassNames.size() < 2)
	    tagSQLFirewallManager = " SQLFirewallManager class: ";
	else
	    tagSQLFirewallManager = " SQLFirewallManager classes: ";

	System.out.println(SqlTag.SQL_PRODUCT_START + " Loading Database " + database + tagSQLFirewallManager);

	SqlFirewallsCreator sqlFirewallsCreator = new SqlFirewallsCreator(sqlFirewallClassNames);
	List<SqlFirewallManager> sqlFirewallManagers = sqlFirewallsCreator.getSqlFirewalls();

	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
	    debug("==> sqlFirewallManager: " + sqlFirewallManager);
	}

	sqlFirewallClassNames = sqlFirewallsCreator.getSqlFirewallClassNames();
	classNameToLoad = sqlFirewallClassNames.toString();

	for (String sqlFirewallClassName : sqlFirewallClassNames) {
	    System.out.println(SqlTag.SQL_PRODUCT_START + "   -> " + sqlFirewallClassName);
	}

	return sqlFirewallManagers;

    }

    /**
     * Loads the database configurators.
     * 
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
    private DatabaseConfigurator loadDatabaseConfigurator(String database)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException, SQLException {

	// WARNING: Database configurator must be loaded prior to firewalls
	// because a getConnection() is used to test SqlFirewallManager

	// String databaseConfiguratorClassName;
	// databaseConfiguratorClassName =
	// ConfPropertiesStore.get().getDatabaseConfiguratorClassName(database);

	DatabaseConfiguratorClassNameBuilder databaseConfiguratorClassNameBuilder = DatabaseConfiguratorClassNameBuilderCreator
		.createInstance();
	String databaseConfiguratorClassName = databaseConfiguratorClassNameBuilder.getClassName(database);

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

//    public Exception getException() {
//	return exception;
//    }
//
//    public String getInitErrrorMesage() {
//	return initErrrorMesage;
//    }

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
