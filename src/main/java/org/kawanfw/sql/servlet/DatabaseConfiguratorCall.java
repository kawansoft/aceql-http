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
package org.kawanfw.sql.servlet;

/**
 *
 * Calls DatabaseConfigurator methods using reflection
 *
 * @author Nicolas de Pomereu
 *
 */

public class DatabaseConfiguratorCall {

//    /**
//     * Returns the result of DatabaseConfigurator.allowExecuteUpdate
//     *
//     * @param databaseConfigurator
//     *            the DatabaseConfigurator instance
//     * @param username
//     *            the client username
//     * @param connection
//     *            The JDBC Connection
//     * @return the result of DatabaseConfigurator.allowExecuteUpdate
//     * @throws IOException
//     * @throws SQLException
//     */
//    public static boolean allowExecuteUpdate(
//	    DatabaseConfigurator databaseConfigurator, String username,
//	    Connection connection) throws IOException, SQLException {
//	return databaseConfigurator.allowExecuteUpdate(username, connection);
//    }

//    /**
//     * Returns the result of DatabaseConfigurator.allowStatementClass
//     *
//     * @param databaseConfigurator
//     *            the DatabaseConfigurator instance
//     * @param username
//     *            the client username
//     * @param connection
//     *            The JDBC Connection
//     * @return the result of DatabaseConfigurator.allowStatementClass
//     * @throws IOException
//     * @throws SQLException
//     */
//    public static boolean allowStatementClass(
//	    DatabaseConfigurator databaseConfigurator, String username,
//	    Connection connection) throws IOException, SQLException {
//	return databaseConfigurator.allowStatementClass(username, connection);
//    }


//    /**
//     * Runs DatabaseConfigurator.runIfStatementRefused
//     *
//     * @param databaseConfigurator
//     *            the DatabaseConfigurator instance
//     * @param username
//     *            the client username
//     * @param connection
//     *            The JDBC Connection
//     * @throws IOException
//     * @throws SQLException
//     */
//    public static void runIfStatementRefused(
//	    DatabaseConfigurator databaseConfigurator, String username,
//	    Connection connection, String ipAddress, String sql,
//	    List<Object> parameterValues) throws IOException, SQLException {
//	databaseConfigurator.runIfStatementRefused(username, connection,
//		ipAddress, sql, parameterValues);
//    }

//    /**
//     * Return the result of getMaxRowsToReturn method of DatabaseConfigurator,
//     * if it's implemented, else 0.
//     *
//     * @param databaseConfigurator
//     *            the DatabaseConfigurator instance
//     * @return the result of getMaxRowsToReturn of DatabaseConfigurator, if it's
//     *         implemented, else 0;
//     *
//     * @throws IOException
//     * @throws SQLException
//     */
//    public static int getMaxRowsToReturn(
//	    DatabaseConfigurator databaseConfigurator)
//	    throws IOException, SQLException {
//	return databaseConfigurator.getMaxRows();
//    }

}
