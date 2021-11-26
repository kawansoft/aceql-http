/*
 * This file is part of AceQL JDBC Driver.
 * AceQL JDBC Driver: Remote JDBC access over HTTP with AceQL HTTP.
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
package org.kawanfw.sql.metadata.dto;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 
 * All Remote database info Holder.
 * @author Nicolas de Pomereu
 *
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
    public DatabaseInfo(DatabaseMetaData databaseMetaData) throws SQLException {
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
