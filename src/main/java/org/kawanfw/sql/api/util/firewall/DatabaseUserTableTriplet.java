/**
 *
 */
package org.kawanfw.sql.api.util.firewall;

/**
 * A triplet that allows to identify a CSV rule of CsvAllowFirewallManager SQL Firewall Manager.
 * @author Nicolas de Pomereu
 *
 */
public class DatabaseUserTableTriplet {

    private String database = null;
    private String username = null;
    private String table = null;

    /**
     * Constructor.
     * @param database
     * @param username
     * @param table
     */
    public DatabaseUserTableTriplet(String database, String username, String table) {

	if (database == null) {
	    throw new NullPointerException("database is null!");
	}
	if (username == null) {
	    throw new NullPointerException("username is null!");
	}
	if (table == null) {
	    throw new NullPointerException("table is null!");
	}

	this.database = database;
	this.username = username;
	this.table = table;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getTable() {
        return table;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((database == null) ? 0 : database.hashCode());
	result = prime * result + ((table == null) ? 0 : table.hashCode());
	result = prime * result + ((username == null) ? 0 : username.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	DatabaseUserTableTriplet other = (DatabaseUserTableTriplet) obj;
	if (database == null) {
	    if (other.database != null)
		return false;
	} else if (!database.equals(other.database))
	    return false;
	if (table == null) {
	    if (other.table != null)
		return false;
	} else if (!table.equals(other.table))
	    return false;
	if (username == null) {
	    if (other.username != null)
		return false;
	} else if (!username.equals(other.username))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "DatabaseUserTableTriplet [database=" + database + ", username=" + username + ", table=" + table + "]";
    }



}
