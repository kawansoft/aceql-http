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
package org.kawanfw.sql.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Nicolas de Pomereu
 *
 *         Allow to debug files contained in
 *         user.home/.kanwansoft/aceql-debug-server.ini.
 *
 */
public class FrameworkDebug {
    /** The file that contain the classes to debug in user.home */
    private static String ACEQL_DEBUG_FILE = "aceql-debug-server.ini";

    /** Stores the classes to debug */
    private static Set<String> CLASSES_TO_DEBUG = new HashSet<String>();

    /**
     * Protected constructor
     */
    protected FrameworkDebug() {

    }

    /**
     * Says if a class must be in debug mode
     *
     * @param clazz the class to analyze if debug must be on
     * @return true if the class must be on debug mode, else false
     */
    public static boolean isSet(Class<?> clazz) {
	load();

	String className = clazz.getName();
	String rawClassName = StringUtils.substringAfterLast(className, ".");

	return CLASSES_TO_DEBUG.contains(className)
		|| CLASSES_TO_DEBUG.contains(rawClassName);
    }

    /**
     * Load the classes to debug from the file
     *
     * @throws IOException
     */
    private static void load() {
	if (!CLASSES_TO_DEBUG.isEmpty()) {
	    return;
	}

	File kawansoftDir = new File(
		FrameworkFileUtil.getUserHomeDotKawansoftDir());
	kawansoftDir.mkdirs();

	String file = kawansoftDir + File.separator + ACEQL_DEBUG_FILE;

	// Nothing to load if file not set
	if (!new File(file).exists()) {
	    CLASSES_TO_DEBUG.add("empty");
	    return;
	}

	try (LineNumberReader lineNumberReader = new LineNumberReader(
		new FileReader(file));) {

	    String line = null;
	    while ((line = lineNumberReader.readLine()) != null) {
		line = line.trim();

		if (line.startsWith("//") || line.startsWith("#")
			|| line.isEmpty()) {
		    continue;
		}

		CLASSES_TO_DEBUG.add(line);
	    }
	} catch (FileNotFoundException e) {
	    throw new IllegalArgumentException(
		    "Wrapped IOException. Impossible to load debug file: "
			    + file,
			    e);
	} catch (IOException e) {
	    throw new IllegalArgumentException(
		    "Wrapped IOException. Error reading debug file: " + file,
		    e);
	}
    }

}
