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
package org.kawanfw.sql.api.server.auth;

import java.io.IOException;
import java.sql.SQLException;

/**
 * A concrete and unsafe {@code UserAuthenticator} that always grant access to
 * remote client users. <br>
 * This class will be automatically loaded if no
 * {@code userAuthenticatorClassName} property has been declared in the
 * {@code aceql-server.properties} file.
 *
 * @see UserAuthenticator
 * @author Nicolas de Pomereu
 * @since 5.0
 *
 */
public class DefaultUserAuthenticator implements UserAuthenticator {

    /**
     * @return true. (Client is always granted access).
     */
    @Override
    public boolean login(String username, char[] password, String database, String ipAddress)
	    throws IOException, SQLException {
	return true;
    }
}
