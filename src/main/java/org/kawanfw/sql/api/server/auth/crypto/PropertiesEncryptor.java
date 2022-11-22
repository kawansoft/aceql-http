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
package org.kawanfw.sql.api.server.auth.crypto;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

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
 * <li>{@code cd <installation-directory>/AceQL/bin}</li>
 * <li>Windows: run {@code properties-encryptor.bat}.</li>
 * <li>Linux: run {@code properties-encryptor} Bash.</li>
 * <li>Follow the instructions in order to create the password and to encrypt
 * some property values.</li>
 * </ul>
 * 
 * @author Nicolas de Pomereu
 *
 */
public class PropertiesEncryptor {

   
    /**
     * DefaultVersion of PropertiesEncryptor
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

	StandardPBEStringEncryptor encryptor = PropertiesEncryptorWrap.createEncryptor(password);
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
