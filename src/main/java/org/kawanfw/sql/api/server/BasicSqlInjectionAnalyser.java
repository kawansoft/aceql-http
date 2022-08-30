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
package org.kawanfw.sql.api.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.parser.SqlCommentsDetector;
import org.kawanfw.sql.util.parser.SqlStringTokenizer;

/**
 * Provides misc methods to anlyse basic elements of an SQL statement and to check SQL injection attempts.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class BasicSqlInjectionAnalyser {

    public static boolean DEBUG = FrameworkDebug.isSet(BasicSqlInjectionAnalyser.class);
	
    private String sql;

    private boolean detectLineBreaks = true;
    private boolean detectComments = true;
    private boolean detectSeparators = true;
    private boolean detectTabs = true;
    
    private boolean detectDoubleQuotes = true;
    private boolean detectNoSpaces = true;
    
    private Set<String>  forbiddenKeywordList = new LinkedHashSet<>();
    private boolean withLineBreaks;
    private boolean withComments;
    private boolean withSeparators;
    private boolean withTabs;
    
    private boolean withDoubleQuotes;
    private boolean withNoSpaces;
    
    private boolean withForbiddenKeywords;
    
    
    private boolean withOddQuotesNumber;
    private String keywordDetected;
    
    private boolean withNestedComments;
    private boolean withEqualValuesAroundEqual;

    private String anomalyDetected;


    /**
     * Constructor 
     * @param sql	the SQL statement to analyse
     */
    public BasicSqlInjectionAnalyser(String sql) {
	Objects.requireNonNull(sql, "sql cannot be null!");
	this.sql = sql;
    }
    
    /**
     * Sets if tabs should be detected. Defaults to true.
     * @param detectTabs true if tabs should be detected, else false
     */
    public void setDetectTabs(boolean detectTabs) {
	this.detectTabs = detectTabs;
    }

    /**
     * Sets if commands separators (;) should be detected.  Defaults to true.
     * @param detectSeparators true if commands separators should be detected, else false
     */
    public void setDetectSeparators(boolean detectSeparators) {
	this.detectSeparators = detectSeparators;
    }

    /**
     * Sets if line breaks should be detected.  Defaults to true.
     * @param detectLineBreaks  true if line breaks should be detected, else false
     */
    public void setDetectLineBreaks(boolean detectLineBreaks) {
	this.detectLineBreaks = detectLineBreaks;
    }

    /**
     * Sets if comments should be detected.  Defaults to true.
     * @param detectComments  true if comments should be detected, else false
     */
    public void setDetectComments(boolean detectComments) {
	this.detectComments = detectComments;
    }
    
    /**
     * Sets if double quotes should be detected.  Defaults to true.
     * @param detectDoubleQuotes  true if double quotes should be detected, else false
     */
    public void setDetectDoubleQuotes(boolean detectDoubleQuotes) {
	this.detectDoubleQuotes = detectDoubleQuotes;
    }
    
    /**
     * Sets if double quotes should be detected.  Defaults to true.
     * @param detectDoubleQuotes  true if double quotes should be detected, else false
     */
    public void setDetectNoSpaces(boolean detectNoSpaces) {
	this.detectNoSpaces = detectNoSpaces;
    }
    
    
    /**
     * Sets the keyword list that are forbidden to use and will trigger a SQL injection suspicion
     * @param forbiddenKeywordList the keyword list that are forbidden to use and will trigger a SQL injection suspicion
     */
    public void setForbiddenKeywordList(Set<String> forbiddenKeywordList) {
	Objects.requireNonNull(forbiddenKeywordList, "forbiddenKeywordList cannot be null!");
	this.forbiddenKeywordList = forbiddenKeywordList;
    }

    
    /**
     * Returns the anomaly detected
     * @return the anomaly detected
     */
    public String getAnomalyDetected() {
        return anomalyDetected;
    }

    /**
     * 
     * @return true id the 
     */
    public boolean isWithNestedComments() {
        return withNestedComments;
    }

    /**
     * 
     * @return true if the SQL statement has line breaks
     */
    public boolean isWithLineBreaks() {
        return withLineBreaks;
    }

    /**
     * 
     * @return true if the SQL statement contains comments
     */
    public boolean isWithComments() {
        return withComments;
    }

    /**
     * 
     * @return true if the SQL statement contains statement separators
     */
    public boolean isWithSeparators() {
        return withSeparators;
    }

    /**
     * 
     * @return true if the SQL statement contains at least one forbidden keyword
     */
    public boolean isWithForbiddenKeywords() {
        return withForbiddenKeywords;
    }

    
    public boolean isWithEqualValuesAroundEqual() {
        return withEqualValuesAroundEqual;
    }

    /**
     * Says if the SQL statement is a suspect for SQL injection attempt 
     * @return true if the SQL statement is a suspect for SQL injection attempt 
     */
    public boolean isSqlInjectionSuspect() {
	
	if (withEqualValuesAroundEqual) {
	    return true;
	}
	
	if (detectComments && withComments) {
	    return true;
	}
	
	if (detectLineBreaks && withLineBreaks) {
	    return true;
	}
	
	if (detectSeparators && withSeparators) {
	    return true;
	}
	
	if (detectTabs && withTabs) {
	    return true;
	}
	
	if (withForbiddenKeywords) {
	    return true;
	}
	
	if (detectDoubleQuotes && withDoubleQuotes) {
	    return true;
	}
	
	if (detectNoSpaces && withNoSpaces) {
	    return true;
	}
	
	return false;
    }
    
    
    /**
     * Analyze the SQL statement with the set parameters and keywords 
     */
    public void analyse() {
	
	initResults();
	
	if (detectNoSpaces) {
	    if (! sql.trim().contains(" ")) {
		withNoSpaces = true;
		anomalyDetected = "SqlWithNoSpaces";
	    }
	}
	
	withNestedComments = containsNestedComments(sql);
	
	// We do not support nested comments. Too complicated for version 1.0... */
	if (withNestedComments) {
	    anomalyDetected = "SqlWithNestedComments";
	    return;
	}
	
	if (this.detectLineBreaks) {
	    if (checkIfStringContainsNewLineCharacters(sql)) {
		anomalyDetected = "SqlWithLineBreaks";
		withLineBreaks = true;
		return;
	    }
	}
	
	// We always remove comments, otw we cannot pare correctly...
	SqlCommentsDetector sqlCommentsDetector = new SqlCommentsDetector(sql);
	sql = sqlCommentsDetector.removeComments();
	
	this.withComments = sqlCommentsDetector.isWithComments();
	
	if (this.detectComments && this.withComments) {
	    anomalyDetected = "SqlWithComments";
	    return;
	}
	
	debug("sql after remove comments: " + sql);
	debug("this.withComments        : " + this.withComments);
	
	int singleQuoteQuantity = StringUtils.countMatches(sql, "'");

	if (singleQuoteQuantity % 2 != 0) {
	    withOddQuotesNumber = true;
	    anomalyDetected = "SqlWithOddQuotesNumber";
	    return;
	}
	
	if ( hasEqualValuesAroundEqual(sql)) {
	    withEqualValuesAroundEqual = true;
	    anomalyDetected = "SqlWithEqualValuesAroundEqual";
	    return;
	}
	
	List<String> tokens = SqlStringTokenizer.getTokensSplitOnSinglesQuotes(sql);
	debug("Split on quotes - display 2:");
	for (int i = 0; i < tokens.size(); i++) {
	    debug(i + ": " + tokens.get(i));
	    
	    // Even tokens contain no ' single quote surrounded values...
	    if (i % 2 == 0) {
		boolean injectionDetected = analyseDeep(tokens.get(i));
		if (injectionDetected) {
		    return; // Stop at first detection
		}
	    }
	}    
	
    }


    private void initResults() {
	withLineBreaks = false;
	withComments = false;
	withSeparators = false;
	withTabs = false;
	withForbiddenKeywords = false;
	withOddQuotesNumber = false;
	keywordDetected = null;
	withNestedComments = false;
	withEqualValuesAroundEqual = false;
	withDoubleQuotes = false;
	withNoSpaces = false;
	anomalyDetected = null;
    }

    /**
     * 
     * @return true if sql statement contains an odd number of single quote (') and thus cannot be processed
     */
    public boolean isWithOddQuotesNumber() {
        return withOddQuotesNumber;
    }

    /**
     * Do a deep analysis of the sql tokens to check if suspicious 
     * @param sqlTokens sql substring without values inside quotes
     * @return true if sql tokens are suspicious
     */
    private boolean analyseDeep(String sqlTokens) {
	
	// Do if test for not breaking previous set values to true...
	

	if (this.detectComments) {
	    if (sqlTokens.contains("--") || sqlTokens.contains("#")) {
		withComments = true;
		anomalyDetected = "SqlWithComments";
		return true;
	    }
	}
	
	if (this.detectSeparators) {
	    if (sqlTokens.contains(";")) {
		withSeparators = true;
		anomalyDetected = "SqlWithSeparators";
		return true;
	    }
	}
	
	if (this.detectTabs) {
	    if (sqlTokens.contains("\t")) {
		withTabs = true;
		anomalyDetected = "SqlWithTabs";
		return true;
	    }
	}
	
	if (this.detectDoubleQuotes) {
	    if (sqlTokens.contains("\"")) {
		withDoubleQuotes = true;
		anomalyDetected = "SqlWithDoubleQuotes";
		return true;
	    }
	}
	
	if (containsForbiddenKeywords(sqlTokens)) {
	    withForbiddenKeywords = true;
	    anomalyDetected = "SqlWithForbiddenKeywords. Keyword detected: " + keywordDetected;
	    return true;
	}
	
	return false;

    }
    
    /**
     * Test the 1=1 variations
     * @param sqlTokens sql substring without values inside quotes
     * @return true if sql line contains variations of 1=1
     */
    public static boolean hasEqualValuesAroundEqual(String sqlTokens) {
	
	while (sqlTokens.contains("= ")) {
	    sqlTokens = StringUtils.replace(sqlTokens, "= ", "=");
	}
	
	while (sqlTokens.contains(" =")) {
	    sqlTokens = StringUtils.replace(sqlTokens, " =", "=");
	}
	
	List<String> tokens =  getTokensSplitOnEquals(sqlTokens);
	
	boolean injectionDetected = false;
	for (int i = 0; i < tokens.size(); i++) {
	    
	    debug("hasEqualValuesAroundEqual " + i + ": " + tokens.get(i));
	    if (i == 0) {
		continue;
	    }
	    
	    String currentValue = StringUtils.substringBefore(tokens.get(i), " ").trim();
	    String previousValue = StringUtils.substringAfterLast(tokens.get(i - 1).trim(), " ").trim();

	    if (currentValue.equals(previousValue)) {
		debug("Injection on: " + previousValue + "=" + currentValue);
		injectionDetected = true;
	    }
	    
	}
	
	return injectionDetected;
    }
    
    public static List<String> getTokensSplitOnEquals(final String sql) {
	Objects.requireNonNull(sql, "sql cannot be null!");

	List<String> tokens = new ArrayList<>();
	StringTokenizer stringTokenizer = new StringTokenizer(sql, "=", false);

	while (stringTokenizer.hasMoreElements()) {
	    tokens.add(stringTokenizer.nextToken());
	}

	return tokens;
    }

    public static boolean containsNestedComments(String sql) {
	Objects.requireNonNull(sql, "sql cannot be null!");
	String[] stringArray = sql.split("/\\*");
	for (String string : stringArray) {
	    debug("Split on /*: " + string);
	    if (StringUtils.countMatches(string, "*/") > 1 && ! ( string.contains("'") && string.indexOf("'") > StringUtils.lastIndexOf(string, "*/"))) {
		debug("Contains > 1 */");
		return true;
	    }
	}
	
	return false;
	
    }



    private boolean containsForbiddenKeywords(String sqlTokens) {
	
	sqlTokens = sqlTokens.trim().toLowerCase();
		
	if (StringUtils.lastIndexOf(sqlTokens, "#") > StringUtils.lastIndexOf(sqlTokens, "'")) {
	    sqlTokens = StringUtils.substringBeforeLast(sqlTokens, "#");
	}
	
	if (StringUtils.lastIndexOf(sqlTokens, "--") > StringUtils.lastIndexOf(sqlTokens, "'")) {
	    sqlTokens = StringUtils.substringBeforeLast(sqlTokens, "--");
	}
	
	for (String keyword : forbiddenKeywordList) {
	    if (sqlTokens.contains(keyword.toLowerCase())) {
		this.keywordDetected = keyword;
		return true;
	    }
	}
	return false;
    }

    private static boolean checkIfStringContainsNewLineCharacters(String str){
        if(!StringUtils.isEmpty(str)){
            Scanner scanner = new Scanner(str);
            scanner.nextLine();
            boolean hasNextLine =  scanner.hasNextLine();
            scanner.close();
            return hasNextLine;
        }
        return false;
    }
    
    public static void debug(String s) {
	if (DEBUG)
	    System.out.println(new Date() + " " + s);
    }
	    
}
