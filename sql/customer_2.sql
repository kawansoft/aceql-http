CREATE TABLE customer_2
(
    customer_id     integer     not null,
    customer_title  char(4)         null,
    fname           varchar(32)     null,
    lname           varchar(32) not null,
    addressline     varchar(64) not null,
    town            varchar(32) not null,
    zipcode         char(10)    not null,
    phone           varchar(32)     null,
    row_2           varchar(32)     null,
    row_count       varchar(32)     null,
        PRIMARY KEY(customer_id)
);