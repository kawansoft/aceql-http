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
package org.kawanfw.sql.servlet.injection.classes;
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

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import org.kawanfw.sql.servlet.injection.classes.InjectedClasses.InjectedClassesBuilder;
import org.kawanfw.sql.servlet.injection.classes.creator.RequestHeadersAuthenticatorCreator;
import org.kawanfw.sql.util.SqlTag;

public class ProEditionRequestHeadersAuthenticatorLoader implements RequestHeadersAuthenticatorLoader {

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
