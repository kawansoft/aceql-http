/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
