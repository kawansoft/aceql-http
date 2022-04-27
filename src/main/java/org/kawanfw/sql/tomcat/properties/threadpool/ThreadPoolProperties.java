package org.kawanfw.sql.tomcat.properties.threadpool;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.util.SqlTag;

public class ThreadPoolProperties {

    public static final int DEFAULT_CORE_POOL_SIZE = 10;
    public static final int DEFAULT_MAXIMUM_POOL_SIZE = 125;
    public static final int DEFAULT_KEEP_ALIVE_TIME = 10;
    public static final int DEFAULT_BLOCKING_QUEUE_CAPACITY = 50000;
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;
    
    private String corePoolSizeStr;
    private String maximumPoolSizeStr;
    private String unitStr;
    private  String keepAliveTimeStr;
    private String workQueueClassName;
    private String capacityStr;

    private int corePoolSize= DEFAULT_CORE_POOL_SIZE;
    private int maximumPoolSize= DEFAULT_MAXIMUM_POOL_SIZE;
    private TimeUnit unit = DEFAULT_UNIT;
    private int keepAliveTime= DEFAULT_KEEP_ALIVE_TIME;
    private int capacity= DEFAULT_BLOCKING_QUEUE_CAPACITY;
    private BlockingQueue<Runnable> workQueue;

    public ThreadPoolProperties(Properties properties) {
	Objects.requireNonNull(properties, "properties cannot be null!");

	corePoolSizeStr = properties.getProperty("corePoolSize");
	maximumPoolSizeStr = properties.getProperty("maximumPoolSize");
	unitStr = properties.getProperty("unit");
	keepAliveTimeStr = properties.getProperty("keepAliveTime");
	workQueueClassName = properties.getProperty("workQueueClassName");
	capacityStr = properties.getProperty("capacity");

	checkAndFillParameters();
	createWorkingQueue();
    }


    @SuppressWarnings("unchecked")
    private void createWorkingQueue() {
	String className = null;
	if (workQueueClassName != null) {
	    className = workQueueClassName;
	} else {
	    className = "java.util.concurrent.ArrayBlockingQueue";
	}

	Class<?> clazz = null;
	// Create Queue
	try {
	    clazz = Class.forName(className);

	    if (capacity > 0) {
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

    public void setCapacityStr(String capacityStr) {
        this.capacityStr = capacityStr;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setWorkQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
    }


}
