/**
 * 
 */
package org.kawanfw.test.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import org.kawanfw.sql.servlet.metadata.JsonDatabaseMetaData;
import org.kawanfw.test.parms.ConnectionLoader;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JsonDatabaseMetaDataTest {

    /**
     * 
     */
    public JsonDatabaseMetaDataTest() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	Connection connection = ConnectionLoader.getLocalConnection();
	DatabaseMetaData databaseMetaData = connection.getMetaData();

	JsonDatabaseMetaData jsonDatabaseMetaData = new JsonDatabaseMetaData(
		databaseMetaData);
	String JsonString = jsonDatabaseMetaData.build();
	System.out.println(JsonString);

    }

}
