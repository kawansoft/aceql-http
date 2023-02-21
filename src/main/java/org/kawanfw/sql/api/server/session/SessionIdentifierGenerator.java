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

import java.security.SecureRandom;

/**
 * 
 * Session id generator with 26 long strings.
 * <p>
 * Uses a static {@code SecureRandom}. <br>
 * Each call to {@code nextSessionId()} calls {@code SecureRandom#nextInt(int)}.
 * <br>
 * See Open Source Edition <a href=
 * "https://github.com/kawansoft/aceql-http/blob/master/src/main/java/org/kawanfw/sql/api/server/session/SessionIdentifierGenerator.java">source
 * code</a>.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class SessionIdentifierGenerator {

    private static final String AB = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();

    /**
     * Returns the next session id using a {@code SecureRandom}
     * 
     * @return the next session id using a {@code SecureRandom}
     */
    public String nextSessionId() {
	return randomString(26);
    }

    private String randomString(int len) {
	StringBuilder sb = new StringBuilder(len);
	for (int i = 0; i < len; i++)
	    sb.append(AB.charAt(rnd.nextInt(AB.length())));
	return sb.toString();
    }

}
