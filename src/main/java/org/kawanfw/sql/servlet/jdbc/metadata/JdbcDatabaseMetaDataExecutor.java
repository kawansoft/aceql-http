/**
 *
 */
package org.kawanfw.sql.servlet.jdbc.metadata;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.kawanfw.sql.jdbc.metadata.DatabaseMetaDataMethodCallDTO;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.HtmlConverter;
import org.kawanfw.sql.util.Tag;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JdbcDatabaseMetaDataExecutor {

    /** Set to true to display/log debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(JavaValueBuilder.class);

    private DatabaseMetaDataMethodCallDTO databaseMetaDataMethodCallDTO;
    private OutputStream out;
    private Connection connection;

    /**
     * Constructor.
     * @param databaseMetaDataMethodCallDTO
     * @param out
     * @param connection
     */
    public JdbcDatabaseMetaDataExecutor(DatabaseMetaDataMethodCallDTO databaseMetaDataMethodCallDTO, OutputStream out,
	    Connection connection) {
	this.databaseMetaDataMethodCallDTO = databaseMetaDataMethodCallDTO;
	this.out = out;
	this.connection = connection;
    }

    /**
     * Executes the call of the DatabaseMetaData method asked by the PC side.
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void callDatabaseMetaDataMethod() throws SQLException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
	String methodName = databaseMetaDataMethodCallDTO.getMethodName();
	List<String> listParamsTypes = databaseMetaDataMethodCallDTO.getParamTypes();
	List<String> listParamsValues = databaseMetaDataMethodCallDTO.getParamValues();

	DatabaseMetaData databaseMetaData = connection.getMetaData();

	// Trap DatabaseMetaData.getTables() & DatabaseMetaData.getUDTs()
	// that have special array String[] or int[] parameters
	/**
	 * <pre>
	 * <code>
	if (methodName.equals("getTables") || methodName.equals("getUDTs")
		|| methodName.equals("getPrimaryKeys")) {
	    DatabaseMetaDataSpecial databaseMetaDataSpecial = new DatabaseMetaDataSpecial(
		    databaseMetaData, methodName, listParamsValues);
	    ResultSet rs = databaseMetaDataSpecial.execute();
	    dumpResultSetOnServletOutStream(rs);
	    return;
	}
	</code>
	 * </pre>
	 */

	@SuppressWarnings("rawtypes")
	Class[] argTypes = new Class[listParamsTypes.size()];
	Object[] values = new Object[listParamsValues.size()];

	for (int i = 0; i < listParamsTypes.size(); i++) {
	    String value = listParamsValues.get(i);

	    String javaType = listParamsTypes.get(i);
	    JavaValueBuilder javaValueBuilder = new JavaValueBuilder(javaType, value);

	    argTypes[i] = javaValueBuilder.getClassOfValue();
	    values[i] = javaValueBuilder.getValue();

	    // Trap NULL values
	    if (values[i].equals("NULL")) {
		values[i] = null;
	    }

	    debug("argTypes[i]: " + argTypes[i]);
	    debug("values[i]  : " + values[i]);
	}

	Object resultObj = callMethodWithReflection(methodName, databaseMetaData, argTypes, values);

	if (resultObj instanceof ResultSet) {
	    ResultSet rs = (ResultSet) resultObj;
	    // dumpResultSetOnServletOutStream(rs);

	} else {
	    // All other formats are handled in String
	    String result = null;
	    if (resultObj != null)
		result = resultObj.toString();
	    debug("actionInvokeRemoteMethod:result: " + result);
	    result = HtmlConverter.toHtml(result);

	    // out.println(TransferStatus.SEND_OK);
	    // out.println(result);
	}

    }

    /**
     * Calls the
     * @param methodName
     * @param databaseMetaData
     * @param argTypes
     * @param values
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Object callMethodWithReflection(String methodName, DatabaseMetaData databaseMetaData, Class[] argTypes,
	    Object[] values) throws ClassNotFoundException, SQLException, SecurityException, NoSuchMethodException,
	    IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	Class<?> c = Class.forName("java.sql.DatabaseMetaData");
	Object theObject = databaseMetaData;

	// Invoke the method
	Method main = null;
	Object resultObj = null;

	// Get the Drvier Info
	String database = "";
	String productVersion = "";
	String DriverName = "";
	String DriverVersion = "";
	String driverInfo = Tag.PRODUCT;

	database = databaseMetaData.getDatabaseProductName();
	productVersion = databaseMetaData.getDatabaseProductVersion();
	DriverName = databaseMetaData.getDriverName();
	DriverVersion = databaseMetaData.getDriverVersion();
	driverInfo += database + " " + productVersion + " " + DriverName + " " + DriverVersion;

	String methodParams = getMethodParams(values);

	try {
	    main = c.getDeclaredMethod(methodName, argTypes);
	} catch (SecurityException e) {
	    throw new SecurityException(driverInfo + " - Security - Impossible to get declared DatabaseMetaData."
		    + methodName + "(" + methodParams + ")");
	} catch (NoSuchMethodException e) {
	    throw new NoSuchMethodException(driverInfo + " - No Such Method - Impossible get declared DatabaseMetaData."
		    + methodName + "(" + methodParams + ")");
	}

	try {
	    resultObj = main.invoke(theObject, values);
	} catch (IllegalArgumentException e) {
	    throw new IllegalArgumentException(
		    driverInfo + " - Impossible to call DatabaseMetaData." + methodName + "(" + methodParams + ")");
	} catch (IllegalAccessException e) {
	    throw new IllegalAccessException(driverInfo + " - Impossible to access DatabaseMetaData method."
		    + methodName + "(" + methodParams + ")");
	} catch (InvocationTargetException e) {
	    throw new InvocationTargetException(e, driverInfo + " - Impossible to invoke DatabaseMetaData method."
		    + methodName + "(" + methodParams + ")");
	}
	return resultObj;
    }

    /**
     * Returns the method parameters as (value1, value2, ...)
     *
     * @param values the value array
     * @return the method parameters as (value1, value2, ...)
     */
    private String getMethodParams(Object[] values) {

	if (values.length == 0) {
	    return "";
	}

	String returnValue = "(";

	for (int i = 0; i < values.length; i++) {
	    returnValue += values[i];
	    if (i < values.length - 1) {
		returnValue += ", ";
	    }
	}

	returnValue += ")";

	return returnValue;
    }

    /**
     * Debug tool
     *
     * @param s
     */
    private void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }
}
