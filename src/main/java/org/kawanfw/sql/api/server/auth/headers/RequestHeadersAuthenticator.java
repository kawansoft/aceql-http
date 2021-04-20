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
