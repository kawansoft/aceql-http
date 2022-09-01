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
    
    private static final String CONF_DIR = SystemUtils.USER_HOME + File.separator + ".kawanwall" + File.separator + "conf" + File.separator;

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
