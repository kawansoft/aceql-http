@ECHO OFF
REM ********************************************************************
REM AceQL Web Server Launcher                                          *
REM                                                                    *        
REM Before launch:                                                     *
REM  - 1) set ACEQL_HOME to AceQL installation directory               *  
REM  - 2) Drop your JDBC Driver into ACEQL_SERVER\lib-server directory *                                                            *                                 *
REM ********************************************************************

IF [%ACEQL_HOME%] == [] (
   echo ACEQL_HOME is not defined! Can not start AceQL Web Server.
   goto :END
 )

IF NOT EXIST %ACEQL_HOME% (
   echo ACEQL_HOME directory does not exist: %ACEQL_HOME%. 
   echo Can not start AceQL Web Server.
   echo Check that ACEQL_HOME points to correct AceQL installation directory. 
   goto :END
 )

IF NOT EXIST %ACEQL_HOME%\lib-server (
   echo AceQL library directory does not exist: %ACEQL_HOME%\lib-server.
   echo Can not start AceQL Web Server.
   echo Check that ACEQL_HOME points to correct AceQL installation directory.
   goto :END
 )  
      
REM remove quotes (") from ACEQL_HOME
set ACEQL_HOME_NO_QUOTES=%ACEQL_HOME%
for /f "useback tokens=*" %%a in ('%ACEQL_HOME_NO_QUOTES%') do set ACEQL_HOME_NO_QUOTES=%%~a
		                           
java -Xms128m -Xmx256m -classpath "%ACEQL_HOME_NO_QUOTES%\lib-server/*";%CLASSPATH% -Dfrom.aceql-server.script=true org.kawanfw.sql.WebServer %1 %2 %3 %4 %5 %6 %7

:END
