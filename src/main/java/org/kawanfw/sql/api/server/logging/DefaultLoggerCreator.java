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
package org.kawanfw.sql.api.server.logging;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.kawanfw.sql.servlet.util.logging.GenericLoggerCreator;
import org.kawanfw.sql.servlet.util.logging.LoggerCreatorProperties;
import org.kawanfw.sql.util.FrameworkDebug;
import org.slf4j.Logger;

/**
 * Creates a default Logback/sl4fj Logger for main AceQL activity.
 * 
 * Logger has default characteristics:
 * <ul>
 * <li>name: DefaultLoggerCreator</li>
 * <li>Log directory: {@code user.home/.kawansoft/log}</li>
 * <li>File name pattern: {@code "aceql_%d.log.%i"} (example of file created:
 * {@code aceql_2022-07-01.log.1}.)</li>
 * <li>Pattern of each line of log: <code> "%d{HH:mm:ss.SSS} [%thread] %-5level
 * %logger{36} - %msg%n"}</code></li>
 * <li>Maximum File Size: 300Mb</li>
 * <li>Total Size Cap: 30Gb</li>
 * </ul>
 * These default values may be superseded by creating a
 * {@code DefaultLoggerCreator.properties} file in
 * {@code user.home/.kawansoft/conf}. <br>
 * <br>
 * See the <a href=
 * file:../../../../../../../resources/DefaultLoggerCreator.properties>DefaultLoggerCreator.properties</a>
 * format.<br>
 * 
 * @author Nicolas de Pomereu
 *
 */
public class DefaultLoggerCreator implements LoggerCreator {

    /** The debug flag */
    private static boolean DEBUG = FrameworkDebug.isSet(DefaultLoggerCreator.class);

    private static final String MAIN_LOG_FILE_PATTERN = "aceql_%d.log.%i";
    private Logger logger;

    private Map<String, String> elements;

    /**
     * Constructor
     * 
     * @throws IOException if any I/O error occurs when accessing the {@code DefaultLoggerCreator.properties} file.
     * @throws FileNotFoundException if the the {@code DefaultLoggerCreator.properties} file does not exist.
     */
    public DefaultLoggerCreator() throws FileNotFoundException, IOException {

	LoggerCreatorProperties loggerCreatorProperties = LoggerCreatorProperties
		.getFileBasedProperties(DefaultLoggerCreator.class.getSimpleName());

	LoggerCreator loggerCreator = null;
	if (loggerCreatorProperties != null) {

	    String fileNamePattern = loggerCreatorProperties.getFileNamePattern();
	    if (fileNamePattern == null) {
		fileNamePattern = MAIN_LOG_FILE_PATTERN;
	    }

	    loggerCreator = GenericLoggerCreator.newBuilder().name(DefaultLoggerCreator.class.getSimpleName())
		    .fileNamePattern(fileNamePattern).pattern(loggerCreatorProperties.getPattern())
		    .logDirectory(loggerCreatorProperties.getLogDirectory())
		    .maxFileSize(loggerCreatorProperties.getMaxFileSize())
		    .totalSizeCap(loggerCreatorProperties.getTotalSizeCap())
		    .displayOnConsole(loggerCreatorProperties.isDisplayOnConsole())
		    .displayLogStatusMessages(loggerCreatorProperties.isDisplayLogStatusMessages()).build();
	    

	} else {
	    loggerCreator = GenericLoggerCreator.newBuilder().name(DefaultLoggerCreator.class.getSimpleName())
		    .fileNamePattern(MAIN_LOG_FILE_PATTERN).build();
	}

	elements = loggerCreator.getElements();
	logger = loggerCreator.getLogger();
    }

    @Override
    public Logger getLogger() {
	return logger;
    }

    
    @Override
    public Map<String, String> getElements() {
        return elements;
    }
    
    /**
     * Debug tool
     *
     * @param s
     */

    @SuppressWarnings("unused")
    private void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
