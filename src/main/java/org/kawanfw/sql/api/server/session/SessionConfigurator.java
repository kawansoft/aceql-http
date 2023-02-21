/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.server.session;

import java.io.IOException;

/**
 * Interface that defines how to generate and verify session id for (username,
 * database) sessions. <br>
 * <br>
 * Interface implementation allows to:
 * <ul>
 * <li>Define how to generate a session id after client /connect call.</li>
 * <li>Define the sessions lifetime.</li>
 * <li>Define how to verify that the stored session is valid and not
 * expired.</li>
 * </ul>
 * <p>
 * Two implementations are provided:
 * <ul>
 * <li>The {@link DefaultSessionConfigurator} default implementation stores user
 * info on the server.</li>
 * <li>The {@link JwtSessionConfigurator} implementation generates self
 * contained JWT (JSON Web Tokens) and there is no info storage on the
 * server.</li>
 * </ul>
 *
 * @author Nicolas de Pomereu
 */
public interface SessionConfigurator {
    /**
     * Generates a unique session id for the (username, database) couple. The
     * session id is used to authenticate clients calls that pass it at each http
     * call. <br>
     * <br>
     * Method may be also use to store in server memory the (username, database)
     * couple for the generated session id. This is done in the default
     * implementation {@link DefaultSessionConfigurator}.
     *
     * @param username the username to store for the passed session id
     * @param database the database to store for the passed session id
     *
     * @return a unique session id for the (username, database) couple.
     * @throws IOException if any I/O error
     */
    public String generateSessionId(String username, String database) throws IOException;

    /**
     * Loads the username stored for the passed session id
     *
     * @param sessionId the session id
     * @return the username stored for the passed session Id
     */
    public String getUsername(String sessionId);

    /**
     * Loads the database stored for the passed session id
     *
     * @param sessionId the session id
     * @return the database stored for the passed session Id
     */
    public String getDatabase(String sessionId);

    /**
     * Removes storage for the passed session Id. Method is called by AceQL when
     * client side calls {@code logout}
     *
     * @param sessionId the session id
     */
    public void remove(String sessionId);

    /**
     * Verify that this session id corresponds to an existing and is still valid
     * (non expired).
     *
     * @param sessionId the session id to verify
     * @return true if the sessionId is valid
     * @throws IOException if any I/O error
     */
    public boolean verifySessionId(String sessionId) throws IOException;

    /**
     * Allows to define the sessions lifetime in minutes
     *
     * @return the sessions lifetime in minutes
     * @throws IOException if any I/O error
     */
    public int getSessionTimelifeMinutes() throws IOException;
}
