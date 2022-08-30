/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Our own internal private logger that will always catch all Exceptions includind rutime Exceptions
 * @author Nicolas de Pomereu
 *
 */
public class PrivateTmpLogger {

    private static final int MB = 1024 * 1024;
    private static final long MAX_LENGTH = 200 * MB;
    
    private Throwable throwable = null;

   
    /**
     * Constructor
     * @param throwable	thrown when any catch or runtime exceptions are thrown
     */
    public PrivateTmpLogger(Throwable throwable) {
	this.throwable = throwable;
    }


    public void log() throws FileNotFoundException, IOException {

	File tempDir = SystemUtils.getJavaIoTmpDir();
	String tempKawanSoftDirStr = tempDir.toString();
	if (! tempKawanSoftDirStr.endsWith(File.separator)) {
	    tempKawanSoftDirStr+=File.separator;
	}
	
	tempKawanSoftDirStr += ".kawansoft";
	File tempKawanSoftDir = new File(tempKawanSoftDirStr);
	if (!tempKawanSoftDir.exists()) {
	    tempKawanSoftDir.mkdirs();
	}
	
	File file = new File(tempKawanSoftDirStr +  File.separator + "aceql_exceptions.log");
	
	// Security delete if more than 200 MB
	if (file.length() > MAX_LENGTH) {
	    file.delete();
	}
	
	try(OutputStream out = new BufferedOutputStream(new FileOutputStream(file, true))) {
	    ServerSqlManager.writeLine(out, getNowFormatted() + " " + ExceptionUtils.getStackTrace(throwable));	
	}
	
    }
    
    public static String getNowFormatted() {
	Timestamp tsNow = new Timestamp(System.currentTimeMillis());
	DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss.SSS");
	String now = df.format(tsNow);
	return now;
    }
    
    
    

}
