/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2020,  KawanSoft SAS
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

package org.kawanfw.sql.api.server.auth;

import java.io.IOException;
import java.sql.SQLException;

/**
 * A concrete and unsafe {@link UserAuthenticator} that always grant access to
 * remote client users. <br>
 * This class will be automatically loaded if no {@code UserAuthenticator} has
 * been declared in the aceql-server.properties file.
 *
 * @author Nicolas de Pomereu
 * @since 5.0
 *
 */
public class DefaultUserAuthenticator implements UserAuthenticator {

    /**
     * Constructor. {@code UserAuthenticator} implementation must have no
     * constructor or a unique no parameters constructor.
     */
    public DefaultUserAuthenticator() {

    }

    /**
     * @return <code>true</code>. (Client is always granted access).
     */
    @Override
    public boolean login(String username, char[] password, String database, String ipAddress)
	    throws IOException, SQLException {
	return true;
    }
}
