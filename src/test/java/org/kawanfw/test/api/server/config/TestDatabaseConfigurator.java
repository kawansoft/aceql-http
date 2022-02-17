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
package org.kawanfw.test.api.server.config;

import java.util.Date;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 *
 *         DatabaseConfigurator implementation. Its extends the default
 *         configuration and provides a security mechanism for login.
 */

public class TestDatabaseConfigurator extends DefaultDatabaseConfigurator
	implements DatabaseConfigurator {

    /** Debug info */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(TestDatabaseConfigurator.class);


    /**
     * @param s
     *            the content to log/debug
     */
    @SuppressWarnings("unused")
    private void debug(String s) {
	if (DEBUG)
	    System.out.println(
		    this.getClass().getName() + " " + new Date() + " " + s);
    }

}
