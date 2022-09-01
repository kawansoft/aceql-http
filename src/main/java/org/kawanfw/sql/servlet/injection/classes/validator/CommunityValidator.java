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
package org.kawanfw.sql.servlet.injection.classes.validator;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;
import org.kawanfw.sql.tomcat.properties.threadpool.ThreadPoolProperties;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.util.Tag;
import org.kawanfw.sql.version.EditionUtil;

/**
 * Test at server startup that the pro properties are not modified
 * 
 * @author Nicolas de Pomereu
 *
 */
public class CommunityValidator {

    private static boolean DEBUG = FrameworkDebug.isSet(CommunityValidator.class);
    private String propertiesFile;

    public CommunityValidator(String propertiesFile) {
	this.propertiesFile = propertiesFile;
    }

    public void validate() throws IOException {

	// Nothing todo if Pro Edition
	if (!EditionUtil.isCommunityEdition()) {
	    return;
	}

	File file = new File(propertiesFile);
	Properties properties = PropertiesFileUtil.commonsGetProperties(file);
	checkProperty(properties, "aceQLManagerServletCallName", "aceql");

	Set<String> databases = ConfPropertiesStore.get().getDatabaseNames();
	TomcatStarterUtil.testDatabasesLimit(databases);

	for (String database : databases) {
	    String propertyName = database + ".databaseConfiguratorClassName";
	    checkPropertyMustBeNull(properties, propertyName);
	}

	checkProperty(properties, "defaultDatabaseConfigurator.maxRow", "0");

	for (String database : databases) {
	    String propertyName = database + ".updateListenerClassNames";
	    checkPropertyMustBeNull(properties, propertyName);
	}

	for (String database : databases) {
	    String propertyName = database + ".sqlFirewallTriggerClassName";
	    checkPropertyMustBeNull(properties, propertyName);
	}

	checkPropertyMustBeNull(properties, "requestHeadersAuthenticatorClassName");

	/**
	 * <pre>
	 * <code>
	# The number of threads to keep in the pool, even if they are idle
	corePoolSize=10
	
	# The maximum number of threads to allow in the pool
	maximumPoolSize=125
	
	#the time unit for the keepAliveTime argument
	unit=SECONDS
	
	# When the number of threads is greater than the core, this is 
	# the maximum time that excess idle threads will wait for new tasks 
	# before terminating
	keepAliveTime=10
	
	# The BlockingQueue class to use in ThreadPoolExecutor constructor
	workQueueClassName=java.util.concurrent.ArrayBlockingQueue
	
	# The initial capacity of the BloquingQueue<Runnable> 
	# (0 for no or default initial capacity.)
	capacity=50000
	
	# Servlets Section (Enterprise Edition)    
	# Error if more than one servlet is active 
	
	#HTTP2 Configuration Section (Enterprise Edition) 
	updateToHttp2Protocol=false 
	</code>
	 * </pre>
	 */

	checkThradPoolPropertyWarningOnly(properties, "corePoolSize", ThreadPoolProperties.DEFAULT_CORE_POOL_SIZE + "");
	checkThradPoolPropertyWarningOnly(properties, "maximumPoolSize",
		ThreadPoolProperties.DEFAULT_MAXIMUM_POOL_SIZE + "");
	checkThradPoolPropertyWarningOnly(properties, "unit", ThreadPoolProperties.DEFAULT_UNIT.toString());
	checkThradPoolPropertyWarningOnly(properties, "keepAliveTime",
		ThreadPoolProperties.DEFAULT_KEEP_ALIVE_TIME + "");
	checkThradPoolPropertyWarningOnly(properties, "workQueueClassName",
		ThreadPoolProperties.DEFAULT_BLOCKING_QUEUE_NAME);
	checkThradPoolPropertyWarningOnly(properties, "capacity",
		ThreadPoolProperties.DEFAULT_BLOCKING_QUEUE_CAPACITY + "");

	checkProperty(properties, "updateToHttp2Protocol", false + "");

	checkPropertyMustBeNullOrDefaultValue(properties, "blobDownloadConfiguratorClassName", "DefaultBlobDownloadConfigurator");
	checkPropertyMustBeNullOrDefaultValue(properties, "blobUploadConfiguratorClassName", "DefaultBlobUploadConfigurator") ;

	checkPropertyMustBeNull(properties, "propertiesPasswordManagerClassName");

    }

    private void checkPropertyMustBeNullOrDefaultValue(Properties properties, String propertyName, String defaultValue) {
	if (properties.getProperty(propertyName) == null) {
	    return;
	}
	
	String valueInFile = properties.getProperty(propertyName);
	if (! valueInFile.endsWith(defaultValue)) {
		throw new UnsupportedOperationException(Tag.PRODUCT + " " + "Server cannot start. In Community Edition, the \""
			+ propertyName + "\" property cannot be set from default value.");
	}
	
    }

    private void checkThradPoolPropertyWarningOnly(Properties properties, String propertyName, String value) {
	if (properties.getProperty(propertyName) == null) {
	    return;
	}

	String valueInFile = properties.getProperty(propertyName);

	if (!valueInFile.equals(value)) {
	    System.err.println(
		    SqlTag.SQL_PRODUCT_START + " " + Tag.WARNING + " In Community Edition, the ThreadPoolExecutor \""
			    + propertyName + "\" property" + ValidatorUtil.CR_LF + SqlTag.SQL_PRODUCT_START + " "
			    + ValidatorUtil.WARNING_SECOND_LINE_SPACES + " is enforced to the default install value: " + value
			    + " (Set value in .properties file ignored: " + valueInFile + ")");
	}
    }

    private void checkPropertyMustBeNull(Properties properties, String propertyName) {
	if (properties.getProperty(propertyName) == null) {
	    return;
	}

	throw new UnsupportedOperationException(Tag.PRODUCT + " " + "Server cannot start. In Community Edition, the \""
		+ propertyName + "\" property cannot be set.");
    }

    private void checkProperty(Properties properties, String propertyName, String value) {
	if (properties.getProperty(propertyName) == null) {
	    return;
	}

	String valueInFile = properties.getProperty(propertyName);

	if (!valueInFile.equals(value)) {
	    throw new UnsupportedOperationException(
		    Tag.PRODUCT + " " + "Server cannot start. In Community Edition, the \"" + propertyName
			    + "\" property cannot be changed from it's default \"" + value + "\" value.");
	}

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
