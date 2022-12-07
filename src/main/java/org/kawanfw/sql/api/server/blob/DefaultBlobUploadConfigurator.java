/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.server.blob;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.SystemUtils;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.util.FrameworkDebug;

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
    private static boolean DEBUG = FrameworkDebug.isSet(DefaultBlobUploadConfigurator.class);

    private Properties properties = null;
    
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
	
	// Set an upload limit, if any
	try {
	    long maxBlobLength = getMaxBlobLength();
	    if (getMaxBlobLength() > 0) {
	    	upload.setFileSizeMax(maxBlobLength);
	    }
	} catch (SQLException e) {
	    throw new IOException(e);
	}

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
    
    /**
     * @return the value of the property {@code defaultBlobUploadConfigurator.maxBlobLength}
     *         defined in the {@code aceql-server.properties} file at server
     *         startup. If the property does not exist, returns 0 (i.e. no Blob upload limit).
     */
    @Override
    public long getMaxBlobLength() throws IOException, SQLException {
	long maxBlobLength = 0;
	setProperties();

	String maxBlobLengthStr = properties.getProperty("defaultBlobUploadConfigurator.maxBlobLength");

	// No limit if not set
	if (maxBlobLengthStr == null) {
	    return 0;
	}
	
	try {
	    maxBlobLength = Long.parseLong(maxBlobLengthStr);
	} catch (NumberFormatException e) {
	    throw new IllegalArgumentException(
		    "The defaultBlobUploadConfigurator.maxBlobLength property is not numeric: " + maxBlobLengthStr);
	}

	return maxBlobLength;
    }
    
    /**
     * Sets in memory the Properties of the used {@code aceql-server.properties}
     * file.
     * 
     * @throws IOException
     * @throws DatabaseConfigurationException
     */
    private void setProperties() throws IOException, DatabaseConfigurationException {
	if (properties == null) {
	    File file = PropertiesFileStore.get();
	    properties = PropertiesFileUtil.getProperties(file);
	}
    }
    

    private void debug(String s) {
	if (DEBUG)
	    System.out.println(new Date() + " " + s);
    }

}
