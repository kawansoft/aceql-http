/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.util.parser;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Detects if sql statement part without values in quotes has comments
 * @author Nicolas de Pomereu
 *
 */
public class SqlCommentsDetector {

    private static Pattern commentPattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    
    private String sql;

    private boolean withComments;

    public SqlCommentsDetector(String sql) {
	this.sql = sql;
    }
    
    /**
     * @return true if the SQL statement part contains comments
     */
    public boolean isWithComments() {
        return withComments;
    }

    /**
     * Remove the comments from the statement part
     * @return statement part without comments
     */
    public String removeComments() {
	// 1) Surrounds /* and */ with spaces
	sql = StringUtils.replace(sql, "/*", " /* ");
	sql = StringUtils.replace(sql, "*/", " */ ");
	
	// 2) Remvoe then 
	final String sqlOut = commentPattern.matcher(sql).replaceAll("");
	
	this.withComments = sql.length() == sqlOut.length() ? false:true;
	
	return sqlOut;
    }
    
   
}
