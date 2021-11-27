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

package org.kawanfw.sql.metadata.dto;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Container to transport DatabaseInfo instance.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class DatabaseInfoDto {

    private String status = "OK";
    private int datatabaseMajorVersion;
    private int databaseMinorVersion;
    private String  databaseProductName;
    private String databaseProductVersion;
    private int driverMajorVersion;
    private int driverMinorVersion;
    private String driverName;
    private String driverVersion;

    /**
     * Constructor
     * @param databaseMetaData
     * @throws SQLException
     */
    public DatabaseInfoDto(DatabaseMetaData databaseMetaData) throws SQLException {
	Objects.requireNonNull(databaseMetaData, "databaseMetaData cannot be null!");
	
	try {
	    datatabaseMajorVersion = databaseMetaData.getDatabaseMajorVersion();
	} catch (SQLException e) {
	    datatabaseMajorVersion = 0;
	}
	try {
	    databaseMinorVersion = databaseMetaData.getDatabaseMinorVersion();
	} catch (SQLException e) {
	    databaseMinorVersion = 0;
	}
	
	try {
	    databaseProductName = databaseMetaData.getDatabaseProductName();
	} catch (SQLException e) {
	    databaseProductName = "unknown";
	}
	
	try {
	    databaseProductVersion = databaseMetaData.getDatabaseProductVersion();
	} catch (SQLException e) {
	    databaseProductVersion = "unknown";
	}
	
	try {
	    driverMajorVersion = databaseMetaData.getDriverMajorVersion();
	} catch (Exception e) {
	    driverMajorVersion = 0;
	}
	
	try {
	    driverMinorVersion = databaseMetaData.getDriverMinorVersion();
	} catch (Exception e) {
	    driverMinorVersion = 0;
	}	
	
	try {
	    driverName = databaseMetaData.getDriverName();
	} catch (SQLException e) {
	    driverName = "unknown";
	}
	
	try {
	    driverVersion = databaseMetaData.getDriverVersion();
	} catch (SQLException e) {
	    driverVersion = "unknown";
	}

    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the datatabaseMajorVersion
     */
    public int getDatatabaseMajorVersion() {
        return datatabaseMajorVersion;
    }

    /**
     * @return the databaseMinorVersion
     */
    public int getDatabaseMinorVersion() {
        return databaseMinorVersion;
    }

    /**
     * @return the databaseProductName
     */
    public String getDatabaseProductName() {
        return databaseProductName;
    }

    /**
     * @return the databaseProductVersion
     */
    public String getDatabaseProductVersion() {
        return databaseProductVersion;
    }

    /**
     * @return the driverMajorVersion
     */
    public int getDriverMajorVersion() {
        return driverMajorVersion;
    }

    /**
     * @return the driverMinorVersion
     */
    public int getDriverMinorVersion() {
        return driverMinorVersion;
    }

    /**
     * @return the driverName
     */
    public String getDriverName() {
        return driverName;
    }

    /**
     * @return the driverVersion
     */
    public String getDriverVersion() {
        return driverVersion;
    }

    @Override
    public String toString() {
	return "DatabaseInfoDto [status=" + status + ", datatabaseMajorVersion=" + datatabaseMajorVersion
		+ ", databaseMinorVersion=" + databaseMinorVersion + ", databaseProductName=" + databaseProductName
		+ ", databaseProductVersion=" + databaseProductVersion + ", driverMajorVersion=" + driverMajorVersion
		+ ", driverMinorVersion=" + driverMinorVersion + ", driverName=" + driverName + ", driverVersion="
		+ driverVersion + "]";
    } 
    
}
