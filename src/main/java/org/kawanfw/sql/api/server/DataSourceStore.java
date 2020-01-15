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
package org.kawanfw.sql.api.server;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.kawanfw.sql.tomcat.ServletParametersStore;
import org.kawanfw.sql.tomcat.TomcatSqlModeStore;

/**
 * 
 * Allows to retrieve for each database the
 * {@code org.apache.tomcat.jdbc.pool.DataSource} corresponding to the Tomcat
 * JDBC Pool created at AceQL Web server startup. <br>
 * (Pools are defined in aceql-server.properties file, Tomcat JDBC Connection
 * Pool Section).
 * 
 * @author Nicolas de Pomereu
 *
 */
public class DataSourceStore {

	/**
	 * Protected Constructor
	 */
	protected DataSourceStore() {

	}

	/**
	 * Method to be called by users servlets to get info on the JDBC Pool
	 * DataSources created for each database. <br>
	 * <br>
	 * The {@link DefaultPoolsInfo} servlet with url-pattern
	 * {@code /default_pools_info} is provided to query some info for of each
	 * database {@code DataSource}. <br>
	 * 
	 * @return the the {@code Map} of {@code org.apache.tomcat.jdbc.pool.DataSource}
	 *         per database
	 */
	public static Map<String, DataSource> getDataSources() {
		Set<String> databases = ServletParametersStore.getDatabaseNames();

		Map<String, DataSource> dataSourceSet = new ConcurrentHashMap<>();

		if (databases == null || databases.isEmpty()) {
			return dataSourceSet;
		}

		for (String database : databases) {
			dataSourceSet.put(database, TomcatSqlModeStore.getDataSource(database));
		}
		return dataSourceSet;
	}

}
