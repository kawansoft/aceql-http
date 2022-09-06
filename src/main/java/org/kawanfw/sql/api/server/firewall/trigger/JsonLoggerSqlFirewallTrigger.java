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
package org.kawanfw.sql.api.server.firewall.trigger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.api.server.logging.LoggerCreator;
import org.kawanfw.sql.servlet.util.SqlFirewallTriggerUtil;
import org.kawanfw.sql.servlet.util.logging.GenericLoggerCreator;
import org.kawanfw.sql.servlet.util.logging.LoggerCreatorProperties;
import org.kawanfw.sql.servlet.util.logging.LoggerWrapper;
import org.kawanfw.sql.util.FrameworkDebug;
import org.slf4j.Logger;

/**
 * A trigger that will log using JSON format the {@code SqlEvent} info
 * and the {@code sqlFirewallManager} class name.
 * 
 * @author Nicolas de Pomereu
 * @since 11.0
 */

public class JsonLoggerSqlFirewallTrigger implements SqlFirewallTrigger {

    /** The debug flag */
    private static boolean DEBUG = FrameworkDebug.isSet(JsonLoggerSqlFirewallTrigger.class);

    private static Logger ACEQL_LOGGER = null;
    private static Map<String, String> LOGGER_ELEMENTS = new ConcurrentHashMap<>();
        
    /**
     * Logs using JSON format the {@code SqlEvent} and the
     * {@code SqlFirewallManager} class name into a {@code Logger} with parameters:
     * <ul>
     * <li>Name: {@code sonLoggerSqlFirewallTrigger}</li>
     * <li>File name pattern: {@code user.home/.kawansoft/log/JsonLoggerSqlFirewallTrigger_%d.log.%i} (example of file
     * created: {@code JsonLoggerSqlFirewallTrigger_2022-07-01.log.1}.).</li>
     * <li>Pattern of each line of log: <code>"%msg%n"</code></li>
     * <li>Maximum File Size: 300Mb</li>
     * <li>Total Size Cap: 30Gb</li>
     * </ul>
     * These default values may be superseded by creating a
     * {@code JsonLoggerSqlFirewallTrigger.properties} file in
     * {@code user.home/.kawansoft/conf}. <br>
     * <br>
     * 
     * See the <a href=
     * file:../../../../../../../../resources/JsonLoggerSqlFirewallTrigger.properties>JsonLoggerSqlFirewallTrigger.properties</a>
     * format.<br>
     * <br>
     * <br>
     */
    @Override
    public void runIfStatementRefused(SqlEvent sqlEvent, SqlFirewallManager sqlFirewallManager, Connection connection)
	    throws IOException, SQLException {
	String jsonString = SqlFirewallTriggerUtil.toJsonString(sqlEvent, sqlFirewallManager);
	
	if (ACEQL_LOGGER == null) {
	    LoggerCreator loggerCreator = getLoggerCreator();
	    ACEQL_LOGGER = loggerCreator.getLogger();
	    LOGGER_ELEMENTS = loggerCreator.getElements();
	}
	
	LoggerWrapper.log(ACEQL_LOGGER, jsonString);
    }

    /**
     * Returns the Logger elements (for debug purpose)
     * 
     * @return the lOGGER_ELEMENTS
     */
    public static Map<String, String> getLoggerElements() {
	return LOGGER_ELEMENTS;
    }

    
    /**
     * Builds a {@code LoggerCreator} for Json logging.
     * @return	the created {@code LoggerCreator} 
     * @throws IOException
     */
    private LoggerCreator getLoggerCreator() throws IOException {

	// kawanwall_%d.log.%i" as sample
	String fileNamePattern = JsonLoggerSqlFirewallTrigger.class.getSimpleName() + "_%d.log.%i";

	LoggerCreatorProperties loggerCreatorProperties = LoggerCreatorProperties
		.getFileBasedProperties(JsonLoggerSqlFirewallTrigger.class.getSimpleName());

	LoggerCreator loggerCreator = null;
	if (loggerCreatorProperties != null) {

	    debug("loggerCreatorProperties: " + loggerCreatorProperties.toString());

	    loggerCreator = GenericLoggerCreator.newBuilder().name(JsonLoggerSqlFirewallTrigger.class.getSimpleName())
		    .fileNamePattern(fileNamePattern).pattern(loggerCreatorProperties.getPattern())
		    .logDirectory(loggerCreatorProperties.getLogDirectory())
		    .maxFileSize(loggerCreatorProperties.getMaxFileSize())
		    .totalSizeCap(loggerCreatorProperties.getTotalSizeCap())
		    .displayOnConsole(loggerCreatorProperties.isDisplayOnConsole())
		    .displayLogStatusMessages(loggerCreatorProperties.isDisplayLogStatusMessages()).build();
	} else {

	    debug("loggerCreatorProperties is null!");

	    loggerCreator = GenericLoggerCreator.newBuilder().name(JsonLoggerSqlFirewallTrigger.class.getSimpleName())
		    .fileNamePattern(fileNamePattern).pattern("%msg%n") // Empty pattern
		    .build();
	}
	
	return loggerCreator;
	
    }

    /**
     * Debug tool
     *
     * @param s
     */

    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }
}
