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
