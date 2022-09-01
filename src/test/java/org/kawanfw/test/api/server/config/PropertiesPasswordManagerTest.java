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
package org.kawanfw.test.api.server.config;

import java.io.FileInputStream;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.properties.EncryptableProperties;

/**
 * @author Nicolas de Pomereu
 *
 */
public class PropertiesPasswordManagerTest {


    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	 StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	 encryptor.setPassword("azerty123"); // could be got from web, env variable...
	 encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
	 encryptor.setIvGenerator(new RandomIvGenerator());
	 
	 /*
	  * Create our EncryptableProperties object and load it the usual way.
	  */
	 Properties props = new EncryptableProperties(encryptor);
	 props.load(new FileInputStream("I:\\_dev_awake\\aceql-http-main\\aceql-http\\conf\\aceql-server.properties"));
	 
	 System.out.println(props.get("sampledb.username"));
	 System.out.println(props.get("sampledb.password"));
    }

}
