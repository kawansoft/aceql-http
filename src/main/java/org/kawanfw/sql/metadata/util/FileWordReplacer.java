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
import java.util.Objects;

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

	this.inFile = Objects.requireNonNull(inFile, "inFile cannot be null!");
	this.outFile = Objects.requireNonNull(outFile, "outFile cannot be null!");
	this.oldWords = Objects.requireNonNull(oldWords, "oldWords cannot be null!");
	this.newWords = Objects.requireNonNull(newWords, "newWords cannot be null!");

	if (! inFile.exists()) {
	    throw new FileNotFoundException("inFile does not exist: " + inFile);
	}

	if (oldWords.size() != newWords.size()) {
	    throw new IllegalArgumentException("oldWords & newWords sizes are different!");
	}


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
