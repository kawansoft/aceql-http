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
package org.kawanfw.sql.api.server;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.parser.SqlCommentsDetector;
import org.kawanfw.sql.util.parser.SqlStringTokenizer;

/**
 * Allows to "normalize" the text of a SQL statement. The normalization will
 * remove all excess spaces, tabs, or line breaks. Also, the SQL keywords will
 * appear in uppercase, and columns and table names in lowercase. This ensures
 * that a SQL statement that should be recognized won't be rejected due to
 * differences in capitalization or spaces between words. <br>
 * <br>
 * For example the two following statements:
 * 
 * <pre>
 {@code SELECT *     from     my_table   where my_colum   =   ?} 
 {@code SELECT 	*         from     my_table      where     my_colum   =   ?"}
 * </pre>
 * 
 * will be normalized to the same String with extra spaces removed: <br>
 * {@code SELECT * FROM my_table WHERE my_colum = ?} <br>
 * <br>
 * Note that all string and numeric values are replaced by interrogation marks.
 * <br>
 * So, when using normalization, the following input statement are different:
 * <ul>
 * <li>{@code SELECT film_title, RENTAL_RATE from FILM where film_title like '%Star%' and rental_rate > 2.20}</li>
 * <li>{@code select film_title, rental_rate from film where film_title like '%Alien%' and rental_rate > 3.30}</li>
 * <li>{@code select film_title, rental_rate from film where film_title like '%Odyssey%' and rental_rate > 4.40}</li>
 * </ul>
 * They will all be normalized to:
 * <ul>
 * <li>{@code SELECT film_title , rental_rate FROM film WHERE film_title LIKE ? AND rental_rate > ?}</li>
 * </ul>
 * If normalization cannot be applied due to unsupported or sloppy formatting,
 * the original SQl statement is returned and an Exception is set. <br>
 * <br>
 * The two main reasons of normalization failure are:
 * <ul>
 * <li>The input SQL statement contains <i>nested</i> SQL comments which this
 * version's parser do not support and thus cannot treat. This triggers an
 * SQLException. (Regular non-nested SQL comments are successfully parsed.)</li>
 * <li>The input SQl statement is somewhat invalid and triggers an Exception.
 * call.</li>
 * </ul>
 * You may check if the statement is successfully normalized with the
 * {@link #isSuccess()} call. <br>
 * The caught Exception may be retrieved by a
 * {@link StatementNormalizer#getException()} call. <br>
 * Note that normalization is used in all provided {@link SqlFirewallManager}
 * implementations, this means that the SQL statements are all normalized before
 * the applying the firewall rules and security checks.
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */
public class StatementNormalizer {

    private static boolean DEBUG = FrameworkDebug.isSet(StatementNormalizer.class);

    private String sql;

    private boolean success;
    private boolean withNestedComments;
    private boolean withOddQuotesNumber;

    private Exception exception;

    /**
     * Constructor
     * 
     * @param sql the SQL statement to normalize
     */
    public StatementNormalizer(String sql) {
	super();
	this.sql = sql;
    }

    /**
     * Returns normalized text of the SQL statement. This means that in addition to
     * clean the statement, numbers and strings (contained in '') will be replaced
     * by "?" characters.
     * 
     * @return the normalized text of the SQL statement.
     */
    public String getNormalized() {
	String sqlOut = sql;
	try {
	    sqlOut = getNormalizedWithLevel(sql, true);
	} catch (Exception exception) {
	    this.success = false;
	    this.exception = exception;
	    return sql;
	}
	this.success = true;
	return sqlOut;
    }

    /**
     * Says if the normalization attempt is a success.
     * 
     * @return true if the normalization attempt is a success ,else false
     */
    public boolean isSuccess() {
	return success;
    }

    /**
     * Says if the failure reason was that the SQL statement had unsupported nested
     * comments
     * 
     * @return if the failure reason was that the SQL statement had unsupported
     *         nested comments
     */
    public boolean isWithNestedComments() {
	return withNestedComments;
    }

    /**
     * Says if the failure reason was that the SQL statement had an odd number of
     * single quote and thus could not be treated
     * 
     * @return true if the failure reason was that the SQL statement had an odd
     *         number of single quote and thus could not be treated, else false
     */
    public boolean isWithOddQuotesNumber() {
	return withOddQuotesNumber;
    }

    /**
     * Gets the Exception caught if the normalization was a failure (nested
     * comments, odd number of single quotes or any other cause).
     * 
     * @return Gets the Exception caught
     */
    public Exception getException() {
	return exception;
    }

    /**
     * Returns the normalized text of the SQL statement.
     * 
     * @param sql the SQL statement to normalize
     * @return the normalized text of the SQL statement.
     * @throws SQLException
     */
    private String getNormalizedWithLevel(String sql, boolean strongNormalizationLevel) throws SQLException {
	Objects.requireNonNull(sql, "sql cannot be null!");

	this.success = false;
	withNestedComments = BasicSqlInjectionAnalyser.containsNestedComments(sql);

	// We do not support nested comments. Too complicated for version 1.0... */
	if (withNestedComments) {
	    throw new SQLException("Input SQL contains not supported nested comments.");
	}

	SqlCommentsDetector sqlCommentsDetector = new SqlCommentsDetector(sql);
	sql = sqlCommentsDetector.removeComments();

	// Number of single quotes must be even
	int singleQuoteQuantity = StringUtils.countMatches(sql, "'");

	if (singleQuoteQuantity % 2 != 0) {
	    withOddQuotesNumber = true;
	    throw new SQLException("Input SQL contains an invalid odd number of single quotes.");
	}

	List<String> tokens = SqlStringTokenizer.getTokensSplitOnSinglesQuotes(sql);

	// List<String> finalTokens = new ArrayList<>();

	StringBuffer sb = new StringBuffer();

	debug("display 2:");
	for (int i = 0; i < tokens.size(); i++) {
	    debug(i + ": " + tokens.get(i));

	    // Even tokens contain no ' single quote
	    if (i % 2 == 0) {
		// finalTokens.add(StatementNormalizer.getNormalizedSubtring(tokens.get(i)));
		sb.append(StatementNormalizer.getNormalizedSubtring(tokens.get(i)));
	    } else {

		// Odd tokens are between single quotes (')
		if (strongNormalizationLevel) {
		    // finalTokens.add("?");
		    sb.append(" ? ");
		} else {
		    // finalTokens.add("\'" + tokens.get(i) + "\'");
		    sb.append("\'" + tokens.get(i) + "\'");
		}
	    }
	}

	// StatementAnalyzerUtil.debugDisplayTokens(finalTokens);

	// Build final concatenation
	// String normalized = SqlStringTokenizer.tokensTrimAndConcatenate(finalTokens);
	String normalized = sb.toString();

	// Do we have to do strong normalization?
	if (strongNormalizationLevel) {
	    normalized = replaceNumericValuesPerQuestionMark(normalized);
	} else {
	    // Put back double quotes
	    normalized = normalized.replace(SqlStringTokenizer.KAWAN_SINGLE_QUOTE, "''");
	}

	return normalized;
    }

    private static String replaceNumericValuesPerQuestionMark(String normalized) {
	StringTokenizer stringTokenizer = new StringTokenizer(normalized, " ()", true);

	StringBuffer stringBuffer = new StringBuffer();
	while (stringTokenizer.hasMoreElements()) {
	    String token = stringTokenizer.nextToken();

	    if (NumberUtils.isParsable(token)) {
		stringBuffer.append("?");
	    } else {
		stringBuffer.append(token);
	    }
	    // stringBuffer.append(" ");
	}

	return stringBuffer.toString().trim();
    }

    private static String getNormalizedSubtring(String substring) {
	Objects.requireNonNull(substring, "substring cannot be null");
	if (substring.contains("\'")) {
	    throw new IllegalArgumentException("substring to normalize cannot contains quotes (\').");
	}

	if (substring.contains("\"")) {
	    throw new IllegalArgumentException(
		    "A Statement to normalize cannot contain double-quotes outside of a string enclosed in single quotes: "
			    + substring);
	}

	if (substring.contains(";")) {
	    throw new IllegalArgumentException(
		    "A Statement to normalize cannot contain semicolons outside of a string enclosed in single quotes: "
			    + substring);
	}

	if (substring.contains("#")) {
	    throw new IllegalArgumentException(
		    "A Statement to normalize cannot contain hashtags outside of a string enclosed in single quotes: "
			    + substring);
	}

	// 1) Get tokens:
	List<String> tokens = SqlStringTokenizer.getTokensSplitOnSpace(substring);

	// 2) Rebuild text with only space separation between elements:
	String normalizedString = SqlStringTokenizer.tokensTrimAndConcatenate(tokens);
	return normalizedString;
    }

    private static void debug(String s) {
	if (DEBUG)
	    System.out.println(new Date() + " " + s);
    }
}
