# AceQL HTTP v5.0   - March 20,  2020

# Server Installation and Configuration Guide  

<img src="https://www.aceql.com/favicon.png" alt="AceQL HTTP Icon"/> 


   * [Fundamentals](#fundamentals)
      * [Overview](#overview)
      * [Technical operating environment](#technical-operating-environment)
   * [Download &amp; Installation](#download--installation)
      * [Linux / Unix Installation](#linux--unix-installation)
         * [Update the PATH (Optional)](#update-the-path-optional)
         * [Testing server installation](#testing-server-installation)
      * [Windows Installation](#windows-installation)
   * [Server side configuration](#server-side-configuration)
      * [The AceQL Manager servlet](#the-aceql-manager-servlet)
      * [The aceql-server.properties file](#the-aceql-serverproperties-file)
         * [AceQL Manager servlet Section](#aceql-manager-servlet-section)
         * [Tomcat JDBC Connection Pool Section](#tomcat-jdbc-connection-pool-section)
         * [Database Configurators Section](#database-configurators-section)
         * [Default Tomcat HTTP Connector Sections (Standalone server only)](#default-tomcat-http-connector-sections-standalone-server-only)
            * [Default Tomcat HTTP Connector Section - Base attributes](#default-tomcat-http-connector-section---base-attributes)
            * [Default Tomcat HTTP Connector Section - SSL Attributes](#default-tomcat-http-connector-section---ssl-attributes)
         * [Sample aceql-server.properties file](#sample-aceql-serverproperties-file)
      * [Starting/Stopping the AceQL Web Server from Linux/Unix](#startingstopping-the-aceql-web-server-from-linuxunix)
         * [Add your JDBC driver to the AceQL installation](#add-your-jdbc-driver-to-the-aceql-installation)
         * [Starting the AceQL Web Server](#starting-the-aceql-web-server)
         * [Examples](#examples)
            * [Starting the AceQL Web Server on port 9090](#starting-the-aceql-web-server-on-port-9090)
            * [Starting the AceQL Web Server on port 9091](#starting-the-aceql-web-server-on-port-9091)
         * [Using SSL from the client side](#using-ssl-from-the-client-side)
         * [Stopping the AceQL Web Server](#stopping-the-aceql-web-server)
         * [Linux: running the AceQL Web server as a service](#linux-running-the-aceql-web-server-as-a-service)
      * [Starting/Stopping the AceQL WebServer from Windows](#startingstopping-the-aceql-webserver-from-windows)
         * [Running the AceQL Web Server without Windows Desktop](#running-the-aceql-web-server-without-windows-desktop)
      * [Starting/Stopping the AceQL WebServer from a Java program](#startingstopping-the-aceql-webserver-from-a-java-program)
      * [Running AceQL HTTP in a Java EE servlet container](#running-aceql-http-in-a-java-ee-servlet-container)
         * [Installation](#installation)
         * [AceQL servlet configuration in web.xml](#aceql-servlet-configuration-in-webxml)
         * [Testing the servlet configuration](#testing-the-servlet-configuration)
      * [Database Configurators](#database-configurators)
         * [Development Environment](#development-environment)
         * [Database Configurator interface](#database-configurator-interface)
         * [Passing concrete DatabaseConfigurator classes](#passing-concrete-databaseconfigurator-classes)
      * [Coding Database Configurators](#coding-database-configurators)
         * [Extracting a Connection from your connection pool system](#extracting-a-connection-from-your-connection-pool-system)
         * [Login method - authenticating the client username and password](#login-method---authenticating-the-client-username-and-password)
      * [Coding SQL Firewall Rulesets](#coding-sql-firewall-rulesets)
         * [The SqlFirewallManager interface SQL security methods](#the-sqlfirewallmanager-interface-sql-security-methods)
         * [Passing concrete SqlFirewallManager classes](#passing-concrete-sqlfirewallmanager-classes)
         * [Chaining SqlFirewallManager  classes](#chaining-sqlfirewallmanager--classes)
   * [Session management and security](#session-management-and-security)
      * [SessionConfigurator interface](#sessionconfigurator-interface)
      * [Session management default implementation](#session-management-default-implementation)
      * [Session management using JWT](#session-management-using-jwt)
         * [Activating JwtSessionConfigurator](#activating-jwtsessionconfigurator)
         * [Creating your own session management](#creating-your-own-session-management)
      * [Interacting with the JDBC Pool at runtime](#interacting-with-the-jdbc-pool-at-runtime)
   * [State management / Stateful Mode](#state-management--stateful-mode)
   * [AceQL internals](#aceql-internals)
      * [Data transport](#data-transport)
         * [Transport format](#transport-format)
         * [Content streaming and memory management](#content-streaming-and-memory-management)
      * [Managing temporary files](#managing-temporary-files)




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

-  **Standard Usage / Quickstart** : this part describes how to run through complete setup using only configuration files and CSV files.  

- **Advanced Usage** : this part describes advanced setup and fine tuning. It includes powerful configuration and customization using injection code of  your own Java classes.

  

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
AceQL HTTP Community v5.0 - 19-March-2020
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

The file is organized in Sections. We will cover for Only the first Section must be configured in order to start the AceQL Manager. The Sections 2 to 4 allow to secure Aceql and are optional . (The subsequent Sections are covered later in Advanced Usage)

1. **Tomcat JDBC Connection Pool Section**
2. User Authentication Section 
3. SQL Firewall Managers Section
4. SSL Section.

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
| `WindowsUserAuthenticator`    | Authenticates the remote client(username, password) against the Windows server on which the AceQL server is running. | The Windows domain                            |

Just select in the `aceql-server.properties` section the  `userAuthenticatorClassName` to use, and fill the required parameters.

All the rest will be done automatically once the AceQL server is started.

#### The WebServiceUserAuthenticator usage

This User Authenticator is a Web service that will be called by AceQL: Authentication Web Service.

The Authentication Web Service must be developed and deployed by your organization. 
It must accept 2 POST parameters `username` and `password` and must return either:

 - The JSON string `{"status"="OK"}` if the authentication succeeds.
- The JSON string `{"status"="FAIL"}` if the authentication fails.

### SQL Firewall Managers Section

The SQL Firewall Managers Section allows to define SQL firewall rulesets to use for each database.

The rulesets are defines through a "SQL Firewall Manager",  Java classes that are injected at AceQL Server startup. It is a built-in or user-developed Java class that implements the `SqlFirewallManager` interface built in AceQL.  

A `SqlFirewallManager` concrete implementation allows to: 

- Define if a client user has the right to call a `Statement.executeUpdate` (i.e. call a statement that updates the database).
- Define if a client user has the right to call a raw `Statement` that is not a `PreparedStatement`.
- Define if a client user has the right to call a the AceQL Metadata API.
- Define a specific piece of Java code to analyze the source code of the SQL statement before allowing or not it's execution.

Multiple `SqlFirewallManager` may be defined and chained. 

AceQL provides 5 built-in (and ready to use without any coding)  SQL Firewall Managers:

AceQL provides 5 built-in (and ready to use without any coding) User Authenticators:

| SQL Firewall Manager Name     | Details                                                      |
| ----------------------------- | ------------------------------------------------------------ |
| CsvRulesManager               | Authenticates the remote client (username, password) against a LDAP server. |
| `SshUserAuthenticator`        | Authenticates the remote client(username, password) against a SSH server. |
| `WebServiceUserAuthenticator` | Authenticates the remote client(username, password) against a Web service. (See below). |
| `WindowsUserAuthenticator`    | Authenticates the remote client(username, password) against the Windows server on which the AceQL server is running. |

Just select in the `aceql-server.properties` section the  stack `userAuthenticatorClassName` to use.









------

