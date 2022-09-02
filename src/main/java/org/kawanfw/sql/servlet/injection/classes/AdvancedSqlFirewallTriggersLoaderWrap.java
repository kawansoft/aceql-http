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
import java.util.Set;

import org.kawanfw.sql.api.server.firewall.trigger.SqlFirewallTrigger;
import org.kawanfw.sql.servlet.injection.classes.InjectedClasses.InjectedClassesBuilder;
import org.kawanfw.sql.util.SqlTag;

public class AdvancedSqlFirewallTriggersLoaderWrap{

    /**
     * @param database
     * @param injectedClassesBuilder
     * @param sqlFirewallTriggerClassNames
     * @param sqlFirewallTriggers
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     */
    public static Set<SqlFirewallTrigger> loadSqlFirewallTriggersWrap(String database, InjectedClassesBuilder injectedClassesBuilder,
	    Set<String> sqlFirewallTriggerClassNames, Set<SqlFirewallTrigger> sqlFirewallTriggers)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
        
        for (String sqlFirewallTriggerClassName : sqlFirewallTriggerClassNames) {
            System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + sqlFirewallTriggerClassName);
        }
        
        return sqlFirewallTriggers;
    }

}
