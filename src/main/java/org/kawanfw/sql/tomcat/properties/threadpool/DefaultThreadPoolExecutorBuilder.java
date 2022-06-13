/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.
 *
 * AceQL HTTP is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * AceQL HTTP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301  USA
 *
 * Any modifications to this file must keep this entire header
 * intact.
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
