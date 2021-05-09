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
package org.kawanfw.sql.api.server.auth.crypto;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;

/**
 * Command line interface for property values encryption. It Allows:
 * <ul>
 * <li>To define a password for encrypting property values.</li>
 * <li>Build the encrypted property values to put back in the
 * {@code aceql-server.properties} file.</li>
 * </ul>
 * In order to run:
 * <ul>
 * <li>Open a command line on Windows or Linux/Bash.</li>
 * <li>{@code java -jar <aceql installation dir>/lib-server/properties-encryptor-1.0.jar}.</li>
 * <li>Follow the instructions in order to create the password and to encrypt
 * some property values.</li>
 * </ul>
 * 
 * @author Nicolas de Pomereu
 *
 */
public class PropertiesEncryptor {

   
    /**
     * Version of PropertiesEncryptor
     */
    public static final String VERSION = "1.0";

    /**
     * Calls doIt method.
     * 
     * @param args no values are passed
     * @throws Exception if any Exception occurs
     */
    public static void main(String[] args) throws Exception {
	doIt();
    }

    /**
     * Create a password to encrypt property values and then encrypts each passed
     * value.
     */
    public static void doIt() {
	System.out.println();
	System.out.println("Welcome to AceQL Properties Encryptor " + VERSION + "!");
	System.out.println("Enter \"quit\" to exit at any time.");
	System.out.println();
	System.out.println("First step is to choose a password that will be used to encrypt properties.");
	System.out.println("(This password will be returned by the PropertiesPasswordManager.getPassword() implementation.)");
	System.out.println();

	String password = createPassword();

	if (password == null) {
	    return;
	}

	System.out.println();
	System.out.println("Password created! ");
	System.out.println();
	System.out.println("Second step is to encrypt desired property values:");
	System.out.println(" - Replace each clear value with the encrypted one in the aceql-properties file.");
	System.out.println(" - Encrypted values include the \"ENC(\" prefix and \")\" trailer.");
	System.out.println();

	StandardPBEStringEncryptor encryptor = createEncryptor(password);
	java.io.Console console = System.console();

	while (true) {
	    String valueToEncrypt = console.readLine("Value to encrypt: ");

	    if (valueToEncrypt == null || valueToEncrypt.isEmpty()) {
		continue;
	    }

	    if (valueToEncrypt.equals("quit")) {
		break;
	    }

	    String encryptedValue = encryptor.encrypt(valueToEncrypt);
	    System.out.println("Encrypted value : " + "ENC(" + encryptedValue + ")");
	    System.out.println();
	}
    }

    private static StandardPBEStringEncryptor createEncryptor(String password) {
	StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	encryptor.setPassword(password);
	encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
	encryptor.setIvGenerator(new RandomIvGenerator());
	return encryptor;
    }

    private static String createPassword() {

	java.io.Console console = System.console();

	String password1;
	String password2;

	while (true) {
	    char [] passwordChars = console.readPassword("Enter Password: ");

	    if (passwordChars == null || passwordChars.length == 0) {
		continue;
	    }
	    
	    password1 = new String(passwordChars);

	    if (password1.equals("quit")) {
		return null;
	    }

	    if (password1.length() < 8) {
		System.out.println("Password is too short. Must be 8 characters minimium. Please retry.");
		System.out.println();
		continue;
	    }

	    password2 = new String(console.readPassword("Verify Password: "));
	    if (password1 == null || password1.isEmpty() || password2 == null || password2.isEmpty()) {
		System.out.println("Please enter a value! (or \"quit\" to exit)");
		continue;
	    }
	    if (password1.equals(password2)) {
		return password1;
	    }
	    System.out.println("Password values do not match! Please retry or enter \"quit\" to exit");
	    System.out.println();
	}

    }

}
