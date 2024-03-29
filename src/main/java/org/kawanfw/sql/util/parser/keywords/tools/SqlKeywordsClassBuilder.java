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
package org.kawanfw.sql.util.parser.keywords.tools;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

/**
 * @author Nicolas de Pomereu
 *
 */
class SqlKeywordsClassBuilder {
    
    public static String SQL_KEYWORDS_TXT = "/org/kawanfw/sql/util/parser/keywords/tools/sql_keywords.txt";
	
    public static void main(String[] args) throws IOException {
	SqlKeywordsFileReader sqlKeywordsFileReader = new SqlKeywordsFileReader(SQL_KEYWORDS_TXT);
	Set<String> keywords = sqlKeywordsFileReader.buildKeywordsSet();
	
	printClass(keywords, System.out);
	
    }

    /**
     * Prints the SqlKeywordsArray on a print stream
     * @param keywords
     * @param ps
     */
    public static void printClass(Set<String> keywords, PrintStream ps) {
	ps.println("package org.kawanfw.sql.util.parser.keywords;");
	ps.println();
	ps.println("public class SqlKeywordsArray {");
	ps.println();
	ps.println("	private static String [] KEYWORDS = { ");
	printKeywords(keywords, ps);
	ps.println("	}; ");
	ps.println("}");
    }

    /**
     * Prints all keywords on a print stream
     * @param keywords
     * @param ps
     */
    private static void printKeywords(Set<String> keywords, PrintStream ps) {
	for (String keyword : keywords) {
	    ps.println("		\"" +  keyword + "\",");
	}
	
    }

}
