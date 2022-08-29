/*
 * This file is part of KawanWall.
 * KawanWall: Firewall for SQL statements
 * Copyright (C) 2022,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.                 
 *                                                                         
 * KawanWall is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.         
 *              
 * KawanWall is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Affero General Public License for more details.       
 *                                  
 * You should have received a copy of the GNU Affero General 
 * Public License along with this program; if not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * If you develop commercial activities using KawanWall, you must: 
 * a) disclose and distribute all source code of your own product,
 * b) license your own product under the GNU General Public License.
 * 
 * You can be released from the requirements of the license by
 * purchasing a commercial license. Buying such a license will allow you 
 * to ship KawanWall with your closed source products without disclosing 
 * the source code.
 *
 * For more information, please contact KawanSoft SAS at this
 * address: sales@kawansoft.com
 * 
 * Any modifications to this file must keep this entire header
 * intact.
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
