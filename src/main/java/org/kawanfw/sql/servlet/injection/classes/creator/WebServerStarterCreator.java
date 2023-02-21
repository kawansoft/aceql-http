/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.injection.classes.creator;

import java.lang.reflect.Constructor;
import java.sql.SQLException;

import org.kawanfw.sql.servlet.injection.classes.DefaultWebServerStarter;
import org.kawanfw.sql.servlet.injection.classes.WebServerStarter;

/**
 * @author Nicolas de Pomereu
 *
 */
public class WebServerStarterCreator {


    public WebServerStarter createInstance()
	    throws SQLException {

	    Class<?> c;
	    try {
		c = Class.forName("org.kawanfw.sql.pro.reflection.builders.ProEditionWebServerStarter");
		Constructor<?> constructor = c.getConstructor();
		WebServerStarter webServerStarter = (WebServerStarter) constructor.newInstance();
		return webServerStarter;
	    } catch (ClassNotFoundException e) {
		System.err.println(e.toString());
		return new DefaultWebServerStarter();
	    } catch (Exception e) {
		throw new SQLException(e);
	    }
    }

}
