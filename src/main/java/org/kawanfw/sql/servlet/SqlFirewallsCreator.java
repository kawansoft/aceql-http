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
package org.kawanfw.sql.servlet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.kawanfw.sql.api.server.firewall.DefaultSqlFirewallManager;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;

/**
 *
 * @author Nicolas de Pomereu
 *
 */
public class SqlFirewallsCreator {

    private List<String> sqlFirewallClassNames = new ArrayList<>();
    private List<SqlFirewallManager> sqlFirewallManagers = new ArrayList<>();

    public SqlFirewallsCreator(List<String> sqlFirewallClassNames)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException {

	if (sqlFirewallClassNames != null && !sqlFirewallClassNames.isEmpty()) {

	    for (String sqlFirewallClassName : sqlFirewallClassNames) {
		Class<?> c = Class.forName(sqlFirewallClassName);
		Constructor<?> constructor = c.getConstructor();
		SqlFirewallManager sqlFirewallManager = (SqlFirewallManager) constructor.newInstance();
		sqlFirewallClassName = sqlFirewallManager.getClass().getName();

		this.sqlFirewallManagers.add(sqlFirewallManager);
		this.sqlFirewallClassNames.add(sqlFirewallClassName);
	    }

	} else {
	    SqlFirewallManager sqlFirewallManager = new DefaultSqlFirewallManager();
	    String sqlFirewallClassName = sqlFirewallManager.getClass().getName();

	    this.sqlFirewallManagers.add(sqlFirewallManager);
	    this.sqlFirewallClassNames.add(sqlFirewallClassName);
	}

    }

    public List<SqlFirewallManager> getSqlFirewalls() {
	return sqlFirewallManagers;
    }

    public List<String> getSqlFirewallClassNames() {
	return sqlFirewallClassNames;
    }

}
