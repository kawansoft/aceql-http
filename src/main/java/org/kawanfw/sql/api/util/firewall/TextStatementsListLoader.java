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
package org.kawanfw.sql.api.util.firewall;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.kawanfw.sql.api.server.StatementNormalizer;
import org.kawanfw.sql.util.Tag;

/**
 * Loads all the sql statements contained in a text file structured like:
 *
 * <pre>
 * <code>
select * from my_table_1
update my_table set column1 = 'my_value'
select * from my_table_2 where column2 = ?
...
</code>
 * </pre>
 *
 * @author Nicolas de Pomereu
 * @since 11.0
 *
 */

public class TextStatementsListLoader {

    private File file = null;

    /** The set of normalized statements to deny */
    private Set<String> normalizedStatementSet = new HashSet<>();

    /**
     * Constructor.
     *
     * @param file the file containing all the statements list, one per line
     */
    public TextStatementsListLoader(File file) {
	this.file = Objects.requireNonNull(file, "file cannot be null!");
    }

    /**
     * Loads the CSV containing the rules, one rule per line.
     *
     * @param file the CSV containing the rules, one rule per line.
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void load() throws IOException, FileNotFoundException {

	if (!file.exists()) {
	    throw new FileNotFoundException(Tag.PRODUCT_USER_CONFIG_FAIL +  " The file does not exist: " + file);
	}

	try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file));) {
	    String line = null;
	    while ((line = bufferedReader.readLine()) != null) {

		if (line.isEmpty() || line.startsWith("#")) {
		    continue;
		}

		String normalizedStatement = StatementNormalizer.getNormalized(line);
		normalizedStatementSet.add(normalizedStatement);

	    }
	}
    }

    /**
     * @return the normalizedStatementSet
     */
    public Set<String> getNormalizedStatementSet() {
	return normalizedStatementSet;
    }

}
