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
package org.kawanfw.test.api.server.auth;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.kawanfw.sql.api.server.auth.WindowsUserAuthenticator;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;

/**
 * @author Nicolas de Pomereu
 *
 */
public class WindowsUserAuthenticatorTest {

    /**
     * Tests a login using a Windows server.
     * @throws IOException
     * @throws SQLException
     */
    public static void test() throws IOException, SQLException {
	PropertiesFileStore.set(new File("I:\\_dev_awake\\aceql-http-main\\aceql-http\\conf\\aceql-server.properties"));
	WindowsUserAuthenticator windowsUserAuthenticator = new WindowsUserAuthenticator();
	String username = "user1";
	String password = FileUtils.readFileToString(new File("I:\\__NDP\\_MyPasswords\\login_user1.txt"), "UTF-8");

	boolean logged = windowsUserAuthenticator.login(username, password.toCharArray(), "database", "10.0.0.10");
	System.out.println(new Date() + " WindowsUserAuthenticator logged: " + logged);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	test();
    }

}
