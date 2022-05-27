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
package org.kawanfw.sql.servlet;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.util.ClasspathUtil;

/**
 * Allows to locate the license file. In CLASSPATH if we are embedded mode, else in native Tomcat the value 
 * @author Nicolas de Pomereu
 *
 */
public class AceQLLicenseFileFinder {

    private static final String ACEQL_LICENSE_KEY_TXT = "aceql_license_key.txt";
    
    /**
     * Returns the file of the existing license file, else null if file can not be found.
     * @return the file of the existing license file, else null if file can not be found.
     */
    public static File getLicenseFile() {
	
	// 1) Try in CLASSPATH (Tomcat Embedded)
	File licenseFile = getLicenseFileFromClassPath();
	
	if (licenseFile != null) {
	    return licenseFile;
	}
	
	// 2) If we are in native Tomcat : Get the web.xml licenseFile initParam 
	String licenseFileStr = ServerSqlManager.getLicenseFileStr();
	
	licenseFile = null;
	if ( licenseFileStr != null && ! licenseFileStr.isEmpty()) {
	    licenseFile = new File(licenseFileStr);
	}

	return licenseFile;

    }

    /**
     * Returns the license file that is in the same directory as the aceql-server.properties file 
     * @return the AceQL license file in the same directory
     */
    private static File getLicenseFileFromClassPath() {
	
	List<String> classpathList = ClasspathUtil.getClasspath();
	
	for (String classpathElement : classpathList) {
	    String directory = StringUtils.substringBeforeLast(classpathElement, File.separator);
	    directory += File.separator;
	    File licenceFile = new File (directory +  ACEQL_LICENSE_KEY_TXT);
	    if (licenceFile.exists()) {
		return licenceFile;
	    }
	}
	return null;
    }
}
