/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.sql;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * All the allowed SQL types in software
 *
 * @author Nicolas de Pomereu
 *
 */
public class AceQLTypes {


    public static final String BIGINT = "BIGINT";
    public static final String BINARY = "BINARY";
    public static final String BIT = "BIT";
    public static final String BLOB = "BLOB";
    public static final String CHAR = "CHAR";
    public static final String CHARACTER = "CHARACTER";
    public static final String CLOB = "CLOB";
    public static final String DATE = "DATE";
    public static final String DECIMAL = "DECIMAL";
    public static final String DOUBLE_PRECISION = "DOUBLE_PRECISION";
    public static final String FLOAT = "FLOAT";
    public static final String INTEGER = "INTEGER";
    public static final String LONGVARBINARY = "LONGVARBINARY";
    public static final String LONGVARCHAR = "LONGVARCHAR";
    public static final String NUMERIC = "NUMERIC";
    public static final String REAL = "REAL";
    public static final String SMALLINT = "SMALLINT";
    public static final String TIME = "TIME";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String TINYINT = "TINYINT";
    public static final String URL = "URL";
    public static final String VARBINARY = "VARBINARY";
    public static final String VARCHAR = "VARCHAR";

    private static final String[] SQL_TYPES = { BIGINT, BINARY, BIT, BLOB, CHAR,
	    CHARACTER, CLOB, DATE, DECIMAL, DOUBLE_PRECISION, FLOAT, INTEGER,
	    LONGVARBINARY, LONGVARCHAR, NUMERIC, REAL, SMALLINT, TIME,
	    TIMESTAMP, TINYINT, URL, VARBINARY, VARCHAR };

    public static final Set<String> SQL_TYPES_SET = new HashSet<String>(
	    Arrays.asList(SQL_TYPES));

    protected AceQLTypes() {

    }

}
