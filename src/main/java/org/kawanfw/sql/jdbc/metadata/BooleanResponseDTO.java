/*
 * This file is part of AceQL Client SDK.
 * AceQL Client SDK: Remote JDBC access over HTTP with AceQL HTTP.
 * Copyright (C) 2021,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
