package org.kawanfw.sql.tomcat.properties.threadpool;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.util.SqlTag;

public class ThreadPoolProperties {

    public static final int DEFAULT_CORE_POOL_SIZE = 10;
    public static final int DEFAULT_MAXIMUM_POOL_SIZE = 125;
    public static final int DEFAULT_KEEP_ALIVE_TIME = 60;
    public static final String DEFAULT_BLOCKING_QUEUE_NAME = "java.util.concurrent.SynchronousQueue";
    public static final int DEFAULT_BLOCKING_QUEUE_CAPACITY = 0;
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;
    public static final boolean PRESTART_ALL_CORE_THREADS = true;
    
    private String corePoolSizeStr;
    private String maximumPoolSizeStr;
    private String unitStr;
    private  String keepAliveTimeStr;
    private String workQueueClassName;
    private String capacityStr;
    private String prestartAllCoreThreadsStr;
    
    private int corePoolSize= DEFAULT_CORE_POOL_SIZE;
    private int maximumPoolSize= DEFAULT_MAXIMUM_POOL_SIZE;
    private TimeUnit unit = DEFAULT_UNIT;
    private int keepAliveTime= DEFAULT_KEEP_ALIVE_TIME;
    private int capacity= DEFAULT_BLOCKING_QUEUE_CAPACITY;
    
    private BlockingQueue<Runnable> workQueue;
    private boolean prestartAllCoreThreads;


    public ThreadPoolProperties(Properties properties) {
	Objects.requireNonNull(properties, "properties cannot be null!");

	corePoolSizeStr = properties.getProperty("corePoolSize");
	maximumPoolSizeStr = properties.getProperty("maximumPoolSize");
	unitStr = properties.getProperty("unit");
	keepAliveTimeStr = properties.getProperty("keepAliveTime");
	workQueueClassName = properties.getProperty("workQueueClassName");
	capacityStr = properties.getProperty("capacity");
	prestartAllCoreThreadsStr = properties.getProperty("prestartAllCoreThreads");
	
	checkAndFillParameters();
	createWorkingQueue();
    }


    @SuppressWarnings("unchecked")
    private void createWorkingQueue() {
	String className = null;
	if (workQueueClassName != null) {
	    className = workQueueClassName;
	} else {
	    className = DEFAULT_BLOCKING_QUEUE_NAME;
	}

	Class<?> clazz = null;
	// Create Queue
	try {
	    clazz = Class.forName(className);

	    if (capacity > 0 && isConstructorWithCapacity(className)) {
		Constructor<?> constructor = clazz.getConstructor(int.class);
		workQueue = (BlockingQueue<Runnable>) constructor.newInstance(capacity);
	    } else {
		Constructor<?> constructor = clazz.getConstructor();
		workQueue = (BlockingQueue<Runnable>) constructor.newInstance();
	    }

	} catch (Exception e) {
	    String CR_LF = System.getProperty("line.separator");
	    throw new DatabaseConfigurationException("blockingQueueClassName instance for name " + className
		    + " could not be created." + CR_LF + "Reason: " + e.toString() + ". " + SqlTag.PLEASE_CORRECT);
	}
    }


    /**
     * To Enhance later
     * @param className
     * @return
     */
    public boolean isConstructorWithCapacity(String className) {
	return className.contains(ArrayBlockingQueue.class.getSimpleName()) 
		|| className.contains(PriorityBlockingQueue.class.getSimpleName())
			|| className.contains(LinkedBlockingDeque.class.getSimpleName());

    }

    private void checkAndFillParameters() {
	if (corePoolSizeStr != null) {
	    throwExceptionValueIfNotNumeric("corePoolSize", corePoolSizeStr);
	    corePoolSize = Integer.parseInt(corePoolSizeStr);
	}

	if (maximumPoolSizeStr != null) {
	    throwExceptionValueIfNotNumeric("maximumPoolSize", maximumPoolSizeStr);
	    maximumPoolSize = Integer.parseInt(maximumPoolSizeStr);
	}

	if (unitStr != null) {
	    try {
		unit = TimeUnit.valueOf(unitStr);
	    } catch (Exception e) {
		throw new DatabaseConfigurationException(
			"unit value is invalid: " + unitStr + ". " + SqlTag.PLEASE_CORRECT);
	    }
	}

	if (keepAliveTimeStr != null) {
	    throwExceptionValueIfNotNumeric("keepAliveTime", keepAliveTimeStr);
	    keepAliveTime = Integer.parseInt(keepAliveTimeStr);
	}

	if (capacityStr != null) {
	    throwExceptionValueIfNotNumeric("capacity", capacityStr);
	    capacity= Integer.parseInt(capacityStr);
	}

	if (maximumPoolSize < corePoolSize) {
	    throw new DatabaseConfigurationException("maximumPoolSize (" + maximumPoolSize + ") is < corePoolSize ("
		    + corePoolSize + "). maximumPoolSize Must be >= corePoolSize. " + SqlTag.PLEASE_CORRECT);
	}

	if (capacity < 0) {
	    throw new DatabaseConfigurationException("capacity must be >= 0. " + SqlTag.PLEASE_CORRECT);
	}
	
	if (prestartAllCoreThreadsStr== null || prestartAllCoreThreadsStr.isEmpty()) {
	    prestartAllCoreThreads = true;
	}
	else {
	    prestartAllCoreThreads = Boolean.parseBoolean(prestartAllCoreThreadsStr);  
	}
    }

    private void throwExceptionValueIfNotNumeric(String name, String value) {
	if (!StringUtils.isNumeric(value)) {
	    throw new DatabaseConfigurationException(name + " property is not numeric. " + SqlTag.PLEASE_CORRECT);
	}
    }

    public String getCapacityStr() {
        return capacityStr;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public BlockingQueue<Runnable> getWorkQueue() {
        return workQueue;
    }

    /**
     * @return the prestartAllCoreThreads
     */
    public boolean isPrestartAllCoreThreads() {
        return prestartAllCoreThreads;
    }

    



}
