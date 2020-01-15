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
package org.kawanfw.sql.api.server.blob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * Class that allows downloading Blob/Clobs. Default implementation. <br>
 * It is not required nor recommended to extend this class or to develop another
 * {@code BlobDownloadConfigurator} implementation. <br>
 * Extend this class and override
 * {@link #download(HttpServletRequest, File, OutputStream)} only if you want to
 * implement your own advanced download mechanism with special features: file
 * chunking, recovery mechanisms, etc.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class DefaultBlobDownloadConfigurator
	implements BlobDownloadConfigurator {

    /**
     * Constructor. {@code BlobDownloadConfigurator} implementation must have no
     * constructor or a unique no parms constructor.
     */
    public DefaultBlobDownloadConfigurator() {

    }

    /**
     * Simple copy of file to download on Servlet output stream.
     */
    @Override
    public void download(HttpServletRequest request, File file,
	    OutputStream outputStream) throws IOException {

	if (!file.exists()) {
	    throw new FileNotFoundException(
		    "File does not exist: " + file.getName());
	}

	Files.copy(file.toPath(), outputStream);

    }

}
