package org.kawanfw.sql.tomcat;

import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.Set;

import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.util.SqlTag;

public class TomcatStarterUtilInstancesTester {

    public TomcatStarterUtilInstancesTester() {
	// TODO Auto-generated constructor stub
    }

    public static void testConfigurators(Properties properties) {

        if (properties == null) {
            throw new IllegalArgumentException("properties is null");
        }

        System.out.println(
        	SqlTag.SQL_PRODUCT_START + " Testing Declared Configurators:");

        Set<String> databases = TomcatStarterUtil.getDatabaseNames(properties);
        for (String database : databases) {
            // Database configurator
            String databaseConfiguratorClassName = properties.getProperty(
        	    database + "." + ServerSqlManager.DATABASE_CONFIGURATOR_CLASS_NAME);

            if (databaseConfiguratorClassName != null) {
        	loadInstance(databaseConfiguratorClassName);

        	System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + database
        		+ " Database Configurator " + TomcatStarterUtil.CR_LF
        		+ SqlTag.SQL_PRODUCT_START + "     "
        		+ databaseConfiguratorClassName + " OK.");
            }
        }

        String className = properties.getProperty(
        	ServerSqlManager.BLOB_DOWNLOAD_CONFIGURATOR_CLASS_NAME);

        if (className != null) {
            loadInstance(className);

            System.out.println(SqlTag.SQL_PRODUCT_START + "  -> Configurator "
        	    + className + " OK.");
        }

        className = properties.getProperty(
        	ServerSqlManager.BLOB_UPLOAD_CONFIGURATOR_CLASS_NAME);

        if (className != null) {
            loadInstance(className);

            System.out.println(SqlTag.SQL_PRODUCT_START + "  -> Configurator "
        	    + className + " OK.");
        }

        className = properties
        	.getProperty(ServerSqlManager.SESSION_CONFIGURATOR_CLASS_NAME);

        if (className != null) {
            loadInstance(className);

            System.out.println(SqlTag.SQL_PRODUCT_START + "  -> Configurator "
        	    + className + " OK.");
        }

    }

    static void loadInstance(String configuratorClassName) {
        Class<?> c = null;

        try {
            c = Class.forName(configuratorClassName);

            // @SuppressWarnings("unused")
            // Object theObject = c.newInstance();
            Constructor<?> constructor = c.getConstructor();
            @SuppressWarnings("unused")
            Object theObject = constructor.newInstance();

        } catch (Exception e) {
            throw new IllegalArgumentException(
        	    "Exception when loading Configurator "
        		    + configuratorClassName + ": " + e.toString() + ". "
        		    + SqlTag.PLEASE_CORRECT,
        	    e);
        }

    }

}
