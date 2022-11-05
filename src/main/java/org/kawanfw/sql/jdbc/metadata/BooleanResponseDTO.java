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
package org.kawanfw.sql.jdbc.metadata;
/**
 * A boolean response DTO.
 * @author Nicolas de Pomereu
 *
 */

public class BooleanResponseDTO {

    private String status = "OK";
    private Boolean response;

    /**
     * Constructor.
     * @param response	the true/false response.
     */
    public BooleanResponseDTO(boolean response) {
	super();
	this.response = response;
    }

    public Boolean getResponse() {
        return response;
    }

    @Override
    public String toString() {
	return "BooleanResponseDTO [status=" + status + ", response=" + response + "]";
    }

}
