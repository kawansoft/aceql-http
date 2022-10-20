/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.injection.classes.validator;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.util.Tag;

/**
 * Valid
 * 
 * @author Nicolas de Pomereu
 *
 */
public class ThreadPoolCapacityWarner {

    public static String CR_LF = System.getProperty("line.separator");
    private static final int MAX_QUEUE_CAPACITY_RECOMMANDED = 100;
    private static final String WARNING_SECOND_LINE_SPACES = "         ";
    
    private String propertiesFile;

    public ThreadPoolCapacityWarner(String propertiesFile) {
	this.propertiesFile = propertiesFile;
    }

    public void warnOnThreadPoolExecutorParams() throws IOException {
	
	File file = new File(propertiesFile);
	Properties properties = PropertiesFileUtil.getProperties(file);

	treatCapacityWarningMessage(properties);

    }

    /**
     * @param properties
     * @throws NumberFormatException
     */
    public void treatCapacityWarningMessage(Properties properties) throws NumberFormatException {
	String workQueueClassName = properties.getProperty("workQueueClassName");

	// No problem for an always empty SynchronousQueue
	if (workQueueClassName != null
		&& !workQueueClassName.equals(java.util.concurrent.SynchronousQueue.class.getName())) {
	    // if capacity > 100, display warning message
	    String capcacityStr = properties.getProperty("capacity");
	    if (capcacityStr == null || capcacityStr.isEmpty()) {
		return;
	    }

	    // We have checked before format is OK
	    int capacity = Integer.parseInt(capcacityStr);

	    if (capacity > MAX_QUEUE_CAPACITY_RECOMMANDED) {
		System.err.println(SqlTag.SQL_PRODUCT_START + " " + Tag.WARNING
			+ " The ThreadPoolExecutor Queue \"" + "capacity" + "\" property"
			+ " should not be > " + MAX_QUEUE_CAPACITY_RECOMMANDED + CR_LF
			+ SqlTag.SQL_PRODUCT_START + WARNING_SECOND_LINE_SPACES + " "
			+ " because of a potential SQL run bottleneck. (Set value in .properties file: " + capacity
			+ ")");
	    }
	}
    }
}
