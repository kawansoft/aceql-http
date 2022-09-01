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
import java.util.Objects;

import org.apache.commons.lang3.SystemUtils;
import org.kawanfw.sql.servlet.util.logging.GenericLoggerCreator.Builder;

/**
 * The implementation of the GenericLoggerCreator.Builder with the default
 * values
 * 
 * @author Nicolas de Pomereu
 *
 */
public class LoggerCreatorBuilderImpl implements GenericLoggerCreator.Builder {

    static final long KB = 1024;
    static final long MB = 1024 * KB;
    static final long GB = 1024 * MB;
        
    public static final File DEFAULT_LOG_DIRECTORY = new File(SystemUtils.USER_HOME + File.separator + ".kawanwall" + File.separator + "log");
    public static final String DEFAULT_PATTERN = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";
    public static final long DEFAULT_MAX_SIZE = MB * 300;
    public static final long DEFAULT_TOTAL_SIZE_CAP = GB * 30;
    
    public static final String SHORT_PATTERN = "%msg%n";

    String name = null;
    String fileNamePattern = null;
    File logDirectory = DEFAULT_LOG_DIRECTORY;
    String pattern = DEFAULT_PATTERN;
    long maxFileSize = DEFAULT_MAX_SIZE;
    long totalSizeCap = DEFAULT_TOTAL_SIZE_CAP;
    boolean displayOnConsole = false;
    boolean displayLogStatusMessages = false;

    @Override
    public Builder name(String name) {
	this.name = name;
	return this;
    }

    @Override
    public Builder fileNamePattern(String fileNamePattern) {
	this.fileNamePattern = fileNamePattern;
	return this;
    }

    @Override
    public Builder logDirectory(File logDirectory) {
	this.logDirectory = logDirectory;
	return this;
    }

    @Override
    public Builder pattern(String pattern) {
	this.pattern = pattern;
	return this;
    }

    @Override
    public Builder maxFileSize(long maxFileSize) {
	this.maxFileSize = maxFileSize;
	return this;
    }

    @Override
    public Builder totalSizeCap(long totalSizeCap) {
	this.totalSizeCap = totalSizeCap;
	return this;
    }

    @Override
    public Builder displayOnConsole(boolean displayOnConsole) {
	this.displayOnConsole = displayOnConsole;
	return this;
    }
    
    @Override
    public Builder displayLogStatusMessages(boolean displayLogStatusMessages) {
	this.displayLogStatusMessages = displayLogStatusMessages;
	return this;
    }   
    
    /**
     * Builds the new instance. Returns a new {@link ConnectionSecure} built from
     * the current state of this builder builder.
     */
    @Override
    public GenericLoggerCreator build() {

	Objects.requireNonNull(name, "name cannnot be null!");
	Objects.requireNonNull(logDirectory, "logDirectory cannnot be null!");

	if (!logDirectory.exists()) {
	    boolean created = logDirectory.mkdirs();
	    if (!created) {
		throw new IllegalArgumentException("The log directory can not be created: " + logDirectory);
	    }
	}

	Objects.requireNonNull(name, "name cannnot be null!");
	Objects.requireNonNull(fileNamePattern, "fileNamePattern cannnot be null!");
	Objects.requireNonNull(pattern, "pattern cannnot be null!");

	if (maxFileSize < MB) {
	    throw new IllegalArgumentException("maxFileSize must be > 1 MB");
	}

	if (totalSizeCap < maxFileSize) {
	    throw new IllegalArgumentException("totalSizeCap must be >= maxFileSize");
	}

	GenericLoggerCreator genericLoggerCreator = new GenericLoggerCreator(this);
	return genericLoggerCreator;
    }

}
