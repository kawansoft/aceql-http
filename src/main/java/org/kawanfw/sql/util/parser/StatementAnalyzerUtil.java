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

import java.util.List;

import org.kawanfw.sql.api.server.StatementNormalizer;

/**
 * @author Nicolas de Pomereu
 *
 */
public class StatementAnalyzerUtil {

    /**
     * Replace fulltext with _fulltext_ because of a bug in JSQLParser.
     * @param sql	the sql query
     * @return	the sql query with fulltext replaced by _fulltext_ and FULLTEXT replaced by _FULLTEXT_
     */
    public static String fixForJsqlparser(final String sql) {

	if (sql == null) {
	    return null;
	}

	String theSql = sql;
	if (theSql.contains(" fulltext")) {
	    theSql = theSql.replace(" fulltext", " _fulltext_");
	}
	if (theSql.contains(" FULLTEXT")) {
	    theSql = theSql.replace(" FULLTEXT", " _FULLTEXT_");
	}
	if (theSql.contains("fulltext ")) {
	    theSql = theSql.replace("fulltext ", "_fulltext_ ");
	}
	if (theSql.contains("FULLTEXT ")) {
	    theSql = theSql.replace("FULLTEXT ", "_FULLTEXT_ ");
	}
	
	return theSql;
    }

    /**
     * @param finalTokens
     */
    public static void debugDisplayTokens(List<String> finalTokens) {
        if (StatementNormalizer.DEBUG) {
            StatementNormalizer.debug("");
            StatementNormalizer.debug("display 3:");
            for (int i = 0; i < finalTokens.size(); i++) {
        	StatementNormalizer.debug(i + ": " + finalTokens.get(i));
            }
        }
    }

}