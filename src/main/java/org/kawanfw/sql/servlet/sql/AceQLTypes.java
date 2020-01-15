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

    protected AceQLTypes() {

    }

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

}
