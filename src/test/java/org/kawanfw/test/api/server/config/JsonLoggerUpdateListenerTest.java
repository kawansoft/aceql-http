/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.api.server.config;

import java.util.ArrayList;
import java.util.List;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.SqlEventWrapper;
import org.kawanfw.sql.servlet.util.UpdateListenerUtil;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JsonLoggerUpdateListenerTest {

    public static void main(String[] args) throws Exception {
	List<Object> list = new ArrayList<>();
	list.add("value1");
	list.add("value2");
	list.add("value3");
	SqlEvent evt = SqlEventWrapper.sqlEventBuild("user1", "db1", "10.0.0.0", "select * from table", false, list, false);
	
	String jsonString = UpdateListenerUtil.toJsonString(evt);
	System.out.println(jsonString);
    }
}
