/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2020,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.                                
 *                                                                               
 * AceQL HTTP is free software; you can redistribute it and/or                 
 * modify it under the terms of the GNU Lesser General Public                    
 * License as published by the Free Software Foundation; either                  
 * version 2.1 of the License, or (at your option) any later version.            
 *                                                                               
 * AceQL HTTP is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU             
 * Lesser General Public License for more details.                               
 *                                                                               
 * You should have received a copy of the GNU Lesser General Public              
 * License along with this library; if not, write to the Free Software           
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301  USA
 * 
 * Any modifications to this file must keep this entire header
 * intact.
 */
package com.aceql.client.jdbc.http;

import static java.lang.System.currentTimeMillis;
import static java.net.URLConnection.guessContentTypeFromName;
import static java.util.logging.Logger.getLogger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

/**
 * @author Nicolas de Pomereu
 *
 */
class MultipartUtility {

    public boolean DEBUG = false;

    @SuppressWarnings("unused")
    private static final Logger log = getLogger(
	    MultipartUtility.class.getName());

    // Keep this! No System.getProperty("line.separator") that fails on
    // Android
    private static final String CRLF = "\r\n";

    private static final String CHARSET = "UTF-8";

    private HttpURLConnection connection;

    private final OutputStream outputStream;
    private final Writer writer;
    private final String boundary;

    // for log formatting only
    @SuppressWarnings("unused")
    private final URL url;
    @SuppressWarnings("unused")
    private final long start;

    private AtomicInteger progress;
    private AtomicBoolean cancelled;
    private long totalLength;

    public MultipartUtility(final URL url, HttpURLConnection connection,
	    int connectTimeout, AtomicInteger progress, AtomicBoolean cancelled,
	    long totalLength) throws IOException {
	start = currentTimeMillis();

	if (url == null) {
	    throw new IllegalArgumentException("url is null!");
	}

	if (connection == null) {
	    throw new IllegalArgumentException("connection is null!");
	}

	this.progress = progress;
	this.cancelled = cancelled;
	this.totalLength = totalLength;

	this.url = url;
	this.connection = connection;

	boundary = "---------------------------" + currentTimeMillis();

	this.connection.setRequestProperty("Accept-Charset", CHARSET);
	this.connection.setRequestProperty("Content-Type",
		"multipart/form-data; boundary=" + boundary);

	// outputStream = connection.getOutputStream();
	TimeoutConnector timeoutConnector = new TimeoutConnector(connection,
		connectTimeout);
	outputStream = timeoutConnector.getOutputStream();

	writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET));
    }

    public void addFormField(final String name, final String value)
	    throws IOException {
	writer.append("--").append(boundary).append(CRLF)
		.append("Content-Disposition: form-data; name=\"").append(name)
		.append("\"").append(CRLF)
		.append("Content-Type: text/plain; charset=").append(CHARSET)
		.append(CRLF).append(CRLF).append(value).append(CRLF);
    }

    public void addFilePart(final String fieldName, InputStream inputStream,
	    String fileName) throws IOException, InterruptedException {
	// final String fileName = uploadFile.getName();
	writer.append("--").append(boundary).append(CRLF)
		.append("Content-Disposition: form-data; name=\"")
		.append(fieldName).append("\"; filename=\"").append(fileName)
		.append("\"").append(CRLF).append("Content-Type: ")
		.append(guessContentTypeFromName(fileName)).append(CRLF)
		.append("Content-Transfer-Encoding: binary").append(CRLF)
		.append(CRLF);

	writer.flush();
	// outputStream.flush();

	try {
	    uploadUsingInputStream(inputStream);
	} finally {
	    if (inputStream != null) {
		try {
		    inputStream.close();
		} catch (Exception e) {
		    // e.printStackTrace();
		}
	    }
	}

    }

    public void addFilePart(final String fieldName, final File uploadFile)
	    throws IOException, InterruptedException {
	final String fileName = uploadFile.getName();
	writer.append("--").append(boundary).append(CRLF)
		.append("Content-Disposition: form-data; name=\"")
		.append(fieldName).append("\"; filename=\"").append(fileName)
		.append("\"").append(CRLF).append("Content-Type: ")
		.append(guessContentTypeFromName(fileName)).append(CRLF)
		.append("Content-Transfer-Encoding: binary").append(CRLF)
		.append(CRLF);

	writer.flush();
	// outputStream.flush();

	try (InputStream inputStream = new BufferedInputStream(
		new FileInputStream(uploadFile));) {
	    uploadUsingInputStream(inputStream);
	}

    }

    private void uploadUsingInputStream(InputStream inputStream)
	    throws IOException, InterruptedException {

	/*
	 * int readBufferSize = 4096;
	 * 
	 * final byte[] buffer = new byte[readBufferSize]; int bytesRead; while
	 * ((bytesRead = inputStream.read(buffer)) != -1) {
	 * outputStream.write(buffer, 0, bytesRead); }
	 */

	debug("totalLength: " + totalLength);
	debug("progress   : " + progress);
	debug("cancelled  : " + cancelled);

	// Case no progress/cancelled/totaLenth set: direct copy
	if (totalLength <= 0 || progress == null || cancelled == null) {
	    IOUtils.copy(inputStream, outputStream);
	    return;
	}

	int tempLen = 0;
	byte[] buffer = new byte[1024 * 4];
	int n = 0;

	while ((n = inputStream.read(buffer)) != -1) {
	    tempLen += n;

	    if (totalLength > 0 && tempLen > totalLength / 100) {
		tempLen = 0;
		int cpt = progress.get();
		cpt++;

		// Update the progress value for progress
		// indicator
		progress.set(Math.min(99, cpt));
		debug("progress   : " + progress);
	    }

	    // If progress indicator says that user has cancelled the
	    // download, stop now!
	    if (cancelled.get()) {
		throw new InterruptedException(
			"Blob upload cancelled by user.");
	    }

	    outputStream.write(buffer, 0, n);
	}

	// outputStream.flush();
	// writer.append(CRLF); // No! will fail by adding it to the
	// uploaded file
    }

    public void addHeaderField(String name, String value) throws IOException {
	writer.append(name).append(": ").append(value).append(CRLF);
    }

    public void finish() throws IOException {
	writer.append(CRLF).append("--").append(boundary).append("--")
		.append(CRLF);
	writer.close();

    }

    /**
     * Returns the current HttpUrlConnection in use.
     * 
     * @return the current HttpUrlConnection in use
     */
    public HttpURLConnection getConnection() {
	return connection;
    }

    private void debug(String s) {
	System.out.println(new java.util.Date() + " " + s);
    }
}
