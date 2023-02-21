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
package org.kawanfw.sql.servlet.util.logging;

import java.util.LinkedHashMap;
import java.util.Map;

public class GenericLoggerCreatorUtil {

    /**
     * Create the elements Map
     * 
     * @param name
     * @param logDirectory
     * @param fileNamePattern
     * @param maxFileSize
     * @param totalSizeCap
     * @param displayOnConsole
     * @param displayLogStatusMessages
     */
    public static Map<String, String> createElements(String name, String logDirectory, String fileNamePattern,
	    long maxFileSize, long totalSizeCap, boolean displayOnConsole, boolean displayLogStatusMessages) {
	Map<String, String> elements = new LinkedHashMap<>();
	elements.put("name", name);
	elements.put("logDirectory", logDirectory);
	elements.put("fileNamePattern", fileNamePattern);
	elements.put("maxFileSize", getInMbOrGb(maxFileSize));
	elements.put("totalSizeCap", getInMbOrGb(totalSizeCap));
	elements.put("displayOnConsole", displayOnConsole + "");
	elements.put("displayLogStatusMessages", displayLogStatusMessages + "");
	return elements;
    }

    /**
     * Transforms size value in text with "Mb" or "Gb"
     * 
     * @param size the size in bytes
     * @return the size in text with "Mb" or "Gb"
     */
    public static String getInMbOrGb(long size) {

	if (size >= LoggerCreatorBuilderImpl.GB) {
	    return size / LoggerCreatorBuilderImpl.GB + "Gb";
	} else {
	    return size / LoggerCreatorBuilderImpl.MB + "Mb";
	}

    }

}
