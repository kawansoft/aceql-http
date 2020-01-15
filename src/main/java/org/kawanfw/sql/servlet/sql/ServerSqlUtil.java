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
package org.kawanfw.sql.servlet.sql;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
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
     * @param statement
     *            the statement to set
     * @param databaseConfigurator
     *            the DatabaseConfigurator which contains the
     *            getMaxRowsToReturn() method
     * @throws SQLException
     */
    public static void setMaxRowsToReturn(Statement statement,
	    DatabaseConfigurator databaseConfigurator)
	    throws SQLException, IOException {

	int maxRowsToReturn = databaseConfigurator.getMaxRows();

	if (maxRowsToReturn > 0) {
	    if (statement.getMaxRows() == 0
		    || (statement.getMaxRows() > maxRowsToReturn)) {
		statement.setFetchSize(0); // To avoid any possible conflict
		statement.setMaxRows(maxRowsToReturn);
	    }
	}
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
