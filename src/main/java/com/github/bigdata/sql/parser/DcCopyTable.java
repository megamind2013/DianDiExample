package com.github.bigdata.sql.parser;

public class DcCopyTable extends Statement{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String oldDatabaseName;
	private String oldTableName;
	private String newDatabaseName;
	private String newTableName;
	private Boolean ifNotExists = false;
	private Boolean external = false;
	private Boolean temporary = false;

	public DcCopyTable() {}
	public DcCopyTable(String oldDatabaseName,String oldTableName,String newDatabaseName,String newTableName,Boolean ifNotExists,Boolean external,Boolean temporary) {
		this.oldDatabaseName = oldDatabaseName;
		this.oldTableName = oldTableName;
		this.newDatabaseName = newDatabaseName;
		this.newTableName = newTableName;
		this.ifNotExists = ifNotExists;
		this.external = external;
		this.temporary = temporary;
	}
	public String getOldDatabaseName() {
		return oldDatabaseName;
	}
	public void setOldDatabaseName(String oldDatabaseName) {
		this.oldDatabaseName = oldDatabaseName;
	}
	public String getOldTableName() {
		return oldTableName;
	}
	public void setOldTableName(String oldTableName) {
		this.oldTableName = oldTableName;
	}
	public String getNewDatabaseName() {
		return newDatabaseName;
	}
	public void setNewDatabaseName(String newDatabaseName) {
		this.newDatabaseName = newDatabaseName;
	}
	public String getNewTableName() {
		return newTableName;
	}
	public void setNewTableName(String newTableName) {
		this.newTableName = newTableName;
	}
	public Boolean getIfNotExists() {
		return ifNotExists;
	}
	public void setIfNotExists(Boolean ifNotExists) {
		this.ifNotExists = ifNotExists;
	}
	public Boolean getExternal() {
		return external;
	}
	public void setExternal(Boolean external) {
		this.external = external;
	}
	public Boolean getTemporary() {
		return temporary;
	}
	public void setTemporary(Boolean temporary) {
		this.temporary = temporary;
	}
	
}

/**


data class DcCopyTable(
        val oldDatabaseName: String?,
        val oldTableName: String,
        val newDatabaseName: String?,
        val newTableName: String,
        var ifNotExists: Boolean = false,
        var external: Boolean = false,
        var temporary: Boolean = false) : Statement()

**/