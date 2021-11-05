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
package org.kawanfw.sql.util.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Remove all CR/LF from a string.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class StringFlattener {

    private final String inString;

    public StringFlattener(String inString) {
	this.inString = inString;
    }

    /**
     * Flatten the inString by removing all CR/LF.
     * 
     * @return The flattened inString
     * @throws IOException 
     */
    public String flatten() throws IOException {

	if (inString == null) {
	    return null;
	}

	StringBuffer buffer = new StringBuffer();

	BufferedReader bufferedReader = new BufferedReader(new StringReader(inString));

	String line = null;
	while ((line = bufferedReader.readLine()) != null) {
	    buffer.append(line);
	}

	return buffer.toString();
    }

}
