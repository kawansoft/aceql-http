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
package org.kawanfw.sql.servlet.injection.classes;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.kawanfw.sql.api.server.firewall.trigger.SqlFirewallTrigger;
import org.kawanfw.sql.servlet.injection.classes.InjectedClasses.InjectedClassesBuilder;

public class DefaultSqlFirewallTriggersLoader implements SqlFirewallTriggersLoader {

    
    @Override
    public Set<SqlFirewallTrigger> loadSqlFirewallTriggers(String database, InjectedClassesBuilder injectedClassesBuilder,
	    Set<String> sqlFirewallTriggerClassNames)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException {

	Set<SqlFirewallTrigger> sqlFirewallTriggers =  new LinkedHashSet<>();
	return sqlFirewallTriggers;
    }

    @Override
    public String getClassNameToLoad() {
	Set<String> classNameToLoad =  new LinkedHashSet<>();
	return classNameToLoad.toString();
    }

}
