package com.github.bigdata.sql.parser;

public class DcAlterColumn extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String databaseName;
    private String tableName;
    private String oldName;
    private String newName;
    private String comment;

    public DcAlterColumn() {}
    
    public DcAlterColumn(String databaseName,String tableName,String oldName,String newName,String comment) {
    	this.databaseName = databaseName;
    	this.tableName = tableName;
    	this.oldName = oldName;
    	this.newName = newName;
    	this.comment = comment;
    }

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
    
    
	
}
