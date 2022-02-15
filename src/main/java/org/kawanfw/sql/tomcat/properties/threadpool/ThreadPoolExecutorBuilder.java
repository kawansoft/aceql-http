
package org.kawanfw.sql.tomcat.properties.threadpool;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

public interface ThreadPoolExecutorBuilder {

    /**
     * Creates the ThreadPoolExecutor that will be used using properties
     * @param properties
     *
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    ThreadPoolExecutor build(Properties properties);

}