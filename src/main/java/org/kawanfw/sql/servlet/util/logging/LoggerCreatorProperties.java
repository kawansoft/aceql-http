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
package org.kawanfw.sql.servlet.util.logging;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 *
 */
public class LoggerCreatorProperties {

    /** The debug flag */
    private static boolean DEBUG = FrameworkDebug.isSet(LoggerCreatorProperties.class);
    
    private static final String CONF_DIR = SystemUtils.USER_HOME + File.separator + ".kawansoft" + File.separator + "conf" + File.separator;

    private Properties properties;

    private String fileNamePattern = null;
    private File logDirectory = LoggerCreatorBuilderImpl.DEFAULT_LOG_DIRECTORY;
    private String pattern = LoggerCreatorBuilderImpl.DEFAULT_PATTERN;
    private long maxFileSize = LoggerCreatorBuilderImpl.DEFAULT_MAX_SIZE;
    private long totalSizeCap = LoggerCreatorBuilderImpl.DEFAULT_TOTAL_SIZE_CAP;
    private boolean displayOnConsole = false;
    private boolean displayLogStatusMessages = false;
   
    public Properties getProperties() {
        return properties;
    }

    public String getFileNamePattern() {
        return fileNamePattern;
    }

    public File getLogDirectory() {
        return logDirectory;
    }

    public String getPattern() {
        return pattern;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public long getTotalSizeCap() {
        return totalSizeCap;
    }

    public boolean isDisplayOnConsole() {
        return displayOnConsole;
    }

    public boolean isDisplayLogStatusMessages() {
        return displayLogStatusMessages;
    }

    /**
     * Constructor
     * @param properties the properties to load
     */
    private LoggerCreatorProperties(Properties properties) {
	this.properties = properties;
    }

    private void set() {
	if (properties.get("fileNamePattern") != null) {
	    fileNamePattern = (String) properties.get("fileNamePattern");
	}
	if (properties.get("logDirectory") != null && ! ((String) properties.get("logDirectory")).trim().isEmpty()  ) {
	    logDirectory = new File((String) properties.get("logDirectory"));
	    logDirectory.mkdirs();
	}
	if (properties.get("pattern") != null) {
	    pattern = (String) properties.get("pattern");
	}
	if (properties.get("maxFileSize") != null) {
	    String maxFileSizeStr = (String) properties.get("maxFileSize");
	    maxFileSize = Long.parseLong(maxFileSizeStr);
	}
	if (properties.get("totalSizeCap") != null) {
	    String totalSizeCapStr = (String) properties.get("totalSizeCap");
	    totalSizeCap = Long.parseLong(totalSizeCapStr);
	}
	displayOnConsole = Boolean.parseBoolean((String)properties.get("displayOnConsole"));
	displayLogStatusMessages = Boolean.parseBoolean((String)properties.get("displayLogStatusMessages"));
	
    }

    public static LoggerCreatorProperties getFileBasedProperties(String loggerProperties) throws IllegalArgumentException, IOException {
	Objects.requireNonNull(loggerProperties, "loggerProperties cannot be null!");
	File file = new File (CONF_DIR + loggerProperties +".properties");
	
	debug("loggerProperties File: " + file);
	
	if (! file.exists()) {
	    return null;
	}
	
	Properties properties = PropertiesFileUtil.getProperties(file);
	LoggerCreatorProperties loggerCreatorProperties = new LoggerCreatorProperties(properties);
	loggerCreatorProperties.set();
	
	return loggerCreatorProperties;
	
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
