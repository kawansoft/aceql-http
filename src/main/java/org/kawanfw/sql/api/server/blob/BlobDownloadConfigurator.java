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
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface that defines the download method that will do the effective
 * Blob/Clob download. <br>
 * The {@link DefaultBlobDownloadConfigurator} default implementation is fully
 * functional. <br>
 * Create your own implementation only if you want to implement your own
 * advanced download mechanism with special features: file chunking, recovery
 * mechanisms, etc.
 * 
 * @author Nicolas de Pomereu
 *
 */
public interface BlobDownloadConfigurator {

    /**
     * Method that will do the effective download.
     * 
     * @param request
     *            the http servlet request. May be use to get supplemental
     *            parameters passed by client side.
     * @param file
     *            the file to download corresponding to the blob id called by
     *            the client side
     * @param outputStream
     *            the servlet output stream on which to download the file. Must
     *            *not* be closed at end of download, because it can be reused
     *            to send error message to client side after this method
     *            execution
     * @throws IOException
     *             if any I/O exception occurs during the download
     */

    void download(HttpServletRequest request, File file,
	    OutputStream outputStream) throws IOException;

}
