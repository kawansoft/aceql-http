/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2020,  KawanSoft SAS
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
package org.kawanfw.test.parms;

/**
 *
 * Defines the JDBC driver parameters
 * 
 * @author Nicolas de Pomereu
 *
 */

public class JdbcDriverParms {

    public static String POSTGRES_DRIVER_CLASS_NAME = "org.postgresql.Driver";
    public static String POSTGRES_URL = "jdbc:postgresql://localhost:5432/kawansoft_example";

    public static String MYSQL_DRIVER_CLASS_NAME = "org.gjt.mm.mysql.Driver";
    public static String MYSQL_URL = "jdbc:mysql://localhost:3306/kawansoft_example";

    public static String MARIADB_DRIVER_CLASS_NAME = "org.mariadb.jdbc.Driver";
    public static String MARIADB_URL = "jdbc:mysql://localhost:3307/kawansoft_example";

    public static String ORACLE_DRIVER_CLASS_NAME = "oracle.jdbc.driver.OracleDriver";
    public static String ORACLE_URL = "jdbc:oracle:thin:kawansoft_example@//localhost:1521/XE";

    public static String SQL_SERVER_JTDS_DRIVER_CLASS_NAME = "net.sourceforge.jtds.jdbc.Driver";
    public static String SQL_SERVER_JTDS_URL = "jdbc:jtds:sqlserver://localhost:1433/kawansoft_example";

    // MS JDBC Driver (Test deferred because of bug with Java 6 u29. Does not
    // work!)
    public static String SQL_SERVER_MS_DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    // "jdbc:sqlserver://localhost:1433;integratedSecurity=true;";
    public static String SQL_SERVER_MS_URL = "jdbc:sqlserver://localhost:1433;database=kawansoft_example";

    public static String DB2_DRIVER_CLASS_NAME = "com.ibm.db2.jcc.DB2Driver";
    public static String DB2_URL = "jdbc:db2://localhost:50000/KAWAN_EX";

    public static String ASE_DRIVER_CLASS_NAME = "com.sybase.jdbc4.jdbc.SybDriver";
    public static String ASE_URL = "jdbc:sybase:Tds:DELL-NDP:5000/kawansoft_example";

    // ASE Notation with jTds Driver
    // jdbc:jtds:<server_type>://<server>[:<port>][/<database>][;<property>=<value>[;...]]
    // public static String ASE_DRIVER_CLASS_NAME =
    // "net.sourceforge.jtds.jdbc.Driver";
    // public static String ASE_URL =
    // "jdbc:jtds:sybase://DELL-NDP:5000/kawansoft_example";

    // Sybase SQL Anywhere
    public static String SQL_ANYWHERE_DRIVER_CLASS_NAME = "sybase.jdbc4.sqlanywhere.IDriver";
    public static String SQL_ANYWHERE_URL = "jdbc:sqlanywhere:eng=kawansoft_example";

    public static String INFORMIX_CLASS_NAME = "com.informix.jdbc.IfxDriver";
    public static String INFORMIX_URL = "jdbc:informix-sqli://127.0.0.1:9088/kawansoft_example:INFORMIXSERVER=ol_ids_1150_1";

    // HSQLDB
    // cd C:\Users\Nicolas de
    // Pomereu\Documents\_Dell_6420\softwares\hsqldb-2.2.8\hsqldb
    // java -classpath lib/hsqldb.jar org.hsqldb.server.Server
    public static String HSQLDB_DRIVER_CLASS_NAME = "org.hsqldb.jdbcDriver";
    public static String HSQLDB_URL = "jdbc:hsqldb:hsql://localhost/kawansoft_example";

    public static String H2_DRIVER_CLASS_NAME = "org.h2.Driver";
    public static String H2_URL = "jdbc:h2:tcp://localhost/~/kawansoft_example";

    public static String INGRES_DRIVER_CLASS_NAME = "com.ingres.jdbc.IngresDriver";
    public static String INGRES_URL = "jdbc:ingres://localhost:II7/kawansoft_example";

    public static String TERADATA_DRIVER_CLASS_NAME = "com.teradata.jdbc.TeraDriver";
    public static String TERADATA_URL = "jdbc:teradata://dbc/database=vmtest,tmode=ANSI,charset=UTF8";

    public static String ACCESS_CLASS_NAME = "sun.jdbc.odbc.JdbcOdbcDriver";
    public static String ACCESS_URL = "jdbc:odbc:kawansoft_example";

    public static String USERNAME = "user1";
    public static String PASSWORD = "password1";

    /**
     * Constructor
     */
    protected JdbcDriverParms() {
    }

}

/**
 * 
 */
