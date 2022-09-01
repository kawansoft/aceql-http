/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.server.auth.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;

/**
 * This default implementation will extract the password from the "password"
 * property of the file {@code properties_password_manager.properties} which
 * must be located in the same directory as the {@code aceql-server.properties}
 * file. <br/>
 * <br/>
 * This default implementation is provided <i>as is</i>: password is not secured
 * if an attacker gets access to the server. <br/>
 * Note that the {@link #getPassword()} will return {@code null} if the file
 * does not exists. <br/>
 * <br/>
 * 
 * @author Nicolas de Pomereu
 *
 */
public class DefaultPropertiesPasswordManager implements PropertiesPasswordManager {

    /** Debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(DefaultPropertiesPasswordManager.class);
    
    /**
     * Returns the value of the "password" property contained in the file
     * {@code properties_password_manager.properties} which must be located in the
     * same directory as the {@code aceql-server.properties} file. <br>
     * Returns {@code null} if the file does not exist.
     */
    @Override
    public char[] getPassword() throws IOException, SQLException {

	File dir = PropertiesFileStore.get().getParentFile();
	File file = new File(dir + File.separator + "properties_password_manager.properties");
	
	debug("Dir of aceql-server.properties                :" + dir);
	debug("File of properties_password_manager.properties:" + file);
	
	if (!file.exists()) {
	    debug(file.toString() + " does not exist. No decryption todo.");
	    return null;
	}

	Properties properties = new Properties();
	try (InputStream in = new FileInputStream(file);) {
	    properties.load(in);
	}

	String password = properties.getProperty("password");
	debug("password: " + password);

	if (password == null || password.isEmpty()) {
	    throw new IOException(SqlTag.USER_CONFIGURATION + " password property not defined in file: " + file);
	}

	return password.toCharArray();
    }

    /**
     * Print debug info
     *
     * @param s
     */

    private static void debug(String s) {
	if (DEBUG)
	    System.out.println(new Date() + " "  + DefaultPropertiesPasswordManager.class.getSimpleName() + " " + s);
    }
}
