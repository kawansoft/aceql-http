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
package org.kawanfw.sql.servlet.injection.classes;

import java.lang.reflect.InvocationTargetException;

import org.kawanfw.sql.api.server.auth.headers.DefaultRequestHeadersAuthenticator;
import org.kawanfw.sql.servlet.injection.classes.InjectedClasses.InjectedClassesBuilder;

public class DefaultRequestHeadersAuthenticatorLoader implements RequestHeadersAuthenticatorLoader {

    /**
     *  Loads a RequestHeadersAuthenticator instance.
     *  
     * @param injectedClassesBuilder
     * @param requestHeadersAuthenticatorClassName
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Override
    public void loadRequestHeadersAuthenticator(InjectedClassesBuilder injectedClassesBuilder,
	    String requestHeadersAuthenticatorClassName)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	injectedClassesBuilder.requestHeadersAuthenticator(new DefaultRequestHeadersAuthenticator());
    }
}
