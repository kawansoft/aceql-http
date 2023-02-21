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
package org.kawanfw.sql.metadata.dto;

import java.sql.SQLException;
import java.util.Objects;

/**
 * 
 * A simple shortcut class that contains main remote database & JDBC info.
 * 
 * @author Nicolas de Pomereu
 * @since 8.1
 */
public class DatabaseInfo {
    
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
    DatabaseInfo(DatabaseInfoDto databaseMetaData) throws SQLException {
	Objects.requireNonNull(databaseMetaData, "databaseMetaData cannot be null!");
	datatabaseMajorVersion = databaseMetaData.getDatatabaseMajorVersion();
	databaseMinorVersion = databaseMetaData.getDatabaseMinorVersion();
	databaseProductName = databaseMetaData.getDatabaseProductName();
	databaseProductVersion = databaseMetaData.getDatabaseProductVersion();
	driverMajorVersion = databaseMetaData.getDriverMajorVersion();
	driverMinorVersion = databaseMetaData.getDriverMinorVersion();
	driverName = databaseMetaData.getDriverName();
	driverVersion = databaseMetaData.getDriverVersion();
    }

    /**
     * Gets the database major version 
     * @return the database major version 
     */
    public int getDatatabaseMajorVersion() {
        return datatabaseMajorVersion;
    }

    /**
     * Gets the database minor version 
     * @return the database minor version 
     */
    public int getDatabaseMinorVersion() {
        return databaseMinorVersion;
    }

    /**
     * Gets the database product name
     * @return the database product name
     */
    public String getDatabaseProductName() {
        return databaseProductName;
    }

    /**
     * Gets the database product version
     * @return the database product version
     */
    public String getDatabaseProductVersion() {
        return databaseProductVersion;
    }

    /**
     * Gets the driver major version 
     * @return the driver major version 
     */
    public int getDriverMajorVersion() {
        return driverMajorVersion;
    }

    /**
     * Gets the driver minor version 
     * @return the driver minor version 
     */
    public int getDriverMinorVersion() {
        return driverMinorVersion;
    }

    /**
     * Gets the driver name 
     * @return the driver name
     */
    public String getDriverName() {
        return driverName;
    }

    /**
     * Gets the driver version 
     * @return the driver version 
     */
    public String getDriverVersion() {
        return driverVersion;
    }

    @Override
    public String toString() {
	return "DatabaseInfo [datatabaseMajorVersion=" + datatabaseMajorVersion + ", databaseMinorVersion="
		+ databaseMinorVersion + ", databaseProductName=" + databaseProductName + ", databaseProductVersion="
		+ databaseProductVersion + ", driverMajorVersion=" + driverMajorVersion + ", driverMinorVersion="
		+ driverMinorVersion + ", driverName=" + driverName + ", driverVersion=" + driverVersion + "]";
    }


    
}
