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
package org.kawanfw.sql.servlet.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.kawanfw.sql.api.server.StatementAnalyzer;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SqlFirewalManagerUtil {

    /**
     * Says if user is allowed to write on database
     * 
     * @param username
     * @param database
     * @param sqlOrder
     * @param isAllowedExecuteUpdate
     * @param sqlFirewallManager
     * @param connection
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public static boolean isWriteDatabaseAllowed(String username, String database, String sqlOrder,
            boolean isAllowedExecuteUpdate, SqlFirewallManager sqlFirewallManager, Connection connection)
            throws SQLException, IOException {
        List<Object> parameters = new ArrayList<>();
        StatementAnalyzer analyzer = new StatementAnalyzer(sqlOrder, parameters);
        if (analyzer.isDelete() || analyzer.isInsert() || analyzer.isUpdate() || analyzer.isDcl() || analyzer.isDdl()
        	|| analyzer.isTcl()) {
            isAllowedExecuteUpdate = sqlFirewallManager.allowDatabaseWrite(username, database, connection);
        }
        return isAllowedExecuteUpdate;
    }

}
