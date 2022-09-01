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
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.ServletException;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.api.server.firewall.trigger.SqlFirewallTrigger;
import org.kawanfw.sql.api.server.listener.UpdateListener;
import org.kawanfw.sql.api.server.session.JwtSessionConfigurator;
import org.kawanfw.sql.servlet.injection.classes.InjectedClasses.InjectedClassesBuilder;
import org.kawanfw.sql.servlet.injection.classes.creator.BlobDownloadConfiguratorCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.BlobUploadConfiguratorCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.DatabaseConfiguratorCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.SessionConfiguratorCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.SqlFirewallsCreator;
import org.kawanfw.sql.servlet.injection.classes.creator.UserAuthenticatorCreator;
import org.kawanfw.sql.servlet.injection.classes.validator.EnterpriseWarner;
import org.kawanfw.sql.servlet.injection.properties.ConfProperties;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesManager;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesStore;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesUtil;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.tomcat.TomcatSqlModeStore;
import org.kawanfw.sql.tomcat.TomcatStarterMessages;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;
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
	
	debug("propertiesFileStr: " + propertiesFileStr);
	debug("licenseFileStr   : " + licenseFileStr);
	
	classNameToLoad = null;
	try {
	    
	    //Future usage...
	    //PropertiesFileFormatValidator propertiesFileFormatValidator = new PropertiesFileFormatValidator(propertiesFileStr);
	    //propertiesFileFormatValidator.validate();
	    
	    // Test if we are in Native Tomcat and do specific stuff.
	    if (!TomcatSqlModeStore.isTomcatEmbedded()) {

		Objects.requireNonNull(licenseFileStr, "The init param \\\"properties\\\" has not been defined in web.xml!");

		if (licenseFileStr != null) {
		    File licenseFile = new File(licenseFileStr);
		    if (!licenseFile.exists()) {
			throw new FileNotFoundException(
				"The file defined by the  web.xml init param \"licenseFile\" does not exist:"
					+ licenseFile);
		    }
		}

		TomcatStarterMessages.printBeginMessage();
//		
//		NativeTomcatElementsBuilder nativeTomcatElementsBuilder = NativeTomcatElementsBuilderCreator
//			.createInstance();
//		nativeTomcatElementsBuilder.create(propertiesFileStr);
		
	        createNativeTomcat(propertiesFileStr);
		
	    }
		
	    //CommunityValidator communityValidator = new CommunityValidator(propertiesFileStr);
	    //communityValidator.validate();
	    
	    Set<String> databases = ConfPropertiesStore.get().getDatabaseNames();

	    TomcatStarterUtil.testDatabasesLimit(databases);

	    // Create out InjectedClasses builder
	    InjectedClassesBuilder injectedClassesBuilder = new InjectedClassesBuilder();

	    // Ouf first loader is for authentication
	    loadUserAuthenticator(injectedClassesBuilder);

	    loadRequestHeadersAuthenticator(injectedClassesBuilder);

	    //ThreadPoolExecutorBuilder threadPoolExecutorBuilder = ThreadPoolExecutorBuilderCreator.createInstance();
	    //ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorBuilder.build();
	    //injectedClassesBuilder.threadPoolExecutor(threadPoolExecutor);
	    
	    ProEditionThreadPoolExecutorBuilder threadPoolExecutorBuilder = new ProEditionThreadPoolExecutorBuilder();
	    ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorBuilder.build();
	    injectedClassesBuilder.threadPoolExecutor(threadPoolExecutor);
	    
	    // Check ThreadPoolExecutor parameters
	    EnterpriseWarner enterpriseWarner = new EnterpriseWarner(propertiesFileStr);
	    enterpriseWarner.warnOnThreadPoolExecutorParams();
	    
	    // All elements that depend on database
	    loadPerDatabase(databases, injectedClassesBuilder);
	    loadBlobDownloadConfigurator(injectedClassesBuilder);
	    loadBlobUploadConfigurator(injectedClassesBuilder);

	    loadSessionManagerConfigurator(injectedClassesBuilder);

	    // Create the InjectedClasses instance
	    InjectedClasses injectedClasses = injectedClassesBuilder.build();

	    // Store the InjectedClasses instance statically
	    InjectedClassesStore.set(injectedClasses);
	    
	    if (!TomcatSqlModeStore.isTomcatEmbedded()) {
		TomcatStarterMessages.printFinalOkMessage();
	    }

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
	    exception.printStackTrace(System.out);
	    String initErrrorMesage = Tag.RUNNING_PRODUCT + " " + exception.getMessage();
	    throw new IOException(initErrrorMesage);

	}

	// treatException();
    }

    /**
     * Create elements for Nativr Tomcat
     * @param propertiesFileStr
     * @throws DatabaseConfigurationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SQLException
     */
    public void createNativeTomcat(String propertiesFileStr)
	    throws DatabaseConfigurationException, FileNotFoundException, IOException, SQLException {
	if (propertiesFileStr == null || propertiesFileStr.isEmpty()) {
	    throw new DatabaseConfigurationException(Tag.PRODUCT_USER_CONFIG_FAIL
		    + " AceQL servlet param-name \"properties\" not set. Impossible to load the AceQL Server properties file.");
	}
	File file = new File(propertiesFileStr);
  
	if (!file.exists()) {
	    throw new DatabaseConfigurationException(
		    Tag.PRODUCT_USER_CONFIG_FAIL + " properties file not found: " + propertiesFileStr);
	}
	
	PropertiesFileStore.set(file);
	Properties properties = PropertiesFileUtil.getProperties(file);
	
	System.out.println(TomcatStarterUtil.getJavaInfo());
	System.out.println(SqlTag.SQL_PRODUCT_START + " " + "Using properties file: ");
	System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + PropertiesFileStore.get());
  
	// Create all configuration properties from the Properties and store
	ConfPropertiesManager confPropertiesManager = new ConfPropertiesManager(properties);
	ConfProperties confProperties = confPropertiesManager.createConfProperties();
	ConfPropertiesStore.set(confProperties);
  
	// Create the default DataSource if necessary
	TomcatStarterUtil.createAndStoreDataSources(properties);
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

//	SessionConfiguratorClassNameBuilder sessionConfiguratorClassNameBuilder = SessionConfiguratorClassNameBuilderCreator
//		.createInstance();
//	String sessionConfiguratorClassName = sessionConfiguratorClassNameBuilder.getClassName();
	
        String sessionConfiguratorClassName = ConfPropertiesStore.get().getSessionConfiguratorClassName();

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

//	BlobUploadConfiguratorClassNameBuilder blobUploadConfiguratorClassNameBuilder = BlobUploadConfiguratorClassNameBuilderCreator
//		.createInstance();
//	String blobUploadConfiguratorClassName = blobUploadConfiguratorClassNameBuilder.getClassName();

        String blobUploadConfiguratorClassName = ConfPropertiesStore.get().getBlobUploadConfiguratorClassName();
	
	classNameToLoad = blobUploadConfiguratorClassName;
	BlobUploadConfiguratorCreator blobUploadConfiguratorCreator = new BlobUploadConfiguratorCreator(
		blobUploadConfiguratorClassName);
	injectedClassesBuilder.blobUploadConfigurator(blobUploadConfiguratorCreator.getBlobUploadConfigurator());
	blobUploadConfiguratorClassName = blobUploadConfiguratorCreator.getBlobUploadConfiguratorClassName();

	if (!blobUploadConfiguratorClassName
		.equals(org.kawanfw.sql.api.server.blob.DefaultBlobUploadConfigurator.class.getName())) {
	    System.out.println(SqlTag.SQL_PRODUCT_START + " Loading blobUploadConfiguratorClassName: ");
	    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + blobUploadConfiguratorClassName);
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

	//BlobDownloadConfiguratorClassNameBuilder blobDownloadConfiguratorClassNameBuilder = BlobDownloadConfiguratorClassNameBuilderCreator
	//	.createInstance();
	//String blobDownloadConfiguratorClassName = blobDownloadConfiguratorClassNameBuilder.getClassName();
	
        String blobDownloadConfiguratorClassName = ConfPropertiesStore.get().getBlobDownloadConfiguratorClassName();

	classNameToLoad = blobDownloadConfiguratorClassName;
	BlobDownloadConfiguratorCreator blobDownloadConfiguratorCreator = new BlobDownloadConfiguratorCreator(
		blobDownloadConfiguratorClassName);
	injectedClassesBuilder.blobDownloadConfigurator(blobDownloadConfiguratorCreator.getBlobDownloadConfigurator());
	blobDownloadConfiguratorClassName = blobDownloadConfiguratorCreator.getBlobDownloadConfiguratorClassName();

	if (!blobDownloadConfiguratorClassName
		.equals(org.kawanfw.sql.api.server.blob.DefaultBlobDownloadConfigurator.class.getName())) {
	    System.out.println(SqlTag.SQL_PRODUCT_START + " Loading blobDownloadConfiguratorClassName: ");
	    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + blobDownloadConfiguratorClassName);
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

//	RequestHeadersAuthenticatorLoader requestHeadersAuthenticatorLoader = RequestHeadersAuthenticatorLoaderCreator
//		.createInstance();
//	requestHeadersAuthenticatorLoader.loadRequestHeadersAuthenticator(injectedClassesBuilder,
//		requestHeadersAuthenticatorClassName);
	
	ProEditionRequestHeadersAuthenticatorLoader proEditionRequestHeadersAuthenticatorLoader 
		=new ProEditionRequestHeadersAuthenticatorLoader();
	proEditionRequestHeadersAuthenticatorLoader.loadRequestHeadersAuthenticator(injectedClassesBuilder, requestHeadersAuthenticatorClassName);
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

	
//	UpdateListenersLoader updateListenersLoader = UpdateListenersLoaderCreator.createInstance();
//	List<UpdateListener> updateListeners = updateListenersLoader.loadUpdateListeners(database,
//		injectedClassesBuilder, updateListenerClassNames);
		
	ProEditionUpdateListenersLoader updateListenersLoader = new ProEditionUpdateListenersLoader();
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


//	SqlFirewallTriggersLoader sqlFirewallTriggersLoader = SqlFirewallTriggersLoaderCreator.createInstance();
//	List<SqlFirewallTrigger> sqlFirewallTriggers = sqlFirewallTriggersLoader.loadSqlFirewallTriggers(database,
//		injectedClassesBuilder, sqlFirewallTriggerClassNames);

	ProEditionSqlFirewallTriggersLoader sqlFirewallTriggersLoader = new ProEditionSqlFirewallTriggersLoader();
	List<SqlFirewallTrigger> sqlFirewallTriggers 
	= sqlFirewallTriggersLoader.loadSqlFirewallTriggers(database, injectedClassesBuilder, sqlFirewallTriggerClassNames);
	
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

	System.out.println(SqlTag.SQL_PRODUCT_START + " " + database + " Database - Loading " + tagSQLFirewallManager);

	SqlFirewallsCreator sqlFirewallsCreator = new SqlFirewallsCreator(sqlFirewallClassNames);
	List<SqlFirewallManager> sqlFirewallManagers = sqlFirewallsCreator.getSqlFirewalls();

	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
	    debug("==> sqlFirewallManager: " + sqlFirewallManager);
	}

	sqlFirewallClassNames = sqlFirewallsCreator.getSqlFirewallClassNames();
	classNameToLoad = sqlFirewallClassNames.toString();

	for (String sqlFirewallClassName : sqlFirewallClassNames) {
	    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + sqlFirewallClassName);
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

	//DatabaseConfiguratorClassNameBuilder databaseConfiguratorClassNameBuilder = DatabaseConfiguratorClassNameBuilderCreator
	//	.createInstance();
	//String databaseConfiguratorClassName = databaseConfiguratorClassNameBuilder.getClassName(database);
	String databaseConfiguratorClassName = ConfPropertiesStore.get().getDatabaseConfiguratorClassName(database);
	
	debug("databaseConfiguratorClassName    : " + databaseConfiguratorClassName);

	// Check spelling with first letter capitalized
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

	System.out.println(SqlTag.SQL_PRODUCT_START + " " + database + " Database - Loading DatabaseConfigurator class:");
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
	    System.out.println(new Date() + " " + InjectedClassesManagerNew.class.getSimpleName() + " " + s);
	}
    }

}
