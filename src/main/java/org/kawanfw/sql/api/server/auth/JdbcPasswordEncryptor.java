/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.server.auth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.util.auth.ConfigurablePasswordEncryptorUtil;
import org.kawanfw.sql.api.util.auth.PasswordEncryptorUtil;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.version.VersionWrapper;

/**
 * Tooling class that allows to generate to hashed/encrypted passwords for
 * {@code JdbcUserAuthenticator}. The encrypted passwords are to be inserted in
 * the users table. <br>
 * <br>
 * The hash parameters (algorithm, iterations, salt) are defined in the
 * {@code aceql-sever.properties}. <br>
 * <br>
 * The {@link #encryptPassword(String)} method may be called from a Java
 * program:
 * 
 * <pre><code>
    File file = new File("/path/to/aceql-server.properties");
    String password = "myPassword";
    
    JdbcPasswordEncryptor jdbcPasswordEncryptor = new JdbcPasswordEncryptor(file);
    String encryptedPassword = jdbcPasswordEncryptor.encryptPassword(password);
    System.out.println(encryptedPassword);
 * </code></pre>
 * 
 * It may also be run as command line:
 * <ul>
 * <li>Open a command line on Windows or Linux/Bash.</li>
 * <li>{@code cd <installation-directory>/AceQL/bin}</li>
 * <li>Windows: run {@code jdbc_password_encryptor.bat -help}</li>
 * <li>Linux: run {@code jdbc_password_encryptor -help}</li>
 * </ul>
 * 
 * @author Nicolas de Pomereu
 * @since 10.1
 */
public class JdbcPasswordEncryptor {

    private ConfigurablePasswordEncryptor passwordEncryptor;

    /**
     * Default constructor.
     * 
     * @param propertiesFile the aceql-server.properties file
     * @throws IOException                    if an I/O Exception occurs
     * @throws DatabaseConfigurationException if a configuration Exception occurs
     */
    public JdbcPasswordEncryptor(File propertiesFile) throws DatabaseConfigurationException, IOException {
	Objects.requireNonNull(propertiesFile, "propertiesFile cannot be null!");
	if (!propertiesFile.exists()) {
	    throw new FileNotFoundException("The properties file does not exist: " + propertiesFile);
	}
	PropertiesFileStore.set(propertiesFile);
	Properties properties = PropertiesFileUtil.getProperties(propertiesFile);
	passwordEncryptor = ConfigurablePasswordEncryptorUtil.getConfigurablePasswordEncryptor(properties);

    }

    /**
     * Encrypts the password passed as parameter.
     * 
     * @param password the password to encrypt
     * @return the encrypted value of the password
     */
    public String encryptPassword(String password) {
	Objects.requireNonNull(password, "password cannot be null!");
	String encryptedPassword = passwordEncryptor.encryptPassword(password);
	return encryptedPassword.trim().toLowerCase();
    }

    /**
     * Allows to encrypt password for the {@code JdbcUserAuthenticator}
     * implementation. <br>
     * A call with the clear password as parameter will print the encrypted
     * password.
     *
     * @param args the arguments: pass "-help" to have arguments list.
     *
     * @throws ParseException                 if any Exception when parsing command
     *                                        line
     * @throws IOException                    if any I/O Exception
     * @throws DatabaseConfigurationException if any error in configuration
     *                                        properties file
     */
    public static void main(String[] args) throws ParseException, DatabaseConfigurationException, IOException {
	CommandLine cmd = null;
	Options options = null;
	try {
	    options = PasswordEncryptorUtil.createOptions();
	    CommandLineParser parser = new DefaultParser();
	    cmd = parser.parse(options, args);
	} catch (UnrecognizedOptionException e) {
	    System.out.println(e.getMessage());
	    System.out.println();
	    PasswordEncryptorUtil.printUsage(options);
	    System.exit(1);
	}

	if (cmd.hasOption("help")) {
	    PasswordEncryptorUtil.printUsage(options);
	    System.exit(1);
	}

	if (cmd.hasOption("version")) {
	    System.out.println(VersionWrapper.getServerVersion());
	    System.out.println();
	    System.exit(1);
	}
	
	String fileStr = null;
	if (cmd.hasOption("properties")) {
	    fileStr = cmd.getOptionValue("properties");
	} else {
	    System.out.println("Missing properties option.");
	    PasswordEncryptorUtil.printUsage(options);
	    System.exit(1);
	}

	String password = null;
	if (cmd.hasOption("password")) {
	    password = cmd.getOptionValue("password");
	} else {
	    System.out.println("Missing password option.");
	    PasswordEncryptorUtil.printUsage(options);
	    System.exit(1);
	}

	File file = new File(fileStr);
	JdbcPasswordEncryptor jdbcPasswordEncryptor = new JdbcPasswordEncryptor(file);
	String passwordEncrypted = jdbcPasswordEncryptor.encryptPassword(password);
	System.out.println(passwordEncrypted);
    }

}
