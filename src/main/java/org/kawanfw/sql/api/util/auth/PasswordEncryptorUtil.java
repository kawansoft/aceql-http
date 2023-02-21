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
package org.kawanfw.sql.api.util.auth;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Utility methods for {@code JdbcPasswordEncryptor} tool.
 * @author Nicolas de Pomereu
 *
 */
public class PasswordEncryptorUtil {

    /**
     * Create the CLI Options
     *
     * @return the CLI Options
     * @throws IllegalArgumentException
     */
    public static Options createOptions() throws IllegalArgumentException {
	Options options = new Options();
	
	Option versionOption  = Option.builder("version")
                .desc("print the version")
                .build();
	
	Option helpOption  = Option.builder("help")
                .desc("print this message")
                .build();
	
	Option propertiesOption  = Option.builder("properties")
                .argName("file")
                .hasArg()
                .desc("the path to aceql-server.properties file")
                .build();
	
	Option passwordOption  = Option.builder("password")
                .argName("password")
                .hasArg()
                .desc("the password to encrypt")
                .build();

	options.addOption(versionOption);
	options.addOption(helpOption);
	options.addOption(propertiesOption);
	options.addOption(passwordOption);

	return options;
    }
    
    /**
     * Prints usage
     *
     * @param options the CLI Options
     */
    public static void printUsage(Options options) {
	// automatically generate the help statement
	HelpFormatter formatter = new HelpFormatter();
	formatter.setWidth(200);

	String help = "jdbc_password_encryptor -properties <file> " + "-password <password>";

	formatter.printHelp(help, options);
	System.out.println();
    }

    
}
