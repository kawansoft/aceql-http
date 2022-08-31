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
package org.kawanfw.sql.api.server.session;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * Session management using self-contained JWT (JSON Web Token). <br>
 * See <a href="https://jwt.io">https://jwt.io</a> for more info on JWT. <br>
 * <br>
 * Advantage of JWT is that no session info is stored on the server. <br>
 * Disadvantage of JWT is that the token are much longer and thus generate more
 * HTTP traffic and are less convenient to use "manually" (with cURL, etc.) <br>
 * <br>
 * Implementation is coded with the
 * <a href="https://github.com/auth0/java-jwt">java-jwt</a> library. <br>
 * <br>
 * Note that:
 * <ul>
 * <li>A secret valued must be defined using the
 * {@code jwtSessionConfiguratorSecret} property in
 * {@code aceql-server.properties}.</li>
 * <li>The JWT lifetime value used is
 * {@link DefaultSessionConfigurator#getSessionTimelifeMinutes()} value.
 * </ul>
 *
 * @author Nicolas de Pomereu
 */
public class JwtSessionConfigurator implements SessionConfigurator {

    /** The aceql-server.properties file. Used to get the session time life */
    private Properties properties = null;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.kawanfw.sql.api.server.session.SessionConfigurator#generateSessionId(
     * java.lang.String, java.lang.String)
     */
    /**
     * Generates a self contained JWT that stores the username and the database.
     * @throws IOException if any I/O error occurs
     */
    @Override
    public String generateSessionId(String username, String database) throws IOException {

	try {
	    String secret = ConfPropertiesStore.get().getJwtSessionConfiguratorSecretValue();

	    if (secret == null || secret.isEmpty()) {
		throw new IllegalArgumentException(
			"The jwtSessionConfiguratorSecret property value defined in the AceQL properties file cannot be null.");
	    }

	    Algorithm algorithm = Algorithm.HMAC256(secret);

	    Builder builder = JWT.create();
	    builder.withClaim("usr", username);
	    builder.withClaim("dbn", database);
	    builder.withIssuedAt(new Date());

	    if (getSessionTimelifeMinutes() != 0) {
		Date expiresAt = new Date(System.currentTimeMillis() + (getSessionTimelifeMinutes() * 60 * 1000));
		builder.withExpiresAt(expiresAt);
	    }

	    String token = builder.sign(algorithm);
	    return token;
	} catch (JWTCreationException exception) {
	    // Invalid Signing configuration / Couldn't convert Claims.
	    throw new IllegalArgumentException(exception);
	}

    }

    /*
     * (non-Javadoc)
     *
     * @see org.kawanfw.sql.api.server.session.SessionConfigurator#getUsername(java.
     * lang.String)
     */
    /**
     * Extracts the username from the decoded JWT.
     */
    @Override
    public String getUsername(String sessionId) {
	try {
	    DecodedJWT jwt = JWT.decode(sessionId);
	    Map<String, Claim> claims = jwt.getClaims(); // Key is the Claim
							 // name
	    Claim claim = claims.get("usr");
	    return claim.asString();

	} catch (JWTDecodeException exception) {
	    exception.printStackTrace();
	    return null;
	}
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kawanfw.sql.api.server.session.SessionConfigurator#getDatabase(java.
     * lang.String)
     */
    /**
     * Extracts the Database from the decoded JWT.
     */
    @Override
    public String getDatabase(String sessionId) {
	try {
	    DecodedJWT jwt = JWT.decode(sessionId);
	    Map<String, Claim> claims = jwt.getClaims(); // Key is the Claim
							 // name
	    Claim claim = claims.get("dbn");
	    return claim.asString();

	} catch (JWTDecodeException exception) {
	    System.err.println(exception);
	    return null;
	}
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kawanfw.sql.api.server.session.SessionConfigurator#remove(java.lang.
     * String)
     */
    @Override
    public void remove(String sessionId) {
	// Nothing to do.
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kawanfw.sql.api.server.session.SessionConfigurator#verifySessionId(
     * java.lang.String)
     */
    /**
     * Performs the verification against the given JWT Token, using any previous
     * configured options. <br>
     * Also verifies that the token is not expired, i.e. its lifetime is shorter
     * than {@code getSessionTimelife()}
     *
     * @throws IOException if an IOException occurs
     */
    @Override
    public boolean verifySessionId(String sessionId) throws IOException {

	try {
	    String secret = ConfPropertiesStore.get().getJwtSessionConfiguratorSecretValue();
	    Algorithm algorithm = Algorithm.HMAC256(secret);
	    JWTVerifier verifier = JWT.require(algorithm).build(); // Reusable
								   // verifier
								   // instance
	    DecodedJWT jwt = verifier.verify(sessionId);

	    if (getSessionTimelifeMinutes() == 0) {
		return true;
	    }

	    Date issuedAt = jwt.getIssuedAt();

	    if (issuedAt != null) {
		// Check if session is expired.
		Date now = new Date();
		if (now.getTime()- issuedAt.getTime() > (getSessionTimelifeMinutes() * 60 * 1000)) {
		    return false;
		}
	    }
	} catch (JWTVerificationException exception) {
	    System.err.println(exception);
	    return false;
	}

	return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.kawanfw.sql.api.server.session.SessionConfigurator#getSessionTimelife ()
     */
    /**
     * Returns same as
     * {@link DefaultSessionConfigurator#getSessionTimelifeMinutes()} value.
     *
     * @throws IOException if an IOException occurs
     */
    @Override
    public int getSessionTimelifeMinutes() throws IOException {
	if (properties == null) {
	    File file = PropertiesFileStore.get();
	    properties = PropertiesFileUtil.getProperties(file);
	}

	return DefaultSessionConfigurator.getSessionTimelifeMinutesPropertyValue(properties);
    }

}
