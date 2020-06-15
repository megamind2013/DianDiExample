package com.github.bigdata.sql.parser;

public class RefreshData extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String databaseName;
    private String tableName;

    public RefreshData() {}
    
    public RefreshData(String databaseName,String tableName) {
    	this.databaseName = databaseName;
    	this.tableName = tableName;
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
    
    
    
/**
data class (val databaseName: String?,
                     val tableName: String) : Statement()
 */
	
}
