/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.server;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 *
 * Interface that defines the database configurations for AceQL HTTP.
 * <p>
 * The implemented methods will be called by AceQL when a client program,
 * referred by a user username, asks for a JDBC operation from the Client side.
 * <p>
 * A concrete implementation should be developed on the server side in order to:
 * <ul>
 * <li>Define how to extract a JDBC Connection from a Connection Pool.</li>
 * <li>Define the directories where the Blobs/Clobs are located for upload and
 * download.</li>
 * <li>Define some Java code to execute before/after a
 * <code>Connection.close()</code>.
 * <li>Define the maximum number of rows that may be returned to the
 * client.</li>
 * <li>Define the {@code Logger} to use to trap server Exceptions.</li>
 * </ul>
 * <p>
 * Note that the framework comes with a default
 * <code>DatabaseConfigurator</code> implementation that is *not* secured and
 * should be extended: {@link DefaultDatabaseConfigurator}.
 * <p>
 *
 * @author Nicolas de Pomereu
 */

public interface DatabaseConfigurator {

    /**
     * <p>
     * Attempts to establish a connection with an underlying data source.
     *
     * @param database the database name to get the connection from
     *
     * @return a <code>Connection</code> to the data source
     * @exception SQLException if a database access error occurs
     */
    public Connection getConnection(String database) throws SQLException;

    /**
     * Allows to define how to close the Connection acquired with
     * {@link DatabaseConfigurator#getConnection(String)} and return it to the pool.
     * <br>
     * <br>
     * Most connection pool implementations just require to call
     * <code>Connection.close()</code> to release the connection, but it is
     * sometimes necessary for applications to add some operation before/after the
     * close. This method allows you to add specific code before or after the close.
     * <br>
     * <br>
     * It is not required to implement this method, because the default
     * implementation {@link DefaultDatabaseConfigurator#close(Connection)} closes
     * the connection with an explicit call to <code>Connection.close()</code>. <br>
     * If you implement this method, you *must* close the connection.
     *
     * @param connection a connection to the data source
     * @throws SQLException if a SQLException occurs
     */
    public void close(Connection connection) throws SQLException;

    /**
     * Allows to define the maximum rows per request to be returned to the client.
     * If this limit is exceeded, the excess rows are silently dropped.
     *
     * @param username the client username to check the max rows for.
     * @param database the database name as defined in the JDBC URL field
     * @return the maximum rows per request to be returned to the client; zero means
     *         there is no limit
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public int getMaxRows(String username, String database) throws IOException, SQLException;

    /**
     * Allows to define the directory into which Blobs/Clobs are uploaded by client
     * side, and from which Blobs/Clobs are downloaded by client side. <br>
     * See default implementation in:
     * {@link DefaultDatabaseConfigurator#getBlobsDirectory(String)}.
     *
     * @param username the client username
     * @return the Blob/Clob upload and download directory for this username
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public File getBlobsDirectory(String username) throws IOException, SQLException;

    /**
     * Returns the {@link Logger} that will be used by AceQL for logging:
     * <ul>
     * <li>All Exceptions thrown by server side will be logged.</li>
     * <li>Exceptions thrown are logged with <code>Level.WARNING</code>.</li>
     * </ul>
     * It is not necessary nor recommended to implement this method; do it only if
     * you want take control of the logging to modify the default characteristics of
     * {@link DefaultDatabaseConfigurator#getLogger()}.
     *
     * @return the {@code Logger} that will be used by AceQL logging;
     * @throws IOException if an IOException occurs
     */
    public Logger getLogger() throws IOException;

}
