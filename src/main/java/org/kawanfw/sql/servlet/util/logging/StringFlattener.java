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
package org.kawanfw.sql.servlet.util.logging;

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

	try (BufferedReader bufferedReader = new BufferedReader(new StringReader(inString));) {
	    String line = null;
	    while ((line = bufferedReader.readLine()) != null) {
		buffer.append(line);
	    }

	    return buffer.toString();
	}

    }

}
