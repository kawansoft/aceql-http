/*
 * This file is part of KawanWall.
 * KawanWall: Firewall for SQL statements
 * Copyright (C) 2022,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.                 
 *                                                                         
 * KawanWall is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.         
 *              
 * KawanWall is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Affero General Public License for more details.       
 *                                  
 * You should have received a copy of the GNU Affero General 
 * Public License along with this program; if not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * If you develop commercial activities using KawanWall, you must: 
 * a) disclose and distribute all source code of your own product,
 * b) license your own product under the GNU General Public License.
 * 
 * You can be released from the requirements of the license by
 * purchasing a commercial license. Buying such a license will allow you 
 * to ship KawanWall with your closed source products without disclosing 
 * the source code.
 *
 * For more information, please contact KawanSoft SAS at this
 * address: sales@kawansoft.com
 * 
 * Any modifications to this file must keep this entire header
 * intact.
 */
package com.kawanwall.server.api.logging;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.kawanfw.sql.util.FrameworkDebug;
import org.slf4j.Logger;

import com.kawanwall.server.logging.GenericLoggerCreator;
import com.kawanwall.server.logging.LoggerCreatorProperties;

/**
 * Creates a default Logback/sl4fj Logger for main KawanWall activity.
 * 
 * Logger has default characteristics:
 * <ul>
 * <li>name: DefaultLoggerCreator</li>
 * <li>Log directory: {@code user.home/.kawansoft/log}</li>
 * <li>File name pattern: {@code "kawanwall_%d.log.%i"} (example of file
 * created: {@code kawanwall_2022-07-01.log.1}.)</li>
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
 * "https://docs.kawanwall.com/rest/soft/1.0/src/DefaultLoggerCreator.properties">DefaultLoggerCreator.properties</a>
 * format.<br><br>
 * 
 * @author Nicolas de Pomereu
 *
 */
public class DefaultLoggerCreator implements LoggerCreator {

    /** The debug flag */
    private static boolean DEBUG = FrameworkDebug.isSet(DefaultLoggerCreator.class);

    private static final String MAIN_LOG_FILE_PATTERN = "kawanwall_%d.log.%i";
    private Logger logger;

    private Map<String, String> elements;

    /**
     * Constructor
     * 
     * @throws IOException
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     */
    public DefaultLoggerCreator() throws IllegalArgumentException, FileNotFoundException, IOException {

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
