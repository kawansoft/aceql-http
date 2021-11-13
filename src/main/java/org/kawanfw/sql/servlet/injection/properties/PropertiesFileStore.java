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

package org.kawanfw.sql.servlet.injection.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

/**
 * Static store of the aceql-server.properties file in use
 * @author Nicolas de Pomereu
 *
 */
public class PropertiesFileStore {

    private static File propertiesFile = null;

    private PropertiesFileStore() {

    }

    /** 
     * Sets in store the propertiesFile
     * @param propertiesFile the aceql-server.properties file in use
     * @throws FileNotFoundException 
     */
    public static void set(File propertiesFile) throws FileNotFoundException {
	PropertiesFileStore.propertiesFile = Objects.requireNonNull(propertiesFile, "propertiesFile cannot be null! ");
	
	if (! propertiesFile.exists()) {
	    throw new FileNotFoundException("propertiesFile does not exist: " + propertiesFile);
	}
    }

    /**
     * @return the propertiesFile
     */
    public static File get() {
        return propertiesFile;
    }

    @Override
    public String toString() {
	return "PropertiesFileStore [propertiesFile=" + propertiesFile + "]";
    }
    
}
