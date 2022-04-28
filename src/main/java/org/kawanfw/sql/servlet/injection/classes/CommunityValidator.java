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
import java.util.Properties;
import java.util.Set;

import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesStore;
import org.kawanfw.sql.servlet.injection.properties.DefaultPropertiesBuilder;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;
import org.kawanfw.sql.tomcat.properties.threadpool.ThreadPoolProperties;
import org.kawanfw.sql.util.Tag;

/**
 * Test at server startup that the pro properties are not modified
 * 
 * @author Nicolas de Pomereu
 *
 */
public class CommunityValidator {

    private String propertiesFile;

    public CommunityValidator(String propertiesFile) {
	this.propertiesFile = propertiesFile;
    }

    public void validate() throws IOException {
	
	// Nothing todo if Pro Edition
	if (!TomcatStarterUtil.isCommunityEdition()) {
	    return;
	}
	
	File file = new File(propertiesFile);		
	Properties properties = DefaultPropertiesBuilder.commonsGetProperties(file);
	checkProperty(properties, "aceQLManagerServletCallName", "aceql");

	Set<String> databases = ConfPropertiesStore.get().getDatabaseNames();

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
	
	# Servlets Section (Professional Edition)    
	# Error if more than one servlet is active 
	
	#HTTP2 Configuration Section (Professional Edition) 
	updateToHttp2Protocol=false 
	</code>
	 * </pre>
	 */
	
	checkProperty(properties, "corePoolSize", ThreadPoolProperties.DEFAULT_CORE_POOL_SIZE  +"");
	checkProperty(properties, "maximumPoolSize", ThreadPoolProperties.DEFAULT_MAXIMUM_POOL_SIZE + "");
	checkProperty(properties, "unit", ThreadPoolProperties.DEFAULT_UNIT.toString());
	checkProperty(properties, "keepAliveTime", ThreadPoolProperties.DEFAULT_KEEP_ALIVE_TIME + "");
	checkProperty(properties, "capacity", ThreadPoolProperties.DEFAULT_BLOCKING_QUEUE_CAPACITY + "");
	
	checkProperty(properties, "updateToHttp2Protocol", false + "");
	
	checkPropertyMustBeNull(properties, "blobDownloadConfiguratorClassName");
	checkPropertyMustBeNull(properties, "blobUploadConfiguratorClassName");
	
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

}
