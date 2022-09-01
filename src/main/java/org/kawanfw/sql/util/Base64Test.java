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
package org.kawanfw.sql.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

/**
 * @author Nicolas de Pomereu
 *
 */
public class Base64Test {

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {

	System.out.println(new Date() + " Begin...");
	File file = new File("c:\\test\\proust.txt");
	String text = FileUtils.readFileToString(file, "UTF-8");
	System.out.println(text);

	String originalInput = text;
	org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64(80);
	String encodedString = new String(base64.encode(originalInput.getBytes()));
	System.out.println(encodedString);
    }

}
