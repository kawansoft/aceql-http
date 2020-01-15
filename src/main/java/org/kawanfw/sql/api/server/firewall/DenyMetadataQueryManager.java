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
package org.kawanfw.sql.api.server.firewall;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Firewall manager that denies the use of the AceQL Metadata Query API.
 *
 * @author Nicolas de Pomereu
 *
 */
public class DenyMetadataQueryManager extends DefaultSqlFirewallManager implements SqlFirewallManager {

    /**
     * @return <code><b>false</b></code>. (Client programs will never allowed to
     *         call the Metadata Query API).
     */
    @Override
    public boolean allowMetadataQuery(String username, String database, Connection connection)
	    throws IOException, SQLException {
	return false;
    }
}
