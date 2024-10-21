/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.server.blob;

//see https://commons.apache.org/proper/commons-fileupload/migration.html and https://stackoverflow.com/a/79047694
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.javax.JavaxServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.FrameworkFileUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

/**
 *
 * Class that allows uploading Blob/Clobs. Default implementation. <br>
 * It is not required nor recommended to extend this class or to develop another
 * {@code BlobUploadConfigurator} implementation. <br>
 * Extend this class and override
 * {@link #upload(HttpServletRequest, HttpServletResponse, File, long)} only if you
 * want to implement your own advanced upload mechanism with special features:
 * file chunking, recovery mechanisms, etc.
 *
 * @author Nicolas de Pomereu
 *
 */

public class DefaultBlobUploadConfigurator implements BlobUploadConfigurator {
    private static boolean DEBUG = FrameworkDebug.isSet(DefaultBlobUploadConfigurator.class);
    
    /**
     * Simple upload of file into user directory.
     */
    @Override
    public void upload(HttpServletRequest request, HttpServletResponse response, File blobDirectory, long maxBlobLength)
	    throws IOException {

	debug("in upload()");

	response.setContentType("text/html");
	// Prepare the response

	// Check that we have a file upload request
	boolean isMultipart = JavaxServletFileUpload.isMultipartContent(request);
	debug("isMultipart: " + isMultipart);

	if (!isMultipart) {
	    return;
	}

	// Default upload directory is user.home/tmp
	File tempRepository = new File(SystemUtils.getUserHome() + File.separator + "tmp");
	tempRepository.mkdirs();
	debug("tempRepository: " + tempRepository);

	// Create a factory for disk-based file items
	//DiskFileItemFactory factory = new DiskFileItemFactory();
	//factory.setRepository(tempRepository);
	DiskFileItemFactory factory =
			new DiskFileItemFactory.Builder().setPath(tempRepository.getPath()).get();

	// Create a new file upload handler using the factory
	// that define the secure temp dir
	JavaxServletFileUpload upload = new JavaxServletFileUpload(factory);
	
	debug("maxBlobLength: " + maxBlobLength);
	if (DEBUG) {
	    String tempDir = FrameworkFileUtil.getUserHomeDotKawansoftDir();
	    FileUtils.write(new File(tempDir + File.separator + "maxBlobLength.txt"), maxBlobLength + "", StandardCharsets.UTF_8);
	}
	// Set an upload limit, if any
	if (maxBlobLength > 0) {
	    upload.setFileSizeMax(maxBlobLength);
	}
	else {
	    upload.setFileSizeMax(Long.MAX_VALUE);   
	}

	// Parse the request
	FileItemInputIterator iter = upload.getItemIterator(request);

	String blobId = null;
	// Parse the request
	while (iter.hasNext()) {
	    FileItemInput item = iter.next();
	    String name = item.getFieldName();
	    debug("name: " + name);

	    // The input Stream for the File

	    try (InputStream inputstream = item.getInputStream()) {

		if (item.isFormField()) {
		    if (name.equals("blob_id")) {
			blobId = IOUtils.toString(inputstream, StandardCharsets.UTF_8);
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
