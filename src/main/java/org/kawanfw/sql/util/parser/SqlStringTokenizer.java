/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.util.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.parser.keywords.SqlKeywords;

/**
 * Split SQL string on space. Do it securely by addinf extras spaces on operators that cab be glued to values
 * @author Nicolas de Pomereu
 *
 */
public class SqlStringTokenizer {
    public static boolean DEBUG = FrameworkDebug.isSet(SqlStringTokenizer.class);
    
    public static final String KAWAN_SINGLE_QUOTE = "__kawan_single_quote__";
    
    private static final String KAWAN_LT = "__kawan_lt__";
    private static final String KAWAN_GT = "__kawan_gt__";
    private static final String KAWAN_NE = "__kawan_ne__";
    private static final String KAWAN_NE_2 = "__kawan_ne2__";

    private static final String KAWAN_GTE = "__kawan_gte__";
    private static final String KAWAN_LTE = "__kawan_lte__";

    private static final String KAWAN_PLUS = "__kawan_plus__";
    private static final String KAWAN_MINUS = "__kawan_minus__";
    private static final String KAWAN_MULTIPLY = "__kawan_muliply__";;
    private static final String KAWAN_DIVIDE = "__kawan_divide__";
    private static final String KAWAN_MODULO = "__kawan_modulo__";

    private static final String KAWAN_ADD_EQUALS = "__kawan_add_equals__";
    private static final String KAWAN_SUBTRACT_EQUALS = "__kawan_subtract_equals__";
    private static final String KAWAN_MULTIPLY_EQUALS = "__kawan_multiply_equals__";
    private static final String KAWAN_DIVIDE_EQUALS = "__kawan_divide_equals__";
    private static final String KAWAN_MODULO_EQUALS = "__kawan_modulo_equals__";
    private static final String KAWAN_BITWISE_AND_EQUALS = "__kawan_bitwise_and_equals__";
    private static final String KAWAN_BITWISE_EXCLUSIVE_EQUALS = "__kawan_bitwise_exclusive_equals__";
    private static final String KAWAN_BITWISE_OR_EQUALS = "__kawan_bitwise_or_equals__";

    private static final String KAWAN_HASHTAG = "__kawan_hashtag__";;
    private static final String KAWAN_COMMENTS_MINUS = "__kawan_comments_minus__";
    private static final String KAWAN_COMMENTS_OPENING = "__kawan_comments_opening__";
    private static final String KAWAN_COMMENTS_CLOSING = "__kawan_comments_closing__";

    private static final String KAWAN_NULL_SAFE_EQUAL = "__kawan_null_safe_equal__";

    private static final String KAWAN_EXCLAMATION = "__KAWAN_EXCLAMATION__";
    private static final String KAWAN_AMPERSAND = "__KAWAN_AMPERSAND__";
    private static final String KAWAN_RIGHT_SHIFT = "__KAWAN_RIGHT_SHIFT__";
    private static final String KAWAN_LEFT_SHIFT = "__KAWAN_LEFT_SHIFT__";

    private static final String KAWAN_INVERT_BIT = "__KAWAN_INVERT_BIT__";
    private static final String KAWAN_BITWISE_OR = "__KAWAN_BITWISE_OR__";
    private static final String KAWAN_BITWISE_XOR = "__KAWAN_BITWISE_XOR__";

    /**
     * Split the input String on " "
     * 
     * @param str the input string to split
     * @return array of elements
     */
    public static List<String> getTokensSplitOnSpace(final String str) {

	String strNew = str;
	strNew = replaceOperatorsAddBlanksApache(strNew);
	
	List<String> tokens = new ArrayList<>();
	StringTokenizer tokenizer = new StringTokenizer(strNew, " ");
	while (tokenizer.hasMoreElements()) {
	    String token = tokenizer.nextToken().trim();
	    
	    // Uppercase if SQL keyword, else lowercase
	    if (SqlKeywords.getKeywordSet().contains(token.toUpperCase())) {
		token = token.toUpperCase();
	    }
	    else {
		token = token.toLowerCase();
	    }
	    
	    tokens.add(token);
	}
	return tokens;
    }

    /**
     * Replace operators with values surrounded by blanks
     * @param strNew
     * @return
     */
    /**
    <pre><code>
    private static String replaceOperatorsAddBlanks(String strNew) {
	strNew = strNew.replace("(", " ( ");
	strNew = strNew.replace(")", " ) ");
	strNew = strNew.replace(",", " , ");
	strNew = strNew.replace("!=", KAWAN_NE);
	strNew = strNew.replace("<>", KAWAN_NE_2);

	strNew = strNew.replace(">=", KAWAN_GTE);
	strNew = strNew.replace("<=", KAWAN_LTE);

	strNew = strNew.replace(">", KAWAN_GT);
	strNew = strNew.replace("<", KAWAN_LT);

	strNew = strNew.replace("+=", KAWAN_ADD_EQUALS);
	strNew = strNew.replace("-=", KAWAN_SUBTRACT_EQUALS);
	strNew = strNew.replace("*=", KAWAN_MULTIPLY_EQUALS);
	strNew = strNew.replace("/=", KAWAN_DIVIDE_EQUALS);
	strNew = strNew.replace("%=", KAWAN_MODULO_EQUALS);
	strNew = strNew.replace("&=", KAWAN_BITWISE_AND_EQUALS);
	strNew = strNew.replace("^-=", KAWAN_BITWISE_EXCLUSIVE_EQUALS);
	strNew = strNew.replace("|*=", KAWAN_BITWISE_OR_EQUALS);
	
	strNew = strNew.replace("+", KAWAN_PLUS);
	
	strNew = strNew.replace("-", KAWAN_MINUS);
	strNew = strNew.replace("*", KAWAN_MULTIPLY);
	strNew = strNew.replace("/", KAWAN_DIVIDE);
	strNew = strNew.replace("%", KAWAN_MODULO);

	strNew = strNew.replace("=", " = ");
	
	strNew = strNew.replace(KAWAN_ADD_EQUALS, " += ");
	strNew = strNew.replace(KAWAN_SUBTRACT_EQUALS, " -= ");
	strNew = strNew.replace(KAWAN_MULTIPLY_EQUALS, " *= ");
	strNew = strNew.replace(KAWAN_DIVIDE_EQUALS, " /= ");
	strNew = strNew.replace(KAWAN_MODULO_EQUALS, " %= ");
	strNew = strNew.replace(KAWAN_BITWISE_AND_EQUALS, " &= ");
	strNew = strNew.replace(KAWAN_BITWISE_EXCLUSIVE_EQUALS, " ^-= ");
	strNew = strNew.replace(KAWAN_BITWISE_OR_EQUALS, " |*= ");
	
	//System.out.println(strNew);
	strNew = strNew.replace(KAWAN_PLUS, " + ");
	strNew = strNew.replace(KAWAN_MINUS, " - ");
	strNew = strNew.replace(KAWAN_MULTIPLY, " * ");
	strNew = strNew.replace(KAWAN_DIVIDE, " / ");
	strNew = strNew.replace(KAWAN_MODULO, " % ");
	
	strNew = strNew.replace(KAWAN_GTE, " >= ");
	strNew = strNew.replace(KAWAN_LTE, " <= ");

	strNew = strNew.replace(KAWAN_GT, " > ");
	strNew = strNew.replace(KAWAN_LT, " < ");

	strNew = strNew.replace(KAWAN_NE, " != ");
	strNew = strNew.replace(KAWAN_NE_2, " <> ");
	return strNew;
    }
    </code></pre>
    */
    
    /**
     * Replace operators with values surrounded by blanks
     * @param strNew
     * @return
     */
    public static String replaceOperatorsAddBlanksApache(String strNew) {
	strNew = StringUtils.replace(strNew, "(", " ( ");
	strNew = StringUtils.replace(strNew, ")", " ) ");
	strNew = StringUtils.replace(strNew, ",", " , ");
	strNew = StringUtils.replace(strNew, "!=", KAWAN_NE);
	strNew = StringUtils.replace(strNew, "<>", KAWAN_NE_2);
	
	strNew = StringUtils.replace(strNew, "<=>", KAWAN_NULL_SAFE_EQUAL);
	
	strNew = StringUtils.replace(strNew, ">>", KAWAN_RIGHT_SHIFT);
	strNew = StringUtils.replace(strNew, "<<", KAWAN_LEFT_SHIFT);
	
	strNew = StringUtils.replace(strNew, ">=", KAWAN_GTE);
	strNew = StringUtils.replace(strNew, "<=", KAWAN_LTE);
	
	strNew = StringUtils.replace(strNew, ">", KAWAN_GT);
	strNew = StringUtils.replace(strNew, "<", KAWAN_LT);

	strNew = StringUtils.replace(strNew, "#", KAWAN_HASHTAG);
	strNew = StringUtils.replace(strNew, "--", KAWAN_COMMENTS_MINUS);
	strNew = StringUtils.replace(strNew, "/*", KAWAN_COMMENTS_OPENING);
	strNew = StringUtils.replace(strNew, "*/", KAWAN_COMMENTS_CLOSING);
	
	strNew = StringUtils.replace(strNew, "+=", KAWAN_ADD_EQUALS);
	strNew = StringUtils.replace(strNew, "-=", KAWAN_SUBTRACT_EQUALS);
	strNew = StringUtils.replace(strNew, "*=", KAWAN_MULTIPLY_EQUALS);
	strNew = StringUtils.replace(strNew, "/=", KAWAN_DIVIDE_EQUALS);
	strNew = StringUtils.replace(strNew, "%=", KAWAN_MODULO_EQUALS);
	strNew = StringUtils.replace(strNew, "&=", KAWAN_BITWISE_AND_EQUALS);
	strNew = StringUtils.replace(strNew, "^-=", KAWAN_BITWISE_EXCLUSIVE_EQUALS);
	strNew = StringUtils.replace(strNew, "|*=", KAWAN_BITWISE_OR_EQUALS);
	
	strNew = StringUtils.replace(strNew, "+", KAWAN_PLUS);
	strNew = StringUtils.replace(strNew, "-", KAWAN_MINUS);
	strNew = StringUtils.replace(strNew, "*", KAWAN_MULTIPLY);
	strNew = StringUtils.replace(strNew, "/", KAWAN_DIVIDE);
	strNew = StringUtils.replace(strNew, "%", KAWAN_MODULO);
	strNew = StringUtils.replace(strNew, "!", KAWAN_EXCLAMATION);
	
	strNew = StringUtils.replace(strNew, "&", KAWAN_AMPERSAND);
	strNew = StringUtils.replace(strNew, "~", KAWAN_INVERT_BIT);
	strNew = StringUtils.replace(strNew, "|", KAWAN_BITWISE_OR);
	strNew = StringUtils.replace(strNew, "^", KAWAN_BITWISE_XOR);
	
	strNew = StringUtils.replace(strNew, "=", " = ");
	
	strNew = StringUtils.replace(strNew, KAWAN_ADD_EQUALS, " += ");
	strNew = StringUtils.replace(strNew, KAWAN_SUBTRACT_EQUALS, " -= ");
	strNew = StringUtils.replace(strNew, KAWAN_MULTIPLY_EQUALS, " *= ");
	strNew = StringUtils.replace(strNew, KAWAN_DIVIDE_EQUALS, " /= ");
	strNew = StringUtils.replace(strNew, KAWAN_MODULO_EQUALS, " %= ");
	strNew = StringUtils.replace(strNew, KAWAN_BITWISE_AND_EQUALS, " &= ");
	strNew = StringUtils.replace(strNew, KAWAN_BITWISE_EXCLUSIVE_EQUALS, " ^-= ");
	strNew = StringUtils.replace(strNew, KAWAN_BITWISE_OR_EQUALS, " |*= ");
	
	strNew = StringUtils.replace(strNew, KAWAN_RIGHT_SHIFT, " >> ");
	strNew = StringUtils.replace(strNew, KAWAN_LEFT_SHIFT, " << ");
	
	//System.out.println(strNew);
	strNew = StringUtils.replace(strNew, KAWAN_PLUS, " + ");
	strNew = StringUtils.replace(strNew, KAWAN_MINUS, " - ");
	strNew = StringUtils.replace(strNew, KAWAN_MULTIPLY, " * ");
	strNew = StringUtils.replace(strNew, KAWAN_DIVIDE, " / ");
	strNew = StringUtils.replace(strNew, KAWAN_MODULO, " % ");
	
	strNew = StringUtils.replace(strNew, KAWAN_GTE, " >= ");
	strNew = StringUtils.replace(strNew, KAWAN_LTE, " <= ");

	strNew = StringUtils.replace(strNew, KAWAN_GT, " > ");
	strNew = StringUtils.replace(strNew, KAWAN_LT, " < ");
	
	strNew = StringUtils.replace(strNew, KAWAN_HASHTAG, " # ");
	strNew = StringUtils.replace(strNew, KAWAN_COMMENTS_MINUS, " -- ");
	strNew = StringUtils.replace(strNew, KAWAN_COMMENTS_OPENING, " /* ");
	strNew = StringUtils.replace(strNew, KAWAN_COMMENTS_CLOSING, " */ ");
	
	strNew = StringUtils.replace(strNew, KAWAN_EXCLAMATION, " ! ");
	strNew = StringUtils.replace(strNew, KAWAN_AMPERSAND, " & ");
	strNew = StringUtils.replace(strNew, KAWAN_INVERT_BIT, " ~ ");
	strNew = StringUtils.replace(strNew, KAWAN_BITWISE_OR, " | ");
	strNew = StringUtils.replace(strNew, KAWAN_BITWISE_XOR, " ^ ");
	
	strNew = StringUtils.replace(strNew, KAWAN_NE, " != ");
	strNew = StringUtils.replace(strNew, KAWAN_NE_2, " <> ");
	strNew = StringUtils.replace(strNew, KAWAN_NULL_SAFE_EQUAL, " <=> ");
	
	return strNew;
    }
    
    /**
     * Trim each token of the input List and concatenate them
     * 
     * @param tokens the tokens to trim and concatenate
     * @return the final normalized string
     */
    public static String tokensTrimAndConcatenate(List<String> tokens) {
	StringBuffer stringBuffer = new StringBuffer();
	for (String token : tokens) {
	    if (token.isEmpty()) {
		continue;
	    }
	    
	    token = token.trim();
	    
	    debug("display 4:" + token);
	    stringBuffer.append(token);
	    stringBuffer.append(" ");
	}
	String str = stringBuffer.toString();
	return str.trim();
    }
    
    public static List<String> getTokensSplitOnSinglesQuotes(final String sql) {
	Objects.requireNonNull(sql, "sql cannot be null!");
	final String sqlToSplit = sql.replace("''", KAWAN_SINGLE_QUOTE);

	List<String> tokens = new ArrayList<>();
	StringTokenizer stringTokenizer = new StringTokenizer(sqlToSplit, "'", false);

	while (stringTokenizer.hasMoreElements()) {
	    tokens.add(stringTokenizer.nextToken());
	}

	return tokens;
    }
    
    public static void debug(String s) {
	if (DEBUG)
	    System.out.println(new Date() + " " + s);
    }
}
