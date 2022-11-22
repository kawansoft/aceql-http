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
public class DefaultBlobDownloadConfigurator implements BlobDownloadConfigurator {

    /**
     * Simple copy of file to download on Servlet output stream.
     */
    @Override
    public void download(HttpServletRequest request, File file, OutputStream outputStream) throws IOException {

	if (!file.exists()) {
	    throw new FileNotFoundException("File does not exist: " + file.getName());
	}

	Files.copy(file.toPath(), outputStream);

    }

}
