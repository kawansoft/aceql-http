/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.util.firewall.cloudmersive;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;

public class DenySqlInjectionManagerUtil {

    /**
     * Returns the {@code cloudmersive.properties} file
     * 
     * @return {@code cloudmersive.properties} file
     * @throws FileNotFoundException if the file does not exist.
     */
    public static File getCloudmersivePropertiesFile() throws FileNotFoundException {
        File file = PropertiesFileStore.get();
    
        Objects.requireNonNull(file, "file cannot be null!");
    
        if (!file.exists()) {
            throw new FileNotFoundException("The properties file does not exist: " + file);
        }
    
        File dir = PropertiesFileStore.get().getParentFile();
        File cloudmersivePropertiesFile = new File(dir + File.separator + "cloudmersive.properties");
    
        if (!cloudmersivePropertiesFile.exists()) {
            throw new FileNotFoundException(
        	    "The cloudmersive.properties file does not exist: " + cloudmersivePropertiesFile);
        }
    
        return cloudmersivePropertiesFile;
    }


}
