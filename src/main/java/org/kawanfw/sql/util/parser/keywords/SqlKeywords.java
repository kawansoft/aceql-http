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
package org.kawanfw.sql.util.parser.keywords;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Loads the set of keywords in memory and allow to get them.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class SqlKeywords {

    private static Set<String> keywords = null;
    
    /**
     * Get the SQL keywords Set
     * @return the SQL keywords Set
     */
    public static Set<String> getKeywordSet() {
	if (keywords == null) {
	    keywords= new HashSet<String>(Arrays.asList(SqlKeywordsArray.KEYWORDS));
	}
	
	return keywords;
    }
}
