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

package org.kawanfw.sql.api.util.firewall;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.StatementNormalizer;
import org.kawanfw.sql.servlet.util.logging.StringFlattener;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.TimestampUtil;

/**
 * Learn new statements to log in the learn whitelist file
 * 
 * @author Nicolas de Pomereu
 *
 */
public class LearningModeExecutor {

    public static boolean DEBUG = FrameworkDebug.isSet(LearningModeExecutor.class);
    
    public static String CR_LF = System.getProperty("line.separator");

    /**
     * Stores in the learning file in database-whitelist-learning.txt same dir as
     * properties file the sql statements to allows
     * 
     * @param sqlEvent
     * @param propertiesFile
     * @throws SQLException if I/O error occurs, wrapped in SQLException
     */
    public static synchronized void learn(SqlEvent sqlEvent, File propertiesFile) throws SQLException {

	try {
	    File learningFile = getLearningFile(sqlEvent.getDatabase(), propertiesFile);
	    File logFile = new File(learningFile.toString() + ".errors.log");
	    
	    Set<String> lineSet = new LinkedHashSet<>();
	    if (learningFile.exists()) {
		List<String> lines = FileUtils.readLines(learningFile, "UTF-8");
		lineSet = new LinkedHashSet<>(lines);
	    }
	    
	    // Normalize the statement
	    StatementNormalizer statementNormalizer = new StatementNormalizer(sqlEvent.getSql());
	    String sql = statementNormalizer.getNormalized();
	    
	    StringFlattener stringFlattener = new StringFlattener(sql);
	    sql = stringFlattener.flatten();
	    
	    debug("sql: " + sql);
	    debug("statementNormalizer.getException(): " + statementNormalizer.getException());
	    
	    if (!statementNormalizer.isSuccess()) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
		    String message = TimestampUtil.getHumanTimestampNow() 
			    +  " Reason: " + statementNormalizer.getException().getMessage() + " - SQL:  " + sql;
		    writer.write(message + CR_LF);
		}
	    }
	    
	    lineSet.add(sql);

	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(learningFile));) {
		for (String line : lineSet) {
		    writer.write(line + CR_LF);
		}
	    }
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    throw new SQLException("Error when accessing learning file for database " + sqlEvent.getDatabase() + ": "
		    + ioe.getMessage());
	}
    }

    /**
     * Returns the &lt;database&gt;_rules_manager.csv for the passed database
     *
     * @param database
     * @throws FileNotFoundException
     */
    private static File getLearningFile(String database, File file) throws FileNotFoundException {

	Objects.requireNonNull(file, "file cannot be null!");

	if (!file.exists()) {
	    throw new FileNotFoundException("The properties file does not exist: " + file);
	}
	File dir = file.getParentFile();
	File learningFile = new File(dir + File.separator + database + "_deny_except_whitelist.txt");

	return learningFile;

    }
    
    public static void debug(String s) {
	if (DEBUG)
	    System.out.println(new Date() + " " + s);
    }

}
