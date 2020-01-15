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
	database = database.trim();
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
