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
 * @author Nicolas de Pomereu
 *
 */
public class AceQLLicenseFileLoader {

    private static final String ACEQL_LICENSE_KEY_TXT = "aceql_license_key.txt";
    
    private static File aceqlLicenseFile;
    
    /**
     * Returns the license file that is in the same directory as the aceql-server.properties file 
     * @return the AceQL license file in the same directory
     */
    public static File getLicenseFileFromClassPath() {
	
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

    /**
     * Returns the AceQL License file 
     * @return the  AceQL License file 
     */
    public static File getAceqlLicenseFile() {
        return AceQLLicenseFileLoader.aceqlLicenseFile;
    }

    /**
     * Sets the AceQL License file 
     * @param aceqlLicenseFile the AceQL License file to set
     */
    public static void setAceqlLicenseFile(File aceqlLicenseFile) {
        AceQLLicenseFileLoader.aceqlLicenseFile = aceqlLicenseFile;
    }

    
    
}
