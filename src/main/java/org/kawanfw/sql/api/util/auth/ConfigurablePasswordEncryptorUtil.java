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

package org.kawanfw.sql.api.util.auth;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.digest.config.EnvironmentStringDigesterConfig;
import org.jasypt.salt.StringFixedSaltGenerator;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.kawanfw.sql.util.SqlTag;

public class ConfigurablePasswordEncryptorUtil {

    public static final String DEFAULT_AUTHENTICATION_QUERY = "SELECT encrypted_password FROM users WHERE username = ?";
    public static final int DEFAULT_HASH_ITERATIONS = 1;
    public static final String DEFAULT_HASH_ALGORITHM = "SHA-256";
    public static final String DEFAULT_SAULT = null;

    public static ConfigurablePasswordEncryptor getConfigurablePasswordEncryptor(Properties properties)
	    throws IOException {

	/*
	 * # The database to use. If not set, the first value in the "databases" #
	 * property at top of file will be used jdbcUserAuthenticator.database=
	 * 
	 * # The algorithm to use to hash passwords. Defaults to SHA-256 if no set.
	 * jdbcUserAuthenticator.hashAlgorithm=SHA-256
	 * 
	 * # The number of hashing iterations. Defaults to 1 if no set.
	 * jdbcUserAuthenticator.hashIterations=1
	 * 
	 * # The salt string to use. If not set, no salt will be used.
	 * jdbcUserAuthenticator.salt=
	 * 
	 * # The query that will be executed in order to authenticate the user. #
	 * Default to "SELECT password FROM user WHERE username = ?"
	 * jdbcUserAuthenticator.authenticationQuery=SELECT password FROM user WHERE
	 * username = ?
	 */

	Objects.requireNonNull(properties, "properties cannot be null!");

	String hashAlgorithm = properties.getProperty("jdbcUserAuthenticator.hashAlgorithm");
	if (hashAlgorithm == null || hashAlgorithm.isEmpty()) {
	    hashAlgorithm = ConfigurablePasswordEncryptorUtil.DEFAULT_HASH_ALGORITHM;
	}

	String hashIterationsStr = properties.getProperty("jdbcUserAuthenticator.hashIterations");
	if (hashIterationsStr == null || hashIterationsStr.isEmpty()) {
	    hashIterationsStr = "" + ConfigurablePasswordEncryptorUtil.DEFAULT_HASH_ITERATIONS;
	}

	if (!StringUtils.isNumeric(hashIterationsStr)) {
	    throw new IOException(SqlTag.USER_CONFIGURATION
		    + ". jdbcUserAuthenticator.hashIterations property value is not numeric : " + hashIterationsStr);
	}

	String salt = properties.getProperty("jdbcUserAuthenticator.salt");
	if (salt == null || salt.isEmpty()) {
	    salt = ConfigurablePasswordEncryptorUtil.DEFAULT_SAULT;
	}

	ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
	passwordEncryptor.setStringOutputType("hexadecimal");

	EnvironmentStringDigesterConfig digesterConfig = new EnvironmentStringDigesterConfig();
	digesterConfig.setAlgorithm(hashAlgorithm);

	if (salt == null) {
	    digesterConfig.setSaltSizeBytes(0);
	} else {
	    digesterConfig.setSaltGenerator(new StringFixedSaltGenerator(salt));
	}

	digesterConfig.setIterations(Integer.parseInt(hashIterationsStr));
	passwordEncryptor.setConfig(digesterConfig);

	return passwordEncryptor;
    }

}
