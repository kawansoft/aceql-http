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
 * The Sempahore file is created when the server is stopped with WebServerApi.
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
