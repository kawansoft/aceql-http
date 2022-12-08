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
     * @param request       the http servlet request
     * @param response      the http servlet response to use to write the out stream
     *                      on. The underlying output stream must *not* be closed at
     *                      end of download, because it can be reused to send error
     *                      message to client side after this method execution.
     * @param blobDirectory the directory into which the blob must be uploaded
     * @param maxBlobLength the maximum length authorized for of a Blob to be
     *                      uploaded. 0 means there is no limit.
     * @throws IOException         if any I/O exception occurs during the upload
     * @throws FileUploadException if any exception during upload
     */
    void upload(HttpServletRequest request, HttpServletResponse response, File blobDirectory, long maxBlobLength)
	    throws IOException, FileUploadException;


}
