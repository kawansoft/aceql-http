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
package org.kawanfw.sql.servlet.util.max_rows;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.api.server.DatabaseConfigurator;

/**
 * @author Nicolas de Pomereu
 *
 */
public class DefaultMaxRowsSetter implements MaxRowsSetter {

    @Override
    public void setMaxRows(HttpServletRequest request, String username, String database, Statement statement,
	    DatabaseConfigurator databaseConfigurator) throws NumberFormatException, SQLException, IOException {
	// do nothing
    }

}
