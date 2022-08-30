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
package org.kawanfw.sql.util.parser.keywords.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Nicolas de Pomereu
 *
 */
class SqlKeywordsFileReader {

    private String name;
	

    public SqlKeywordsFileReader(String name) {
	this.name = name;
    }

    public Set<String> buildKeywordsSet() throws IOException {
	
	Set<String> keywords = new LinkedHashSet<>();
	
	InputStream in = getClass().getResourceAsStream(name);
	try (BufferedReader bufferedReader = new BufferedReader(
		new InputStreamReader(in));) {

	    String line = null;
	    while ((line = bufferedReader.readLine()) != null) {
		keywords.add(line.toUpperCase().trim());
	    }
	}
	
	return keywords;
    }

    public static void main(String[] args) throws IOException {
	SqlKeywordsFileReader sqlKeywordsFileReader = new SqlKeywordsFileReader(SqlKeywordsClassBuilder.SQL_KEYWORDS_TXT);
	Set<String> keywords = sqlKeywordsFileReader.buildKeywordsSet();
	for (String keyword : keywords) {
	    System.out.println(keyword);
	}
    }

}
