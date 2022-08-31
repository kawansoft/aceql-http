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
package org.kawanfw.sql.api.server.auth.headers;

import java.io.IOException;
import java.util.Map;

import org.kawanfw.sql.api.server.auth.UserAuthenticator;

/**
 * Allows authenticating a client user using the request headers set and sent from
 * the client side. <br>
 * <br>
 * This allows an alternate or supplementary authentication to
 * {@link UserAuthenticator}. <br>
 * <br>
 * Typical usage would be to send - using HTTP - an authentication token stored
 * in one of the request headers to a remote cloud provider.
 *
 * @author Nicolas de Pomereu
 * @since 6.3
 */
public interface RequestHeadersAuthenticator {

    /**
     * Allows to check/validate the request headers as a mean of client
     * authentication. If method returns {@code false}, user will not be granted
     * access.
     *
     * @param headers the request headers sent by the client side.
     * @return {@code true} if request headers are OK and validated. If
     *         {@code false}, the client side will not be authorized to send any
     *         command.
     * @throws IOException if an IOException occurs
     */
    public boolean validate(Map<String, String> headers) throws IOException;

}
