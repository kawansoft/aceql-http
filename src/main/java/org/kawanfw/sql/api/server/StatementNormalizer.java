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
package org.kawanfw.sql.api.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * Allows to "normalize" the text of a SQL statement. This will remove all
 * spaces, tabs or line feeds in excess. This allows to make sure that two SQL
 * statements that will give identical results but have a different text
 * representations are in fact equals. <br>
 * <br>
 * For example the two following statements:
 * 
 * <pre>
 * <code>
 "SELECT *     from     my_table   where my_colum   =   ?" 
 "SELECT 	*         from     my_table      where     my_colum   =   ?"     
 </code>
 * </pre>
 * 
 * will be normalized to the same String with extra spaces removed:
 * 
 * <pre>
 * <code>
"SELECT * from my_table where my_colum = ?" 
</code></pre>
 * 
 * Note that text between single quotes won't be modified:
 * <pre><code>
 * SELECT    *   from  customer where name = 'John Doe'
</code></pre>
 * will be normalized to:<br>
 * <br>
 * {@code SELECT * from customer where name = 'John Doe'} <br>
 * <br>
 * Note that in this version the normalization is straightforward and applied
 * only on text format, and not on parameters (?) replacement for
 * {@code PreparedStatement}. <br>
 * This means that the two statements: <br>
 * {@code select * from my_table where my_column = ?} and <br>
 * {@code select * from my_table where my_column = 9} <br>
 * will not change after applying normalization and thus still be considered different when
 * comparing with {@code String.equals(Object)}. 
 * <br>
 * &nbsp;
 * @author Nicolas de Pomereu
 * @since 11.0
 */
public class StatementNormalizer {

    private static boolean DEBUG = FrameworkDebug.isSet(StatementNormalizer.class);

    private static final String ACEQL_SINGLE_QUOTE = "**aceql_single_quote**";
    private static final String ACEQL_LT = "**aceql_lt**";
    private static final String ACEQL_GT = "**aceql_gt**";
    private static final String ACEQL_NE = "**aceql_ne**";

    /**
     * Returns the normalized text of the SQL statement.
     * 
     * @param sql the SQL statement to normalize
     * @return the normalized text of the SQL statement.
     */
    public static String getNormalized(String sql) {
	Objects.requireNonNull(sql, "sql cannot be null!");

	// Number of single quotes must be even
	int singleQuoteQuantity = StringUtils.countMatches(sql, "'");

	if (singleQuoteQuantity % 2 != 0) {
	    throw new IllegalArgumentException(
		    "Cannot normalize a statement with an odd number of single quotes: " + singleQuoteQuantity);
	}

	List<String> tokens = splitOnSinglesQuotes(sql);

	List<String> finalTokens = new ArrayList<>();

	for (int i = 0; i < tokens.size(); i++) {
	    debug(i + ": " + tokens.get(i));

	    // Even tokens contain no ' single quote
	    if (i % 2 == 0) {
		finalTokens.add(StatementNormalizer.getNormalizedSubtring(tokens.get(i)));
	    } else {
		// Odd tokens are between single quotes (')
		finalTokens.add("\'" + tokens.get(i) + "\'");
	    }
	}

	if (DEBUG) {
	    debug("");
	    for (int i = 0; i < finalTokens.size(); i++) {
		debug(i + ": " + finalTokens.get(i));
	    }
	}

	// Build final concatenation
	String normalized = StatementNormalizer.tokensTrimAndConcatenate(finalTokens);

	// Put bat double quotes
	normalized = normalized.replace(ACEQL_SINGLE_QUOTE, "''");

	return normalized;
    }

    private static String getNormalizedSubtring(String substring) {
	Objects.requireNonNull(substring, "substring cannot be null");
	if (substring.contains("\'")) {
	    throw new IllegalArgumentException("substring to normalize cannot contains quotes (\').");
	}

	if (substring.contains("\"")) {
	    throw new IllegalArgumentException("A Statement to normalize cannot contain double-quotes outside of a string enclosed in single quotes: " + substring);  
	}
	
	if (substring.contains(";")) {
	    throw new IllegalArgumentException("A Statement to normalize cannot contain semicolons outside of a string enclosed in single quotes: " + substring);  
	}
	
	// 1) Get tokens:
	List<String> tokens = getTokens(substring);

	// 2) Rebuild text with only space separation between elements:
	String normalizedString = tokensTrimAndConcatenate(tokens);
	return normalizedString;
    }

    private static List<String> splitOnSinglesQuotes(final String sql) {
	Objects.requireNonNull(sql, "sql cannot be null!");
	final String sqlToSplit = sql.replace("''", ACEQL_SINGLE_QUOTE);

	List<String> tokens = new ArrayList<>();
	StringTokenizer stringTokenizer = new StringTokenizer(sqlToSplit, "'", false);

	while (stringTokenizer.hasMoreElements()) {
	    tokens.add(stringTokenizer.nextToken());
	}

	return tokens;
    }

    /**
     * Split the input String on " "
     * 
     * @param str the input string to split
     * @return array of elements
     */
    private static List<String> getTokens(String str) {

	str = str.replace(",", " , ");
	str = str.replace("!=", ACEQL_NE);
	str = str.replace(">=", ACEQL_GT);
	str = str.replace("<=", ACEQL_LT);
	str = str.replace("=", " = ");
	str = str.replace(ACEQL_GT, " >= ");
	str = str.replace(ACEQL_LT, " <= ");
	str = str.replace(ACEQL_NE, " != ");

	List<String> tokens = new ArrayList<>();
	StringTokenizer tokenizer = new StringTokenizer(str, " ");
	while (tokenizer.hasMoreElements()) {
	    tokens.add(tokenizer.nextToken().trim());
	}
	return tokens;
    }

    /**
     * Trim each token of the input List and concatenate them
     * 
     * @param tokens the tokens to trim and concatenate
     * @return the final normalized string
     */
    private static String tokensTrimAndConcatenate(List<String> tokens) {
	StringBuffer stringBuffer = new StringBuffer();
	for (String token : tokens) {
	    if (token.isEmpty()) {
		continue;
	    }
	    stringBuffer.append(token.trim());
	    stringBuffer.append(" ");
	}
	String str = stringBuffer.toString();
	return str.trim();
    }

    private static void debug(String s) {
	if (DEBUG)
	    System.out.println(new Date() + " " + s);
    }
}
