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
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * Allows to "normalize" the text of a {@code PreparedStatement}. This will
 * remove all spaces, tabs or line feeds in excess. This allows to make sure
 * that two {@code PreparedStatement} that will give identical results but have
 * a different text representation are in fact equals. <br>
 * <br>
 * For example the following String that represents a {@code PreparedStatement}:
 * 
 * <pre>
 * <code>
 "SELECT 	*         
     from     my_table where my_colum   =   ?" 
 </code>
 * </pre>
 * 
 * will be normalized to the String:
 * 
 * <pre>
 * <code>
"SELECT * from my_table where my_colum = ?" 
</code>
 * </pre>
 * 
 * Note that quotes (') are not supported in this version and will throw an
 * {@code IllegalArgumentException}.
 * 
 * @author Nicolas de Pomereu
 * @since 11.0
 */
public class PreparedStatementNormalizer {

    public static String getNormalizedText(String sql) {
	Objects.requireNonNull(sql, "sql cannot be null");
	if (sql.contains("\'")) {
	    throw new IllegalArgumentException("sql cannot contains quotes (\').");
	}
	
	// 1) Get tokens:
	List<String> tokens = getTokens(sql);
	
	// 2) Rebuild text with only space separation between elements:
	String normalizedString = tokensTrimAndConcatenate(tokens);
	return normalizedString;
    }

    /**
     * Split the input String on " "
     * @param str	the input string to split
     * @return array of elements 
     */
    private static List<String> getTokens(String str) {
	List<String> tokens = new ArrayList<>();
	StringTokenizer tokenizer = new StringTokenizer(str, " ");
	while (tokenizer.hasMoreElements()) {
	    tokens.add(tokenizer.nextToken().trim());
	}
	return tokens;
    }
    
    /**
     * Trim each token of the input List and concatenate them
     * @param tokens	the tokens to trim and concatenate
     * @return the final normalized string
     */
    private static String tokensTrimAndConcatenate(List<String> tokens) {
	StringBuffer stringBuffer = new StringBuffer();
	for (String token : tokens) {
	    if (token.isEmpty()) {
		continue;
	    }
	    stringBuffer.append(token.trim() + " ");
	}
	String str=  stringBuffer.toString();
	return str.trim();
    }

    
}
