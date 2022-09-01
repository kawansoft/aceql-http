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
import org.kawanfw.sql.version.EditionUtil;

/**
 * Valid
 * 
 * @author Nicolas de Pomereu
 *
 */
public class EnterpriseWarner {

    private static final int MAX_QUEUE_CAPACITY_RECOMMANDED = 100;
    
    private String propertiesFile;

    public EnterpriseWarner(String propertiesFile) {
	this.propertiesFile = propertiesFile;
    }

    public void warnOnThreadPoolExecutorParams() throws IOException {
	// Nothing todo if Community Edition
	if (EditionUtil.isCommunityEdition()) {
	    return;
	}

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
			+ " In Enterprise Edition, the ThreadPoolExecutor Queue \"" + "capacity" + "\" property"
			+ " should not be > " + MAX_QUEUE_CAPACITY_RECOMMANDED + ValidatorUtil.CR_LF
			+ SqlTag.SQL_PRODUCT_START + ValidatorUtil.WARNING_SECOND_LINE_SPACES + " "
			+ " because of a potential SQL run bottleneck. (Set value in .properties file: " + capacity
			+ ")");
	    }
	}
    }
}
