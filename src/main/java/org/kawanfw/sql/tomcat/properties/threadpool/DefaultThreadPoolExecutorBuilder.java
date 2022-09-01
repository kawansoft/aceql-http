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
package org.kawanfw.sql.tomcat.properties.threadpool;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.kawanfw.sql.util.SqlTag;

public class DefaultThreadPoolExecutorBuilder implements ThreadPoolExecutorBuilder {

    /**
     * Creates the ThreadPoolExecutor that will be used using properties
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Override
    public ThreadPoolExecutor build() {
	
	int corePoolSize = ThreadPoolProperties.DEFAULT_CORE_POOL_SIZE;
	int maximumPoolSize = ThreadPoolProperties.DEFAULT_MAXIMUM_POOL_SIZE;
	TimeUnit unit = ThreadPoolProperties.DEFAULT_UNIT;
	long keepAliveTime = ThreadPoolProperties.DEFAULT_KEEP_ALIVE_TIME;
	BlockingQueue<Runnable> workQueue = new SynchronousQueue<>();
	boolean prestartAllCoreThreads = ThreadPoolProperties.PRESTART_ALL_CORE_THREADS;
	
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
			+ threadPoolExecutor.getQueue().remainingCapacity() + "), " +  "prestartAllCoreThreads: "+ prestartAllCoreThreads + "]");
	
	return threadPoolExecutor;
    }


}
