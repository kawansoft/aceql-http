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
package com.kawanwall.server.logging;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kawanwall.server.api.logging.LoggerCreator;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Creates a slf4j Logger with Logback implementation that allows to pass
 * all parameters with a builder.
 * 
 * 
 * @author Nicolas de Pomereu
 *
 */
public class GenericLoggerCreator implements LoggerCreator {

    private org.slf4j.Logger logger;
    private LoggerContext context;
    private ByteArrayOutputStream byteArrayOutputStream;
    
    private Map<String, String> elements;
    
    GenericLoggerCreator(LoggerCreatorBuilderImpl builder) {
	
	String name = builder.name;
	
	String logDirectory = builder.logDirectory.toString();
	if (! logDirectory.endsWith(File.separator)) {
	    logDirectory += File.separator;
	}
	
	String fileNamePattern = builder.fileNamePattern;
	String pattern = builder.pattern;
	long maxFileSize = builder.maxFileSize;
	long totalSizeCap = builder.totalSizeCap;
	boolean displayOnConsole = builder.displayOnConsole;
	boolean displayLogStatusMessages = builder.displayLogStatusMessages;

	elements = GenericLoggerCreatorUtil.createElements(name, logDirectory, fileNamePattern, maxFileSize, totalSizeCap, displayOnConsole,
		displayLogStatusMessages);
	
	int maxHistory = 365;

	context = (LoggerContext) LoggerFactory.getILoggerFactory();

	RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<ILoggingEvent>();
	rollingFileAppender.setAppend(true);
	rollingFileAppender.setContext(context);

	// OPTIONAL: Set an active log file (separate from the rollover files).
	// If rollingPolicy.fileNamePattern already set, you don't need this.
	// rollingFileAppender.setFile(LOG_DIR + "kawanwall.log");

	SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<ILoggingEvent>();
	rollingPolicy.setContext(context);
	rollingPolicy.setFileNamePattern(logDirectory + fileNamePattern);
	rollingPolicy.setMaxHistory(maxHistory);
	rollingPolicy.setTotalSizeCap(new FileSize(totalSizeCap));
	rollingPolicy.setMaxFileSize(new FileSize(maxFileSize));
	rollingPolicy.setParent(rollingFileAppender); // parent and context required!	
	rollingPolicy.start();

	rollingFileAppender.setRollingPolicy(rollingPolicy);

	PatternLayoutEncoder encoder = new PatternLayoutEncoder();
	encoder.setPattern(pattern);
	encoder.setContext(context);
	encoder.start();

	rollingFileAppender.setEncoder(encoder);
	rollingFileAppender.start();
	
	ConsoleAppender<ILoggingEvent> logConsoleAppender = new ConsoleAppender<>();
	logConsoleAppender.setContext(context);
	logConsoleAppender.setName("console");
	logConsoleAppender.setEncoder(encoder);
	logConsoleAppender.start();
	    
	byteArrayOutputStream = new ByteArrayOutputStream();
	PrintStream ps = new PrintStream(byteArrayOutputStream);
	StatusPrinter.setPrintStream(ps);
	StatusPrinter.print(context);
	
	logger = context.getLogger(name);
	((ch.qos.logback.classic.Logger) logger).addAppender(rollingFileAppender);
	((ch.qos.logback.classic.Logger) logger).setAdditive(false);

	if (displayOnConsole) {
	    ((ch.qos.logback.classic.Logger) logger).addAppender(logConsoleAppender);
	}
	
	if (displayLogStatusMessages) {
	    System.err.println(byteArrayOutputStream.toString());
	}
    }

    /**
     * Creates a new {@code ConnectionSecure} builder.
     *
     * <p> Builders returned by this method create instances
     * of the default {@code ConnectionSecure} implementation.
     *
     * @return a {@code ConnectionSecure.Builder}
     */
    public static Builder newBuilder() {
	return new LoggerCreatorBuilderImpl();
    }
    

    /**
     * Returns the Logback Logger as sl4j instance
     * @return the Logback Logger as sl4j instance
     */
    @Override
    public Logger getLogger() {
        return logger;
    }

    
    @Override
    public Map<String, String> getElements() {
	return elements;
    }

    /**
     * A builder of {@code GenericLoggerCreator}.
     *
     * <p>
     * Builders are created by invoking {@link GenericLoggerCreator#newBuilder()
     * newBuilder}. Each of the setter methods modifies the state of the builder and
     * returns the same instance. Builders are not thread-safe and should not be
     * used concurrently from multiple threads without external synchronization.
     *
     * @since 1.0
     */
    public interface Builder {

	/**
	 * Sets the name of the Logger. Defaults to "Main"
	 * @param name	the name of the Logger
	 * @return this Builder
	 */
	Builder name(String name);
	
	/**
	 * Sets the file name pattern following Logback naming. Must include date and numbering
	 * such as in "kawanwall_%d.log.%i".
	 * @param setFileNamePattern the file name pattern following Logback naming
	 * @return this Builder
	 */
	public Builder fileNamePattern(String setFileNamePattern);
	
	/**
	 * Sets the Pattern of each log entry. Defaults to "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n" in default implementation.
	 * @param pattern  Pattern of each log entry
	 * @return this Builder
	 */
	public Builder pattern(String pattern);

	/**
	 * Sets the Log directory. Defaults to USER.HOME/.kawansoft.log in default implementation.
	 * @param logDirectory the log directory
	 * @return this Builder
	 */
	public Builder logDirectory(File logDirectory);

	/**
	 * Sets the maximum size per log file in bytes. Defaults to 300Mb in default implementation.
	 * @param maxFileSize the maximum size per log file in bytes.
	 * @return this Builder
	 */
	public Builder maxFileSize(long maxFileSize);

	/**
	 * Sets the total size before rotation occurs. Defaults to 30Gb  in default implementation.
	 * @param totalSizeCap the total size before rotation occurs.
	 * @return this Builder
	 */
	public Builder totalSizeCap(long totalSizeCap);

	/**
	 * If true, log messages are displayed on console. Defaufts to false.
	 * @param displayOnConsole If true, log messages are displayed on console	
	 * @return this Builder
	 */
	Builder displayOnConsole(boolean displayOnConsole);
	
	/**
	 * If true, the status of the log creation is displayed on stderr. This is to be used to debug the log creation.
	 * @param displayLogStatusMessages If true, the status of the log creation is displayed on stderr
	 * @return this Builder
	 */
	Builder displayLogStatusMessages(boolean displayLogStatusMessages);
	
	/**
	 * Builds the new instance. Returns a new {@link GenericLoggerCreator} built from
	 * the current state of this builder builder.
	 */
	public GenericLoggerCreator build();

    }
    
    

}
