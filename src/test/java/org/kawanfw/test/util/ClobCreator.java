/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
