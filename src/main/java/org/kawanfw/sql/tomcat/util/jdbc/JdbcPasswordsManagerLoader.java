package org.kawanfw.sql.tomcat.util.jdbc;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.server.auth.jdbc.JdbcPasswordsManager;
import org.kawanfw.sql.util.Tag;

public class JdbcPasswordsManagerLoader {


    public static char [] getPasswordUsingJdbcPasswordManagers(String database, Properties properties) throws IOException, SQLException {
	Objects.requireNonNull(database, "database cannot be null!");
	Objects.requireNonNull(database, "database cannot be null!");
	
	String jdbcPasswordsManagerClassName = properties.getProperty("jdbcPasswordsManagerClassName");
	
	if ((jdbcPasswordsManagerClassName == null) || jdbcPasswordsManagerClassName.isEmpty()) {
	    // No JdbcPasswordManager implementation ==> return null 
	    return null;
	}
	
	JdbcPasswordsManager jdbcPasswordsManager = null;
	
	// Load it, and get the password
	try {
	    Class<?> c = Class.forName(jdbcPasswordsManagerClassName);
	    Constructor<?> constructor = c.getConstructor();
	    jdbcPasswordsManager = (JdbcPasswordsManager) constructor.newInstance();
	} catch (Exception e) {
	    String initErrrorMesage = "Impossible to load JdbcPasswordsManager concrete class: " + jdbcPasswordsManagerClassName;
	    e.printStackTrace();
	    throw new DatabaseConfigurationException(initErrrorMesage);
	} 
	
	char [] password =  jdbcPasswordsManager.getPassword(database);
	return password;    
    }

    /*
    initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
	    + " Impossible to load (ClassNotFoundException) Configurator class: " + classNameToLoad;
     */
}
