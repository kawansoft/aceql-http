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
package org.kawanfw.sql.api.server.auth.headers;

import java.io.IOException;
import java.util.Map;

/**
 * A concrete and unsafe {@link RequestHeadersAuthenticator} that always grant
 * access to remote client users. <br>
 * This class will be automatically loaded if no
 * {@code RequestHeadersAuthenticator} has been declared in the
 * {@code aceql-server.properties} file.
 *
 * @author Nicolas de Pomereu
 * @since 6.3
 *
 */
public class DefaultRequestHeadersAuthenticator implements RequestHeadersAuthenticator {

    /**
     * @return <code>true</code>. (Client is always granted access).
     */
    @Override
    public boolean validate(Map<String, String> headers) throws IOException {
	return true;
    }
}
