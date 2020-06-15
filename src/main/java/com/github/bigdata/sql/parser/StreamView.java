package com.github.bigdata.sql.parser;

public class StreamView extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tableName;
    private String querySql;

    public StreamView() {}
    
    public StreamView(String tableName,String querySql) {
    	this.tableName = tableName;
    	this.querySql = querySql;
    }

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getQuerySql() {
		return querySql;
	}

	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}
    
    
}
