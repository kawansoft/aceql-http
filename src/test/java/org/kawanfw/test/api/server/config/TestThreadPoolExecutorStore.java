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
package org.kawanfw.test.api.server.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

import org.kawanfw.sql.tomcat.properties.threadpool.ThreadPoolExecutorBuilder;
import org.kawanfw.sql.tomcat.properties.threadpool.ThreadPoolExecutorBuilderCreator;

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
		
	ThreadPoolExecutorBuilder threadPoolExecutorBuilder = ThreadPoolExecutorBuilderCreator.createInstance();
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
