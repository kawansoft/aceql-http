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
package org.kawanfw.sql.api.server.executor;

import java.sql.SQLException;
import java.util.List;

/**
 * A internal wrapper for Java package protected calls. <br>
 * This is an internal undocumented class that should not be used nor called by
 * the users of the AceQL APis.
 *
 * @author Nicolas de Pomereu
 * @since 9.0
 */

public class ClientEventWrapper {

    protected ClientEventWrapper() {

    }

    public static ClientEvent builderClientEvent(String username, String database, String ipAddress,
	    List<Object> parameterValues) throws SQLException {
	return new ClientEvent(username, database, ipAddress, parameterValues);
    }
}
