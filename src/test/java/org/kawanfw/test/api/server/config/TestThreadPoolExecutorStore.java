/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.api.server.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

import org.kawanfw.sql.servlet.injection.classes.ProEditionThreadPoolExecutorBuilder;

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
		
	//ThreadPoolExecutorBuilder threadPoolExecutorBuilder = ThreadPoolExecutorBuilderCreator.createInstance();
	//ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorBuilder.build();
	
	ProEditionThreadPoolExecutorBuilder threadPoolExecutorBuilder = new ProEditionThreadPoolExecutorBuilder();
	ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorBuilder.build();
	    
	System.out.println();
	System.out.println("threadPoolExecutor: " + threadPoolExecutor);
	
	/*
	Class<?> c =  Class.forName("java.util.concurrent.ArrayBlockingQueue");
	
	Constructor<?> constructor = c.getConstructor(int.class);
	BlockingQueue<Runnable> workingQueue = (BlockingQueue<Runnable>) constructor.newInstance(2000);
	
	System.out.println(workingQueue.getClass().getSimpleName());
	System.out.println(workingQueue.remainingCapacity());
	*/
	
    }

}
