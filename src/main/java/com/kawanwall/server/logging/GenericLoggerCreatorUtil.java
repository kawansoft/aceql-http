/*
 * This file is part of KawanWall.
 * KawanWall: Firewall for SQL statements
 * Copyright (C) 2022,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.                 
 *                                                                         
 * KawanWall is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.         
 *              
 * KawanWall is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Affero General Public License for more details.       
 *                                  
 * You should have received a copy of the GNU Affero General 
 * Public License along with this program; if not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * If you develop commercial activities using KawanWall, you must: 
 * a) disclose and distribute all source code of your own product,
 * b) license your own product under the GNU General Public License.
 * 
 * You can be released from the requirements of the license by
 * purchasing a commercial license. Buying such a license will allow you 
 * to ship KawanWall with your closed source products without disclosing 
 * the source code.
 *
 * For more information, please contact KawanSoft SAS at this
 * address: sales@kawansoft.com
 * 
 * Any modifications to this file must keep this entire header
 * intact.
 */
package com.kawanwall.server.logging;

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
