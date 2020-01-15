/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2020,  KawanSoft SAS
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

package org.kawanfw.sql.tomcat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.util.SqlTag;

public class ThreadPoolExecutorStore {

    public static final int DEFAULT_CORE_POOL_SIZE = 100;
    public static final int DEFAULT_MAXIMUM_POOL_SIZE = 200;
    public static final long DEFAULT_KEEP_ALIVE_TIME = 10;
    public static final int DEFAULT_BLOCKING_QUEUE_CAPACITY = 50000;

    private static ThreadPoolExecutor threadPoolExecutor = null;
    private Properties properties = null;

    /**
     * Constructor
     * 
     * @param properties
     *            the ThreadPoolExecutor configuration is the properties
     */
    public ThreadPoolExecutorStore(Properties properties) {

	if (properties == null) {
	    throw new NullPointerException("properties is null!");
	}

	this.properties = properties;
    }

    /**
     * Creates the ThreadPoolExecutor that will be used using properties
     * 
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @SuppressWarnings("unchecked")
    public void create() {

	String corePoolSize = properties.getProperty("corePoolSize");
	String maximumPoolSize = properties.getProperty("maximumPoolSize");
	String unitStr = properties.getProperty("unit");
	String keepAliveTime = properties.getProperty("keepAliveTime");
	String workQueueClassName = properties.getProperty("workQueueClassName");
	String capacity = properties.getProperty("capacity");

	int corePoolSizeInt = DEFAULT_CORE_POOL_SIZE;
	int maximumPoolSizeInt = DEFAULT_MAXIMUM_POOL_SIZE;
	TimeUnit unit = TimeUnit.SECONDS;
	long keepAliveTimeLong = DEFAULT_KEEP_ALIVE_TIME;
	int capacityInt = DEFAULT_BLOCKING_QUEUE_CAPACITY;

	System.out.println(SqlTag.SQL_PRODUCT_START + " Creating ThreadPoolExecutor:");

	// Set values if they exist. Throw Exception if value is not numeric and are
	// numeric
	if (corePoolSize != null) {
	    throwExceptionValueIfNotNumeric("corePoolSize", corePoolSize);
	    corePoolSizeInt = Integer.parseInt(corePoolSize);
	}

	if (maximumPoolSize != null) {
	    throwExceptionValueIfNotNumeric("maximumPoolSize", maximumPoolSize);
	    maximumPoolSizeInt = Integer.parseInt(maximumPoolSize);
	}

	if (unitStr != null) {
	    try {
		unit = TimeUnit.valueOf(unitStr);
	    } catch (Exception e) {
		throw new DatabaseConfigurationException(
			"unit value is invalid: " + unitStr + ". " + SqlTag.PLEASE_CORRECT);
	    }
	}

	if (keepAliveTime != null) {
	    throwExceptionValueIfNotNumeric("keepAliveTime", keepAliveTime);
	    keepAliveTimeLong = Integer.parseInt(keepAliveTime);
	}

	if (capacity != null) {
	    throwExceptionValueIfNotNumeric("capacity", capacity);
	    capacityInt = Integer.parseInt(capacity);
	}

	if (maximumPoolSizeInt < corePoolSizeInt) {
	    throw new DatabaseConfigurationException("maximumPoolSize (" + maximumPoolSize + ") is < corePoolSize ("
		    + corePoolSize + "). maximumPoolSize Must be >= corePoolSize. " + SqlTag.PLEASE_CORRECT);
	}

	if (capacityInt < 0) {
	    throw new DatabaseConfigurationException("capacity must be >= 0. " + SqlTag.PLEASE_CORRECT);
	}

	String className = null;
	if (workQueueClassName != null) {
	    className = workQueueClassName;
	} else {
	    className = "java.util.concurrent.ArrayBlockingQueue";
	}

	BlockingQueue<Runnable> workQueue = null;

	Class<?> clazz = null;
	// Create Queue
	try {
	    clazz = Class.forName(className);

	    if (capacityInt > 0) {
		Constructor<?> constructor = clazz.getConstructor(int.class);
		workQueue = (BlockingQueue<Runnable>) constructor.newInstance(capacityInt);
	    } else {
		Constructor<?> constructor = clazz.getConstructor();
		workQueue = (BlockingQueue<Runnable>) constructor.newInstance();
	    }

	} catch (Exception e) {
	    String CR_LF = System.getProperty("line.separator");
	    throw new DatabaseConfigurationException("blockingQueueClassName instance for name " + className
		    + " could not be created." + CR_LF + "Reason: " + e.toString() + ". " + SqlTag.PLEASE_CORRECT);
	}

	threadPoolExecutor = new ThreadPoolExecutor(corePoolSizeInt, maximumPoolSizeInt, keepAliveTimeLong, unit,
		workQueue);

	System.out.println(SqlTag.SQL_PRODUCT_START + "  -> [corePoolSize: " + threadPoolExecutor.getCorePoolSize()
		+ ", maximumPoolSize: " + threadPoolExecutor.getMaximumPoolSize() + ", unit: " + unit + ", ");
	System.out.println(SqlTag.SQL_PRODUCT_START + "  ->  keepAliveTime: " + threadPoolExecutor.getKeepAliveTime(unit) + ", workQueue: "
		+ threadPoolExecutor.getQueue().getClass().getSimpleName() + "("
		+ threadPoolExecutor.getQueue().remainingCapacity() + ")]");
    }

    /**
     * Gets the static instance of ThreadPoolExecutor to be used in main servlet
     * 
     * @return the threadPoolExecutor the instance to be used in main servlet
     */
    public static ThreadPoolExecutor getThreadPoolExecutor() {
	return threadPoolExecutor;
    }

    private void throwExceptionValueIfNotNumeric(String name, String value) {
	if (!StringUtils.isNumeric(value)) {
	    throw new DatabaseConfigurationException(name + " property is not numeric. " + SqlTag.PLEASE_CORRECT);
	}
    }

}
