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
package org.kawanfw.sql.servlet.sql;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.util.max_rows.MaxRowsSetter;
import org.kawanfw.sql.servlet.util.max_rows.MaxRowsSetterCreator;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 *
 * Utility class to use for SQL on the server side:
 * <ul>
 * <li>setConnectionProperties: set the Connection properties on the server
 * side.</li>
 * <li>decryptSqlOrder: decrypt the SQL orders on the server.</li>
 * </ul>
 *
 * @author Nicolas de Pomereu
 */
public class ServerSqlUtil {

    private static boolean DEBUG = FrameworkDebug.isSet(ServerSqlUtil.class);

    /**
     * Protected constructor
     */
    protected ServerSqlUtil() {

    }

    /**
     * Set the maximum rows to return to the client side
     *
     * @param request
     * @param username
     * @param database
     * @param statement            the statement to set
     * @param databaseConfigurator the DatabaseConfigurator which contains the
     *                             getMaxRowsToReturn() method
     *
     * @throws SQLException
     * @throws IOException
     */
    public static void setMaxRowsToReturn(HttpServletRequest request, String username, String database,
	    Statement statement, DatabaseConfigurator databaseConfigurator) throws SQLException, IOException {

	MaxRowsSetter maxRowsSetter = MaxRowsSetterCreator.createInstance();
	maxRowsSetter.setMaxRows(request, username, database, statement, databaseConfigurator);
	
    }

    /**
     * @param s
     */

    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }
}
