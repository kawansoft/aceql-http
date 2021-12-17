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
package org.kawanfw.test.api.server.executor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.kawanfw.sql.api.server.executor.ClientEvent;
import org.kawanfw.sql.api.server.executor.ServerQueryExecutor;

/**
 * A query on sampledb database
 * @author Nicolas de Pomereu
 *
 */
public class MyServerQueryExecutor implements ServerQueryExecutor {

    /**
     * Simple select * from customer where customer_id >= 1 order by customer_id query
     */
    @Override
    public ResultSet executeQuery(ClientEvent clientEvent, Connection connection) throws IOException, SQLException {
		
	List<Object> params = clientEvent.getParameterValues();
	Integer customerIdParam = (Integer)params.get(0);
	
	String sql = "select * from customer where customer_id >= ? order by customer_id";
	PreparedStatement preparedStatement = connection.prepareStatement(sql);
	preparedStatement.setInt(1, customerIdParam.intValue());
	preparedStatement.execute();

	ResultSet rs = preparedStatement.getResultSet();
	return rs;
    }

}
