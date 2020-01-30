/**
 *
 */
package org.kawanfw.sql.api.util.firewall;

/**
 * Stores the allowed statements of a table for an username.
 *
 * @author Nicolas de Pomereu
 *
 */
public class TableAllowStatements {

    // Says for each statement if it is allows
    private String username = null;
    private String table = null;
    private boolean allowDelete = false;
    private boolean allowInsert = false;
    private boolean allowSelect = false;
    private boolean allowUpdate = false;


    /**
     * Constructor.
     * @param username 	  the username for the rule.
     * @param table       the table name for the rule.
     * @param allowDelete if true, delete will be allowed on table.
     * @param allowInsert if true, insert will be allowed on table.
     * @param allowSelect if true, select will be allowed on table.
     * @param allowUpdate if true, update will be allowed on table.
     */
    public TableAllowStatements(String username, String table, boolean allowDelete, boolean allowInsert,
	    boolean allowSelect, boolean allowUpdate) {

	if (username == null) {
	    throw new NullPointerException("username is null!");
	}

	if (table == null) {
	    throw new NullPointerException("table is null!");
	}

	this.username = username;
	this.table = table;
	this.allowDelete = allowDelete;
	this.allowInsert = allowInsert;
	this.allowSelect = allowSelect;
	this.allowUpdate = allowUpdate;
    }


    public String getUsername() {
        return username;
    }

    public String getTable() {
	return table;
    }

    public boolean isAllowDelete() {
	return allowDelete;
    }

    public boolean isAllowInsert() {
	return allowInsert;
    }

    public boolean isAllowSelect() {
	return allowSelect;
    }

    public boolean isAllowUpdate() {
	return allowUpdate;
    }


    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
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
	TableAllowStatements other = (TableAllowStatements) obj;
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
	return "TableAllowStatements [username=" + username + ", table=" + table + ", allowDelete=" + allowDelete
		+ ", allowInsert=" + allowInsert + ", allowSelect=" + allowSelect + ", allowUpdate=" + allowUpdate
		+ "]";
    }

}
