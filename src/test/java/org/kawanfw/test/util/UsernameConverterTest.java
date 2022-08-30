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
package org.kawanfw.test.util;

import org.kawanfw.sql.api.server.util.UsernameConverter;
import org.kawanfw.sql.util.HtmlConverter;

public class UsernameConverterTest {
    /**
     * @param args
     */
    public static void main(String[] args) {
	String string = "user-<>:\"/\\|?*";
	System.out.println(string);
	String specialString = UsernameConverter.fromSpecialChars(string);
	System.out.println(specialString);
	string = UsernameConverter.toSpecialChars(specialString);
	System.out.println(string);

	System.out.println();
	String ldapUser = "cn=read-only-admin,dc=example,dc=com";
	String ldapUserHtml = HtmlConverter.toHtml(ldapUser);
	System.out.println(ldapUserHtml);

	ldapUser = "CN=L. Eagle,O=Sue\\2C Grabbit and Runn,C=GB";
	ldapUser = UsernameConverter.fromSpecialChars(ldapUser);
	System.out.println(ldapUser);
	ldapUser = HtmlConverter.toHtml(ldapUser);
	System.out.println(ldapUser);
    }

}
