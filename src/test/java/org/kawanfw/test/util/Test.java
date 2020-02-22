/**
 *
 */
package org.kawanfw.test.util;

import java.io.File;

import org.apache.commons.io.FileUtils;

import waffle.windows.auth.impl.WindowsAuthProviderImpl;

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

	WindowsAuthProviderImpl windowsAuthProviderImpl = new WindowsAuthProviderImpl();
	windowsAuthProviderImpl.logonDomainUser("Nicolas de Pomereu", ".", FileUtils.readFileToString(new File("I:\\__NDP\\_MyPasswords\\login.txt"), "UTF-8"));
	System.out.println("logged!");

	/*
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
	*/
    }

}
