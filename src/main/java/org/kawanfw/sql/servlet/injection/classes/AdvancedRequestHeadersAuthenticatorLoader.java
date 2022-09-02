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
import java.sql.SQLException;

import org.kawanfw.sql.servlet.injection.classes.InjectedClasses.InjectedClassesBuilder;
import org.kawanfw.sql.servlet.injection.classes.creator.RequestHeadersAuthenticatorCreator;
import org.kawanfw.sql.util.SqlTag;

public class AdvancedRequestHeadersAuthenticatorLoader implements RequestHeadersAuthenticatorLoader {

    /**
     * Loads a RequestHeadersAuthenticatorCreator instance.
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
     * @throws SQLException
     */
    @Override
    public void loadRequestHeadersAuthenticator(InjectedClassesBuilder injectedClassesBuilder,
	    String requestHeadersAuthenticatorClassName)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {

	RequestHeadersAuthenticatorCreator userAuthenticatorCreator = new RequestHeadersAuthenticatorCreator(
		requestHeadersAuthenticatorClassName);

	injectedClassesBuilder.requestHeadersAuthenticator(userAuthenticatorCreator.getRequestHeadersAuthenticator());

	requestHeadersAuthenticatorClassName = userAuthenticatorCreator.getRequestHeadersAuthenticatorClassName();

	System.out.println(SqlTag.SQL_PRODUCT_START + " Loading RequestHeadersAuthenticator class:");
	System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + requestHeadersAuthenticatorClassName);
    }


}
