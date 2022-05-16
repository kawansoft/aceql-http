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
package org.kawanfw.test.util;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.kawanfw.sql.api.util.firewall.CloudmersiveApi;

/**
 * Tests the CloudmersiveApi wrapper class.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class CloudmersiveApiTest {

    /**
     * @param args
     * @throws IOException
     * @throws InterruptedException
     * @throws SQLException
     */
    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
	CloudmersiveApi cloudmersiveApi = new CloudmersiveApi();
	File file = new File("I:\\_dev_awake\\aceql-http-main\\aceql-http\\conf_test\\cloudmersive.properties");
	cloudmersiveApi.connect(file);

	int cpt = 0;
	while (true) {
	    cpt++;
	    String sql = "select * from password where password = 'my_password' and 1 = 1";
	    System.out.println();
	    System.out.println("sql: " + sql);
	    System.out.println(cpt + " " + new Date() + " cloudmersiveApi.sqlInjectionDetect(sql): "
		    + cloudmersiveApi.sqlInjectionDetect(sql));
	    Thread.sleep(60000);
	}

    }

}
