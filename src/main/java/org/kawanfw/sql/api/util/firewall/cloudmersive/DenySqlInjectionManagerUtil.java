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
