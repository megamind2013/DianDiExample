package com.github.bigdata.sql.parser;

import java.util.List;
import java.util.Map;

public class StreamSinkTable extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tableName;
    private List<StreamColumn> columns;
    private Map<String, String> properties;

    public StreamSinkTable() {}
    
    public StreamSinkTable(String tableName,List<StreamColumn> columns,Map<String, String> properties) {
    	this.tableName = tableName;
    	this.columns = columns;
    	this.properties = properties;
    }

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<StreamColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<StreamColumn> columns) {
		this.columns = columns;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
    
}
