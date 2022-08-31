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
     * @return the propertiesFile. No controls are done except initial null.
     */
    public static File get()  {
	Objects.requireNonNull(propertiesFile, "propertiesFile was never set and is null!");	
        return propertiesFile;
    }

    @Override
    public String toString() {
	return "PropertiesFileStore [propertiesFile=" + propertiesFile + "]";
    }
    
}
