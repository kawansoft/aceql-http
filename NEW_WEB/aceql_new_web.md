# *TITRE* 

**A framework to connect securely to your SQL databases from anywhere using HTTP**

AceQL HTTP allows to connect from all devices (mobile, tablets, PCs) to your remote or Cloud SQL databases with SQL.
Just develop regular SQL calls with your usual C#, Java, or Python IDE. The software takes care of all protocol, communications and security aspects.

![https://www.aceql.com/img/AceQL-Schema-min.jpg](https://www.aceql.com/img/AceQL-Schema-min.jpg)

**Why AceQL?** 

**Case 1** : **Simplify your Desktop and Mobile App developments**

Usual access of remote SQL databases data from a desktop app or a mobile requires the writing, testing and deploying of Web Services. 

It also requires managing the server code, the client code, and finally the client-server dialog, including error detection.

With AceQL HTTP you only code the client part to access the remote SQL data, there is no server code nor duplex communication between the client code and a server Web Service.

The framework takes care of all the complex aspects of the client-server dialog (communications, data parsing, error detection).

**Case 2: Give your users easy and secured access to your SQL DBs from their favorite database tool** 

No need to setup VPN for your users to access the SQL databases from their favorite. Because AceQL uses only HTTP protocol, just give your users the address of the database, the username and password and they are ready.  Of course, all can be highly secured.

________________________________________



**Use your favorite language from client side with unmodified SQL syntax**

The C# / Xamarin Client SDK allows SQL calls to be encoded with a standard C# SQL API: the SDK C# SQL syntax is identical to the [Microsoft SQL Server C# API.](https://docs.microsoft.com/en-us/dotnet/api/system.data.sqlclient?redirectedfrom=MSDN&view=net-5.0)

The Java Client JDBC Driver allows SQL calls to be encoded with standard unmodified JDBC syntax ([java.sql package interfaces)](https://www.aceql.com/rest/soft_java_client/6.0/javadoc/).

The Python Client SDK allows SQL calls to be encoded with standard unmodified DB-API 2.0 syntax ([PEP 249 -- DB-API 2.0 interface](https://www.python.org/dev/peps/pep-0249/)).

Want to use another language? Just wrap your SQL calls and parse the query results using our [universal Rest APIs](https://github.com/kawansoft/aceql-http/blob/master/aceql-http-user-guide-api.md). 

Or just tell us your need for an implementation with your preferred X / Y / Z language, we will integrate the request in our road map.

_____________________________________________________



**Use advanced SQL features directly  in your Desktop and Mobile Apps**

AceQL does not limit to simple SELECT / INSERT / UPDATE / DELETE calls.

You can use the advanced options: 

- build transactions, 
- insert/upload or select/download BLOB contents as files. 
- create batches to quickly feed your remote databases, 
- access to your corporate existing stored procedures.



