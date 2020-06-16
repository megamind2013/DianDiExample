package com.github.bigdata.sql.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TidbCreateTable extends Statement {

	public TidbCreateTable(String databaseName, String tableName, String comment, ArrayList<DcColumn> columns,
			ArrayList<String> pkColumns, ArrayList<TidbColumn> uniques, HashMap<String, String> properties,
			boolean ifNotExists) {
		this.databaseName = databaseName;
		this.tableName = tableName;
		this.comment = comment;
		this.columns = columns;
		this.pkColumns = pkColumns;
		this.uniques = uniques;
		this.properties = properties;
		this.ifNotExists = ifNotExists;
	}
	
	private static final long serialVersionUID = 1L;
	
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
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public List<DcColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<DcColumn> columns) {
		this.columns = columns;
	}
	public List<String> getPkColumns() {
		return pkColumns;
	}
	public void setPkColumns(List<String> pkColumns) {
		this.pkColumns = pkColumns;
	}
	public List<TidbColumn> getUniques() {
		return uniques;
	}
	public void setUniques(List<TidbColumn> uniques) {
		this.uniques = uniques;
	}
	public Map<String, String> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	public Boolean getIfNotExists() {
		return ifNotExists;
	}
	public void setIfNotExists(Boolean ifNotExists) {
		this.ifNotExists = ifNotExists;
	}
	private String databaseName;
	private String tableName;
	private String comment;
	private List<DcColumn> columns;
	private List<String> pkColumns;
	private List<TidbColumn> uniques;
	private Map<String, String> properties;
	private Boolean ifNotExists = false;
}

/**
data class TidbCreateTable(
        val databaseName: String?,
        val tableName: String,
        val comment: String?,
        var columns: List<DcColumn>,
        var pkColumns: List<String>,
        var uniques: List<TidbColumn>,
        var properties: Map<String, String>,
        var ifNotExists: Boolean = false) : Statement() {}
        **/
