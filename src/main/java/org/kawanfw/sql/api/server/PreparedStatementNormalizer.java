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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    
    /**
     * Split string on spaces in java, except if between quotes.
     * Future usage. See https://code-examples.net/en/q/5967a.
     * @param str  the string to split
     * @return the split string
     */
    public static List<String> splitOnSpaces(String str) {
	//String str = "This is a string that \"will be\" highlighted when your 'regular expression' matches something.";
	str = str + " "; // add trailing space
	int len = str.length();
	Matcher m = Pattern.compile("((\"[^\"]+?\")|('[^']+?')|([^\\s]+?))\\s++").matcher(str);

	List<String> tokens = new ArrayList<>();
	
	for (int i = 0; i < len; i++)
	{
	    m.region(i, len);

	    if (m.lookingAt())
	    {
	        String s = m.group(1);

	        if ((s.startsWith("\"") && s.endsWith("\"")) ||
	            (s.startsWith("'") && s.endsWith("'")))
	        {
	            s = s.substring(1, s.length() - 1);
	        }

	        //System.out.println(i + ": \"" + s + "\"");
	        tokens.add(s);
	        i += (m.group(0).length() - 1);
	    }
	}
	
	return tokens;
	
    }

    
}
