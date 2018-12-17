/**
 * 
 */
package org.kawanfw.sql.api.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * THe default {@link Executor} used by the main async servlet. 
 * <br>
 * <br>
 * The class creates a  {code ThreadPoolExecutor} using the {@link ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue) constructor.
 * <br>Contructor parameters values are defined in the member values of this class. 
 * 
 * @author Nicolas de Pomereu
 *
 */
public class DefaultExecutor implements Executor {

    /**
     * number of threads to keep in the pool, even if they are
     * idle, unless {@code allowCoreThreadTimeOut} is set
     */
    public final int corePoolSize = 100;
    
    /**
     *maximum number of threads to allow in the pool
     */
    public final int maximumPoolSize = 200;

    /**
     *  time unit for the {@code keepAliveTime} argument
     */
    public final TimeUnit unit = TimeUnit.MILLISECONDS;
    
    /**
     * when the number of threads is greater than the core, this is
     * the maximum time that excess idle threads will wait for new tasks before
     * terminating.
     */
    public final  long keepAliveTime = 50000L;

    /**
     *  capacity of the workQueue
     */
    public final  int capacity = 200;
    
    /**
     * queue to use for holding tasks before they are executed. This
     * queue will hold only the {@code Runnable} tasks submitted by the
     * {@code execute} method.
     */
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(capacity);

    /**
     * the {@link ThreadPoolExecutor} instance to run inside main servlet
     */
    private ThreadPoolExecutor threadPoolExecutor = null;
    
    /**
     * Constructor.
     * <br>
     * Creates a  {@code ThreadPoolExecuto} instance. 
     */
    public DefaultExecutor() {
	this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public void execute(Runnable command) {
	threadPoolExecutor.execute(command);
    }

}
