/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.sql;

import java.sql.Types;

import org.apache.commons.lang3.StringUtils;

/**
 * Class to convert a SQL type as string into the numerical java.sql.types and
 * reverse
 *
 * @author Nicolas de Pomereu
 *
 */
public class JavaSqlConversion {

    /**
     * Decode the String param type passed by user on client side into a Types
     * corresponding value.
     *
     * @param sqlParamType
     * @return
     */
    public static int fromSqlToJava(String sqlParamType) {
	int javaType = 0;

	if (sqlParamType.equals(AceQLTypes.CHARACTER)) {
	    javaType = Types.CHAR;
	} else if (sqlParamType.equals(AceQLTypes.VARCHAR)) {
	    javaType = Types.VARCHAR;
	} else if (sqlParamType.equals(AceQLTypes.LONGVARCHAR)) {
	    javaType = Types.LONGVARCHAR;
	}
	else if (sqlParamType.equals(AceQLTypes.BIT)) {
	    javaType = Types.BIT;
	}
	// HACK DefaultVersion 3.2.2: add AceQLTypes.DECIMAL
	else if (sqlParamType.equals(AceQLTypes.DECIMAL)) {
	    javaType = Types.DECIMAL;
	}
	else if (sqlParamType.equals(AceQLTypes.NUMERIC)) {
	    javaType = Types.NUMERIC;
	} else if (sqlParamType.equals(AceQLTypes.TINYINT)) {
	    javaType = Types.TINYINT;
	} else if (sqlParamType.equals(AceQLTypes.SMALLINT)) {
	    javaType = Types.SMALLINT;
	} else if (sqlParamType.equals(AceQLTypes.INTEGER)) {
	    javaType = Types.INTEGER;
	} else if (sqlParamType.equals(AceQLTypes.BIGINT)) {
	    javaType = Types.BIGINT;
	} else if (sqlParamType.equals(AceQLTypes.REAL)) {
	    javaType = Types.REAL;
	} else if (sqlParamType.equals(AceQLTypes.FLOAT)) {
	    javaType = Types.FLOAT;
	} else if (sqlParamType.equals(AceQLTypes.DOUBLE_PRECISION)) {
	    javaType = Types.DOUBLE;
	} else if (sqlParamType.equals(AceQLTypes.DATE)) {
	    javaType = Types.DATE;
	} else if (sqlParamType.equals(AceQLTypes.TIME)) {
	    javaType = Types.TIME;
	} else if (sqlParamType.equals(AceQLTypes.TIMESTAMP)) {
	    javaType = Types.TIMESTAMP;
	} else if (sqlParamType.equals(AceQLTypes.BINARY)) {
	    javaType = Types.BINARY;
	} else if (sqlParamType.equals(AceQLTypes.VARBINARY)) {
	    javaType = Types.VARBINARY;
	} else if (sqlParamType.equals(AceQLTypes.DATE)) {
	    javaType = Types.LONGVARBINARY;
	} else if (sqlParamType.equals(AceQLTypes.BLOB)) {
	    javaType = Types.BLOB;
	} else if (sqlParamType.equals(AceQLTypes.CLOB)) {
	    javaType = Types.CLOB;
	} else if (sqlParamType.startsWith("TYPE_NULL")) {
	    String javaTypeStr = StringUtils.substringAfter(sqlParamType,
		    "TYPE_NULL");
	    javaType = Integer.parseInt(javaTypeStr);
	} else {
	    throw new IllegalArgumentException(
		    "Unsuported data type for null setting: " + sqlParamType);
	}
	return javaType;
    }

    public static String fromJavaToSql(int javaType) {
	String sqlType = null;

	if (javaType == Types.CHAR) {
	    sqlType = AceQLTypes.CHAR;
	} else if (javaType == Types.VARCHAR) {
	    sqlType = AceQLTypes.VARCHAR;
	} else if (javaType == Types.LONGVARCHAR) {
	    sqlType = AceQLTypes.LONGVARCHAR;
	} else if (javaType == Types.NUMERIC) {
	    sqlType = AceQLTypes.NUMERIC;
	} else if (javaType == Types.BIT) {
	    sqlType = AceQLTypes.BIT;
	} else if (javaType == Types.NUMERIC) {
	    sqlType = AceQLTypes.NUMERIC;
	} else if (javaType == Types.DECIMAL) {
	    sqlType = AceQLTypes.DECIMAL;
	} else if (javaType == Types.TINYINT) {
	    sqlType = AceQLTypes.TINYINT;
	} else if (javaType == Types.SMALLINT) {
	    sqlType = AceQLTypes.SMALLINT;
	} else if (javaType == Types.INTEGER) {
	    sqlType = AceQLTypes.INTEGER;
	} else if (javaType == Types.BIGINT) {
	    sqlType = AceQLTypes.BIGINT;
	} else if (javaType == Types.REAL) {
	    sqlType = AceQLTypes.REAL;
	} else if (javaType == Types.FLOAT) {
	    sqlType = AceQLTypes.FLOAT;
	} else if (javaType == Types.DOUBLE) {
	    sqlType = AceQLTypes.DOUBLE_PRECISION;
	} else if (javaType == Types.DATE) {
	    sqlType = AceQLTypes.DATE;
	} else if (javaType == Types.TIME) {
	    sqlType = AceQLTypes.TIME;
	} else if (javaType == Types.TIMESTAMP) {
	    sqlType = AceQLTypes.TIMESTAMP;
	} else if (javaType == Types.BINARY) {
	    sqlType = AceQLTypes.BINARY;
	} else if (javaType == Types.VARBINARY) {
	    sqlType = AceQLTypes.VARBINARY;
	} else if (javaType == Types.LONGVARBINARY) {
	    sqlType = AceQLTypes.LONGVARBINARY;
	} else if (javaType == Types.BLOB) {
	    sqlType = AceQLTypes.BLOB;
	} else if (javaType == Types.CLOB) {
	    sqlType = AceQLTypes.CLOB;
	} else {
	    return "UNKNOWN";
	}
	return sqlType;
    }

}
