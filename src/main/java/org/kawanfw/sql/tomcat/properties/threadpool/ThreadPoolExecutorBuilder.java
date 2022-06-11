
package org.kawanfw.sql.tomcat.properties.threadpool;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.concurrent.ThreadPoolExecutor;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;

public interface ThreadPoolExecutorBuilder {

    /**
     * Creates the ThreadPoolExecutor that will be used using properties
     * @throws SQLException 
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    ThreadPoolExecutor build() throws DatabaseConfigurationException, IOException, SQLException ;

}