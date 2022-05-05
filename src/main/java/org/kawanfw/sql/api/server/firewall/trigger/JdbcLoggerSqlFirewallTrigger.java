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

package org.kawanfw.sql.api.server.firewall.trigger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;

/**
 * A trigger that will INSERT THE {@code SqlEvent} info and the
 * {@code sqlFirewallManager} class name into a {@code denied_requests} SQL table.
 * <br>
 * Table structure is:
 * 
 * <pre>
 * <code>
create table denied_requests
(               
  date_event		timestamp	not null,  
  username		varchar(254)	not null,  	  
  ip_address		varchar(254) 	not null, 
  sql_database		varchar(254)    not null,	
  sql_firewall_manager	varchar(254)    not null,
  sql_statement		varchar(4000)	not null,
  is_metadata		integer			, 
  is_prepared_statement	integer			,
  parameter_values	varchar(4000)	not null
);
 * </code>
 * </pre>
 * 
 * @author Nicolas de Pomereu
 * @since 11.0
 */

public class JdbcLoggerSqlFirewallTrigger implements SqlFirewallTrigger {

    /**
     * LInsert into the denied_requests SQL table {@code ClientEvent} and the
     * {@code SqlFirewallManager} class name
     */
    @Override
    public void runIfStatementRefused(SqlEvent sqlEvent, SqlFirewallManager sqlFirewallManager, Connection connection)
	    throws IOException, SQLException {
	String sql = "insert into denied_requests values (?, ?, ?, ?, ?, ?, ?, ?)";
	try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
	    int i = 1;
	    preparedStatement.setTimestamp(i++, new Timestamp(System.currentTimeMillis()));
	    preparedStatement.setString(i++, sqlEvent.getUsername());
	    preparedStatement.setString(i++, sqlEvent.getIpAddress());
	    preparedStatement.setString(i++, sqlEvent.getDatabase());
	    preparedStatement.setString(i++, sqlFirewallManager.getClass().getName());
	    preparedStatement.setString(i++, sqlEvent.getSql());
	    preparedStatement.setInt(i++, sqlEvent.isMetadataQuery() ? 1 : 0);
	    preparedStatement.setInt(i++, sqlEvent.isPreparedStatement() ? 1 : 0);
	    preparedStatement.setString(i++, sqlEvent.getParameterStringValues().toString());
	    preparedStatement.executeUpdate();
	}
    }

}
