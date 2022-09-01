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
