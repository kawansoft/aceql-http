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

		//String normalizedStatement = StatementNormalizer.getNormalized(line);
		StatementNormalizer statementNormalizer = new StatementNormalizer(line);
		String normalizedStatement = statementNormalizer.getNormalized();
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
