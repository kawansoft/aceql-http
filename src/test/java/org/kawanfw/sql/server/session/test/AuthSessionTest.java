/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.server.session.test;

import org.kawanfw.sql.api.server.session.SessionIdentifierGenerator;
import org.kawanfw.sql.api.server.session.SessionInfo;

/**
 * @author Nicolas de Pomereu
 *
 */
public class AuthSessionTest {


    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	String username = "user1";
	String database = "my_database";

	SessionIdentifierGenerator sessionIdentifierGenerator = new SessionIdentifierGenerator();
	String sessionId = sessionIdentifierGenerator.nextSessionId();

	@SuppressWarnings("unused")
	SessionInfo SessionInfo = new SessionInfo(sessionId, username,
		database);
	System.out.println("sessionId: " + sessionId);

    }

}
