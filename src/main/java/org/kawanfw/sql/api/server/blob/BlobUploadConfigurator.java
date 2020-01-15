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
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileUploadException;

/**
 * Interface that defines the upload method that will do the effective Blob/Clob
 * upload. <br>
 * The {@link DefaultBlobUploadConfigurator} default implementation is fully
 * functional. <br>
 * Create your own implementation only if you want to implement your own
 * advanced upload mechanism with special features: file chunking, recovery
 * mechanisms, etc.
 * 
 * @author Nicolas de Pomereu
 *
 */
public interface BlobUploadConfigurator {

    /**
     * Method that will do the effective upload.
     * 
     * @param request
     *            the http servlet request
     * @param response
     *            the http servlet response to use to write the out stream on.
     *            The underlying output stream must *not* be closed at end of
     *            download, because it can be reused to send error message to
     *            client side after this method execution.
     * @param blobDirectory
     *            the directory into which the blob must be uploaded
     * 
     * @throws IOException
     *             if any I/O exception occurs during the upload
     * @throws FileUploadException
     *             if any exception during upload
     */
    void upload(HttpServletRequest request, HttpServletResponse response,
	    File blobDirectory) throws IOException, FileUploadException;

}
