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
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletConfig;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.servlet.injection.properties.ConfProperties;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesManager;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.util.Tag;

/**
 * Process to do for Native Tomcat only
 * @author Nicolas de Pomereu
 *
 */
public class NativeTomcatElementsCreator {

    private ServletConfig config;

    /**
     * Constructor. 
     * @param config	servlet configuration elements (native Tomcat only);
     */
    public NativeTomcatElementsCreator(ServletConfig config) {
	this.config = config;
    }

    /**
     * Creates the the datasources and the ConfProperties.
     * @throws DatabaseConfigurationException
     * @throws IOException
     * @throws SQLException 
     */
    public void create() throws DatabaseConfigurationException, IOException, SQLException {
	String propertiesFileStr = config.getInitParameter("properties");

	if (propertiesFileStr == null || propertiesFileStr.isEmpty()) {
	    throw new DatabaseConfigurationException(Tag.PRODUCT_USER_CONFIG_FAIL
		    + " AceQL servlet param-name \"properties\" not set. Impossible to load the AceQL Server properties file.");
	}
	File propertiesFile = new File(propertiesFileStr);

	if (!propertiesFile.exists()) {
	    throw new DatabaseConfigurationException(
		    Tag.PRODUCT_USER_CONFIG_FAIL + " properties file not found: " + propertiesFile);
	}
	
	PropertiesFileStore.set(propertiesFile);
	Properties properties = PropertiesFileUtil.getProperties(propertiesFile);
	
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
    
    
    
}
