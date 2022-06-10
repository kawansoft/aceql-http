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

package org.kawanfw.sql.api.server;

import java.sql.SQLException;
import java.util.List;

/**
 * A internal wrapper for Java package protected calls. <br>
 * This is an internal undocumented class that should not be used nor called by
 * the users of the AceQL APis.
 *
 * @author Nicolas de Pomereu
 * @since 9.0
 */

public class SqlEventWrapper {

       
    protected SqlEventWrapper() {

    }

    public static SqlEvent sqlEventBuild(String username, String database, String ipAddress, String sql,
	    boolean isPreparedStatement, List<Object> parameterValues, boolean isMetadataQuery) throws SQLException {
	return new SqlEvent(username, database, ipAddress, sql, isPreparedStatement, parameterValues, isMetadataQuery);
    }
}
