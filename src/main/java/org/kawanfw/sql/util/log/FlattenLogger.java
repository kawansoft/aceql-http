/**
 * 
 */
package org.kawanfw.sql.util.log;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nicolas de Pomereu
 *
 */
public class FlattenLogger extends Logger {

    public FlattenLogger(String name, String resourceBundleName) {
	super(name, resourceBundleName);
    }

    @Override
    public void log(Level level, String msg) {
	StringFlattener stringFlattener = new StringFlattener(msg);
	String flatten = stringFlattener.flatten();
	super.log(level, flatten);
    }
    
   
}
