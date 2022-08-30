/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.sql.callable.aceqlproc;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Nicolas de Pomereu
 *
 */
public interface ServerQueryExecutorWrapper {

    public void executeQuery(HttpServletRequest request, OutputStream out, String action,
	    Connection connection) throws SQLException, IOException, ClassNotFoundException;
}
