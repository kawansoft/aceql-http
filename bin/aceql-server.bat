@ECHO OFF
REM ********************************************************************
REM AceQL Web Server Launcher                                          *                                                                 *                                                                  *                                 *
REM ********************************************************************

set ACEQL_HOME=%CD%
set ACEQL_HOME=%ACEQL_HOME:~0,-4%
                                   
java -Xms256M -Xmx4096M -classpath "%ACEQL_HOME%\lib-server\*";"%ACEQL_HOME%\lib-jdbc\*";%CLASSPATH% -Dfrom.aceql-server.script=true org.kawanfw.sql.WebServer %1 %2 %3 %4 %5 %6 %7

