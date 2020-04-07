# AceQL HTTP v5.0   - April 7,  2020

# Server Installation and Configuration Guide  

<img src="https://www.aceql.com/favicon.png" alt="AceQL HTTP Icon"/> 



# Fundamentals

## Overview

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

- Configuration classes injected at runtime. These are server classes that ensure both security and configuration.

- The AceQL Client SDKs for [C#](https://github.com/kawansoft/AceQL.Client) ,  [Java](https://github.com/kawansoft/aceql-http-client-sdk) and [Python](https://github.com/kawansoft/aceql-http-client-python) that allow you to wrap AceQL HTTP API calls using fluent code: 

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

The AceQL Web Server embeds its own [Tomcat 8.5](http://tomcat.apache.org/tomcat-8.5-doc/) servlet container in order to run AceQL without any Java EE servlet container.

Note that AceQL can run inside any Java EE servlet container (see [Running AceQL HTTP in a Java EE servlet container)](#running-aceql-http-in-a-java-ee-servlet-container). 

**No Tomcat 8.5 expertise is required in order to configure and use the AceQL Web Server:**

- All Tomcat configuration values are optional: AceQL uses Tomcat 8.5 default values if no user configuration is done.
- You may only be required to read short portions of the Tomcat 8.5 user documentation for using SSL & Certificates.

The AceQL Web Server consists of one Java jar. It is started by calling a Java class on the command line. All configuration elements are defined in a Java `.properties` file, named **aceql-server.properties** file in this document.

All communication between the client and the server uses HTTP/ HTTPS protocols. . 

This User Guide covers:

-  **Standard Usage / Quickstart** : this part describes how to run through complete setup using only configuration files and CSV files.  (No programming, compiling, etc. is necessary.)

-  **Advanced Usage** : this part describes advanced setup and fine tuning. It includes powerful configuration and customization using injection code of  your own Java classes.

## Technical operating environment

The AceQL HTTP server side is entirely written in Java, and functions identically under Microsoft Windows, Linux, and all versions of UNIX that support Java 8+,Servlet 3.1+ and JDBC 4.0+.

The only required third party installation is a Java 8+.

The following environments are supported in this version:

| **Databases**                   |
| ------------------------------- |
| Actian Ingres 10+               |
| IBM DB2  9.7+                   |
| IBM  Informix 11.70+.           |
| MariaDB  10.0+                  |
| Microsoft  SQL Server 2008 R2+  |
| MS Access  2010+                |
| Oracle Database  11g Release 2+ |
| Oracle  MySQL 5.5+              |
| PostgreSQL  8.4.1+              |
| Sybase  ASE 15.7+               |
| Sybase  SQL Anywhere 12+        |
| Teradata  Database 13+          |

Notes:

- All these databases have been intensively tested with AceQL.
- The table designates the tested version. Prior versions *should* work correctly with their corresponding JDBC 4.0 driver.
- AceQL will support all     subsequent versions of each database.

# Download & Installation

## Linux / Unix Installation 

Open a terminal and download with Wget.

```bash
wget https://www.aceql.com/rest/soft/5.0/download/aceql-http-5.0.run
```

If you get a certificate error message, do one of the following:

1. If the problem is that a known root CA is missing and when you are using Ubuntu or Debian,  then you can solve the problem with this one line: `sudo apt-getinstall ca-certificates`. Then retry the Wget call.
2. Retry the Wget call with `--no-check-certificate` at end of command line. Then check the PGP signature of the downloaded file using the corresponding `.asc` signature file available on [download page](https://www.aceql.com/download) using the PGP hyperlink.

Make the file executable and then run it:

```bash
chmod+x aceql-http-5.0.run                                       
./aceql-http-5.0.run
```

This will create the `aceql-http-5.0` folder that you can move where you want.

The full path to the final `aceql-http-5.0` installation folder will be surnamed **ACEQL_HOME** in following text.

Example: if you run `aceql-http-5.0.run` from `/home/mike`, then software is installed in `/home/mike/aceql-http-5.0` which is the value of **ACEQL_HOME.**

### Update the PATH (Optional) 

Open a shell session and make sure `java` binary is in the PATH by typing 

`Java –version` on the command line. 

Add `java` to your PATH if the command does not display Java version. 

Add to your PATH the path to the bin directory of aceql-http-5.0 installation:

```bash
$ PATH=$PATH:/path/to/aceql-http-5.0/bin/
export PATH
```

### Testing server installation

Open a shell session and make sure java binary is in the PATH by typing

`Java –version` on the command line. 

Add java to your PATH if the command does not display Java version.

Call the `aceql-server` script to display the AceQL version:

```
 $ aceql-server -version
```

It will display a line with all version info, like:

```
AceQL HTTP Community v5.0 - 04-Apr-2020
```



## Windows Installation

[Download AceQL Windows Installer.](https://www.aceql.com/download)

Because the software installs and runs a Windows Service, you must be logged as a Windows Administrator to install AceQL.

Run the installer.

It will run AceQL at end of installation and display the Window:

<img src="https://www.aceql.com/rest/soft/5.0/img/aceql_windows_gui_home.png" alt="AceQ HTTP GUI Main Windows"/> 

**N.B:** Because of a bug in early versions of Java 9 on Windows, the interface will appear "ugly"  or "blurred" on Java 9 if you have increased Windows Screen Resolution Options to 125% or 150%. See https://goo.gl/PAVvrd for more info. Set back Windows Screen Resolution to 100% for clean display.

# Standard Usage / Quickstart 

## The AceQL Manager servlet

All HTTP commands sent by the client side are received by the AceQL Manager [servlet](http://en.wikipedia.org/wiki/Java_Servlet). The AceQL Manager servlet then: 

- Authenticates the client call
- Extracts a JDBC Connection from the connection pool 
- Analyzes the JDBC statement that was received
- Executes the statement if the  JDBC statement matches the rules defined by the SQL Firewall Managers (see below)
- Sends the result of the statement back to the client side

## The aceql-server.properties file

Most AceQL configuration is carried out by defining properties in the `aceql-server.propertie`  file, except the hostname and port, which are passed as start parameters.

The file is organized in Sections.  Only the first Section must be configured in order to start the AceQL Manager. The Sections 2 to 4 allow to secure Aceql and are optional . (The subsequent Sections are covered later in Advanced Usage):

1. **Tomcat JDBC Connection Pool Section**.
2. User Authentication Section.
3. SQL Firewall Managers Section.
4. SSL Configuration Section.

### Tomcat JDBC Connection Pool Section

This section allows you to define: 

1. The names of the databases to use.
2. The JDBC parameters used to build the embedded [The Tomcat 8.5 JDBC Connection Pool](http://tomcat.apache.org/tomcat-8.5-doc/jdbc-pool.html) for each database. 


The databases are defined with the databases property. If there are more than one database, separate each name with a comma:

```properties
# Database names separated by commas
databases = my_database1, my_database2 
```

Only the following four properties must be defined per database name if you want to use the Tomcat JDBC Connection Pool: 

| **Property**      | **Value**                                                    |
| ----------------- | ------------------------------------------------------------ |
| `driverClassName` | The fully qualified Java class name of the JDBC driver to be used. |
| `url`             | The  connection URL to be passed to our JDBC driver to establish a connection. |
| `username`        | The  connection username to be passed to our JDBC driver to establish a  connection. |
| `password`        | The  connection password to be passed to our JDBC driver to establish a  connection. |

Each property must be prefixed with the associated database name defined in databases property and a dot.

Example :

```properties
# Database names separated by commas
databases = my_database1, my_database2 

# Mandatory JDBC properties:
my_database1.driverClassName = org.postgresql.Driver
my_database1.url= jdbc:postgresql://localhost:5432/sampledb 
my_database1.username= user1  
my_database1.password= password1 

my_database2.driverClassName = oracle.jdbc.driver.OracleDriver
my_database2.url= jdbc:oracle:thin:my_database2@//localhost:1521/XE 
my_database2.username= user2 
my_database2.password= password2
```

You may add other properties supported by Tomcat JDBC Connection Pool,

as defined in [Common Attributes](http://tomcat.apache.org/tomcat-8.5-doc/jdbc-pool.html#Common_Attributes)  and in [Enhanced Attributes](http://tomcat.apache.org/tomcat-8.5-doc/jdbc-pool.html#Tomcat_JDBC_Enhanced_Attributes).

Note: It is not mandatory to use Tomcat JDBC Connection Pool. If you want to use another preferred Connection Pool system, just comment the `driverClassName` property. Implementing another Connection Pool system is described in [Database Configurators](#database-configurators).

### User Authentication Section 

This section allows to define how to authenticate a remote use client that connects to the AceQL Server.

Authentication is defined through the injection of  an "User Authenticator", a Java class that is injected at AceQL Server startup. It is a built-in or user-developed Java class that implements the `UserAuthenticator` interface built in AceQL.  

The `login` method of the class is called when a remote client first connects to the AceQL and pass it's credentials (username and password). If the `login` method returns `true`, user access is granted, otherwise the user access is denied.

AceQL provides 5 built-in (and ready to use without any coding) User Authenticators:

| User Authenticator Name       | Role                                                         | Parameters                                    |
| ----------------------------- | ------------------------------------------------------------ | --------------------------------------------- |
| `LdapUserAuthenticator`       | Authenticates the remote client (username, password) against a LDAP server. | URL of the LDAP server                        |
| `SshUserAuthenticator`        | Authenticates the remote client(username, password) against a SSH server. | IP or host & port of the SSH server           |
| `WebServiceUserAuthenticator` | Authenticates the remote client(username, password) against a Web service. (See below). | URL of the Web service  & connection timeout. |
| `WindowsUserAuthenticator`    | Authenticates the remote client(username, password) against the Windows server on which the AceQL server is running. | The Windows domain (optional).                |

Just select in the `aceql-server.properties` section the  `userAuthenticatorClassName` to use, and fill the required parameters.

All the rest will be done automatically once the AceQL server is started.

#### The WebServiceUserAuthenticator usage

AceQL allows to authenticate remote client users against a Web service that is  developed and deployed on you web infrastructure by your organization. This allows you to develop or use an existing a specific authentication program that is decoupled from AceQL. You can thus use whatever technology stack to authenticate remote client users, and wrap it in a Web service that is called by AceQL when a remote client user wants to authenticate.

The Web service must just implement these features:

- It must accept 2 POST parameters `username` and `password`.

 - It must return either:
    - The JSON string `{"status"="OK"}` if the authentication succeeds.
   - The JSON string `{"status"="FAIL"}` if the authentication fails.

### SQL Firewall Managers Section

The SQL Firewall Managers Section allows to define SQL firewall rulesets to use for each database.

The rulesets are defines through one or more "SQL Firewall Managers",  Java classes that are injected at AceQL Server startup. A SQL Firewall Manager It a built-in or user-developed Java class that implements the `SqlFirewallManager` interface built in AceQL.  

A `SqlFirewallManager` concrete implementation allows to: 

- Define if a client user has the right to call a `Statement.executeUpdate` (i.e. call a statement that updates the database).
- Define if a client user has the right to call a raw `Statement` that is not a `PreparedStatement`.
- Define if a client user has the right to call a the AceQL Metadata API.
- Define a specific piece of Java code to analyze the source code of the SQL statement before allowing or not it's execution.

Multiple `SqlFirewallManager` may be defined and chained. 

AceQL provides several built-in (and ready to use without any coding)  SQL Firewall Managers:

| SQL Firewall Manager Name   | Details                                                      |
| --------------------------- | ------------------------------------------------------------ |
| `CsvRulesManager`           | Manager that apply rules written in a CSV file. (See below). |
| `DenyDclManager`            | Manager that denies any DCL (Data Control Language) call.    |
| `DenyDdlManager`            | Manager that denies any DDL (Data Definition Language) call. |
| `DenyExecuteUpdateManager`  | Manager that denies any update of the database. (The database will be accessed in read only mode). |
| `DenyMetadataQueryManager`  | Manager that denies the use of the AceQL Metadata Query API. |
| `DenyStatementClassManager` | manager that denies any call of he raw Statement Java class. (Calling Statements without parameters is forbidden). |

Only the following property must be defined per database name if you want to add SQL Firewall Managers:

`sqlFirewallManagerClassNames`.

`SqlFirewallManager` may be chained in property value by separating class names by a comma.  
When `SqlFirewallManager` classes are chained, an AND condition is applied to all the `SqlFirewallManager` execution conditions in order to compute final allow.
For example, the `allowExecuteUpdate()` of each chained `SqlFirewallManager` instance must return true in order to allow updates of the database.

The following example defines two built in firewalls to chain for the `sampledb` database:
 - First `DenyDdlManager` will deny to pass DDL statements such as
    DROP, CREATE TABLE, etc.
 - Second`DenyExecuteUpdateManager` will deny write access to database,

```properties
sampledb.sqlFirewallManagerClassNames=\
    DenyDclManager,\
    DenyExecuteUpdateManager
```

After AceQL server restart, remote clients won't be allowed to execute DDL statements or to update the database.

#### The CsvRulesManagerSQL Firewall Manager

The `CsvRulesManagerSQL` manager allows to define detailed rules just using a CSV file.

It checks each SQL request against the content of a CSV File. The CSV file is loaded in memory at AceQL server startup. 

The name of the CSV file that will be used by a database is:  `<database>_rules_manager.csv`, where `<database>` is the name of the database declared in the `aceql.properties` files.
The file must be located in the same directory as the  `aceql.properties` file used when starting the AceQL server.

The CSV file contains the rules for accessing the tables, with semicolon for separator:  

- First line contains the element names:  

   `username;table;delete;insert;select;update;optional comments`

- Subsequent lines contain the rules, with the values for each element:  
  - `username`: AceQL username of the connected client.
  - `table`: the table name to access. Name must not include dots and prefixes.
  - `delete`: `true` if the username has the right to delete rows of the table, else `false`.
  - `insert`: `true` if the username has the right to insert rows in the table, else `false`.
  - `select`: `true` if the username has the right to select rows of the table, else `false`.
  - `update`: `true` if the username has the right to update rows of the table, else `false`.
  - Optional comments for the rule.


 Note that:  

- `public` value may be used for the `username` column and means any username. At execution time: if a rule with `public` returns true for a CSV column, the rule supersedes other declared rules declared for specific users for the same CSV column.  
- `all` value is allowed for `table` column and means any table. At execution time: If a rule with `all` returns true for a CSV column, the rule supersedes other specific rules declared for specific tables for the same CSV column. 

  Here is an example of a documented CSV File: [sampledb_rules_manager.csv](http://www.aceql.com/rest/soft/5.0/src/sampledb_rules_manager.csv).

### SSL Configuration Section

This section is optional. It allows you to configure the [Tomcat  HTTP Connector](http://tomcat.apache.org/tomcat-8.5-doc/config/http.html) in order to use SSL when calling AceQL Manager Servlet from the client side. 

It also allows you to define the SSL Certificate to be used.

Set the `SSLEnabled` property to true, in order to say that the HTTP Connector will accept SSL calls from client side.

Each property must be prefixed by `sslConnector`.

*Note: If `SSLEnabled` is set to `true`, AceQL HTTP Web server will accept only SSL connections, i.e. a non SSL call from client side with `http` scheme will fail.*

To define SSL HTTP Connector attribute values, refer to the Tomcat 8.5 [SSL Support](http://tomcat.apache.org/tomcat-8.5-doc/config/http.html#SSL_Support) documentation. 

The following properties are mandatory and must be defined:

| **Mandatory Property Name** | **Property Role**                             |
| --------------------------- | --------------------------------------------- |
| `sslConnector.scheme`       | Scheme to  use. Must be set to "https"        |
| `sslConnector.keystoreFile` | The file  containing the SSL/TLS certificates |
| `sslConnector.keystorePass` | The keystore  password                        |
| `sslConnector.keyPass`      | The certificate  password                     |

To create an SSL Certificate, refer to:

- Tomcat 8.5 [Prepare the Certificate Keystore.](http://tomcat.apache.org/tomcat-8.5-doc/ssl-howto.html#Prepare_the_Certificate_Keystore)
- Oracle [JDK Security Tools](http://docs.oracle.com/javase/7/docs/technotes/tools/#security).

## Starting/Stopping the AceQL Web Server from Linux/Unix

### Add your JDBC driver to the AceQL installation

Before starting the AceQL Web Server, drop you JDBC driver jar into

`ACEQL_HOME/lib-jdbc directory` or add it to the Java CLASSPATH.

### Starting the AceQL Web Server

Open a shell and type: 

```bash
$ aceql-server -start -host <hostname> -port <port number> -properties <file>
```

where:

- `-host <hostname>` hostname of the Web server

- `-port <port number>`  port number of the Web server. Defaults to 9090

- `-properties <file>`  properties file to use for this SQL Web server Session. Defaults to `ACEQL_HOME/conf/aceql-server.properties`.


The console will display the properties used and will end with this line if everything is OK (assuming you choose 9090 for port.)

```bash
[ACEQL HTTP START] AceQL HTTP Web Server OK. Running on port 9090
```

If configuration errors occur, they are displayed with the tag 

```
[ACEQL HTTP START FAILURE] - USER CONFIGURATION FAILURE]
```

### Examples

#### Starting the AceQL Web Server on port 9090

```bas
$ aceql-server -start -host localhost
```

The URL to use on the client side will be: `http://localhost:9090/aceql` 

assuming the AceQL Manager Servlet Section contains the following line: 

```properties
aceQLManagerServletCallName=aceql 
```

#### Starting the AceQL Web Server on port 9091

```bash
$ aceql-server -start -host www.acme.org -port 9091
```

The URL to use on the client side will be: `http://www.acme.org:9091/aceql` 

assuming the AceQL Manager Servlet Section contains the following line:

```properties
aceQLManagerServletCallName=aceql 
```

### Using SSL from the client side

Assuming you have enabled SSL and defined a Certificate in the 

`aceql-server.properties` file, the URL to use on the client side will be:

`https://www.acme.org:9091/aceql`  

### Stopping the AceQL Web Server

To stop a running instance of the AceQL Web Server:

```bash
$ aceql-server-stop -port <port number>
```

where:

`-port <port number>`  port number of the Web server. Defaults to 9090

### Linux: running the AceQL Web server as a service

The `aceqlhttp` wrapper allows to run AceQL program as a Linux service.

- Click [here](https://www.aceql.com/rest/soft/5.0/src/aceqlhttp.sh) to download `aceqlhttp.sh`
- Copy aceqlhttp.sh to `/etc/init.d/aceqlhttp` (requires root privilege). 
- `sudo chmod +x /etc/init.d/aceqlhttp`
- Then edit `/etc/init.d/aceqlhttp` and:
  - Modify JAVA_HOME to the path of you Java installation.
  - Modify ACEQL_HOME to the path of your AceQL installation.
  - Modify ACEQL_HOST and ACEQL_PORT with your hostname and port.
  - Modify CLASSPATH if you plan to use a Database Configurator (See [Database Configurators](#database-configurators)).

Then: 

- Test that it runs: `sudo service aceqlhttp start`
- Test that it stop: `sudo service aceqlhttp stop`
- Test that it restarts: `sudo service aceqlhttp restart`


Then check the content of the log file defined by `LOG_PATH_NAME` and which defaults to: `/var/log/aceqlhttp.out`.

## Starting/Stopping the AceQL WebServer from Windows

Server maybe started within the current JVM, or as a Windows Service.

If you wish to run the AceQL HTTP Server as a Windows Service, it is highly recommended to test your configuration by starting once the server in Standard Mode.

The running options are fully described in the user interface help.

# Advanced Usage

## Development Environment

Setting up a development environment will allow you to develop your own Java classes that will be injected at runtime.

Create a “Server” project and add the jars of the:

- `<installation-directory>\AceQL\lib-server subdirectory & <installation-directory>\AceQL\lib-jdbc` to your development CLASSPATH .

Or for Maven users:

```xml
<groupId>com.aceql</groupId>
<artifactId>aceql-http</artifactId>
<version>5.0</version>
```
## AceQL Servlet Name Configuration

Thie AceQL Manager servlet Section in the `aceql-server.proprties` file allows you to define the name of the AceQL SQL Manager Servlet to call from the client side. The default name is `aceql`. It is the name that will be used in the URL by client calls:

```properties
aceQLManagerServletCallName=aceql
```

## Advanced Connection Pool Management

You may define your own preferred connection pool implementation, instead of using the default Tomcat JDBC Connection Pool.

This is done through your own implementation of the [DatabaseConfigurator](http://www.aceql.com/rest/soft/5.0/javadoc/org/kawanfw/sql/api/server/DatabaseConfigurator.html) interface: overload the `DatabaseConfigurator.getConnection()` method in your concrete class implementation.

Your concrete implementations is passed to the AceQL as properties of the **Database Configurators Section** in the `aceql-server.properties` file, as described in the section:

- The  `databaseConfiguratorClassName` property lets you define your concrete implementation of `DatabaseConfigurator`.
- You `DatabaseConfigurator` classes must be added to the CLASSPATH before the start of the AceQL Server.

 Instances are loaded using a non-args constructor.

## Advanced Authentication Configuration

in order to give access access to remote client users to the AceQL server, you may develop entirely your own authentication mechanism. This is done through your own implementation of the [UserAuthenticator](https://www.aceql.com/rest/soft/5.0/javadoc/org/kawanfw/sql/api/server/auth/UserAuthenticator.html) interface: overload the login `UserAuthenticator.login()` in your concrete class implementation.

Your concrete implementations is passed to the AceQL as properties of the **User Authentication Section**  in the `aceql-server.properties` file, as described in the section

- The  **userAuthenticatorClassName** property lets you define your concrete implementation of `UserAuthentication`.
- You `UserAuthentication`classes must be added to the CLASSPATH before the start of the AceQL Server.

 Instances are loaded using a non-args constructor.

## Tomcat HTTP Connector Configuration 

[Tomcat  HTTP Connectors](http://tomcat.apache.org/tomcat-8.5-doc/config/http.html) allow fine tuning of Tomcat 8.5.

It is possible to define properties for a unique HTTP Connector that will either accept HTTP or secured HTTPS calls. The properties must be defined in the **HTTP Connector Attributes Section.**

This section is optional. If no value is defined, default Tomcat values will be used for the default HTTP Connector. 

You may define all [attributes](http://tomcat.apache.org/tomcat-8.5-doc/config/http.html#Attributes) defined in the [Tomcat  HTTP Connector](http://tomcat.apache.org/tomcat-8.5-doc/config/http.html) documentation, except SSL attributes that must be defined in the **SSL Configuration Section**.

Each property must be prefixed by `connector`.

```properties
# Example: Change default maxThreads from 200 to 300
connector.maxThreads=300  
```

## ThreadPoolExecutor Configuration

The AceQL Manager serlvet is executed in [asynchronous  mode](https://docs.oracle.com/javaee/7/tutorial/servlets012.htm). 

The **ThreadPoolExecutor Section** Allows to define the parameters of the `java.util.concurrent.ThreadPoolExecutor` instance used to execute all servlet requests in async mode.

The properties to set in the `aceql-server.properties` file are:

| Property             | Role                                                         |
| -------------------- | ------------------------------------------------------------ |
| `corePoolSize`       | The number of threads to keep in the pool, even if they are idle. |
| `maximumPoolSize`    | The maximum number of threads to allow in the pool.          |
| `unit`               | The time unit for the `keepAliveTime` argument.              |
| `keepAliveTime`      | When the number of threads is greater than the core, this is <br/>the maximum time that excess idle threads will wait for new tasks <br/>before terminating. |
| `workQueueClassName` | The `BlockingQueue` class to use in `ThreadPoolExecutor` constructor. |
| `capacity`           | The initial capacity of the `BloquingQueue<Runnable>` <br/>(0 for no or default initial capacity.) |

The properties are passed to the first  `ThreadPoolExecutor` constructor. See https://bit.ly/2QkMg5S.

See `ThreadPoolExecutor` class Javadoc for more info: https://bit.ly/2MBYQrd.

Default values should be appropriate for most AceQL configurations. 

## Session Management 

### SessionConfigurator interface

After server authentication succeeds (through the [UserAuthenticator.login()](https://www.aceql.com/rest/soft/5.0/javadoc/org/kawanfw/sql/api/server/auth/UserAuthenticator.html#login-java.lang.String-char:A-java.lang.String-java.lang.String-) method), the AceQL Manager builds an authentication session id that is sent back to the client and will be used by each succeeding client call in order to authenticate the calls. 

Session security is managed by implementing the [SessionConfigurator](https://www.aceql.com/rest/soft/5.0/javadoc/org/kawanfw/sql/api/server/session/SessionConfigurator.html) interface that defines how to generate and verify the session id for (username, database) sessions. 

Interface implementation allows you to:  

- Define how to generate a session id after client /login call
- Define the session’s lifetime
- Define how to verify that the stored session is valid and not expired

### Session management default implementation

The default mechanism that builds an authentication session id is coded in the class 

[DefaultSessionConfigurator](https://www.aceql.com/rest/soft/5.0/javadoc/org/kawanfw/sql/api/server/session/DefaultSessionConfigurator.html): 

- Session ids are generated using a `SecureRandom` with the [SessionIdentifierGenerator](http://www.aceql.com/rest/soft/5.0/javadoc/org/kawanfw/sql/api/server/session/SessionIdentifierGenerator.html) class.
- Session info (username, database) and session date/time creation are stored in a `HashMap`, whose key is the session id.
- Session id is sent by client side at each  API call.  AceQL verifies that the `HashMap`  contains the username and that the session is not expired to grant access to the API execution.

Benefits of this implementation are: 

- Session ids are short and generate less HTTP traffic.
- Because session ids are short, they are  easy to use “manually” (with cURL, etc.)

The disadvantage is that session information is stored on the server side.

### Session management using JWT

Session management using JWT is coded in [JwtSessionConfigurator](https://www.aceql.com/rest/soft/5.0/javadoc/org/kawanfw/sql/api/server/session/JwtSessionConfigurator.html).

Session management is done using self-contained JWT (JSON Web Token). 

See <https://jwt.io> for more information on JWT. 

A benefit of JWT is that no session information is stored on the server and that it allows full stateless mode.

A disadvantage of JWT is that the tokens are much longer and thus generate more http traffic and are less convenient to use "manually" (with cURL, etc.). 

#### Activating JwtSessionConfigurator 

Edit the `aceql-server.properties` file and uncomment the two lines:

```properties
sessionConfiguratorClassName=JwtSessionConfigurator
jwtSessionConfiguratorSecret=changeit
```

Change the `jwtSessionConfiguratorSecret` property change it value with your own secret value.

Restart the AceQL Web Server for activation. 

### Creating your own session management 

If you want to create your session management using your own session id generation and security rules, you can implement  the [SessionConfigurator](https://www.aceql.com/rest/soft/5.0/javadoc/org/kawanfw/sql/api/server/session/SessionConfigurator.html) in your own class, and then: 

Add your class in the CLASSPATH.

Add you class name in the `SessionConfigurator` section in your `aceql-server.properties` file: 

```properties
sessionConfiguratorClassName=com.acme.MySessionConfigurator
```

Restart the AceQL Web Server for activation. 

## Advanced Firewall Configuration

AceQL provides several built-in and ready to use SQL Firewall Managers, as described earlier in the **Firewall Configuration** chapter. But you may plug-in your own implementation or third party SQL firewalling tools. 

The [SqlFirewallManager](http://www.aceql.com/rest/soft/4.1/javadoc/org/kawanfw/sql/api/server/firewall/SqlFirewallManager.html) interface allows you to code your own firewall rulesets.

After coding you own `SqlFirewallManager` implementation just declare the full class name in the `sqlFirewallManagerClassNames` property. Note that SQL Firewall Manager may be chained: you may declare several classes.

The following example defines two firewalls to chain for the `sampledb` database:

```properties
sampledb.sqlFirewallManagerClassNames=\
    com.mycompany.firewall.MySqlFirewallManager1\
    com.mycompany.firewall.MySqlFirewallManager2
```


