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
package org.kawanfw.sql.metadata.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Nicolas de Pomereu
 *
 */
public class FileWordReplacer {

    /** Universal and clean line separator */
    private static String CR_LF = System.getProperty("line.separator");

    private File inFile = null;
    private File outFile= null;
    private List<String> oldWords = new ArrayList<>();
    private List<String> newWords=  new ArrayList<>();

    public FileWordReplacer(final File inFile, final File outFile, final List<String> oldWords, final List<String> newWords) throws FileNotFoundException {

	if (inFile == null) {
	    throw new NullPointerException("inFile is null!");
	}
	if (outFile == null) {
	    throw new NullPointerException("outFile is null!");
	}
	if (oldWords == null) {
	    throw new NullPointerException("oldWords is null!");
	}
	if (newWords == null) {
	    throw new NullPointerException("newWords is null!");
	}

	if (! inFile.exists()) {
	    throw new FileNotFoundException("inFile does not exist: " + inFile);
	}

	if (oldWords.size() != newWords.size()) {
	    throw new IllegalArgumentException("oldWords & newWords sizes are different!");
	}

	this.inFile = inFile;
	this.outFile = outFile;
	this.oldWords = oldWords;
	this.newWords = newWords;
    }

    public void replaceAll() throws IOException {

	try (BufferedReader br = new BufferedReader(new FileReader(inFile));
		Writer writer = new BufferedWriter(new FileWriter(outFile));) {

	    String line = null;
	    while ((line = br.readLine()) != null) {
		for (int i = 0; i < oldWords.size(); i++) {
		  line = line.replace(oldWords.get(i), newWords.get(i));
		}

		writer.write(line + CR_LF);
	    }
	}
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
	System.out.println(new Date() + " Begin...");
	File inFile = new File("c:\\test\\sc.out.html");
	File outFile= new File("c:\\test\\sc.out-2.html");

	List<String> oldWords = new ArrayList<>();
	List<String> newWords = new ArrayList<>();

	oldWords.add("SchemaCrawler");
	oldWords.add("16.2.7");

	newWords.add("AceQL");
	newWords.add("4.0");

	FileWordReplacer fileWordReplacer = new FileWordReplacer(inFile, outFile, oldWords, newWords);
	fileWordReplacer.replaceAll();
	System.out.println(new Date() + " Done.");
    }

}
