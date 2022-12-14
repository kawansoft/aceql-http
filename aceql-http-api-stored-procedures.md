# AceQL HTTP v12.1 - December 14, 2022

# API User Guide - Stored Procedures Addendum

<img src="https://docs.aceql.com/favicon.png" alt="AceQL HTTP Icon"/> 

<img src="https://docs.aceql.com/img/AceQL-Schema-min.jpg" alt="AceQL Draw"/>

* [About this User Guide](#about-this-user-guide)
* [General Principles](#general-principles)
* [execute_update for a stored procedure](#execute_update-for-a-stored-procedure)
   * [Server response to execute_update call for a stored procedure](#server-response-to-execute_update-call-for-a-stored-procedure)
   * [cURL example with Windows](#curl-example-with-windows)

# About this User Guide

This User Guide is an addendum to the[AceQL HTTP v12.1 - December 6, 2022
API User Guide](https://github.com/kawansoft/aceql-http/blob/master/aceql-http-user-guide-api.md). It explains how to call stored procedures using the API.

# General Principles

The stored procedures are called using the [execute_update](https://github.com/kawansoft/aceql-http/blob/master/aceql-http-user-guide-api.md#execute_update) and [execute_query](https://github.com/kawansoft/aceql-http/blob/master/aceql-http-user-guide-api.md#execute_query) API with supplemental stored procedures parameters.

We will show an `execute_update` call, principle is the same for a `SELECT` with an `execute_query`.

# execute_update for a stored procedure

Allows to call a stored procedures that returns only `IN OUT` or `OUT` parameters. (Not to be used for a `SELECT` call).

**Note that using parameter names is not supported in this AceQL version.**

| URL  Format                                                  |
| ------------------------------------------------------------ |
| `server/aceql/session/{session_id}/connection/{connection_id}/execute_update` |

| URL  parameter | Description                                                  |
| -------------- | ------------------------------------------------------------ |
| session_id     | The session_id  value returned by `login`.                   |
| connection_id  | The ID that refers the `java.sql.Connection` to use on server.<br>Optional: if not passed, server will use the one created at login. |

| Request  parameter  | Requested | Description                                                  |
| ------------------- | --------- | ------------------------------------------------------------ |
| sql                 | Yes       | The SQL  statement that contains the stored procedure call.  |
| prepared_statement  | Yes       | true.                                                        |
| stored_procedure    | yes       | true                                                         |
| param_type_{i}      | No        | Allows to  define the parameter type of parameter of i index. See values below. |
| param_value_{i}     | No        | Allows to  define the parameter value of parameter of i index. |
| param_direction_{i} | No        | Optional for `in` parameter.<br>Requested to design an `OUT` or `IN OUT` parameter.<br>Possible values are:<br>`in`  (default value),<br>`out`,<br>`inout`. |

| Prepared  Statement - Parameter type values                  |
| ------------------------------------------------------------ |
| BIGINT, BINARY, BIT, BLOB, CHAR, CHARACTER, CLOB, DATE, DECIMAL, DOUBLE_PRECISION, FLOAT, INTEGER, LONGVARBINARY, LONGVARCHAR, NUMERIC, REAL, SMALLINT, TIME, TIMESTAMP, TINYINT, URL, VARBINARY, VARCHAR. |

|                                                              |
| ------------------------------------------------------------ |
| All Request parameters must be URL encoded and formatted in UTF-8.<br />`DATE`, `TIME` and `TIMESTAMP`  values must be passed  in the form the number of milliseconds since January 1, 1970, 00:00:00 GMT. |

## Server response to execute_update call for a stored procedure

If everything is OK:

```json
{
    "status": "OK",
    "parameters_out_per_index": {
        "param rank 1": "param value 1",
        "param rank 2": "param value 2",
        "param rank n": "param value n",
    },
    "parameters_out_per_name": {
    },
    "row_count": 0
}                                             
```

In case of error: 

```
{  
   "status":"FAIL",
   "error_type":{error type numeric value},
   "error_message":"{error message returned by the server}",
   "http_status":{http status code numeric value}
}
```

## cURL example with Windows

Assuming the following Oracle stored procedure:

```plsql
create or replace PROCEDURE ORACLE_IN_OUT
(
  PARAM1 IN NUMBER,
  PARAM2 IN OUT NUMBER,
  PARAM3 IN OUT VARCHAR 
) AS 
BEGIN
  param2 := param1 * param2;
  param3 := param3 || ' ' || TO_CHAR(param2);
END ORACLE_IN_OUT;
```

We first login:

```bash
C:\> curl http://localhost:9090/aceql/database/XE/username/user1/connect?password=MySecret
```

The call will return the `session_id`:

```json
{
    "status": "OK",
    "connection_id": "745745643",
    "session_id": "qlher8drr94kvdrtvxbx8p8ave"
}
```

We then call the stored procedure precising the `OUT` and `IN OUT` parameters 

```bash
C:\> curl --data "prepared_statement=true" ^
 --data "stored_procedure=true" ^
 --data "param_type_1=INTEGER&param_value_1=6" ^
 --data "param_type_2=INTEGER&param_value_2=7" ^
 --data "param_direction_2=inout" ^
 --data "param_type_3=VARCHAR" ^
 --data-urlencode "param_value_3=Meaning of life is:" ^
 --data "param_direction_3=inout" ^
 --data-urlencode  "sql={ call ORACLE_IN_OUT(?, ?, ?) }" ^
 http://localhost:9090/aceql/session/qlher8drr94kvdrtvxbx8p8ave/execute_update
```

The call will return:

```json
{
    "status": "OK",
    "parameters_out_per_index": {
        "2": "42",
        "3": "Meaning of life is: 42"
    },
    "parameters_out_per_name": {
    },
    "row_count": 0
} 
```

________________

