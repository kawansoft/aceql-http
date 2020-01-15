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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

/**
 * Class that store the mode in which we run Tomcat: nativ or embedded. <br>
 * Includes also the data source set/get.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class TomcatSqlModeStore {

	/** Value that says we are in stand alone Server with Tomcat Embed */
	private static boolean tomcatEmbedded = false;

	/** The (Database name Name, DataSource) Map */
	private static Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<String, DataSource>();

	/**
	 * no instantiation
	 */
	private TomcatSqlModeStore() {

	}

	/**
	 * @return the tomcatEmbedded
	 */
	public static boolean isTomcatEmbedded() {
		return tomcatEmbedded;
	}

	/**
	 * @param tomcatEmbedded
	 *            the tomcatEmbedded to set
	 */
	public static void setTomcatEmbedded(boolean tomcatEmbedded) {
		TomcatSqlModeStore.tomcatEmbedded = tomcatEmbedded;
	}

	/**
	 * Stores a DataSource for a specified database.
	 * 
	 * @param database
	 *            the database to store the DataSource for
	 * @param dataSource
	 *            the dataSource to set.
	 */
	public static void setDataSource(String database, DataSource dataSource) {
		dataSourceMap.put(database, dataSource);
	}

	/**
	 * Returns the DataSource associated to a database.
	 * 
	 * @param database
	 *            the database to store the DataSource for
	 * @return the dataSource corresponding to the database
	 */
	public static DataSource getDataSource(String database) {
		return dataSourceMap.get(database);
	}

}
