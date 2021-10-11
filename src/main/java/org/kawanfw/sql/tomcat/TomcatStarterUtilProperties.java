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
package org.kawanfw.sql.tomcat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.properties.EncryptableProperties;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.tomcat.util.LinkedProperties;
import org.kawanfw.sql.tomcat.util.PropertiesPasswordManagerLoader;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * Methods for properties and jasypt encrypted properties loading.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class TomcatStarterUtilProperties {

    /** Debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(TomcatStarterUtilProperties.class);

    /**
     * Returns the Properties extracted from a file.
     *
     * @param file the file containing the properties
     * @return the Properties extracted from the file
     *
     * @throws IOException
     * @throws DatabaseConfigurationException
     */
    public static Properties getProperties(File file) throws IOException, DatabaseConfigurationException {

	if (file == null) {
	    throw new IllegalArgumentException("file can not be null!");
	}

	if (!file.exists()) {
	    throw new DatabaseConfigurationException("properties file not found: " + file);
	}

	// Get the properties with order of position in file:
	Set<String> linkedProperties = LinkedProperties.getLinkedPropertiesName(file);

	// Create the ordered properties Properties properties;
	Properties properties = new Properties();
	try (InputStream in = new FileInputStream(file);) {
	    properties = new LinkedProperties(linkedProperties);
	    properties.load(in);
	}

	char[] password = null;
	try {
	    password = PropertiesPasswordManagerLoader.getPassword(properties);
	} catch (Exception e) {
	    throw new DatabaseConfigurationException(e.getMessage());
	}

	if (password == null) {
	    debug("PropertiesPasswordManager password is null!");
	} else {
	    debug("password: " + new String(password));
	}

	return password == null ? properties : getEncryptedProperties(file, password);

    }

    /**
     * @param file
     * @param password
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static Properties getEncryptedProperties(File file, char[] password)
	    throws IOException, FileNotFoundException {

	// We load the encrypted properties
	StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	encryptor.setPassword(new String(password));
	encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
	encryptor.setIvGenerator(new RandomIvGenerator());

	Properties props = new EncryptableProperties(encryptor);
	try (InputStream in = new FileInputStream(file);) {
	    props.load(in);
	}

	return props;
    }

    /**
     * Print debug info
     *
     * @param s
     */

    private static void debug(String s) {
	if (DEBUG)
	    System.out.println(new Date() + " " + s);
    }

}
