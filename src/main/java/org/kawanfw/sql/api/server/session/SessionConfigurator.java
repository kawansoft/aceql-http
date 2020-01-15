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
package org.kawanfw.sql.api.server.session;

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
     */
    public String generateSessionId(String username, String database);

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
     * Loads the creation time of the instance
     *
     * @param sessionId the session id
     * @return the creation time of the instance
     */
    public long getCreationTime(String sessionId);

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
     */
    public boolean verifySessionId(String sessionId);

    /**
     * Allows to define the sessions lifetime in minutes
     *
     * @return the sessions lifetime in minutes
     */
    public int getSessionTimelife();
}
