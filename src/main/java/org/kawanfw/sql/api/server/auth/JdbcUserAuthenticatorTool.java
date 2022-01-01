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
package org.kawanfw.sql.api.server.auth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.util.auth.JdbcUserAuthenticatorUtil;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;

/**
 * Tooling class that allows to generate to hashed/encrypted passwords.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class JdbcUserAuthenticatorTool {

    private ConfigurablePasswordEncryptor passwordEncryptor;

    /**
     * Default constructor.
     * 
     * @param propertiesFile the aceql-server.properties file
     * @throws IOException	if an I/O Exception occurs
     * @throws DatabaseConfigurationException if a configuration Exception occurs
     */
    public JdbcUserAuthenticatorTool(File propertiesFile) throws DatabaseConfigurationException, IOException {
	Objects.requireNonNull(propertiesFile, "propertiesFile cannot be null!");
	if (!propertiesFile.exists()) {
	    throw new FileNotFoundException("The file does not exist: " + propertiesFile);
	}
	Properties properties = PropertiesFileUtil.getProperties(propertiesFile);
	passwordEncryptor = JdbcUserAuthenticatorUtil.getConfigurablePasswordEncryptor(properties);
	
    }

    /**
     * Encrypts the password passed as parameter.
     * @param password	the password to encrypt
     * @return	the encrypted value of the password
     */
    public String encryptPassword(String password) {
	Objects.requireNonNull(password, "password cannot be null!");
	String encryptedPassword = passwordEncryptor.encryptPassword(password);
	return encryptedPassword;
    }

}
