/**
 * 
 */
package org.kawanfw.test.parms.oracle;

import java.lang.reflect.Constructor;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.kawanfw.test.parms.ConnectionLoaderJdbcInfo;

import oracle.jdbc.OracleTypes;

/**
 * @author Nicolas de Pomereu
 *
 */
public class TestOracleLocalConnection {

    /**
     * 
     */
    public TestOracleLocalConnection() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	ConnectionLoaderJdbcInfo connectionLoaderJdbcInfo = new ConnectionLoaderJdbcInfo("Oracle");
	String driverClassName = connectionLoaderJdbcInfo.getDriverClassName();
	String url = connectionLoaderJdbcInfo.getUrl();
	
	Class<?> c = Class.forName(driverClassName);
	Constructor<?> constructor = c.getConstructor();
	Driver driver = (Driver) constructor.newInstance();
	
	//String username = "sys as sysdba";
	//String password = "327qm9y4";
	
	String username = "C##_KAWAN";
	String password = "327qm9y4";
	
	Properties properties = new Properties();
	properties.put("user", username);
	properties.put("password", password);
	Connection connection = driver.connect(url, properties);

	if (connection == null) {
	    System.err.println("driverClassName: " + driverClassName);
	    System.err.println("url            : " + url);
	    throw new IllegalArgumentException(
		    "Connection is null after driver.connect(url, properties)!");
	}
	
	selectCustomerExecute(connection);
	testStoredProcedureSelectOracleCustomer_2(connection);
	testStoredProcedureOracleInOut_2(connection);
    }
    
    public static void selectCustomerExecute(Connection connection) throws SQLException {
	String sql = "select * from customer where customer_id >= 1 order by customer_id";
	Statement statement = connection.createStatement();
	statement.execute(sql);

	ResultSet rs = statement.getResultSet();

	while (rs.next()) {
	    System.out.println();
	    System.out.println("customer_id   : " + rs.getInt("customer_id"));
	    System.out.println("customer_title: " + rs.getString("customer_title"));
	    System.out.println("fname         : " + rs.getString("fname"));
	    System.out.println("lname         : " + rs.getString("lname"));
	}

	statement.close();
	rs.close();
    }
    
    
    public static void testStoredProcedureSelectOracleCustomer(Connection connection) throws SQLException {
	
	// Calling the ORACLE_SELECT_CUSTOMER stored procedure.
	// Native Oracle JDBC syntax using an Oracle JDBC Driver:
	CallableStatement callableStatement 
		= connection.prepareCall("{ call ORACLE_SELECT_CUSTOMER(?, ?) }");
	callableStatement.setInt(1, 2);
	callableStatement.registerOutParameter(2, OracleTypes.CURSOR);
	callableStatement.executeQuery();
	
	ResultSet rs= (ResultSet) callableStatement.getObject(2);
	
	while (rs.next()) {
	    System.out.println(rs.getInt(1));
	}

	callableStatement.close();

	System.out.println("Done ORACLE_SELECT_CUSTOMER!");
	System.out.println();

    }
    
    public static void testStoredProcedureSelectOracleCustomer_2(Connection connection) throws SQLException {
	
	// Calling the ORACLE_SELECT_CUSTOMER stored procedure.
	// Native Oracle JDBC syntax using an Oracle JDBC Driver:
	CallableStatement callableStatement 
		= connection.prepareCall("{ call ORACLE_SELECT_CUSTOMER_2(?, ?, ?) }");
	callableStatement.setInt(1, 2);
	callableStatement.setString(2, "Doe3");
	callableStatement.registerOutParameter(3, OracleTypes.CURSOR);
	callableStatement.executeQuery();
	
	ResultSet rs= (ResultSet) callableStatement.getObject(3);
	
	while (rs.next()) {
	    System.out.println(rs.getInt(1) + " "+ rs.getString(2));
	}

	callableStatement.close();

	System.out.println("Done ORACLE_SELECT_CUSTOMER_2!");
	System.out.println();

    }
    
    public static void testStoredProcedureOracleInOut_2(Connection connection) throws SQLException {
	
	// Calling the ORACLE_IN_OUT stored procedure.
	// Native Oracle JDBC syntax using an Oracle JDBC Driver:
	CallableStatement callableStatement 
		= connection.prepareCall("{ call ORACLE_IN_OUT_2(?, ?, ?) }");
	callableStatement.setInt(1, 1);
	callableStatement.setInt(2, 2);
	callableStatement.registerOutParameter(2, java.sql.Types.INTEGER);
	callableStatement.setString(3, "Meaning of life is:");
	callableStatement.registerOutParameter(3, java.sql.Types.VARCHAR);
	@SuppressWarnings("unused")
	int n = callableStatement.executeUpdate();
	
	int out2 = callableStatement.getInt(2);
	System.out.println("out2: " + out2);
	String out3 = callableStatement.getString(3);
	System.out.println("out3: " + out3);
	
	callableStatement.close();
	
	System.out.println("Done ORACLE_IN_OUT_2!");
	System.out.println();
    }
    
    public static void testStoredProcedureOracleInOut(Connection connection) throws SQLException {
	
	// Calling the ORACLE_IN_OUT stored procedure.
	// Native Oracle JDBC syntax using an Oracle JDBC Driver:
	CallableStatement callableStatement 
		= connection.prepareCall("{ call ORACLE_IN_OUT(?, ?) }");
	callableStatement.setInt(1, 1);
	callableStatement.setInt(2, 2);
	callableStatement.registerOutParameter(2, java.sql.Types.INTEGER);
	@SuppressWarnings("unused")
	int n = callableStatement.executeUpdate();
	
	int out2 = callableStatement.getInt(2);
	System.out.println("out2: " + out2);
	
	callableStatement.close();
	
	System.out.println("Done ORACLE_IN_OUT!");
	System.out.println();
    }

}
