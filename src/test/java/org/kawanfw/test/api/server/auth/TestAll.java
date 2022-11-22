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
package org.kawanfw.test.api.server.auth;

import java.util.Date;

/**
 * Test all built in Authenticator classes.
 * @author Nicolas de Pomereu
 *
 */
public class TestAll {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	System.out.println(new Date() + " Begin...");
	SshUserAuthenticatorTest.test();
	WindowsUserAuthenticatorTest.test();
	System.out.println(new Date() + " End...");
    }

}
