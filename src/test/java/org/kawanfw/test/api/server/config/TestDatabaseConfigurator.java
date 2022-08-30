/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
