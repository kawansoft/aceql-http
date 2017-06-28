CREATE TABLE user_login
(               
  username              varchar(255)    not null,     
  hash_password         varchar(40)     not null,
        PRIMARY KEY (username)
)


CREATE TABLE banned_usernames
(               
  username              varchar(255)    not null,     
        PRIMARY KEY (username)
)


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


CREATE TABLE orderlog
(
    customer_id     integer     not null,
    item_id         integer     not null,
    description     varchar(64) not null,
    item_cost	    numeric     null,
    date_placed     date        not null,
    date_shipped    timestamp   null, 
    jpeg_image      longbinary  null, 
    is_delivered    numeric     null,
    quantity        integer     not null,    
         PRIMARY KEY(customer_id, item_id)
);

CREATE TABLE product_image
(
    product_id  integer     not null,
    name        varchar(64) not null,

    --  longblob for MySQL 
    --  blob for DB2 & HSQLDB & Informix & Oracle  & Terradata 
    --  oid For PostgreSQL / VarBinary(max) for SQL Server  
    --  Image for Sybase AES & Sybase SQL Anywhere
    image      	longbinary  	  null, 

        PRIMARY KEY(product_id)
);

CREATE TABLE documentation
(
    item_id         integer     not null,
    item_doc        Memo        null,         
    
         PRIMARY KEY(item_id)
);

insert into user_login values ('login', '5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8')
