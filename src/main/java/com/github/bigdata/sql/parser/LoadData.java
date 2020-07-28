package com.github.bigdata.sql.parser;

import java.util.List;

public class LoadData extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String databaseName;
    private String tableName;
    private String loadMode;
    private String resourceName;
    private List<String> partitionVals;

    public LoadData() {}
	public LoadData(String databaseName,String tableName) {
		this.databaseName = databaseName;
		this.tableName = tableName;
	}

    public LoadData(String databaseName,String tableName,String loadMode,String resourceName,List<String> partitionVals) {
    	this.databaseName = databaseName;
    	this.tableName = tableName;
    	this.loadMode = loadMode;
    	this.resourceName = resourceName;
    	this.partitionVals = partitionVals;
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

	public String getLoadMode() {
		return loadMode;
	}

	public void setLoadMode(String loadMode) {
		this.loadMode = loadMode;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public List<String> getPartitionVals() {
		return partitionVals;
	}

	public void setPartitionVals(List<String> partitionVals) {
		this.partitionVals = partitionVals;
	}
/**
data class (val : String?,
                    val : String,
                    val : String? = null,
                    val : String? = null,
                    val : ? = null) : Statement()
 */
	
}
