/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.metadata.dto;

/**
 * Container to transport limits info defined in DatabaseConfigurator.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class LimitsInfoDto {

    private String status = "OK";
    private long maxRows = 0;
    private long maxBlobLength = 0;

    /**
     * Constructor.
     * 
     * @param maxRows       value of {@code DatabaseConfigurator.getMaxRows}
     * @param maxBlobLength value of {@code DatabaseConfigurator.getMaxBlobLength}
     */
    public LimitsInfoDto(long maxRows, long maxBlobLength) {
	this.maxRows = maxRows;
	this.maxBlobLength = maxBlobLength;
    }
    
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the maxRows
     */
    public long getMaxRows() {
        return maxRows;
    }

    /**
     * @return the maxBlobLength
     */
    public long getMaxBlobLength() {
        return maxBlobLength;
    }

    @Override
    public String toString() {
	return "LimitsInfoDto [status=" + status + ", maxRows=" + maxRows + ", maxBlobLength=" + maxBlobLength + "]";
    }

}
