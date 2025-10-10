## Encrypting Properties in the aceql-server.properties file

In order to protect configuration passwords and other confidential values from eavesdropping, each property value may be replaced by an encrypted value in the `aceql-server.properties` file.

The encrypted values are generated using the [PropertiesEncryptor](https://docs.aceql.com/rest/soft/12.5/javadoc/org/kawanfw/sql/api/server/auth/crypto/PropertiesEncryptor.html) class which allows you:

1. To choose a secret password that will be used for encrypting each selected property value.
2. To encrypt each selected value.  

In order for the AceQL Server to decrypt the properties at runtime, the secret password must be returned by the `getPassword()` of a concrete implementation of the [PropertiesPasswordManager](https://docs.aceql.com/rest/soft/12.5/javadoc/org/kawanfw/sql/api/server/auth/crypto/PropertiesPasswordManager.html) interface. A default implementation is provided: [DefaultPropertiesPasswordManager](https://docs.aceql.com/rest/soft/12.5/javadoc/org/kawanfw/sql/api/server/auth/crypto/DefaultPropertiesPasswordManager.html). 

The `PropertiesPasswordManager` concrete class name must then be defined with the `propertiesPasswordManagerClassName` property.  See the `Properties Password Manager Section` of the `aceql-server.properties` file.

### Running the PropertiesEncryptor class 

In order to run the `PropertiesEncryptor` class:

- Open a command line on Windows or Linux/Bash.
- Windows: 
  - `cd <installation-directory>/AceQL/bin>`
  - run `properties-encryptor.bat`.

- Linux/Unix:
  - `cd <installation-directory>/bin>`
  - run `properties-encryptor` Bash.

- Follow the instructions to create the password and encrypt property values.