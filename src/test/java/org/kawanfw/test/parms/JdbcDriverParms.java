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
    public static String POSTGRES_URL = "jdbc:postgresql://localhost:5432/sampledb";

    public static String MYSQL_DRIVER_CLASS_NAME = "org.gjt.mm.mysql.Driver";
    public static String MYSQL_URL = "jdbc:mysql://localhost:3306/sampledb";

    public static String MARIADB_DRIVER_CLASS_NAME = "org.mariadb.jdbc.Driver";
    public static String MARIADB_URL = "jdbc:mysql://localhost:3307/sampledb";

    public static String ORACLE_DRIVER_CLASS_NAME = "oracle.jdbc.driver.OracleDriver";
    public static String ORACLE_URL = "jdbc:oracle:thin:sampledb@//localhost:1521/XE";

    public static String SQL_SERVER_JTDS_DRIVER_CLASS_NAME = "net.sourceforge.jtds.jdbc.Driver";
    public static String SQL_SERVER_JTDS_URL = "jdbc:jtds:sqlserver://localhost:1433/sampledb";

    // MS JDBC Driver (Test deferred because of bug with Java 6 u29. Does not
    // work!)
    public static String SQL_SERVER_MS_DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    // "jdbc:sqlserver://localhost:1433;integratedSecurity=true;";
    public static String SQL_SERVER_MS_URL = "jdbc:sqlserver://localhost:1433;database=sampledb";

    public static String DB2_DRIVER_CLASS_NAME = "com.ibm.db2.jcc.DB2Driver";
    public static String DB2_URL = "jdbc:db2://localhost:50000/KAWAN_EX";

    public static String ASE_DRIVER_CLASS_NAME = "com.sybase.jdbc4.jdbc.SybDriver";
    public static String ASE_URL = "jdbc:sybase:Tds:DELL-NDP:5000/sampledb";

    // ASE Notation with jTds Driver
    // jdbc:jtds:<server_type>://<server>[:<port>][/<database>][;<property>=<value>[;...]]
    // public static String ASE_DRIVER_CLASS_NAME =
    // "net.sourceforge.jtds.jdbc.Driver";
    // public static String ASE_URL =
    // "jdbc:jtds:sybase://DELL-NDP:5000/sampledb";

    // Sybase SQL Anywhere
    public static String SQL_ANYWHERE_DRIVER_CLASS_NAME = "sybase.jdbc4.sqlanywhere.IDriver";
    public static String SQL_ANYWHERE_URL = "jdbc:sqlanywhere:eng=sampledb";

    public static String INFORMIX_CLASS_NAME = "com.informix.jdbc.IfxDriver";
    public static String INFORMIX_URL = "jdbc:informix-sqli://127.0.0.1:9088/sampledb:INFORMIXSERVER=ol_ids_1150_1";

    // HSQLDB
    // cd C:\Users\Nicolas de
    // Pomereu\Documents\_Dell_6420\softwares\hsqldb-2.2.8\hsqldb
    // java -classpath lib/hsqldb.jar org.hsqldb.server.Server
    public static String HSQLDB_DRIVER_CLASS_NAME = "org.hsqldb.jdbcDriver";
    public static String HSQLDB_URL = "jdbc:hsqldb:hsql://localhost/sampledb";

    public static String H2_DRIVER_CLASS_NAME = "org.h2.Driver";
    public static String H2_URL = "jdbc:h2:tcp://localhost/~/sampledb";

    public static String INGRES_DRIVER_CLASS_NAME = "com.ingres.jdbc.IngresDriver";
    public static String INGRES_URL = "jdbc:ingres://localhost:II7/sampledb";

    public static String TERADATA_DRIVER_CLASS_NAME = "com.teradata.jdbc.TeraDriver";
    public static String TERADATA_URL = "jdbc:teradata://dbc/database=vmtest,tmode=ANSI,charset=UTF8";

    public static String ACCESS_CLASS_NAME = "sun.jdbc.odbc.JdbcOdbcDriver";
    public static String ACCESS_URL = "jdbc:odbc:sampledb";

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
