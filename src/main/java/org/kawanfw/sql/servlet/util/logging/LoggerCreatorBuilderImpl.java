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
        
    public static final File DEFAULT_LOG_DIRECTORY = new File(SystemUtils.USER_HOME + File.separator + ".kawansoft" + File.separator + "log");
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
