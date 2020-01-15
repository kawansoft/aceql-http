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
package org.kawanfw.test.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.kawanfw.test.parms.SqlTestParms;

public class ClobCreator {

    public static String CR_LF = System.getProperty("line.separator");

    /**
     * Constructor
     */
    public ClobCreator() {
    }

    /**
     * Create a user.home/kawanfw-test/aceql-text-1.txt
     * 
     * @throws IOException
     */
    public void create(File clobFile, int numberOfLines) throws IOException {

	if (clobFile == null) {
	    throw new IllegalArgumentException("clobFile can not be null!");
	}

	try (Writer out = new BufferedWriter(new FileWriter(clobFile))) {

	    int cpt = 1;

	    String line = "The quick brown fox jumps over the lazy dog ";
	    while (cpt < numberOfLines + 1) {
		out.write(line + cpt++ + CR_LF);
	    }
	}

    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	int numberOfLines = 10000;

	String dir = System.getProperty("user.home");
	if (!dir.endsWith(File.separator))
	    dir += File.separator;

	File clobFile = new File(dir + SqlTestParms.TEXT_FILE_1);

	MessageDisplayer.display("Creating text file...");
	ClobCreator clobCreator = new ClobCreator();
	clobCreator.create(clobFile, numberOfLines);

	MessageDisplayer.display("Done. Text file " + clobFile
		+ " created with " + numberOfLines + " lines.");
    }
}
