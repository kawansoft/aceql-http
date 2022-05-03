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
package org.kawanfw.sql.api.server.firewall;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.StatementAnalyzer;

/**
 * Firewall manager that denies any update of the database for the passed user.
 * The database is thus guaranteed to be accessed in read only from client side.
 * <br>
 * {@code DenyDatabaseWriteManager} should be used only in order to monitor
 * users who try to force writes on database. <br>
 * If you don't need to monitor users and detect hackers, it's better to set the
 * property {@code database.defaultReadOnly=true} in the
 * {@code aceql-server.properties} file: it will launch a
 * {@link Connection#setReadOnly(boolean)} JDBC call at server startup that will
 *  write-protect efficiently the SQL database.
 *
 * @author Nicolas de Pomereu
 * @since 11.0
 */
public class DenyDatabaseWriteManager extends DefaultSqlFirewallManager implements SqlFirewallManager {

    /**
     * @return <code>false</code> if the passed SQL statement tries to update the
     *         database, else <code>true<code>
     */
    @Override
    public boolean allowSqlRunAfterAnalysis(SqlEvent sqlEvent, Connection connection) throws IOException, SQLException {
	StatementAnalyzer analyzer = new StatementAnalyzer(sqlEvent.getSql(), sqlEvent.getParameterValues());
	return !(analyzer.isDelete() || analyzer.isInsert() || analyzer.isUpdate() || analyzer.isDcl()
		|| analyzer.isDdl() || analyzer.isTcl());
    }
}
