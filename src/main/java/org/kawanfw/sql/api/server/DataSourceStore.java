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
package org.kawanfw.sql.api.server;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesStore;
import org.kawanfw.sql.tomcat.TomcatSqlModeStore;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;

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
	Set<String> databases = ConfPropertiesStore.get().getDatabaseNames();

	TomcatStarterUtil.testDatabasesLimit(databases);
	
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
