**A framework to connect securely to your SQL databases from anywhere using HTTP**

AceQL HTTP allows to connect from all devices (mobile, tablets, PCs) to your remote or Cloud SQL databases with SQL.
Just develop regular SQL calls with your usual C#, Java, or Python IDE. The software takes care of all protocol, communications and security aspects.

![https://www.aceql.com/img/AceQL-Schema-min.jpg](https://www.aceql.com/img/AceQL-Schema-min.jpg)

**Why AceQL?**

**Simplify your Desktop and Mobile App developments**...

Usual access of remote SQL databases data from a desktop app or a mobile requires the writing, testing and deploying of Web Services. 

It also requires managing the server code, the client code, and finally the client-server dialog, including error detection.

With AceQL HTTP you only code the client part to access the remote SQL data, there is no server code nor duplex communication between the client code and a server Web Service.

The framework takes care of all the complex aspects of the client-server dialog (communications, data parsing, error detection).

...**And Give your users easy and secured access to your SQL DBs from their favorite database tool** 

No need to setup VPN for your users to access their SQL databases from their favorite. No need to configurate the firewall, just allow regular HTTPS flow. Because AceQL uses only HTTP protocol, just give your users the address of the database, the username and password and they are ready.  Of course, all this can be [highly secured](https://www.aceql.com/sql-over-http-database-security.html).

________________________________________



**Use C#, Java or Python from client side with unmodified SQL syntax**

The C# / Xamarin Client SDK allows SQL calls to be encoded with a standard C# SQL API: the SDK C# SQL syntax is identical to the [Microsoft SQL Server C# API.](https://docs.microsoft.com/en-us/dotnet/api/system.data.sqlclient?redirectedfrom=MSDN&view=net-5.0)

The Java Client JDBC Driver allows SQL calls to be encoded with standard unmodified JDBC syntax ([java.sql package interfaces)](https://www.aceql.com/rest/soft_java_client/6.0/javadoc/).

The Python Client SDK allows SQL calls to be encoded with standard unmodified DB-API 2.0 syntax ([PEP 249 -- DB-API 2.0 interface](https://www.python.org/dev/peps/pep-0249/)).



![](I:\_dev_awake\aceql-http-main\aceql-http\NEW_WEB\new_web_images\logo-csharp.png)

![](I:\_dev_awake\aceql-http-main\aceql-http\NEW_WEB\new_web_images\PikPng.com_java-logo-transparent-png_1469146.png)

![](I:\_dev_awake\aceql-http-main\aceql-http\NEW_WEB\new_web_images\PikPng.com_python-logo-png_2301371.png)

**Use any language from any platform OS**

Want to use another language instead of C#, Java or Python  ? Just wrap your SQL calls and parse the query results using our [universal Rest APIs](https://github.com/kawansoft/aceql-http/blob/master/aceql-http-user-guide-api.md). 

Or [just tell us your need for an implementation with your favorite X / Y / Z language](mailto:contact@kawansoft.com), we will integrate the request in our road map.

_____________________________________________________



**Utilisez des fonctionnalités SQL avancées directement dans le code de vos applications de bureau et mobiles**

AceQL ne se limite pas aux simples appels SELECT / INSERT / UPDATE / DELETE. 

Vous pouvez utiliser les options avancées de SQL :

- créer des transactions en passant une série de commandes en autocommit off,
- Insérer des BLOBs ou lire des Blobs.
- Programmer des batchs  pour alimenter rapidement vos bases de données distantes, 
- Appeler toutes les procédures stockées existantes de votre entreprise ou organisation.

___________________

**Développeurs C# et Xamarin : Codez avec une syntaxe Microsoft SQL Server vos apps Desktop & Mobile**

The C# AceQL Client SDK usedMicrosoft SQL Server like syntax.
SDK class names are the equivalent of SQL Server [System.Data.SqlClient](https://msdn.microsoft.com/en-us/library/system.data.sqlclient.aspx) namespace.
They share the same suffix name for the classes, and the same method names.

```C#
string server = "https://www.acme.com:9443/aceql";
string database = "sampledb";

string connectionString = $"Server={server}; Database={database}";
string username = "MyUsername";
char[] password = { 'M', 'y', 'S', 'e', 'c', 'r', 'e', 't' };

AceQLConnection connection = new AceQLConnection(connectionString)
{
    Credential = new AceQLCredential(username, password)
};

// Attempt to establish a connection to the remote SQL database:
await connection.OpenAsync();
Console.WriteLine("Successfully connected to database " + database + "!");
```

The C# Client SDK is packaged as a .Net Standard 2.0 Library to use in your Xamarin projects.
There is no adaptation per target required. Write a unique and shared C# code and make it run on all major desktop & mobile operating systems:

- Android

- iOS

- macOS

- Windows Desktop

  

**Développeurs Java : Intégrez facilement le Driver JDBC dans vos apps Desktop & Mobile**

Le Driver JDBC AceQL vous permet de développer votre code SQL/JDBC sans aucune apprentissage ni adaptation. Les principales classes et méthodes JDBC sont supportées telles quelles. Il est possible d'intégrer le Driver à vos apps sans modifier leur code source.

Connection à une database distante :

```java
// The URL of the AceQL Server servlet
String url = "https://www.acme.com/aceql";

// Attempts to establish a connection to the remote database:
DriverManager.registerDriver(new AceQLDriver());
Class.forName(AceQLDriver.class.getName());

// User & Password are hardcoded to simplify our sample...
Properties info = new Properties();
info.put("user", "MyUsername");   
info.put("password", "MySecret"); 
info.put("database", "sampledb"); // The remote database to use

Connection connection = DriverManager.getConnection(url, info);
System.out.println("Successfully connected to database " + database + "!");
```

SELECT sur la base distante :

```java
String sql = "SELECT CUSTOMER_ID, FNAME, LNAME FROM CUSTOMER WHERE CUSTOMER_ID = ?";
PreparedStatement prepStatement = connection.prepareStatement(sql);
prepStatement.setInt(1, customerId);

ResultSet rs = prepStatement.executeQuery();
while (rs.next()) {
    System.out.println();
    System.out.println("customer_id: " + rs.getInt("customer_id"));
    System.out.println("fname      : " + rs.getString("fname"));
    System.out.println("lname      : " + rs.getString("lname"));
}
```

Le Driver JDBC AceQL Professionnel Edition s'intègre directement dans votre database visualizer préféré, avec gestion des metadata. 





 



