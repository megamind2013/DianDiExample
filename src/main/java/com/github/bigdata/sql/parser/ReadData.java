package com.github.bigdata.sql.parser;

import java.util.List;

public class ReadData extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String databaseName;
    private String tableName;
    private List<String> partitionVals;
    private int limit;

    public ReadData() {}
    
    public ReadData(String databaseName,String tableName,List<String> partitionVals,int limit) {
    	this.databaseName = databaseName;
    	this.tableName = tableName;
    	this.partitionVals = partitionVals;
    	this.limit = limit;
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

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
    
    

	
/**
data class ReadData(val : String?,
                    val : String,
                    val : ?,
                    val : Int) : Statement()
 *     
 */
    
	
}
