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
package org.kawanfw.sql.servlet.injection.classes.creator;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.kawanfw.sql.api.server.firewall.CsvRulesManager;
import org.kawanfw.sql.api.server.firewall.CsvRulesManagerNoReload;
import org.kawanfw.sql.api.server.firewall.DefaultSqlFirewallManager;
import org.kawanfw.sql.api.server.firewall.DenyDclManager;
import org.kawanfw.sql.api.server.firewall.DenyDdlManager;
import org.kawanfw.sql.api.server.firewall.DenyExecuteUpdateManager;
import org.kawanfw.sql.api.server.firewall.DenyMetadataQueryManager;
import org.kawanfw.sql.api.server.firewall.DenyStatementClassManager;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;

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
public class SqlFirewallsCreator {

    //private static final boolean TEST_FIREWALLS = false;

    private static String[] PREDEFINED_CLASS_NAMES = { CsvRulesManager.class.getSimpleName(),
	    CsvRulesManagerNoReload.class.getSimpleName(), DefaultSqlFirewallManager.class.getSimpleName(),
	    DenyDclManager.class.getSimpleName(), DenyDdlManager.class.getSimpleName(),
	    DenyExecuteUpdateManager.class.getSimpleName(), DenyMetadataQueryManager.class.getSimpleName(),
	    DenyStatementClassManager.class.getSimpleName(), };

    private List<String> sqlFirewallClassNames = new ArrayList<>();
    private List<SqlFirewallManager> sqlFirewallManagers = new ArrayList<>();

    public SqlFirewallsCreator(String database)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, IOException {

	if (sqlFirewallClassNames != null && !sqlFirewallClassNames.isEmpty()) {

	    for (String sqlFirewallClassName : sqlFirewallClassNames) {

		sqlFirewallClassName = sqlFirewallClassName.trim();
		sqlFirewallClassName = getNameWithPackage(sqlFirewallClassName);

		Class<?> c = Class.forName(sqlFirewallClassName);
		Constructor<?> constructor = c.getConstructor();
		SqlFirewallManager sqlFirewallManager = (SqlFirewallManager) constructor.newInstance();

		/**
		 * <pre>
		 * <code>
		if (TEST_FIREWALLS) {
		    try (Connection connection = databaseConfigurator.getConnection(database);) {
			List<Object> parameterValues = new ArrayList<>();
			// We call code just to verify it's OK:
			SqlEvent sqlEvent = SqlEventWrapper.sqlEventBuild("username", database, "127.0.0.1",
				"select * from table", false, parameterValues, false);
			sqlFirewallManager.allowSqlRunAfterAnalysis(sqlEvent, connection);
		    }
		}
		</code>
		 * </pre>
		 */

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
    private static String getNameWithPackage(final String theClassName) {

	for (int i = 0; i < PREDEFINED_CLASS_NAMES.length; i++) {
	    if (PREDEFINED_CLASS_NAMES[i].equals(theClassName)) {
		// Add prefix package
		String theClassNameNew = SqlFirewallManager.class.getPackage().getName() + "." + theClassName;
		return theClassNameNew;
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
