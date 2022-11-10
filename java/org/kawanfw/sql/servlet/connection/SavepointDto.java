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
package org.kawanfw.sql.servlet.connection;
/**
 * Contains the ID and name of a savepont.
 * @author Nicolas de Pomereu
 *
 */

public class SavepointDto {

    private String status = "OK";
    private int id;
    private String name;

    public SavepointDto(int id, String name) {
	this.id = id;
	this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
	return "SavepointDto [status=" + status + ", id=" + id + ", name=" + name + "]";
    }

}
