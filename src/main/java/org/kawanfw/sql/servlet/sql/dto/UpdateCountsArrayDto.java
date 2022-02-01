/*
 * This file is part of AceQL JDBC Driver.
 * AceQL JDBC Driver: Remote JDBC access over HTTP with AceQL HTTP.
 * Copyright (C) 2021,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.
 *
 * Licensed under the Apache License, DefaultVersion 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
