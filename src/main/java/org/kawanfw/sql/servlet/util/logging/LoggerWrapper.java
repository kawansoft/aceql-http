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
package org.kawanfw.sql.servlet.util.logging;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.api.server.firewall.trigger.JsonLoggerSqlFirewallTrigger;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.Tag;
import org.slf4j.Logger;

/**
 * @author Nicolas de Pomereu
 *
 */
public class LoggerWrapper {
    
    /** The debug flag */
    private static boolean DEBUG = FrameworkDebug.isSet(JsonLoggerSqlFirewallTrigger.class);
    
    public static String CR_LF = System.getProperty("line.separator");
    
    /**
     * A default logging method
     * 
     * @param logger  the Logger to use
     * @param message the message to log with Logger.info()
     */
    public static void log(Logger logger, String message) {
	String messageNew = flattenIfNecessary(message);
	logger.info(messageNew);
    }

    /**
     * An error logging method
     * 
     * @param logger  the Logger to use
     * @param message the message to log with Logger.info()
     */
    public static void logError(Logger logger, String message) {
	String messageNew = flattenIfNecessary(message);
	logger.error(messageNew);
    }

    /**
     * A default logging method for clean logging off Exceptions
     * 
     * @param logger    the Logger to use
     * @param message   the message to log with Logger.error()
     * @param throwable the Exception/Throwable to log, that will be flattened
     */
    public static void log(Logger logger, String message, Throwable throwable) {
	try {

	    if (message == null) {
		message = "";
	    }

	    if (!message.endsWith(" ")) {
		message += " ";
	    }
	    
	    message = flattenIfNecessary(message);

	    StringFlattener stringFlattener = new StringFlattener(ExceptionUtils.getStackTrace(throwable));
	    String flattenException = stringFlattener.flatten();
	    
	    if (DEBUG) {
		String thePath = LoggerCreatorBuilderImpl.DEFAULT_LOG_DIRECTORY + File.separator
			+ "LoggerWrapper_debug.txt";
		Files.write(Paths.get(thePath), (message + flattenException + CR_LF).getBytes(),
			StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	    }

	    logger.error(message + flattenException);
	} catch (Throwable throwable2) {
	    logger.error(Tag.RUNNING_PRODUCT + " CAN NOT FLATTEN EXCEPTION IN LOG:");
	    logger.error(message, throwable2);
	}
    }

    private static String flattenIfNecessary(String message) {
	if (message == null || message.isEmpty()) {
	    return message;
	}

	String messageNew = message;
	
	try {
	    StringFlattener stringFlattener = new StringFlattener(messageNew);
	    messageNew = stringFlattener.flatten();

	} catch (Throwable throwable) {
	    System.out.println("CANNOT FLAT MESSAGE:");
	    throwable.printStackTrace();
	    return messageNew;
	}

	return messageNew;
    }

    /**
     * Debug tool
     *
     * @param s
     */

    @SuppressWarnings("unused")
    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }
    
}
