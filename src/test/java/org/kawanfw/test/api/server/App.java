package org.kawanfw.test.api.server;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang3.SystemUtils;
import org.kawanfw.sql.metadata.sc.info.AceQLOutputFormat;
import org.kawanfw.sql.metadata.sc.info.SchemaInfoAccessor;
import org.kawanfw.sql.metadata.sc.info.SchemaInfoSC;
import org.kawanfw.test.sql.metadata.aceql.ConnectionParms;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception {

	boolean doReturn = true;
	if (doReturn) return;

	for (int i = 1; i < 6; i++) {
	    Connection connection = ConnectionParms.getConnection(i);
	    openHtmlForConnection(connection);
	}

    }

    /**
     * @param connection
     * @throws SQLException
     * @throws IOException
     */
    public static void openHtmlForConnection(Connection connection) throws SQLException, IOException {
	DatabaseMetaData databaseMetaData = connection.getMetaData();
	String databaseProductName = databaseMetaData.getDatabaseProductName();

	System.out.println(databaseProductName);
	System.out.println(SystemUtils.JAVA_VERSION);

	System.out.println(new Date() + " Begin...");
	File file = new File("c:\\test\\sc.out.html");

	SchemaInfoAccessor schemaInfoAccessor = new SchemaInfoAccessor(connection);
	System.out.println("schemaInfoAccessor: " + schemaInfoAccessor.isAccessible());

	if (schemaInfoAccessor.isAccessible()) {
	    SchemaInfoSC schemaInfoSC = schemaInfoAccessor.getSchemaInfoSC();

	    String table = null; // customer;
	    // table = "orderlog";

	    schemaInfoSC.buildOnFile(file, AceQLOutputFormat.html, table);
	    System.out.println(schemacrawler.Version.getVersion());

	    System.out.println(new Date() + " Done: " + file);
	    Desktop desktop = Desktop.getDesktop();
	    desktop.browse(file.toURI());

	} else {
	    System.out.println("Can not get full Schema info: " + schemaInfoAccessor.getFailureReason());
	}
    }

}
