/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
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
package org.kawanfw.sql.api.server.auth.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.kawanfw.sql.util.FrameworkFileUtil;

/**
 * This default implementation will extract the password from the "password"
 * property of the file
 * {@code user.home/.kawansoft/properties_password_manager.properties}. <br/>
 * <br/>
 * This default implementation is provided <i>as is</i>: password is not secured if an
 * attacker gets access to the server. <br/>
 * Note that the {@link #getPassword()} will return {@code null} if the file does not
 * exists. <br/>
 * <br/>
 * 
 * @author Nicolas de Pomereu
 *
 */
public class DefaultPropertiesPasswordManager implements PropertiesPasswordManager {

    /**
     * Returns the password value of property contained in
     * {@code user.home/.kawansoft/properties_password_manager.properties}. <br>
     * Returns {@code null} if the file does not exist.
     */
    @Override
    public char[] getPassword() throws IOException, SQLException {
	FrameworkFileUtil.getUserHomeDotKawansoftDir();

	File file = new File(FrameworkFileUtil.getUserHomeDotKawansoftDir() + File.separator
		+ "properties_password_manager.properties");

	if (!file.exists()) {
	    return null;
	}

	Properties properties = new Properties();
	try (InputStream in = new FileInputStream(file);) {
	    properties.load(in);
	}

	String password = properties.getProperty("password");

	if (password == null || password.isEmpty()) {
	    throw new IOException("password property not defined in file: " + file);
	}

	return password.toCharArray();
    }

}
