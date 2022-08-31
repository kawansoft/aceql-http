/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.tomcat.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

/**
 * 
 * Class to manage Semaphore files for each SQL Web server started. <br>
 * <br>
 * A Semaphore file is created in system temp directory and its name is
 * kawanfw-web-server-semaphore-port.&lt;port&gt, where port is the port number
 * of the Web server. <br>
 * <br>
 * The Sempahore file is created when the server is stopped with WebServerApiWrapper.
 * <br>
 * This stops the Web Server.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class PortSemaphoreFile {

    /** The Web server port */
    private int port = -1;

    /**
     * Constructor
     * 
     * @param port
     *            The Web server port
     */
    public PortSemaphoreFile(int port) {
	super();
	this.port = port;
    }

    /**
     * Returns true if the Semaphore File exists.
     * 
     * @return true if the Semaphore File exists
     */
    public boolean exists() {
	return getSemaphoreFile().exists();
    }

    /**
     * Create the semaphore file
     * 
     * @return true if the semaphore file is created
     */
    public boolean create() throws IOException {
	FileUtils.write(getSemaphoreFile(), "" + port,
		Charset.defaultCharset());
	return true;
    }

    /**
     * Delete the semaphore file
     * 
     * @return true if the semaphore file is deleted
     */
    public boolean delete() throws IOException {

	if (!getSemaphoreFile().exists()) {
	    return true;
	}

	FileUtils.forceDelete(getSemaphoreFile());
	return true;

    }

    /**
     * returns the Semaphore File
     * 
     * @return Semaphore File
     */
    public File getSemaphoreFile() {

	String fileStr = System.getProperty("java.io.tmpdir");
	if (!fileStr.endsWith(File.separator)) {
	    fileStr += File.separator;
	}

	fileStr += ".kawansoft";
	new File(fileStr).mkdir();

	if (!fileStr.endsWith(File.separator)) {
	    fileStr += File.separator;
	}

	fileStr += "kawanfw-web-server-semaphore-port." + port;
	return new File(fileStr);
    }

}
