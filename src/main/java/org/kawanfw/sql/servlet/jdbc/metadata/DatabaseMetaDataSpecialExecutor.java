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

package org.kawanfw.sql.servlet.jdbc.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.kawanfw.sql.jdbc.metadata.ArrayTransporter;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 *
 * Dedicated executor for DatabaseMetaData.getTables() &
 * DatabaseMetaData.getUDTs() that have special array String[] or int[]
 * parameters.
 *
 * @author Nicolas de Pomereu
 */
public class DatabaseMetaDataSpecialExecutor {
    /** Set to true to display/log debug info */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(DatabaseMetaDataSpecialExecutor.class);

    /** DatabaseMetaData instance */
    private DatabaseMetaData databaseMetaData = null;

    /* the DatabaseMetaData special method to execute */
    private String methodName = null;

    /* the parameters of the special method to execute */
    private List<String> listParamsValues = null;

    /**
     *
     * @param databaseMetaData
     *            DatabaseMetaData instance
     * @param methodName
     *            the DatabaseMetaData special method to execute
     * @param listParamsValues
     *            the parameters of the special method to execute
     */
    public DatabaseMetaDataSpecialExecutor(DatabaseMetaData databaseMetaData,
	    String methodName, List<String> listParamsValues) {
	if (databaseMetaData == null) {
	    throw new IllegalArgumentException(
		    "databaseMetaData can not be null!");
	}

	if (methodName == null) {
	    throw new IllegalArgumentException("methodName can not be null!");
	}

	if (listParamsValues == null) {
	    throw new IllegalArgumentException(
		    "listParamsValues can not be null!");
	}

	this.databaseMetaData = databaseMetaData;
	this.methodName = methodName;
	this.listParamsValues = listParamsValues;
    }

    /**
     * Execute the special method
     *
     * @return the Result set of DatabaseMetaData.getTables or
     *         DatabaseMetaData.getUDTs
     * @throws SQLException
     */

    public ResultSet execute() throws SQLException {
	ResultSet rs = null;

	if (methodName.equals("getTables")) {
	    rs = executeGetTables();
	} else if (methodName.equals("getUDTs")) {
	    rs = executeGetUDTs();
	} else if (methodName.equals("getPrimaryKeys")) {
	    rs = executeGetPrimaryKeys();
	} else {
	    throw new IllegalArgumentException(
		    "Invalid DatabaseMetaData method name. "
			    + "Must be \"getTables\" or \"getUDTs\" or \"getPrimaryKeys\": "
			    + methodName);
	}

	return rs;

    }

    /**
     * Execute DatabaseMetaData.getTables
     *
     * @return the Result set
     * @throws SQLException
     */
    private ResultSet executeGetTables() throws SQLException {
	// Method prototype:
	// ResultSet getTables(String catalog, String schemaPattern,
	// String tableNamePattern, String types[]) throws SQLException;

	String catalog = listParamsValues.get(0);
	String schemaPattern = listParamsValues.get(1);
	String tableNamePattern = listParamsValues.get(2);
	String join = listParamsValues.get(3);

	debug("catalog          : " + catalog + ":");
	debug("schemaPattern    : " + schemaPattern + ":");
	debug("tableNamePattern : " + tableNamePattern + ":");
	debug("join             : " + join + ":");

	// String[] types =
	// CallMetaDataTransport.fromJsonStringArray(jsonString);
	//String[] types = StringArrayTransport.fromJson(jsonString);

	String[] types = ArrayTransporter.stringToStringArray(join);

	// Detect null transported values
	if (catalog.equals("NULL"))
	    catalog = null;
	if (schemaPattern.equals("NULL"))
	    schemaPattern = null;
	if (tableNamePattern.equals("NULL"))
	    tableNamePattern = null;

	if (types != null && types.length == 1 && types[0].equals("NULL")) {
	    types = null;
	}

	ResultSet rsMetaTables = databaseMetaData.getTables(catalog,
		schemaPattern, tableNamePattern, types);
	return rsMetaTables;
    }

    /**
     * Execute DatabaseMetaData.getUDTs
     *
     * @return the Result set
     * @throws SQLException
     */
    private ResultSet executeGetUDTs() throws SQLException {
	// Method prototype:
	// ResultSet getUDTs(String catalog, String schemaPattern,
	// String typeNamePattern, int[] types)
	// throws SQLException;

	String catalog = listParamsValues.get(0);
	String schemaPattern = listParamsValues.get(1);
	String tableNamePattern = listParamsValues.get(2);
	String join = listParamsValues.get(3);

	int[] types = ArrayTransporter.stringToIntArray(join);

	// Detect null transported values
	if (catalog.equals("NULL"))
	    catalog = null;
	if (schemaPattern.equals("NULL"))
	    schemaPattern = null;
	if (tableNamePattern.equals("NULL"))
	    tableNamePattern = null;

	if (types != null && types.length == 1 && types[0] == -999)
	    types = null;

	ResultSet rs = databaseMetaData.getUDTs(catalog, schemaPattern,
		tableNamePattern, types);
	return rs;
    }

    /**
     * Execute DatabaseMetaData.executeGetPrimaryKeys
     *
     * @return the Result set
     * @throws SQLException
     */
    private ResultSet executeGetPrimaryKeys() throws SQLException {
	// Method prototype:
	// ResultSet getUDTs(String catalog, String schemaPattern,
	// String typeNamePattern, int[] types)
	// throws SQLException;

	String catalog = listParamsValues.get(0);
	String schemaPattern = listParamsValues.get(1);
	String tableNamePattern = listParamsValues.get(2);

	// catalog = HtmlConverter.fromHtml(catalog);
	// schemaPattern = HtmlConverter.fromHtml(schemaPattern);
	// tableNamePattern = HtmlConverter.fromHtml(tableNamePattern);

	// Detect null transported values
	if (catalog.equals("NULL"))
	    catalog = null;
	if (schemaPattern.equals("NULL"))
	    schemaPattern = null;
	if (tableNamePattern.equals("NULL"))
	    tableNamePattern = null;

	ResultSet rs = databaseMetaData.getPrimaryKeys(catalog, schemaPattern,
		tableNamePattern);
	return rs;
    }

    /**
     * Method called by children Servlest for debug purpose Println is done only
     * if class name name is in kawansoft-debug.ini
     */
    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
