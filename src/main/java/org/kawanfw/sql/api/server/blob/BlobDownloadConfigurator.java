/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
