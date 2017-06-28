
--
-- HSQLDB
--

create table REGIONS
   (REGION_NAME varchar(32) NOT NULL,
   ZIPS varchar(32) ARRAY[10] NOT NULL,
   PRIMARY KEY (REGION_NAME));

insert into REGIONS values(
    'Northwest',
    ARRAY['93101', '97201', '99210']);
    
insert into REGIONS values(
    'Southwest',
    ARRAY['94105', '90049', '92027']);    
    
--
-- Postgres
--

create table REGIONS
   (REGION_NAME varchar(32) NOT NULL,
   ZIPS varchar(32) ARRAY[10] NOT NULL,
   PRIMARY KEY (REGION_NAME));

insert into REGIONS values(
    'Northwest',
    '{"93101", "97201", "99210"}');
    
insert into REGIONS values(
    'Southwest',
    '{"94105", "90049", "92027"}');
    
--
-- ORACLE 
--

-- We must create a type for an array:
CREATE OR REPLACE TYPE vcarray AS VARRAY(10) OF VARCHAR(32);

-- We use the type in create table & insert
create table REGIONS
    (REGION_NAME varchar(32) NOT NULL,
    ZIPS  vcarray NOT NULL,
    PRIMARY KEY (REGION_NAME));
    
insert into REGIONS values(
    'Northwest', vcarray('93101', '97201', '99210'));
    
insert into REGIONS values(
    'Southwest', vcarray('94105', '90049', '92027'));
        