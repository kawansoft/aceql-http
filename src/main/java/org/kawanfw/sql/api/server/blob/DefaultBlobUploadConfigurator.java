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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.SystemUtils;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.util.Streams;

/**
 * 
 * Class that allows uploading Blob/Clobs. Default implementation. <br>
 * It is not required nor recommended to extend this class or to develop another
 * {@code BlobUploadConfigurator} implementation. <br>
 * Extend this class and override
 * {@link #upload(HttpServletRequest, HttpServletResponse, File)} only if you
 * want to implement your own advanced upload mechanism with special features:
 * file chunking, recovery mechanisms, etc.
 * 
 * @author Nicolas de Pomereu
 *
 */

public class DefaultBlobUploadConfigurator implements BlobUploadConfigurator {
	private static boolean DEBUG = false;

	/**
	 * Constructor. {@code BlobUploadConfigurator} implementation must have no
	 * constructor or a unique no parms constructor.
	 */
	public DefaultBlobUploadConfigurator() {

	}

	// Max file size
	@SuppressWarnings("unused")
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 20;

	/**
	 * Simple upload of file into user directory.
	 */
	@Override
	public void upload(HttpServletRequest request, HttpServletResponse response, File blobDirectory)
			throws IOException, FileUploadException {

		debug("in upload()");

		response.setContentType("text/html");
		// Prepare the response

		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		debug("isMultipart: " + isMultipart);

		if (!isMultipart) {
			return;
		}

		// Default upload directory is user.home/tmp
		File tempRepository = new File(SystemUtils.getUserHome() + File.separator + "tmp");
		tempRepository.mkdirs();
		debug("tempRepository: " + tempRepository);

		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(tempRepository);

		// Create a new file upload handler using the factory
		// that define the secure temp dir
		ServletFileUpload upload = new ServletFileUpload(factory);

		// Parse the request
		FileItemIterator iter = upload.getItemIterator(request);

		String blobId = null;
		// Parse the request
		while (iter.hasNext()) {
			FileItemStream item = iter.next();
			String name = item.getFieldName();
			debug("name: " + name);

			// The input Stream for the File

			try (InputStream inputstream = item.openStream()) {

				if (item.isFormField()) {
					if (name.equals("blob_id")) {
						blobId = Streams.asString(inputstream);
						debug("blob_id: " + blobId);
					}
				} else {

					debug("");
					debug("File field " + name + " with file name " + item.getName() + " detected.");
					debug("blobId: " + blobId);
					
					// This will throw an IOException if file can not be created
					@SuppressWarnings("unused")
					Path path = Paths.get(blobId);

					String fileName = blobDirectory.toString() + File.separator + blobId;

					Files.copy(inputstream, new File(fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
				}

			}
		}

	}

	private void debug(String s) {
		if (DEBUG)
			System.out.println(new Date() + " " + s);
	}

}
