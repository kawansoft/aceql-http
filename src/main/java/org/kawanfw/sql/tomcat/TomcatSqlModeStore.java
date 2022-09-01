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
     * @param tomcatEmbedded the tomcatEmbedded to set
     */
    public static void setTomcatEmbedded(boolean tomcatEmbedded) {
	TomcatSqlModeStore.tomcatEmbedded = tomcatEmbedded;
    }

    /**
     * Stores a DataSource for a specified database.
     *
     * @param database   the database to store the DataSource for
     * @param dataSource the dataSource to set.
     */
    public static void setDataSource(String database, DataSource dataSource) {
	dataSourceMap.put(database, dataSource);
    }

    /**
     * Returns the DataSource associated to a database.
     *
     * @param database the database to store the DataSource for
     * @return the dataSource corresponding to the database
     */
    public static DataSource getDataSource(String database) {
	return dataSourceMap.get(database);
    }

}
