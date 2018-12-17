/**
 * 
 */
package org.kawanfw.test.api.server.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.kawanfw.sql.tomcat.ThreadPoolExecutorStore;

/**
 * @author Nicolas de Pomereu
 *
 */
public class TestThreadPoolExecutorStore {

    /**
     * @param args
     * @throws ClassNotFoundException 
     */
    public static void main(String[] args) throws Exception {

	File file = new File("I:\\_dev_awake\\aceql-http-main\\aceql-http\\conf\\aceql-server.properties");
	
	Properties properties = new Properties();
	properties.load(new FileInputStream(file));
	
	ThreadPoolExecutorStore threadPoolExecutorStore = new ThreadPoolExecutorStore(properties);
	threadPoolExecutorStore.create();
	
	System.out.println();
	System.out.println(ThreadPoolExecutorStore.getThreadPoolExecutor());
	
	/*
	Class<?> c =  Class.forName("java.util.concurrent.ArrayBlockingQueue");
	
	Constructor<?> constructor = c.getConstructor(int.class);
	BlockingQueue<Runnable> workingQueue = (BlockingQueue<Runnable>) constructor.newInstance(2000);
	
	System.out.println(workingQueue.getClass().getSimpleName());
	System.out.println(workingQueue.remainingCapacity());
	*/
	
    }

}
