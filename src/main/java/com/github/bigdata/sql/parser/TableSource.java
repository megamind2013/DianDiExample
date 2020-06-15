package com.github.bigdata.sql.parser;

import java.util.ArrayList;
import java.util.List;

public class TableSource extends Statement {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String databaseName;
	private String tableName;
	private DcColumn column;
	private List<String> columns = new ArrayList<>();
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
	public DcColumn getColumn() {
		return column;
	}
	public void setColumn(DcColumn column) {
		this.column = column;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	
	
}

/**
data class TableSource(
        var databaseName: String?,
        var tableName: String,
        var column: DcColumn? = null,
        var columns: List<String>? = ArrayList()
): Statement() {
    constructor(databaseName: String?,
                tableName: String): this(databaseName, tableName, null)
}
**/