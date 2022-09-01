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
package org.kawanfw.sql.servlet.injection.classes;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.tomcat.properties.threadpool.ThreadPoolExecutorBuilder;
import org.kawanfw.sql.tomcat.properties.threadpool.ThreadPoolProperties;
import org.kawanfw.sql.util.SqlTag;

public class ProEditionThreadPoolExecutorBuilder implements ThreadPoolExecutorBuilder {

    /**
     * Creates the ThreadPoolExecutor that will be used using properties
     * @throws IOException 
     * @throws DatabaseConfigurationException 
     * @throws SQLException 
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public ThreadPoolExecutor build() throws DatabaseConfigurationException, IOException, SQLException {
		
	File propertiesFile = PropertiesFileStore.get();
	Properties properties = PropertiesFileUtil.getProperties(propertiesFile);

	//return ProEditionThreadPoolExecutorBuilderWrap.buildWrap(properties);
	
	ThreadPoolProperties threadPoolProperties = new ThreadPoolProperties(properties);

	int corePoolSize = threadPoolProperties.getCorePoolSize();
	int maximumPoolSize = threadPoolProperties.getMaximumPoolSize();
	TimeUnit unit = threadPoolProperties.getUnit();
	long keepAliveTime = threadPoolProperties.getKeepAliveTime();
	BlockingQueue<Runnable> workQueue = threadPoolProperties.getWorkQueue();
	boolean prestartAllCoreThreads = threadPoolProperties.isPrestartAllCoreThreads();

	ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
		unit, workQueue);
	if (prestartAllCoreThreads) {
	    threadPoolExecutor.prestartAllCoreThreads();
	}

	System.out.println(SqlTag.SQL_PRODUCT_START + " Loading ThreadPoolExecutor:");
	System.out.println(SqlTag.SQL_PRODUCT_START + "  -> [corePoolSize: " + threadPoolExecutor.getCorePoolSize()
		+ ", maximumPoolSize: " + threadPoolExecutor.getMaximumPoolSize() + ", unit: " + unit + ", ");
	System.out
		.println(SqlTag.SQL_PRODUCT_START + "  ->  keepAliveTime: " + threadPoolExecutor.getKeepAliveTime(unit)
			+ ", workQueue: " + threadPoolExecutor.getQueue().getClass().getSimpleName() + "("
			+ threadPoolExecutor.getQueue().remainingCapacity() + "), " + "prestartAllCoreThreads: "
			+ prestartAllCoreThreads + "]");
	
	return threadPoolExecutor;
    }


}
