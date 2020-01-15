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
package org.kawanfw.sql.servlet.sql;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.util.SqlUtil;
import org.kawanfw.sql.util.FrameworkDebug;

import oracle.jdbc.driver.OracleConnection;

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

/*
 * 
 * This class needs an oracle driver jar to compile. (Example: ojdbc6.jar with
 * Oracle Database 11g.)
 * 
 * If you don't use Oracle, just comment the code between the two tags: // BEGIN
 * COMMENT & // END COMMENT to allow compilation.
 */

public class DbEngineManager {

    private static boolean DEBUG = FrameworkDebug.isSet(DbEngineManager.class);

    /**
     * No constructor
     */
    protected DbEngineManager() {

    }

    /**
     * Add a " limit 1 " clause to a sqlOrder.
     * 
     * @param sqlOrder
     *            the sql order
     * @return the sql order with safely added " limit 1 "
     */
    public static String addLmt1(String sqlOrder, Connection connection)
	    throws SQLException {

	sqlOrder = sqlOrder.trim();

	if (!sqlOrder.toLowerCase().startsWith("select ")) {
	    return sqlOrder;
	}

	SqlUtil sqlUtil = new SqlUtil(connection);

	if (sqlUtil.isPostgreSQL() || sqlUtil.isMySQL() || sqlUtil.isH2()
		|| sqlUtil.isHSQLDB()) {

	    if (DbEngineManagerUtil.containsWord(sqlOrder, "limit")) {
		return sqlOrder;
	    }

	    sqlOrder = DbEngineManagerUtil.removeSemicolon(sqlOrder);

	    sqlOrder += " LIMIT 1";

	    printOrder(sqlUtil.getDatabaseProductName(), sqlOrder);
	    return sqlOrder;
	} else if (sqlUtil.isSQLServer() || sqlUtil.isSQLAnywhere()
		|| sqlUtil.isAdaptiveServerEnterprise()) {
	    // replace the starting "select" by "select TOP 1 "
	    sqlOrder = sqlOrder.substring(0, "select".length()) + " TOP 1 "
		    + sqlOrder.substring("select".length() + 1);
	    printOrder(sqlUtil.getDatabaseProductName(), sqlOrder);
	    return sqlOrder;
	} else if (sqlUtil.isInformix()) {
	    // replace the starting "select" by "select FIRST 1 "
	    sqlOrder = sqlOrder.substring(0, "select".length()) + " FIRST 1 "
		    + sqlOrder.substring("select".length() + 1);
	    printOrder(sqlUtil.getDatabaseProductName(), sqlOrder);
	    return sqlOrder;
	} else if (sqlUtil.isDB2() || sqlUtil.isIngres()) {

	    if (DbEngineManagerUtil.containsWord(sqlOrder, "fetch")) {
		return sqlOrder;
	    }

	    sqlOrder = DbEngineManagerUtil.removeSemicolon(sqlOrder);

	    sqlOrder += " FETCH FIRST 1 ROWS ONLY";
	    printOrder(sqlUtil.getDatabaseProductName(), sqlOrder);
	    return sqlOrder;
	} else if (sqlUtil.isOracle()) {

	    // We can do nothing with previous Oracle versions...
	    if (!isOracleVersionGtOrEq12c(connection)) {
		printOrder(sqlUtil.getDatabaseProductName(), sqlOrder);
		return sqlOrder;
	    }

	    // FETCH FIRST 1 ROWS ONLY
	    if (DbEngineManagerUtil.containsWord(sqlOrder, "fetch")) {
		return sqlOrder;
	    }

	    sqlOrder = DbEngineManagerUtil.removeSemicolon(sqlOrder);

	    sqlOrder += " FETCH NEXT 1 ROWS ONLY";
	    printOrder(sqlUtil.getDatabaseProductName(), sqlOrder);
	    return sqlOrder;
	} else {
	    printOrder(sqlUtil.getDatabaseProductName(), sqlOrder);
	    return sqlOrder;
	}

    }

    /**
     * Prints the product name and the SQL Order with the LIMIT 1 or equivalent
     * 
     * @param databaseProductName
     * @param sqlOrder
     */
    private static void printOrder(String databaseProductName,
	    String sqlOrder) {
	debug("databaseProductName: " + databaseProductName);
	debug("sqlOrder LIMIT 1   : " + sqlOrder);
    }

    /**
     * Checks the DBVendor and license validity and throws a
     * 
     * @param properties
     *            properties that contains license info
     * @param connection
     *            JDBC Connection, necessary
     * @throws SqlConfigurationException
     *             if license is invalid
     */
    public static boolean checkDb(Properties properties, Connection connection)
	    throws DatabaseConfigurationException {
	return true;
    }

    /**
     * Creates an Array for db
     * 
     * @param connection
     *            the JDBC connection
     * @param typeName
     *            the array typeName
     * @param elements
     *            the array elements
     * @return an Array for db
     * @throws SQLException
     *             if an y SQL Exception occurs
     */
    public static Array createArrayOf(Connection connection, String typeName,
	    Object[] elements) throws SQLException {
	Array array = null;

	if (new SqlUtil(connection).isOracle()) {
	    // BEGIN COMMENT
	    if (connection.isWrapperFor(OracleConnection.class)) {
		OracleConnection oracleConnection = connection
			.unwrap(OracleConnection.class);
		array = oracleConnection.createARRAY(typeName, elements);
	    } else {
		array = ((oracle.jdbc.OracleConnection) connection)
			.createARRAY(typeName, elements);
	    }
	    // END COMMENT
	} else {
	    array = connection.createArrayOf(typeName, elements);
	}

	return array;
    }

    /**
     * Returns true if Oracle Version is >= 12.1 (12.c)
     * 
     * @param connection
     * @return true if Oracle Version is >= 12.1 (12.c)
     * @throws SQLException
     */
    public static boolean isOracleVersionGtOrEq12c(Connection connection)
	    throws SQLException {
	DatabaseMetaData databaseMetaData = connection.getMetaData();
	int versionMajor = databaseMetaData.getDatabaseMajorVersion();
	int versionMinnor = databaseMetaData.getDatabaseMinorVersion();

	if (versionMajor < 12) {
	    return false;
	} else if (versionMajor == 12) {
	    if (versionMinnor >= 1) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return true;
	}

    }

    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(s);
	}
    }

}
