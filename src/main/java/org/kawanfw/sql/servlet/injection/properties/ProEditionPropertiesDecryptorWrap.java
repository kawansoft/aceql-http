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
package org.kawanfw.sql.servlet.injection.properties;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.kawanfw.sql.util.FrameworkDebug;

public class ProEditionPropertiesDecryptorWrap {

    /** Debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(ProEditionPropertiesDecryptorWrap.class);
    
    
    public static Properties decrypt(Properties properties, char[] password) throws IOException {
	
	Objects.requireNonNull(properties, "properties cannot be null!");
	Objects.requireNonNull(password, "password cannot be null!");
	
	// We load the encrypted properties
	StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	encryptor.setPassword(new String(password));
	encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
	encryptor.setIvGenerator(new RandomIvGenerator());

	for (Map.Entry<Object, Object> entry : properties.entrySet()) {
	    String key = (String) entry.getKey();
	    String value = (String) entry.getValue();
	    //debug("value : " + value);
	    if (value != null && value.trim().startsWith("ENC(") && value.trim().endsWith(")")) {
		value = value.trim();
		//debug("value encrypted: " + value);
		value = StringUtils.substringAfter(value, "ENC(");
		value = StringUtils.substringBeforeLast(value, ")");
		//debug("value before decryption: " + value);
		value = encryptor.decrypt(value).trim();
		properties.setProperty(key, value);
		//debug("value decrypted: " + value);
	    }
	}

	for (Map.Entry<Object, Object> entry : properties.entrySet()) {
	    String key = (String) entry.getKey();
	    String value = (String) entry.getValue();
	    if (key.contains("password")) {
		debug("key / value: " + key + " / " + value);
	    }
	}
	
	return properties;
    }

    /**
     * Print debug info
     *
     * @param s
     */

    private static void debug(String s) {
	if (DEBUG)
	    System.out.println(new Date() + " "  + ProEditionPropertiesDecryptorWrap.class.getSimpleName() + " " + s);
    }
}
