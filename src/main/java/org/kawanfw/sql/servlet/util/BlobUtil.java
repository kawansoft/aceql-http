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
package org.kawanfw.sql.servlet.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Nicolas de Pomereu
 *
 */
public class BlobUtil {

    /**
     * Protected contrusctor
     */
    protected BlobUtil() {

    }

    public static long getBlobLength(String blobId, File blobDirectory)
	    throws IOException, SQLException {

	Objects.requireNonNull(blobId, "blobId cannot be null!");
	Objects.requireNonNull(blobDirectory, "blobDirectory cannot be null!");

	String fileName = blobDirectory.toString() + File.separator + blobId;

	File file = new File(fileName);

	if (!file.exists()) {
	    throw new FileNotFoundException(
		    "No file found for blob_id: " + blobId);
	}

	return file.length();

    }
}
