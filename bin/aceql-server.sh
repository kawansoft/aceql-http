#*********************************************************************
# AceQL Web Server Launcher                                          *
#                                                                    *
# Before launch:                                                     *
#  - 1) Set ACEQL_HOME to AceQL installation directory               *  
#  - 2) Drop your JDBC Driver into ACEQL_SERVER/lib-server directory *
#*********************************************************************

if [ -z "$ACEQL_HOME" ]; then 
   echo "ACEQL_HOME is not defined! Can not start AceQL Web Server."; 
   exit -1; 
fi

if [ ! -d "$ACEQL_HOME" ]; then
   echo "ACEQL_HOME directory does not exist: $ACEQL_HOME."
   echo "Can not start AceQL Web Server."; 
   echo "Check that ACEQL_HOME points to correct AceQL installation directory.";
   exit -1; 
fi

if [ ! -d "$ACEQL_HOME/lib-server" ]; then 
   echo "AceQL library directory does not exist: $ACEQL_HOME\lib-server. "; 
   echo "Can not start AceQL Web Server.";
   echo "Check that ACEQL_HOME points to correct AceQL installation directory.";
   exit -1; 
fi

case "$ACEQL_HOME" in 
     *\ * )
           echo "ACEQL_HOME directory with spaces is not supported: $ACEQL_HOME.";
		   echo "Can not start AceQL Web Server.";
           exit -1;
	    ;;
       *)
           #echo "no match"
           ;;
esac

java -Xms128m -Xmx256m -classpath "$ACEQL_HOME/lib-server/*":$CLASSPATH -Dfrom.aceql-server.script=true org.kawanfw.sql.WebServer $1 $2 $3 $4 $5 $6 $7
