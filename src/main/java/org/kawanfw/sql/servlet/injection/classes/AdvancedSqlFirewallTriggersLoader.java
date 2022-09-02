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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.firewall.trigger.SqlFirewallTrigger;
import org.kawanfw.sql.servlet.injection.classes.InjectedClasses.InjectedClassesBuilder;
import org.kawanfw.sql.servlet.injection.classes.creator.SqlFirewallTriggersCreator;
import org.kawanfw.sql.util.SqlTag;

public class AdvancedSqlFirewallTriggersLoader implements SqlFirewallTriggersLoader {

   
    private String classNameToLoad;

    /**
     * Loads a List of SqlFirewallTrigger
     * 
     * @param database
     * @param injectedClassesBuilder
     * @param sqlFirewallTriggerClassNames
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     * @throws IOException
     */
    @Override
    public Set<SqlFirewallTrigger> loadSqlFirewallTriggers(String database, InjectedClassesBuilder injectedClassesBuilder,
	    Set<String> sqlFirewallTriggerClassNames)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, IOException {
		
	String tagSqlFirewallTrigger = null;
	if (sqlFirewallTriggerClassNames.size() < 2)
	    tagSqlFirewallTrigger = " SqlFirewallTrigger class: ";
	else
	    tagSqlFirewallTrigger = " SqlFirewallTrigger classes: ";

	System.out.println(SqlTag.SQL_PRODUCT_START + " " + database + " Database - Loading" + tagSqlFirewallTrigger);

	Map<String, DatabaseConfigurator> databaseConfigurators = injectedClassesBuilder.getDatabaseConfigurators();

	DatabaseConfigurator databaseConfigurator = databaseConfigurators.get(database);
	
	SqlFirewallTriggersCreator sqlFirewallTriggersCreator = new SqlFirewallTriggersCreator(sqlFirewallTriggerClassNames, database,
		databaseConfigurator);
	Set<SqlFirewallTrigger> sqlFirewallTriggers = sqlFirewallTriggersCreator.getSqlFirewallTriggers();

	sqlFirewallTriggerClassNames = sqlFirewallTriggersCreator.getSqlFirewallTriggerClassNames();
	classNameToLoad = sqlFirewallTriggerClassNames.toString();

	return AdvancedSqlFirewallTriggersLoaderWrap.loadSqlFirewallTriggersWrap(database, injectedClassesBuilder, sqlFirewallTriggerClassNames, sqlFirewallTriggers);
    }

    @Override
    public String getClassNameToLoad() {
	return classNameToLoad;
    }

}
