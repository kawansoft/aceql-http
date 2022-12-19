package org.kawanfw.test.stored_procedure;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

//import com.aceql.jdbc.driver.free.AceQLDriver;

import java.sql.SQLException;

import oracle.jdbc.OracleArray;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleStruct;
import oracle.jdbc.OracleTypeMetaData;
import oracle.jdbc.OracleTypes;
import oracle.jdbc.pool.OracleDataSource;

public class Main {

    static public void printArray(OracleArray array, int level) throws SQLException {
        Object[] attributes = (Object[])array.getArray();
        System.out.println("printing array at level " + level);
        for (int i = 0; i < attributes.length; i++) {
            if (attributes[i] instanceof OracleStruct) {
                printStruct((OracleStruct) attributes[i], level + 1);
            } else if (attributes[i] instanceof OracleArray) {
                printArray(((OracleArray) attributes[i]), level + 1);
            } else {
                //System.out.println("Have Array attribute at level="+level+", pos="+i+" = "+attributes[i]);
                System.out.println("Have Array attribute at level="+level+", pos="+i+" = "+((oracle.sql.Datum)(attributes[i])).stringValue());

            }
        }
        System.out.println("finished printing array at level="+level+"\n");
    }

    @SuppressWarnings("unused")
    static public void printStruct(OracleStruct data, int level) throws SQLException {
        Object[] attrs = data.getAttributes();
        OracleTypeMetaData meta = data.getOracleMetaData();
        System.out.println("printing struct at level " + level);
        for (int i = 0; i < attrs.length; i++) {
            if (attrs[i] instanceof OracleStruct) {
                printStruct(((OracleStruct) attrs[i]), level + 1);
            } else if (attrs[i] instanceof OracleArray) {
                printArray(((OracleArray) attrs[i]), level + 1);
            } else if (attrs[i] != null) {
                System.out.println("Have struct attribute at level="+level+", pos="+i+" = "+attrs[i]);
            } else {
                System.out.println("Have struct attribute at level="+level+", pos="+i+" = null");
            }
        }
        System.out.println("finished printing struct at level="+level+"\n");
    }
    @SuppressWarnings("unused")
    static public void printOracleAttributes(Object[] attrs, int level) throws SQLException {
        for (int i = 0; i < attrs.length; i++) {
            oracle.sql.Datum datum = (oracle.sql.Datum) attrs[i];

            if (attrs[i] instanceof OracleStruct) {
                printStruct(((OracleStruct) attrs[i]), level + 1);
            } else if (attrs[i] instanceof oracle.sql.ARRAY) {
                printArray(((OracleArray) attrs[i]), level + 1);
            } else if (attrs[i] != null) {
                System.out.println("Have attribute at level="+level+", pos="+i+" = "+((oracle.sql.Datum)(attrs[i])).stringValue());
            } else {
                System.out.println("Have attribute at level="+level+", pos="+i+" = null");
            }
        }
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {

        // The URL of the AceQL Server servlet
        // Port number is the port number used to start the Web Server:
        String url = "http://localhost:50005/aceql";

        // The remote database to use:
        String database = "B2_AWS_DEV";

        // (user, password) for authentication on server side.
        String user = "wally";
        String password = "*****";

        OracleDataSource ods = null;
        try {
            ods = new OracleDataSource();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ods.setURL("jdbc:oracle:thin:F2LDATP0/XXXXXX@B2_AWS_DEV-wad-I1.devops.f4o.ch:1521:nickel");
        //ods.setPassword("XXXXXX");
        try {
            System.out.println("Connecting to database...");
            Connection conn = ods.getConnection();
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("JDBC driver version is " + meta.getDriverVersion());
            // Register Driver
/*            try {
                DriverManager.registerDriver(new AceQLDriver());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                Class.forName(AceQLDriver.class.getName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }*/

         //   Properties info = new Properties();
         //   info.put("user", "F2LDATP0");
         //   info.put("password", password);
            //info.put("database", database);

            try {
                //Connection connection = DriverManager.getConnection(url, info);
                java.sql.CallableStatement csRegister = conn.prepareCall("{call f2qapenv.setlogpar(64147726648959,1,949,9500)}");
                csRegister.execute();
                Object[] attributesInstrument  = new Object[5];
                attributesInstrument[0]        = 33338337572328L;
                attributesInstrument[1]        = 1;
                attributesInstrument[2]        = 4;

                Object[] attributesBookingInfo = new Object[8];
                attributesBookingInfo[5]       = 68880740987050L;

                Object[] attributesOrderData1 = new Object[21];
                attributesOrderData1[0]         = "cliref-01";
                attributesOrderData1[1]         = attributesInstrument;
                attributesOrderData1[2]         = "B2";
                attributesOrderData1[3]         = "TF";
                attributesOrderData1[10]        = 160;

                Object[] attributesOrderData2 = new Object[21];
                attributesOrderData2[0]         = "cliref-02";
                attributesOrderData2[1]         = attributesInstrument;
                attributesOrderData2[2]         = "B2";
                attributesOrderData2[3]         = "TF";
                attributesOrderData2[10]        = 160;


                Object[] attributesOrderReqOuter = new Object[2];

                Object[] attributesOrderReq1      = new Object[12];
                attributesOrderReq1[0]            = 100.0;//menge
                attributesOrderReq1[2]            = attributesBookingInfo;
                attributesOrderReq1[3]            = attributesOrderData1;

                Object[] attributesOrderReq2      = new Object[12];
                attributesOrderReq2[0]            = 200.0;//menge
                attributesOrderReq2[2]            = attributesBookingInfo;
                attributesOrderReq2[3]            = attributesOrderData2;

                attributesOrderReqOuter[0]        = attributesOrderReq1;
                attributesOrderReqOuter[1]        = attributesOrderReq2;

             //   Array orderReq2 = conn.createArrayOf("B2CI_CONSTRUCT_ORDER_REQ_2", attributesOrderReqOuter);
                Object arg1 = attributesOrderReqOuter;
                java.sql.Array array = ((OracleConnection)conn).createOracleArray("B2CI_CONSTRUCT_ORDER_REQ_2", arg1);

                System.out.println("creating input");
                java.sql.CallableStatement cs
                        = conn.prepareCall("{ ? = call B2QI_ORDER_2.F_ConstructOrderRequest(?, ?) }");

                cs.registerOutParameter(1, OracleTypes.ARRAY, "B2CI_ORDER_REQ_2");
                cs.setObject(2,array);
                cs.setString(3,"clirefbase");
                cs.execute();
                java.sql.Array retValue = cs.getArray(1);
                Object [] objectArray = (Object []) retValue.getArray();
                System.out.println("objectArray.length = " + objectArray.length);
                for (int idx=0;idx < objectArray.length; ++idx) {
                    System.out.println("outermost, position="+idx);
                    if (objectArray[idx] instanceof oracle.jdbc.OracleArray) {
                        printArray(((oracle.jdbc.OracleArray) objectArray[idx]), 0);
                    } else if (objectArray[idx] instanceof oracle.jdbc.OracleStruct) {
                        printStruct(((oracle.jdbc.OracleStruct) objectArray[idx]), 0);
                    } else {
                        System.out.println("Have attribute at level=0, pos="+idx+" = "+objectArray[idx]);
                    }
                    System.out.println("on pos="+idx+", have="+objectArray[idx].getClass().getName());

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Create Oracle DatabaseMetaData object DatabaseMetaData meta = conn.getMetaData();
// gets driver info:
    }
}