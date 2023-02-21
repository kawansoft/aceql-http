/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.util.healthcheck;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 * Contains the health check info to send to client side.
 * @author Nicolas de Pomereu
 *
 */
public class HealthCheckInfoDto {

    private String status = "OK";

    private long initMemory;
    private long usedMemory;
    private long maxMemory;
    private long committedMemory;
    
    /**
     * Constructor
     */
    public HealthCheckInfoDto() {
	MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
	initMemory = memoryMXBean.getHeapMemoryUsage().getInit();
	usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
	maxMemory = memoryMXBean.getHeapMemoryUsage().getMax();
	committedMemory = memoryMXBean.getHeapMemoryUsage().getCommitted();
    }

    /**
     * @return the status
     */
    String getStatus() {
        return status;
    }

    /**
     * @return the initMemory
     */
    long getInitMemory() {
        return initMemory;
    }

    /**
     * @return the usedMemory
     */
    long getUsedMemory() {
        return usedMemory;
    }

    /**
     * @return the maxMemory
     */
    long getMaxMemory() {
        return maxMemory;
    }

    /**
     * @return the committedMemory
     */
    long getCommittedMemory() {
        return committedMemory;
    }

    @Override
    public String toString() {
	return "HealthCheckInfoDto [status=" + status + ", initMemory=" + initMemory + ", usedMemory=" + usedMemory
		+ ", maxMemory=" + maxMemory + ", committedMemory=" + committedMemory + "]";
    }
    
   

    
}
