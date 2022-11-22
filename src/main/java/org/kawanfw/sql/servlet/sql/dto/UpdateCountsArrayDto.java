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
package org.kawanfw.sql.servlet.sql.dto;

import java.util.Arrays;

/**
 * Contains the list of SQL batch responses downloaded for server
 * @author Nicolas de Pomereu
 *
 */
public class UpdateCountsArrayDto {

    private String status = "OK";
    private int [] updateCountsArray;

    public UpdateCountsArrayDto(int[] updateCountsArray) {
	this.updateCountsArray = updateCountsArray;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the updateCountsArray
     */
    public int[] getUpdateCountsArray() {
        return updateCountsArray;
    }

    @Override
    public String toString() {
	return "UpdateCountsArrayDto [updateCountsArray=" + Arrays.toString(updateCountsArray) + "]";
    }

   
}
