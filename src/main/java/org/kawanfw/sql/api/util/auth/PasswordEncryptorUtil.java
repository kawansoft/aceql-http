/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
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
