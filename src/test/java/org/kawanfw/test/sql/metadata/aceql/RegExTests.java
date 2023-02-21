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
package org.kawanfw.test.sql.metadata.aceql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExTests {

    public static void main(String[] args) throws Exception {

	String text = "http://jenkov.com";

	Pattern pattern = Pattern.compile("com$");
	Matcher matcher = pattern.matcher(text);

	while(matcher.find()){
	    System.out.println("Found match at: "  + matcher.start() + " to " + matcher.end());
	}
    }

}
