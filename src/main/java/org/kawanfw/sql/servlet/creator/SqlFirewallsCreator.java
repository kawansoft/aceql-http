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
package org.kawanfw.sql.servlet.creator;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.firewall.DefaultSqlFirewallManager;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;

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
public class SqlFirewallsCreator {

    private static String[] PREDEFINED_CLASS_NAMES = {
	    org.kawanfw.sql.api.server.firewall.CsvRulesManager.class.getSimpleName(),
	    org.kawanfw.sql.api.server.firewall.DefaultSqlFirewallManager.class.getSimpleName(),
	    org.kawanfw.sql.api.server.firewall.DenyDclManager.class.getSimpleName(),
	    org.kawanfw.sql.api.server.firewall.DenyDdlManager.class.getSimpleName(),
	    org.kawanfw.sql.api.server.firewall.DenyExecuteUpdateManager.class.getSimpleName(),
	    org.kawanfw.sql.api.server.firewall.DenyMetadataQueryManager.class.getSimpleName(),
	    org.kawanfw.sql.api.server.firewall.DenyStatementClassManager.class.getSimpleName(),
	    };

    private List<String> sqlFirewallClassNames = new ArrayList<>();
    private List<SqlFirewallManager> sqlFirewallManagers = new ArrayList<>();

    public SqlFirewallsCreator(List<String> sqlFirewallClassNames, String database,
	    DatabaseConfigurator databaseConfigurator)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, IOException {

	if (sqlFirewallClassNames != null && !sqlFirewallClassNames.isEmpty()) {

	    for (String sqlFirewallClassName : sqlFirewallClassNames) {

		sqlFirewallClassName = getNameWithPackage(sqlFirewallClassName);

		Class<?> c = Class.forName(sqlFirewallClassName);
		Constructor<?> constructor = c.getConstructor();
		SqlFirewallManager sqlFirewallManager = (SqlFirewallManager) constructor.newInstance();

		try (Connection connection = databaseConfigurator.getConnection(database);) {
		    List<Object> parameterValues = new ArrayList<>();
		    // We call code just to verify it's OK:
		    sqlFirewallManager.allowSqlRunAfterAnalysis("username", database, connection, "127.0.0.1",
			    "select * from table", false, parameterValues);
		}

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

    /**
     * Allows to add automatically the package for predefined classes
     *
     * @param theClassName
     * @return
     */
    private static String getNameWithPackage(String theClassName) {

	for (int i = 0; i < PREDEFINED_CLASS_NAMES.length; i++) {
	    if (PREDEFINED_CLASS_NAMES[i].equals(theClassName)) {
		// Add prefix package
		theClassName = org.kawanfw.sql.api.server.firewall.SqlFirewallManager.class.getPackage()
			.getName() + "." + theClassName;
		return theClassName;
	    }
	}

	return theClassName;
    }

    public List<SqlFirewallManager> getSqlFirewalls() {
	return sqlFirewallManagers;
    }

    public List<String> getSqlFirewallClassNames() {
	return sqlFirewallClassNames;
    }

}
