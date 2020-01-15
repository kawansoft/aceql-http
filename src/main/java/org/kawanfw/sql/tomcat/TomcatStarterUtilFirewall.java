package org.kawanfw.sql.tomcat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TomcatStarterUtilFirewall {


    public static List<String> getList(String sqlFirewallClassNameArray) {

	List<String> sqlFirewallClassNames = new ArrayList<>();

	if (sqlFirewallClassNameArray == null || sqlFirewallClassNameArray.isEmpty()) {
	    return sqlFirewallClassNames;
	}

	String [] array = sqlFirewallClassNameArray.split(",");

	sqlFirewallClassNames = Arrays.asList(array);
	return sqlFirewallClassNames;
    }

}
