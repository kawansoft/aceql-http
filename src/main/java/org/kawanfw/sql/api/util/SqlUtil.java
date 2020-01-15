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
package org.kawanfw.sql.api.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * Provides methods to get database product info & other utilities to format SQL
 * statements.
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */

public class SqlUtil {

    // Database product constants

    /** Constant for defining Microsoft Access */
    public static final String ACCESS = "ACCESS";

    /** Constant for defining Sybase Adaptive Server Enterprise */
    public static final String ADAPTIVE_SERVER_ENTERPRISE = "Adaptive Server Enterprise";

    /** Constant for defining DB2 */
    public static final String DB2 = "DB2";

    /** Constant for defining H2 */
    public static final String H2 = "H2";

    /** Constant for defining HyperSQL */
    public static final String HSQLDB = "HSQL Database Engine";

    /** Constant for defining Informix */
    public static final String INFORMIX = "Informix Dynamic Server";

    /** Constant for defining INGRES */
    public static final String INGRES = "INGRES";

    /** Constant for defining MySQL product */
    public static final String MYSQL = "MySQL";

    /** Constant for defining Oracle Database */
    public static final String ORACLE = "Oracle";

    /** Constant for defining PostgreSQL */
    public static final String POSTGRESQL = "PostgreSQL";

    /** Constant for defining Sybase SQL Anywhere */
    public static final String SQL_ANYWHERE = "SQL Anywhere";

    /** Constant for defining Microsoft SQL Server */
    public static final String SQL_SERVER = "SQL Server";

    /** Constant for defining Teradata */
    public static final String TERADATA = "Teradata";

    /** used to get the database product name */
    private String databaseProductName = null;

    /**
     * Constructor.
     * 
     * @param connection
     *            the JDBC Connection
     */
    public SqlUtil(Connection connection) throws SQLException {

	if (connection == null) {
	    throw new IllegalArgumentException("connection can not be null!");
	}

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	databaseProductName = databaseMetaData.getDatabaseProductName();
    }

    /**
     * Returns the database engine name.
     * 
     * @return the database engine name
     */
    public String getDatabaseProductName() {
	return this.databaseProductName;
    }

    /**
     * Returns true if the database engine is Microsoft Access
     * 
     * @return true if the database engine is Microsoft Access
     */
    public boolean isAccess() {
	return isProduct(ACCESS);
    }

    /**
     * Returns true if the database engine is Adaptive Server Enterprise.
     * 
     * @return true if the database engine is Adaptive Server Enterprise
     */
    public boolean isAdaptiveServerEnterprise() {
	return isProduct(ADAPTIVE_SERVER_ENTERPRISE);
    }

    /**
     * Returns true if the database engine is DB2.
     * 
     * @return true if the database engine is DB2
     */

    public boolean isDB2() {
	return isProduct(DB2);
    }

    /**
     * Returns true if the database engine is HSQLDB (HyperSQL Database).
     * 
     * @return true if the database engine is HSQLDB (HyperSQL Database)
     */

    public boolean isHSQLDB() {
	return isProduct(HSQLDB);
    }

    /**
     * Returns true if the database engine is H2.
     * 
     * @return true if the database engine is H2
     */

    public boolean isH2() {
	return isProduct(H2);
    }

    /**
     * Returns true if the database engine is Informix.
     * 
     * @return true if the database engine is Informix
     */
    public boolean isInformix() {
	return isProduct(INFORMIX);
    }

    /**
     * Returns true if the database engine is Ingres.
     * 
     * @return true if the database engine is Ingres
     */
    public boolean isIngres() {
	return isProduct(INGRES);
    }

    /**
     * Returns true if the database engine is MySQL.
     * 
     * @return true if the database engine is MySQL
     */
    public boolean isMySQL() {
	return isProduct(MYSQL);
    }

    /**
     * Returns true if the database engine is Oracle Database.
     * 
     * @return true if the database engine is Oracle Database
     */
    public boolean isOracle() {
	return isProduct(ORACLE);
    }

    /**
     * Returns true if the database engine is PostgreSQL.
     * 
     * @return true if the database engine is PostgreSQL
     */
    public boolean isPostgreSQL() {
	return isProduct(POSTGRESQL);
    }

    /**
     * Returns true if the database engine is SQL Anywhere.
     * 
     * @return true if the database engine is SQL Anywhere
     */

    public boolean isSQLAnywhere() {
	return isProduct(SQL_ANYWHERE);
    }

    /**
     * Returns true if the database engine is Microsoft SQL Server.
     * 
     * @return true if the database engine is Microsoft SQL Server
     */

    public boolean isSQLServer() {
	return isProduct(SQL_SERVER);
    }

    /**
     * Returns true if the database engine is Teradata.
     * 
     * @return true if the database engine is Teradata
     */
    public boolean isTeradata() {
	return isProduct(TERADATA);
    }

    /**
     * Returns true if the passed product (database engine) is the current
     * product.
     * 
     * @param product
     *            the product name to test
     * @return true if the actual loaded database is the product
     */
    private boolean isProduct(String product) {
	return databaseProductName.toLowerCase().contains(product.toLowerCase())
		? true
		: false;
    }

    /**
     * Transforms a List into a SQL {@code 'IN(..., ...)'} of objects separated
     * with commas.
     * 
     * @param objects
     *            the List of objects
     * @return String of objects separated by commas for SQL
     *         {@code 'IN(..., ...)'} format.
     */

    public static String listToSqlList(List<?> objects) {
	StringBuffer sb = new StringBuffer();

	String sQuote = (objects.get(0) instanceof String) ? "'" : "";

	for (int i = 0; i < objects.size(); i++) {
	    if (i != 0) {
		sb.append(", ");
	    }

	    sb.append(sQuote);
	    sb.append(objects.get(i));
	    sb.append(sQuote);
	}

	return sb.toString();
    }

}
