package com.github.bigdata.sql.parser;

import java.util.List;

public class MergeData extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String databaseName;
    private String tableName;
    private List<String> partitionVals;

    public MergeData() {}
    
    public MergeData(String databaseName,String tableName,List<String> partitionVals) {
    	this.databaseName = databaseName;
    	this.tableName = tableName;
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

	public List<String> getPartitionVals() {
		return partitionVals;
	}

	public void setPartitionVals(List<String> partitionVals) {
		this.partitionVals = partitionVals;
	}
    
    
    
    /**
data class MergeData(val : String?,
                     val : String,
                     val : ?) : Statement()
     */
	
}
