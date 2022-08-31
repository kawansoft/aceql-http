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

    private static final String ACEQL_LICENSE_KEY_TXT = "aceql-license-key.txt";
    
    private static boolean STORED_DONE = false;
    private static File STORED_ACEQL_LICENCSE_FILE = null;
    
    
    /**
     * Reset method to be used when starting from a service, for example
     */
    public static void reset() {
	STORED_DONE = false;
    }

    /**
     * Returns the file of the existing license file, else null if file can not be found.
     * @return the file of the existing license file, else null if file can not be found.
     */
    public static File getLicenseFile() {
	
	if (STORED_DONE) {
	    return STORED_ACEQL_LICENCSE_FILE;
	}
	
	// 1) Try in CLASSPATH (Tomcat Embedded)
	File licenseFile = getLicenseFileFromClassPath();
	
	if (licenseFile != null) {
	    STORED_ACEQL_LICENCSE_FILE = licenseFile;
	    STORED_DONE = true;
	    return licenseFile;
	}
	
	// 2) If we are in native Tomcat : Get the web.xml licenseFile initParam 
	String licenseFileStr = ServerSqlManager.getLicenseFileStr();
	
	licenseFile = null;
	if ( licenseFileStr != null && ! licenseFileStr.isEmpty()) {
	    licenseFile = new File(licenseFileStr);
	}

	STORED_ACEQL_LICENCSE_FILE = licenseFile;
	STORED_DONE = true;
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
