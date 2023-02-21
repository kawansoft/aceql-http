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
package org.kawanfw.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.kawanfw.sql.util.FrameworkSystemUtil;

public class UserPrefManager {

    /**
     * Set the database to use
     *
     * @param database
     *            the database to use
     * @throws IOException
     */
    public static void setDatabaseToUse(String database) throws IOException {
	FileUtils.write(getProductFile(), "database = " + database,
		Charset.defaultCharset());
    }

    /**
     *
     * @return the product to use
     * @throws IOException
     */
    public static String getSqlEngineToUse() throws IOException {

	FileInputStream in = null;

	try {
	    in = new FileInputStream(getProductFile());

	    Properties prop = new Properties();
	    prop.load(in);

	    String product = prop.getProperty("database");
	    product = product.trim();
	    return product;
	} finally {
	    if (in != null)
		in.close();
	}
    }

    /**
     *
     * @return the product file that contains the product name
     */
    private static File getProductFile() {
	// Use absolute directory: client side and Tomcat side don'ts have same
	// user.home value!
	String exchangeDir = null;

	if (SystemUtils.IS_OS_WINDOWS) {
	    exchangeDir = "c:\\temp\\";
	} else {
	    if (FrameworkSystemUtil.isAndroid()) {
		// exchangeDir = "/sdcard/";
		exchangeDir = System.getProperty("java.io.tmpdir");
		if (!exchangeDir
			.endsWith(System.getProperty("file.separator"))) {
		    exchangeDir += System.getProperty("file.separator");
		}
	    } else {
		exchangeDir = "/tmp/";
	    }

	}

	File productFile = new File(exchangeDir + "aceql-database-to-use.txt");
	return productFile;
    }

}
