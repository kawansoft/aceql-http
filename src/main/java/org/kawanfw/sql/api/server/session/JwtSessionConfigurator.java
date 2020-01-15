/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2020,  KawanSoft SAS
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
package org.kawanfw.sql.api.server.session;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import org.kawanfw.sql.tomcat.ServletParametersStore;

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
 * {@link DefaultSessionConfigurator#getSessionTimelife()} value.
 * </ul>
 *
 * @author Nicolas de Pomereu
 */
public class JwtSessionConfigurator implements SessionConfigurator {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.kawanfw.sql.api.server.session.SessionConfigurator#generateSessionId(
     * java.lang.String, java.lang.String)
     */
    /**
     * Generates a self contained JWT that stores the username and the database.
     */
    @Override
    public String generateSessionId(String username, String database) {

	try {
	    String secret = ServletParametersStore
		    .getJwtSessionConfiguratorSecretValue();
	    Algorithm algorithm = Algorithm.HMAC256(secret);

	    /*
	    String token = JWT.create().withIssuedAt(new Date())
		    .withClaim("usr", username).withClaim("dbn", database)
		    .sign(algorithm);
	   */

	    Builder builder = JWT.create();
	    builder.withIssuedAt(new Date());
	    builder.withClaim("usr", username);
	    builder.withClaim("dbn", database);
	    builder.withIssuedAt(new Date());

	    if (getSessionTimelife() != 0) {
		Date expiresAt = new Date(System.currentTimeMillis() + (getSessionTimelife() * 60 * 1000));
		builder.withExpiresAt(expiresAt);
	    }

	    String token = builder.sign(algorithm);
	    return token;

	} catch (UnsupportedEncodingException exception) {
	    throw new IllegalArgumentException(exception);
	} catch (JWTCreationException exception) {
	    // Invalid Signing configuration / Couldn't convert Claims.
	    throw new IllegalArgumentException(exception);
	}

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.kawanfw.sql.api.server.session.SessionConfigurator#getUsername(java.
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
     * @see
     * org.kawanfw.sql.api.server.session.SessionConfigurator#getDatabase(java.
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
     * @see
     * org.kawanfw.sql.api.server.session.SessionConfigurator#getCreationTime(
     * java.lang.String)
     */
    @Override
    public long getCreationTime(String sessionId) {
	try {
	    DecodedJWT jwt = JWT.decode(sessionId);
	    Date issuedAt = jwt.getIssuedAt();
	    return issuedAt.getTime();

	} catch (JWTDecodeException exception) {
	    System.err.println(exception);
	    return 0;
	}
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.kawanfw.sql.api.server.session.SessionConfigurator#remove(java.lang.
     * String)
     */
    @Override
    public void remove(String sessionId) {

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.kawanfw.sql.api.server.session.SessionConfigurator#verifySessionId(
     * java.lang.String)
     */
    /**
     * Performs the verification against the given JWT Token, using any previous
     * configured options. <br>
     * Also verifies that the token is not expired, i.e. its lifetime is shorter
     * than {@code getSessionTimelife()}
     */
    @Override
    public boolean verifySessionId(String sessionId) {

	try {
	    String secret = ServletParametersStore
		    .getJwtSessionConfiguratorSecretValue();
	    Algorithm algorithm = Algorithm.HMAC256(secret);
	    JWTVerifier verifier = JWT.require(algorithm).build(); // Reusable
								   // verifier
								   // instance
	    @SuppressWarnings("unused")
	    DecodedJWT jwt = verifier.verify(sessionId);

//	    Date issuedAt = jwt.getIssuedAt();
//	    Date now = new Date();
//
//	    if (now.getTime()
//		    - issuedAt.getTime() > (getSessionTimelife() * 60 * 1000)) {
//		return false;
//	    }

	} catch (UnsupportedEncodingException exception) {
	    System.err.println(exception);
	    // UTF-8 encoding not supported
	    return false;
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
     * org.kawanfw.sql.api.server.session.SessionConfigurator#getSessionTimelife
     * ()
     */
    /**
     * Returns {@link DefaultSessionConfigurator#getSessionTimelife() value}
     */
    @Override
    public int getSessionTimelife() {
	return new DefaultSessionConfigurator().getSessionTimelife();
    }

}
