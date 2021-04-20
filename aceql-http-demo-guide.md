# AceQL HTTP v7.0 - April 20, 2021

# Demo Guide

<img src="https://www.aceql.com/favicon.png" alt="AceQ HTTP Icon"/>

   * [Overview](#overview)
   * [Server Side Settings](#server-side-settings)
      * [Create the sampledb database](#create-the-sampledb-database)
      * [Linux/Unix Installation &amp; Server Startup](#linuxunix-installation--server-startup)
         * [Download &amp; installation](#download--installation)
         * [Update the PATH (Optional)](#update-the-path-optional)
         * [Testing AceQL HTTP Web server installation](#testing-aceql-http-web-server-installation)
         * [Configure JDBC parameters in aceql-server.properties file](#configure-jdbc-parameters-in-aceql-serverproperties-file)
         * [Add your JDBC driver to AceQL installation](#add-your-jdbc-driver-to-aceql-installation)
         * [Start the AceQL HTTP Web Server](#start-the-aceql-http-web-server)
      * [Windows Installation &amp; Server Startup](#windows-installation--server-startup)
         * [Add your JDBC driver to AceQL installation](#add-your-jdbc-driver-to-aceql-installation-1)
         * [Configure JDBC parameters in aceql-server.properties file](#configure-jdbc-parameters-in-aceql-serverproperties-file-1)
         * [Start the AceQL Web Server](#start-the-aceql-web-server)
   * [Client Side](#client-side)
      * [cURL](#curl)
      * [C# Client SDK](#c-client-sdk)
      * [Java Client SDK / JDBC Driver](#java-client-sdk--jdbc-driver)
      * [Python Client SDK](#python-client-sdk)
   * [From now on…](#from-now-on)


# Overview

AceQL HTTP is a library of REST like APIs that allows you access to remote SQL databases over HTTP from any device that supports HTTP. This software has been designed to handle heavy traffic in production environments.

<img src="https://www.aceql.com/img/AceQL-Schema-min.jpg" alt="AceQL Draw"/>

For example, a select command would be called from the client side using this http call with cURL:

```bash
$ curl --data-urlencode \
 "sql=select id, title, lname from customer where customer_id = 1" \
 https://www.acme.com:9443/aceql/session/mn7andp2tt049iaeaskr28j9ch/execute_query
```

AceQL HTTP is authorized through an Open Source license: [AceQL Open Source License (LGPL v2.1)](http://www.aceql.com/rest/soft/licensing/AceQLOpenSourceLicense.txt).

 The AceQL HTTP framework consists of:

- The AceQL Web Server.

- User configuration classes injected at runtime, these are server classes that ensure both security and configuration. Many built-in classes are provided and standard configuration may be done without any coding.

- The AceQL Client SDKs for [C#](https://github.com/kawansoft/AceQL.Client2) ,  [Java](https://github.com/kawansoft/aceql-http-client-sdk) and [Python](https://github.com/kawansoft/aceql-http-client-python) that allow you to wrap AceQL HTTP API calls using fluent code: 

  - ```C#
    // C# AceQL Client Calls Sample 
    string sql = "select id, title, lname from customer where customer_id = 1";
    
    using (AceQLCommand command = new AceQLCommand(sql, connection))
    using (AceQLDataReader dataReader = await command.ExecuteReaderAsync())
    {
        while (dataReader.Read())
        {
            Console.WriteLine();
            int i = 0;
            Console.WriteLine("customer id   : " + dataReader.GetValue(i++));
            Console.WriteLine("customer title: " + dataReader.GetValue(i++));
            Console.WriteLine("customer name : " + dataReader.GetValue(i++));
        }
    } 
    ```

  - ```java 
    // Java AceQL Client Calls Sample 
    String sql = "select id, title, lname from customer where customer_id = 1";
    
    try (Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);) {
        while (rs.next()) {
    
        System.out.println();
        int i = 1;
        System.out.println("customer id   : " + rs.getInt(i++));
        System.out.println("customer title: " + rs.getString(i++));
        System.out.println("customer name : " + rs.getString(i++));
        }
    }
    ```

  - ```python
    # Python AceQL Client Calls Sample 
    with closing(connection.cursor()) as cursor:
        sql = "select id, title, lname from customer where customer_id = 1";
        cursor.execute(sql)
        rows = cursor.fetchall()
    
        for row in rows:
            print("customer id   : " + str(row[0]))
            print("customer title: " + row[1])
            print("customer name : " + row[2])
    ```


The execution of each AceQL HTTP API statement is conditioned by optional rules, defined in configuration classes called "Configurators."

# Server Side Settings

## Create the sampledb database

Download the database `sampledb` schema corresponding to your database engine: 

- [sampledb_mysq.txt](https://www.aceql.com/rest/soft/7.0/src/sampledb_mysql.txt )
- [sampledb_postgresql.txt](https://www.aceql.com/rest/soft/7.0/src/sampledb_postgresql.txt)
- [sampledb_ms_sql_serverl.txt](https://www.aceql.com/rest/soft/7.0/src/sampledb_ms_sql_server.txt)
- [sampledb_oracle_database.txt](https://www.aceql.com/rest/soft/7.0/src/sampledb_oracle_database.txt)


For other databases engines, just tailor the file as indicated: [sampledb_other_databases.txt](https://www.aceql.com/rest/soft/7.0/src/sampledb_other_databases.txt)

Then launch the script that will create the tables in a database

## Linux/Unix Installation & Server Startup 

AceQL requires the installation of Java version 8+.

### Download & installation

Open a terminal and download with `Wget` 

```bash
$ wget https://www.aceql.com/soft/download/7.0/aceql-http-7.0.run
```

If you get a certificate error message, do one of the following:

1. If the problem is that a known root CA is missing and when you are using Ubuntu or Debian,  then you can solve the problem with this one line: `sudo apt-getinstall ca-certificates`. Then retry the `Wget` call.
2. Retry the `Wget` call with `--no-check-certificate` at end of command line. Then check the PGP signature of the downloaded file using the corresponding `.asc` signature file available on [download page](https://www.aceql.com/download) using the PGP hyperlink.

In following lines we will assume that the Open Source edition is chosen. (Operating mode is the same for Pro edition).

```bash
chmod +x aceql-http-7.0.run
./aceql-http-7.0.run 
```

This will create the `aceql-http-7.0` folder.

The full path to the `aceql-http-7.0` installation folder will be surnamed `ACEQL_HOME` in following text.

Example: if you run `aceql-http-7.0.run` from `/home/mike`, then software is installed in

 `/home/mike/aceql-http-7.0` which is the value of `ACEQL_HOME`.

### Update the PATH (Optional)

Open a shell session and make sure java binary is in the PATH by typing  `Java –version` on the command line. 

Add java to your PATH if the command does not display Java version.

Add to your PATH the path to the bin directory of `aceql-http-7.0` installation:

```bash
$ PATH=$PATH:/path/to/aceql-http-7.0/bin/;export PATH
```

### Testing AceQL HTTP Web server installation

 Call the `aceql-server` script to display the AceQL version:

```bash
$ aceql-server -version
```

It will display a line with all version info, like:

```
AceQL HTTP Community v7.0 - 20-Apr-2021
```

### Configure JDBC parameters in aceql-server.properties file

 The AceQL HTTP Web Server configuration is done in property file whose surname is 

`aceql-server.properties`.

AceQL uses the default file `ACEQL_HOME/conf/aceql-server.properties`

Edit `ACEQL_HOME/conf/aceql-server.properties` and go to the `"Tomcat JDBC Connection Pool Section"`. 

Set the database name to the `databases` property:

```ini
# Database names separated by commas
databases = sampledb
```

Change the 4 JDBC properties values accordingly to your JDBC Driver, your database URL, and your database username/password. Each property name must be prefixed with the database name and a dot:

Example for PostgreSQL:

```properties
# PostgreSQL example
sampledb.driverClassName= org.postgresql.Driver
sampledb.url=jdbc:postgresql://localhost:5432/sampledb
sampledb.username=user1  
sampledb.password=password1
```

### Add your JDBC driver to AceQL installation 

Drop you JDBC driver jar into `ACEQL_HOME/lib-jdbc` directory.

### Start the AceQL HTTP Web Server

We will use for our example the port 9090. You may use any port if 9090 is not free.

```bash
$ aceql-server -start -host localhost –port 9090
```

The console will display the properties used, test that the Connection is established on the server side and tell if everything is OK:

```bash
[ACEQL HTTP START] Starting AceQL HTTP Web Server...
[ACEQL HTTP START] AceQL HTTP Community v7.0 - 20-Apr-2021
[ACEQL HTTP START] Using properties file: 
[ACEQL HTTP START]  -> /home/mike/aceql-http/conf/aceql-server.properties
[ACEQL HTTP START] Setting System Properties:
[ACEQL HTTP START] Creating ThreadPoolExecutor:
[ACEQL HTTP START]  -> [corePoolSize: 100, maximumPoolSize: 200, unit: SECONDS, 
[ACEQL HTTP START]  ->  keepAliveTime: 10, workQueue: ArrayBlockingQueue(50000)]
[ACEQL HTTP START] Setting Default Connector base attributes:
[ACEQL HTTP START]  -> maxThreads = 300
[ACEQL HTTP START] Setting Tomcat JDBC Pool attributes for sampledb database:
[ACEQL HTTP START]  -> driverClassName = org.postgresql.Driver
[ACEQL HTTP START]  -> url = jdbc:postgresql://localhost:5432/sampledb
[ACEQL HTTP START]  -> username = user1
[ACEQL HTTP START]  -> password = ********
[ACEQL HTTP START]  -> initialSize = 10
[ACEQL HTTP START]  -> minIdle = 10
[ACEQL HTTP START]  -> maxIdle = 50
[ACEQL HTTP START]  -> maxActive = 50
[ACEQL HTTP START]  -> rollbackOnReturn = true
[ACEQL HTTP START] Testing DataSource.getConnection() for sampledb database:
[ACEQL HTTP START]  -> Connection OK!
[ACEQL HTTP START] Loading servlets:
[ACEQL HTTP START]  -> Servlet defaultPoolsInfo [url-pattern: /default_pools_info] successfully loaded.
[ACEQL HTTP START] Loading UserAuthenticator class:
[ACEQL HTTP START]  -> org.kawanfw.sql.api.server.auth.DefaultUserAuthenticator
[ACEQL HTTP START] Loading RequestHeadersAuthenticator class:
[ACEQL HTTP START]  -> org.kawanfw.sql.api.server.auth.headers.DefaultRequestHeadersAuthenticator
[ACEQL HTTP START] Loading Database sampledb DatabaseConfigurator class:
[ACEQL HTTP START]  -> org.kawanfw.sql.api.server.DefaultDatabaseConfigurator
[ACEQL HTTP START] Loading Database sampledb SQLFirewallManager class: 
[ACEQL HTTP START]   -> org.kawanfw.sql.api.server.firewall.DefaultSqlFirewallManager
[ACEQL HTTP START] Loaded classes Status: OK.
[ACEQL HTTP START] URL for client side: http://localhost:9090/aceql
[ACEQL HTTP START] AceQL HTTP Web Server OK. Running on port 9090
```

 Don’t take care of INFO warnings displays.

If everything is OK, last line will display: 

```bash
[ACEQL HTTP START] AceQL HTTP Web Server OK. Running on port 9090.
```

 If any, configuration errors are displayed with the tag

```bash
[ACEQL HTTP START FAILURE][USER CONFIGURATION]
```

We are now ready to send SQL requests from client side!

## Windows Installation & Server Startup 

AceQL requires the installation of Java version 8+ (64-bit only).

Because the software installs and runs a Windows Service, you must be logged as a Windows Administrator to install AceQL.

[Download AceQL Windows Installer.](https://www.aceql.com/download)

Run the installer.

It will run AceQL at end of installation and display the Window:

<img src="https://www.aceql.com/rest/soft/7.0/img/aceql_windows_gui_home_flatlaf.png" alt="AceQ HTTP GUI Main Windows"/>

**N.B:** Because of a bug in all Java versions > 8 on Windows, the interface will appear "ugly"  or "blurred" on Java version > 8 if you have increased Windows Screen Resolution Options to 125% or 150%.  See [Java Bug Database](https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8194165) for more info. Set back Windows Screen Resolution to 100% for clean display.

### Add your JDBC driver to AceQL installation

A recent PostgreSQL  JDBC driver is pre-installed. (We could not pre-install other vendor drivers due to license restrictions). Skip this paragraph if you are a PostgreSQL user.

In order to install your JDBC Driver:

1. Quit completely AceQL with `Ctrl-Q` (or `File` menu & `Quit` item).
2. Add your JDBC Driver to your `CLASSPATH` or copy it to the `\lib-jdbc` subdirectory of the main installation directory.
3. Restart AceQL. Then verify that your JDBC Driver is in your current `CLASSPATH` with the `Display CLASSPATH` button. 

### Configure JDBC parameters in aceql-server.properties file 

 The AceQL HTTP Web Server configuration is done in property file whose surname is  `aceqlserver.properties`.

 A default prefilled `aceql-server.properties` file is set on in the interface

Click `Edit` button to change `aceql-server.properties` content and go to the:

 `"Tomcat JDBC Connection Pool Section"`.

Set the database name to the databases property:

```properties
# Databasenames separated by commas
databases = sampledb
```

Change the 4 JDBC properties values accordingly to your JDBC Driver, your database URL, and your database username/password. Each property name must be prefixed with the database name and a dot.

Example for PostgreSQL:

```properties
# PostgreSQL example
sampledb.driverClassName= org.postgresql.Driver
sampledb.url=jdbc:postgresql://localhost:5432/sampledb
sampledb.username=user1  
sampledb.password=password1
```

Leave the default **localhost** value for the Host field.

You may set any port value for the `Port` field if port 9090 is not free on your machine.

### Start the AceQL Web Server

Click on `Start Server  `. This will open a console.

The console will display the properties used, test that the `Connection` is established on the server side and tell if everything is OK:

```bash
[ACEQL HTTP START] Starting AceQL HTTP Web Server...
[ACEQL HTTP START] AceQL HTTP Community v7.0 - 20-Apr-2021
[ACEQL HTTP START] Using properties file: 
[ACEQL HTTP START]  -> I:\_dev_awake\aceql-http-main\aceql-http\conf\aceql-server.properties
[ACEQL HTTP START] Setting System Properties:
[ACEQL HTTP START] Creating ThreadPoolExecutor:
[ACEQL HTTP START]  -> [corePoolSize: 100, maximumPoolSize: 200, unit: SECONDS, 
[ACEQL HTTP START]  ->  keepAliveTime: 10, workQueue: ArrayBlockingQueue(50000)]
[ACEQL HTTP START] Setting Default Connector base attributes:
[ACEQL HTTP START]  -> maxThreads = 300
[ACEQL HTTP START] Setting Tomcat JDBC Pool attributes for sampledb database:
[ACEQL HTTP START]  -> driverClassName = org.postgresql.Driver
[ACEQL HTTP START]  -> url = jdbc:postgresql://localhost:5432/sampledb
[ACEQL HTTP START]  -> username = user1
[ACEQL HTTP START]  -> password = ********
[ACEQL HTTP START]  -> initialSize = 10
[ACEQL HTTP START]  -> minIdle = 10
[ACEQL HTTP START]  -> maxIdle = 50
[ACEQL HTTP START]  -> maxActive = 50
[ACEQL HTTP START]  -> rollbackOnReturn = true
[ACEQL HTTP START] Testing DataSource.getConnection() for sampledb database:
[ACEQL HTTP START]  -> Connection OK!
[ACEQL HTTP START] Loading servlets:
[ACEQL HTTP START]  -> Servlet defaultPoolsInfo [url-pattern: /default_pools_info] successfully loaded.
[ACEQL HTTP START] Loading UserAuthenticator class:
[ACEQL HTTP START]  -> org.kawanfw.sql.api.server.auth.DefaultUserAuthenticator
[ACEQL HTTP START] Loading RequestHeadersAuthenticator class:
[ACEQL HTTP START]  -> org.kawanfw.sql.api.server.auth.headers.DefaultRequestHeadersAuthenticator
[ACEQL HTTP START] Loading Database sampledb DatabaseConfigurator class:
[ACEQL HTTP START]  -> org.kawanfw.sql.api.server.DefaultDatabaseConfigurator
[ACEQL HTTP START] Loading Database sampledb SQLFirewallManager class: 
[ACEQL HTTP START]   -> org.kawanfw.sql.api.server.firewall.DefaultSqlFirewallManager
[ACEQL HTTP START] Loaded classes Status: OK.
[ACEQL HTTP START] URL for client side: http://localhost:9090/aceql
[ACEQL HTTP START] AceQL HTTP Web Server OK. Running on port 9090
```

 If everything is OK, last line will display: 

```bash
[ACEQL HTTP START] AceQL HTTP Server OK. Running on port 9090.
```

If any, configuration errors are displayed with the tag:

```bash
[ACEQL HTTP START FAILURE][ USER CONFIGURATION]
```

 We are now ready to send SQL requests from client side!

#  Client Side

AceQL can be accessed from client side:

- **Using any command line tool to make HTTP calls**. We will provide examples with [cURL](https://curl.haxx.se/).


- **Using the C# Client SDK with C#** SQL regular syntax, same as with SQL Server Client  classes. The C# Client SDK wraps all http communications aspects. Jump to [C# Client SDK](#c-client-sdk).
- **Using the Java Client SDK**  that allows regular JDBC calls and wraps all HTTP communications aspects. Jump to [Java Client SDK / JDBC Driver](#java-client-sdk--jdbc-driver)
- **Using the Python Client SDK** that allows regular [DB API 2.0](https://www.python.org/dev/peps/pep-0249/) SQL calls and wraps all HTTP communications aspects. Jump to [Python Client SDK](#python-client-sdk).
- Using  any other language that supports HTTP GET & POST calls. 

## cURL 

So via cURL we connect to a database `sampledb` with the identifiers `(MyUsername, MySecret)`:

```bash
$ curl --data-urlencode "password=MySecret" \
 http://localhost:9090/aceql/database/sampledb/username/MyUsername/connect
```

le/username/MyUsername/connect

 The command returns a JSON stream with a unique session identifier and a connection identifier:

```bash
{                                             
   "status":"OK",                            
   "session_id":"mn7andp2tt049iaeaskr28j9ch"
   "connection_id":"1628176139"
}
```

On the server side, a JDBC connection is extracted from the connection pool created by the server at startup. The connection will remain ours during the session.

We will use the session identifier to authenticate all subsequent calls.

We insert a customer into the database:

```bash
$ curl --data-urlencode \
 "sql=insert into customer values (1,'Sir', 'Doe', 'John', '1 Madison Ave', 'New York', 'NY  10010', NULL)" \ 
 http://localhost:9090/aceql/session/mn7andp2tt049iaeaskr28j9ch/execute_update
```

Which returns:

```bash
{                    
   "status":"OK",  
   "row_count":1    
}                   
```

We view the inserted customer:

```bash
$ curl \
 --data-urlencode "sql=select * from customer" \
 http://localhost:9090/aceql/session/mn7andp2tt049iaeaskr28j9ch/execute_query
```

This returns the JSON stream:                                

```bash
{  
    "status":"OK",                                               
  	"query_rows":[                                                 
      {                                                          
          "row_1":[                                              
              {                                                   
                  "customer_id":1                                
              },                                                   
              {                                                   
                  "customer_title":"Sir "                         
              },                                                   
              {                                                   
                  "fname":"John"                                   
              },                                                   
              {                                                   
                  "lname":"Doe"                                   
              },                                                   
              {                                                   
                  "addressline":"1600 Pennsylvania Ave NW"         
              },                                                   
              {                                                   
                  "town":"Washington"                             
              },                                                   
              {                                                   
                  "zipcode":"DC 20500  "                           
              },                                                   
              {                                                   
                  "phone":"NULL"                                   
              }                                                   
          ]                                                       
      }                                                           
  ],                                                               
  "row_count":1     
}
```
Let’s update our customer using a prepared statement:

```bash
$ curl --data"prepared_statement=true" \
 --data "param_type_1=VARCHAR&param_value_1=Jim" \
 --data "param_type_2=INTEGER&param_value_2=1"\
 --data-urlencode"sql=update customer set fname=? where customer_id=?" \
 http://localhost:9090/aceql/session/mn7andp2tt049iaeaskr28j9ch/\
 execute_update
```

Which returns:

```bash
{                    
   "status":"OK",  
   "row_count":1    
}      
```

 And now we query back our customer and ask to GZIP the result:

```bash
$ curl \
 --data"gzip_result=true" \
 --data-urlencode \ 
 "sql=select customer_id, customer_title,fname from customer" \
 http://localhost:9090/aceql/session/mn7andp2tt049iaeaskr28j9ch/\
 execute_query>result.gzip
```

 And we end with a clean close of our session:

```bash
$ curl \
 http://localhost:9090/aceql/session/mn7andp2tt049iaeaskr28j9ch/disconnect
```

On the server side, the authentication info is purged and the JDBC connection is released in the pool. (A server thread regularly releases phantom connections that were not closed from the client side.)

From now, you can read the [API User Guide](https://www.aceql.com/DocDownload?doc=https://github.com/kawansoft/aceql-http/blob/master/aceql-http-user-guide-api.md) to learn how to:

- Query or modify the `Connection` properties.
- Create SQL transactions.
- Insert Blobs in the database.
- Retrieve Blobs from the database.

## C# Client SDK 

1. Create the “AceQL.MyRemoteConnection” Windows Classic Desktop Console App in Visual Studio. 

2. Install the [AceQL.Client](https://www.nuget.org/packages/AceQL.Client) package with NuGet.

3. Download this C# source file: [MyRemoteConnection.cs](https://www.aceql.com/rest/soft_csharp/6.0/src/MyRemoteConnection.cs). Then insert it in your project. 

4. The  connection to the remote database is created  using `AceQLConnection` class and passing the URL of the AceQL Servlet Manager of your configuration: 

   ```C#
       /// <summary>
       /// RemoteConnection Quick Start client example.
       /// Creates a Connection to a remote database and open it.
       /// </summary>
       /// <returns>The connection to the remote database</returns>
       /// <exception cref="AceQLException">If any Exception occurs.</exception>
       public static async Task<AceQLConnection> ConnectionBuilderAsync()
       {
           // Port number is the port number used to start the Web Server:
           string server = "https://www.aceql.com:9443/aceql";
           string database = "sampledb";

           string connectionString = $"Server={server}; Database={database}";

           // (username, password) for authentication on server side.
           // No authentication will be done for our Quick Start:
           string username = "MyUsername";
           char[] password = { 'M', 'y', 'S', 'e', 'c', 'r', 'e', 't' };

           AceQLConnection connection = new AceQLConnection(connectionString)
           {
               Credential = new AceQLCredential(username, password)
           };

           // Opens the connection with the remote database.
           // On the server side, a JDBC connection is extracted from the connection 
           // pool created by the server at startup. The connection will remain ours 
           // during the session.
           await connection.OpenAsync();

           return connection;
       }
   ```

5. Build and run. It will insert a new `customer` and a new `orderlog`:

   ```C#
       /// <summary>
       /// Example of 2 INSERT in the same transaction.
       /// </summary>
       /// <param name="customerId">The customer ID.</param>
       /// <param name="itemId">the item ID.</param>
       /// <exception cref="AceQLException">If any Exception occurs.</exception>
       public async Task InsertCustomerAndOrderLogAsync(int customerId, int itemId)
       {
           // Create a transaction
           AceQLTransaction transaction = await connection.BeginTransactionAsync();

           string sql = "insert into customer values " + "" +
               "(@parm1, @parm2, @parm3, @parm4, @parm5, @parm6, @parm7, @parm8)";

           AceQLCommand command = new AceQLCommand(sql, connection);
           try
           {
               command.Parameters.AddWithValue("@parm1", customerId);
               command.Parameters.AddWithValue("@parm2", "Sir");
               command.Parameters.AddWithValue("@parm3", "Doe");
               command.Parameters.AddWithValue("@parm4", "John");
               // Alternate syntax
               command.Parameters.Add(new AceQLParameter("@parm5", "1 Madison Ave"));
               command.Parameters.AddWithValue("@parm6", "New York");
               command.Parameters.AddWithValue("@parm7", "NY 10010");
               command.Parameters.Add(new AceQLParameter("@parm8", AceQLNullType.VARCHAR));

               await command.ExecuteNonQueryAsync();

               sql = "insert into orderlog values " +
                           "(@customer_id, @item_id, @description, " +
                               "@item_cost, @date_placed, @date_shipped, " +
                               "@jpeg_image, @is_delivered, @quantity)";

               command = new AceQLCommand(sql, connection);

               command.Parameters.AddWithValue("@customer_id", customerId);
               command.Parameters.AddWithValue("@item_id", itemId);
               command.Parameters.AddWithValue("@description", "Item Description");
               command.Parameters.AddWithValue("@item_cost", 99D);
               command.Parameters.AddWithValue("@date_placed", DateTime.Now);
               command.Parameters.AddWithValue("@date_shipped", DateTime.Now);
               // No blob in our Quick start
               command.Parameters.Add(new AceQLParameter("@jpeg_image",
                   AceQLNullType.BLOB));
               command.Parameters.AddWithValue("@is_delivered", 1);
               command.Parameters.AddWithValue("@quantity", 1);

               await command.ExecuteNonQueryAsync();
               await transaction.CommitAsync();
           }
           catch (Exception e)
           {
               await transaction.RollbackAsync();
               throw e;
           }
       }
   ```



The `SelectCustomerAndOrderLogAsync()` method of [MyRemoteConnection.cs](https://www.aceql.com/rest/soft_csharp/6.0/src/MyRemoteConnection.cs) displays back the inserted values.

From now on, you can read the [C# Client SDK User Guide](https://www.aceql.com/DocDownload?doc=https://github.com/kawansoft/AceQL.Client2/blob/master/README.md).

## Java Client SDK / JDBC Driver

1. Maven:

   ```xml
   <groupId>com.aceql</groupId>
   <artifactId>aceql-http-client-jdbc-driver</artifactId>
   <version>6.0</version>
   ```

2. If you don’t use Maven: the [aceql-http-client-jdbc-driver-all-6.0.jar](https://www.aceql.com/rest/soft_java_client/6.0/download/aceql-http-client-jdbc-driver-all-6.0.jar) file contains the SDK with all dependencies.
3. Create an `com.aceql.jdbc.commons.examples` package in your IDE.

4. Download this Java source file: [MyRemoteConnection.java](https://www.aceql.com/rest/soft_java_client/6.0/src/MyRemoteConnection.java). Then insert it in the package. 

5. The  connection to the remote database is  created  using loading the `AceQLDriver` class  and passing the URL of the AceQL Servlet Manager of your configuration:

   ```java
       /**
        * Remote Connection Quick Start client example. Loads the AceQL Client JDBC Driver and 
        * Creates a Connection to a remote database.
        *
        * @return the Connection to the remote database
        * @throws SQLException
        *             if a database access error occurs
        * @throws ClassNotFoundException 
        */
   
       public static Connection remoteConnectionBuilder() throws SQLException, ClassNotFoundException {
   
           // The URL of the AceQL Server servlet
           // Port number is the port number used to start the Web Server:
           String url = "http://localhost:9090/aceql";
   
           // The remote database to use:
           String database = "sampledb";
   
           // (username, password) for authentication on server side.
           // No authentication will be done for our Quick Start:
           String username = "MyUsername";
           String password = "MySecret";
   
           // Attempts to establish a connection to the remote database:
           DriverManager.registerDriver(new AceQLDriver());
           Class.forName(AceQLDriver.class.getName());
   
           Properties info = new Properties();
           info.put("user", username);
           info.put("password", password);
           info.put("database", database);
   
           Connection connection = DriverManager.getConnection(url, info);
           return connection;
       }
   ```

6. Compile and run from your IDE the `MyRemoteConnection` class. It will insert a new `customer` and a new `orderlog`:

   ```java
   /** 
    * Example of 2 INSERT in the same transaction.
    * 
    * @param customerId
    *            the Customer Id
    * @param itemId
    *            the Item Id
    * 
    * @throws SQLException
    */
   public void insertCustomerAndOrderLog(int customerId, int itemId)
       throws SQLException {

     connection.setAutoCommit(false);

     try {
         // Create a Customer
         String sql = "INSERT INTO CUSTOMER VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";
         PreparedStatement prepStatement = connection.prepareStatement(sql);

         int i = 1;
         prepStatement.setInt(i++, customerId);
         prepStatement.setString(i++, "Sir");
         prepStatement.setString(i++, "Doe");
         prepStatement.setString(i++, "John");
         prepStatement.setString(i++, "1 Madison Ave");
         prepStatement.setString(i++, "New York");
         prepStatement.setString(i++, "NY 10010");
         prepStatement.setString(i++, null);

         prepStatement.executeUpdate();
         prepStatement.close();

         // Uncomment following line to test transaction behavior
         // if (true) throw new SQLException("Exception thrown.");

         // Create an Order for this Customer
         sql = "INSERT INTO ORDERLOG VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

         // Create a new Prepared Statement
         prepStatement = connection.prepareStatement(sql);

         i = 1;
         long now = new java.util.Date().getTime();

         prepStatement.setInt(i++, customerId);
         prepStatement.setInt(i++, itemId);
         prepStatement.setString(i++, "Item Description");
         prepStatement.setBigDecimal(i++, new BigDecimal("99.99"));
         prepStatement.setDate(i++, new java.sql.Date(now));
         prepStatement.setTimestamp(i++, new Timestamp(now));
         // No Blob in this example.
         prepStatement.setBinaryStream(i++, null);
         prepStatement.setInt(i++, 1);
         prepStatement.setInt(i++, 2);

         prepStatement.executeUpdate();
         prepStatement.close();

         System.out.println("Insert done!");
     } catch (SQLException e) {
         e.printStackTrace();
         connection.rollback();
         throw e;
     } finally {
         connection.setAutoCommit(true);
     }
   }
   ```

The `selectCustomerAndOrderLog` method of [MyRemoteConnection.java](https://www.aceql.com/rest/soft_java_client/6.0/src/MyRemoteConnection.java) displays back the inserted values.

From now on, you can read the AceQL Client JDBC Driver [User Guide](https://www.aceql.com/DocDownload?doc=https://github.com/kawansoft/aceql-http-client-sdk/blob/master/README.md) or run through the [Javadoc](https://www.aceql.com/rest/soft_java_client/6.0/javadoc).

## Python Client SDK

The aceql module supports Python 3.4–3.8. 

1. Create a new project with your favorite IDE. 
2. Install aceql module:

```bash
$ pip install aceql
```

3.    Download this Python class: [my_remote_connection.py](https://www.aceql.com/rest/soft/7.0/src/my_remote_connection.py)

4.    The  connection to the remote database is  created  using a [DB API 2.0](https://www.python.org/dev/peps/pep-0249/)  `Connection` class and passing the URL of the AceQL Servlet Manager of your configuration:

      ```python
          @staticmethod
          def remote_connection_builder():
              """Remote Connection Quick Start client example.

              Creates a Connection to a remote database.
              """

              # The URL of the AceQL Server servlet
              # Port number is the port number used to start the Web Server:
              url = "http://localhost:9090/aceql"

              # The remote database to use:
              database = "sampledb"

              # (username, password) for authentication on server side.
              # No authentication will be done for our Quick Start:
              username = "MyUsername"
              password = "myPassword"

              # Attempt to establish a connection to the remote database.
              # On the server side, a JDBC connection is extracted from the
              # connection pool created by the server at startup.
              # The connection will remain ours during the session.
              connection = aceql.connect(url, database, username, password)
              return connection
      ```


5.    Run from your IDE the `my_remote_connection.py` module.  It will insert a new `customer` and a new `orderlog`: 

      ```python
          def insert_customer_and_order_log(self, customer_id, item_id):
              """Example of 2 INSERT in the same transaction
                 using a customer id and an item id.
              """

              self.connection.set_auto_commit(False)
              cursor = self.connection.cursor()

              try:
                  # Create a Customer
                  sql = "insert into customer values (?, ?, ?, ?, ?, ?, ?, ?)"
                  params = (customer_id, 'Sir', 'John', 'Smith', '1 Madison Ave',
                            'New York', 'NY 10010', '+1 212-586-7000')
                  cursor.execute(sql, params)

                  # Create an Order for this Customer
                  sql = "insert into orderlog values ( ?, ?, ?, ?, ?, ?, ?, ?, ? )"

                  the_datetime = datetime.now()
                  the_date = the_datetime.date()

                  # (None, SqlNullType.BLOB) means to set the jpeg_image BLOB
                  # column to NULL on server:
                  params = (customer_id, item_id, "Item Description", 9999,
                            the_date, the_datetime, (None, SqlNullType.BLOB), 1, 2)
                  cursor.execute(sql, params)

                  self.connection.commit()
              except Error as e:
                  print(e)
                  self.connection.rollback()
                  raise e
              finally:
                  self.connection.set_auto_commit(True)
                  cursor.close()
      ```



The `select_customer_and_orderlog` method of [my_remote_connection.py](https://www.aceql.com/rest/soft/7.0/src/my_remote_connection.py) displays back the inserted values.

From now on, you can read the [Python Client SDK User Guide](https://www.aceql.com/DocDownload?doc=https://github.com/kawansoft/aceql-http-client-python/blob/master/README.md).

# From now on…

You can read the [Server User Guide](https://www.aceql.com/DocDownload?doc=https://github.com/kawansoft/aceql-http/blob/master/README.md). You will learn:

- How to create a Connection Pool.
- How to create a strong authentication on the server for your legitimate users.
- How to secure accesses to your SQL databases.



------




