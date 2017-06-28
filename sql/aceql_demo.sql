--
-- Kawansoft aceql_demo database
--
-- NOTES 
-- Change the type of the following columns according to your database:
-- ==> product_image.image      

CREATE TABLE user_login
(               
  username              varchar(255)    not null,     
  hash_password         varchar(40)     not null,
        PRIMARY KEY (username)
);

CREATE TABLE customer
(
    customer_id     integer     not null,
    customer_title  char(4)         null,
    fname           varchar(32)     null,
    lname           varchar(32) not null,
    addressline     varchar(64) not null,
    town            varchar(32) not null,
    zipcode         char(10)    not null,
    phone           varchar(32)     null,
        PRIMARY KEY(customer_id)
);

CREATE TABLE product_image
(
    product_id  integer     not null,
    name        varchar(64) not null,

    --  longblob for MySQL 
    --  blob for DB2 & HSQLDB & Informix & Oracle  & Terradata 
    --  oid For PostgreSQL / VarBinary(max) for SQL Server  
    --  Image for Sybase AES & Sybase SQL Anywhere
    image      	oid 	    null, 

        PRIMARY KEY(product_id)
);