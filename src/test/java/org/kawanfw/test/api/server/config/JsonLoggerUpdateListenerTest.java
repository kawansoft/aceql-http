/**
 * 
 */
package org.kawanfw.test.api.server.config;

import java.util.ArrayList;
import java.util.List;

import org.kawanfw.sql.api.server.listener.JsonLoggerUpdateListener;
import org.kawanfw.sql.api.server.listener.SqlActionEvent;
import org.kawanfw.sql.api.server.listener.SqlActionEventWrapper;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JsonLoggerUpdateListenerTest {

    public static void main(String[] args) throws Exception {
	List<Object> list = new ArrayList<>();
	list.add("value1");
	list.add("value2");
	list.add("value3");
	SqlActionEvent evt = SqlActionEventWrapper.sqlActionEventBuilder("user1", "db1", "10.0.0.0", "select * from table", false, list);
	
	String jsonString = JsonLoggerUpdateListener.toJsonString(evt);
	System.out.println(jsonString);
    }
}
