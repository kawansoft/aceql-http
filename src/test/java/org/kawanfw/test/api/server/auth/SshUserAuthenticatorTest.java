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
package org.kawanfw.test.api.server.auth;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.kawanfw.sql.api.server.auth.SshUserAuthenticator;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SshUserAuthenticatorTest {

    /**
     * Tests a login using SSH.
     * @throws IOException
     * @throws SQLException
     */
    public static void test() throws IOException, SQLException {
	PropertiesFileStore.set(new File("I:\\_dev_awake\\aceql-http-main\\aceql-http\\conf\\aceql-server.properties"));
	SshUserAuthenticator sshUserAuthenticator = new SshUserAuthenticator();
	String password = FileUtils.readFileToString(new File("I:\\__NDP\\_MyPasswords\\login_user1.txt"), "UTF-8");
	boolean logged = sshUserAuthenticator.login("user1", password.toCharArray(), "database", "10.0.0.10");
	System.out.println(new Date() + " SshUserAuthenticator logged: " + logged);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	test();
    }



}
