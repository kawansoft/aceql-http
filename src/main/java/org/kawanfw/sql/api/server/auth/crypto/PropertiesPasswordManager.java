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

package org.kawanfw.sql.api.server.auth.crypto;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Interface that defines how to get the password used to encrypt the
 * {@code Properties} of the {@code aceql-server.properties} file. <br>
 * The password must have been previously created with the
 * {@link PropertiesEncryptor} class called via command line
 * {@code properties-encryptor.bat} on Windows or {@code properties-encryptor} Bash script
 * on Unix. <br>
 * <br>
 * Implement the {@code getPassword()} in your own
 * {@code PropertiesPasswordManager} concrete method or use the provided
 * {@link DefaultPropertiesPasswordManager} implementation.
 * 
 * @author Nicolas de Pomereu
 * @since 7.0
 */
public interface PropertiesPasswordManager {

    /**
     * Returns the password to use to decrypt the encrypted the {@code Properties}
     * of the {@code aceql-server.properties} file.
     * 
     * @return the password to use to decrypt the {@code Properties} of the
     *         {@code aceql-server.properties} file.
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public char[] getPassword() throws IOException, SQLException;

}
