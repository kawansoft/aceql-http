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
package org.kawanfw.sql.tomcat.license;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * Displays the license file expiration info
 * @author Nicolas de Pomereu
 *
 */
public class LicenseExpirationDisplayer {

    private  File licenseFile;

    
    public LicenseExpirationDisplayer(File licenseFile) throws FileNotFoundException {
	this.licenseFile = Objects.requireNonNull(licenseFile, "licenseFile cannot be null");
	
	if (! licenseFile.exists()) {
	    throw new FileNotFoundException("licenseFile does not exist: " + licenseFile);
	}
    }

    /**
     * @return a expiration info message with remaining days & expiration date
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String getExpirationInfo() throws FileNotFoundException, IOException {
	
	Properties properties = getProperties(licenseFile);
	Date date = new Date();
	
	String expirationDate = getNamedProperty(properties, "ExpirationDate");
	String licenseType = getNamedProperty(properties, "LicenseType");
	
	Date expireDate = null;
	try {
	    expireDate = LicenseExpirationUtil.stringToDate(expirationDate);
	} catch (ParseException e) {
	    throw new IOException(e);
	}
	long remainingDays = LicenseExpirationUtil.getDifferenceInDays(date, expireDate);
	
	String daysText = remainingDays > 1 ? "days": "day";
	String message = StringUtils.capitalize(licenseType.toLowerCase()) + " License - will expire in " + remainingDays + " " + daysText + " (on " + expireDate + ")";
	return message;
	
    }

    /**
     * @param properties
     * @return
     * @throws IllegalArgumentException
     */
    public String getNamedProperty(Properties properties, String name) throws IllegalArgumentException {
	String value = properties.getProperty(name);
	if (value == null || value.isEmpty()) {
	    throw new IllegalArgumentException("The " + name + " property does not exist in license file: " + licenseFile);
	}
	return value;
    }

    private static Properties getProperties(File file) throws FileNotFoundException, IOException {
	Properties properties = new Properties();
	try (InputStream in = new FileInputStream(file);) {
	    properties.load(in);
	}
	return properties;
    }
    
    
    
    

}
