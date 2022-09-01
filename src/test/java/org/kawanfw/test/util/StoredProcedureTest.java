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
package org.kawanfw.test.util;

import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.api.util.SqlUtil;
import org.kawanfw.test.parms.ConnectionLoader;
import org.kawanfw.test.parms.SqlTestParms;

/**
 * @author Nicolas de Pomereu
 *
 */
public class StoredProcedureTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	parseJson();
	
	// Change it to change test SQL engine
	ConnectionLoader.sqlEngine = SqlTestParms.SQLSERVER_MS_DRIVER;

	Connection connection = ConnectionLoader.getLocalConnection();
	DatabaseMetaData databaseMetaData = connection.getMetaData();
	System.out.println("databaseMetaData.getURL(): " + databaseMetaData.getURL());

	String databaseName = getSqlServerDatabaseName(databaseMetaData);

	System.out.println("databaseName: " + databaseName + ":");

	boolean doReturn = true;
	if (doReturn) {
	    return;
	}

	if (ConnectionLoader.sqlEngine.equals(SqlUtil.MYSQL)) {
	    testMySqlStoredProcedure(connection);
	}
	else if (ConnectionLoader.sqlEngine.equals(SqlUtil.POSTGRESQL)) {
	    testPostrgreSqlStoredProcedures(connection);
	}
	else if (ConnectionLoader.sqlEngine.equals(SqlUtil.SQL_SERVER)) {
	    testSqlServerSoredProcedure(connection);
	}

	connection.close();

    }

    /**
     * @param databaseMetaData
     * @return
     * @throws SQLException
     */
    private static String getSqlServerDatabaseName(DatabaseMetaData databaseMetaData) throws SQLException {
	String databaseName = null;

	String [] urlElements = databaseMetaData.getURL().split(";");


	for (String element : urlElements) {
	    if (element.contains("databaseName=")) {
		databaseName = StringUtils.substringAfter(element, "databaseName=");
		break;
	    }
	}
	return databaseName;
    }

    public static void testSqlServerSoredProcedure(Connection connection) throws SQLException {
	CallableStatement callableStatement = connection.prepareCall("{call ProcedureName(?, ?, ?) }");
	callableStatement.registerOutParameter(3, Types.INTEGER);
	callableStatement.setInt(1, 0);
	callableStatement.setInt(2, 2);
	ResultSet rs = callableStatement.executeQuery();

	while (rs.next()) {
	    System.out.println(rs.getString(1));
	}

	int out3 = callableStatement.getInt(3);

	callableStatement.close();

	System.out.println();
	System.out.println("out3: " + out3);

    }

    public static void testMySqlStoredProcedure(Connection connection) throws SQLException {
	CallableStatement callableStatement = connection.prepareCall("{ call demoSp(?, ?, ?) }");
	callableStatement.registerOutParameter(2, Types.INTEGER);
	callableStatement.registerOutParameter(3, Types.INTEGER);
	callableStatement.setString(1, "test");
	callableStatement.setInt(2, 12);
	ResultSet rs = callableStatement.executeQuery();

	while (rs.next()) {
	    System.out.println(rs.getString(1));
	}

	int out2 = callableStatement.getInt(2);
	int out3 = callableStatement.getInt(3);

	callableStatement.close();

	System.out.println();
	System.out.println("out2: " + out2);
	System.out.println("out3: " + out3);

    }

    public static void testPostrgreSqlStoredProcedures(Connection conn) throws SQLException {
	CallableStatement upperProc = conn.prepareCall("{ ? = call upper( ? ) }");
	upperProc.registerOutParameter(1, Types.VARCHAR);
	upperProc.setString(2, "lowercase to uppercase");
	upperProc.executeUpdate();
	String upperCased = upperProc.getString(1);
	upperProc.close();

	System.out.println("upperCased: " + upperCased);
    }


    public static void parseJson() throws Exception {

	/*

 	"parameters_out_per_name":[
            {
                "out_param_two":"13"
            },
            {
                "out_param_three":"12"
            }
    	],
	 */

	//String jsonContent =
	//"[{\"out_param_two\":\"13\"}, {\"out_param_three\":\"12\"}]";

	String jsonContent =
	"[\"3\",\"\"]";
	
        JsonReader reader = Json.createReader(new StringReader(jsonContent));
        JsonArray jsonArray = reader.readArray();

	for (JsonValue jsonValue : jsonArray) {
	    System.out.println(jsonValue.toString());
	    //JsonObject jsonObject = (JsonObject)jsonValue;
	    //System.out.println(jsonObject.keySet());
	}

    }


}
