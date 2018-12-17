/**
 * 
 */
package org.kawanfw.test.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Nicolas de Pomereu
 *
 */
public class Test {

    /**
     * 
     */
    public Test() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	    /*
	    public ThreadPoolExecutor(int corePoolSize,
                    int maximumPoolSize,
                    long keepAliveTime,
                    TimeUnit unit,
                    BlockingQueue<Runnable> workQueue) {
	    */
	
	ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(100, 200, 300,
		TimeUnit.MILLISECONDS,
		new ArrayBlockingQueue<Runnable>(200));
	
	System.out.println(threadPoolExecutor.getCorePoolSize());
	System.out.println(threadPoolExecutor.getMaximumPoolSize());
	System.out.println(threadPoolExecutor.getKeepAliveTime(TimeUnit.MILLISECONDS));
	System.out.println(threadPoolExecutor.getQueue().getClass());
	System.out.println(threadPoolExecutor.getQueue().remainingCapacity());
	
	System.out.println(threadPoolExecutor);

	BlockingQueue<Runnable> blockingQueue= new ArrayBlockingQueue<Runnable>(200);
	System.out.println(blockingQueue.toString());
	
	System.out.println();
	
    }

}
