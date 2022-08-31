/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.server.session.test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.kawanfw.sql.api.server.session.SessionIdentifierGenerator;

/**
 * 
 */

/**
 * 
 * Token ID generator with 26 long strings. Inspired from
 * http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
 * 
 * @author Nicolas de Pomereu
 *
 */
public class SessionIdentifierGeneratorTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

	Set<String> firstSet = new HashSet<>();

	SessionIdentifierGenerator sessionIdentifierGenerator = new SessionIdentifierGenerator();

	for (int i = 0; i < 10_000_000; i++) {

	    String s = sessionIdentifierGenerator.nextSessionId();
	    firstSet.add(s);

	    if (i % 10000 == 0)
		System.out.println(new Date() + " " + i);
	}

	System.out.println(new Date() + " Set Done!");

	for (int i = 0; i < 10_000_000; i++) {
	    String s = sessionIdentifierGenerator.nextSessionId();

	    if (i % 10000 == 0)
		System.out.println(new Date() + " " + i);
	    if (firstSet.contains(s)) {
		throw new IllegalArgumentException(
			"failure: string already in set: " + s);
	    }
	}

	System.out.println(new Date() + " Test Done!");

    }

}
