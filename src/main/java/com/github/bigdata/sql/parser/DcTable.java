package com.github.bigdata.sql.parser;

import java.util.List;
import java.util.Map;

public class DcTable extends Statement{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String databaseName;
	private String tableName;
	private String comment;
	private Integer lifeCycle;
	private List<DcColumn> partitionColumns;
	private List<DcColumn> columns;
	private Map<String, String> properties ;
	private String fileFormate;
	private Boolean ifNotExists = false; //是否存在 if not exists 关键字
	private Boolean ifExists = false;
	private Boolean external = false;
	private Boolean temporary = false;
	private Boolean location = false;
	private String querySql = null;
	private TableData tableData = null; //是否存在 if exists 关键字

	public DcTable(String databaseName,String tableName,String comment,Integer lifeCycle,List<DcColumn> partitionColumns,List<DcColumn> columns,Map<String, String> properties,String fileFormate) {
		this.databaseName  = databaseName;
		this.tableName  = tableName;
		this.comment  = comment;
		this.lifeCycle  = lifeCycle;
		this.partitionColumns  = partitionColumns;
		this.columns  = columns;
		this.properties = properties;
		this.fileFormate = fileFormate;
	}

	public DcTable(String databaseName,String tableName,String comment,Integer lifeCycle,List<DcColumn> partitionColumns,List<DcColumn> columns,String fileFormate) {
		this.databaseName  = databaseName;
		this.tableName  = tableName;
		this.comment  = comment;
		this.lifeCycle  = lifeCycle;
		this.partitionColumns  = partitionColumns;
		this.columns  = columns;
		this.fileFormate = fileFormate;
	}

	public DcTable(String databaseName,String tableName,String comment,Integer lifeCycle,List<DcColumn> partitionColumns,List<DcColumn> columns) {
		this.databaseName  = databaseName;
		this.tableName  = tableName;
		this.comment  = comment;
		this.lifeCycle  = lifeCycle;
		this.partitionColumns  = partitionColumns;
		this.columns  = columns;
	}
	
	public DcTable(String databaseName,String tableName) {
		this.databaseName  = databaseName;
		this.tableName  = tableName;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getLifeCycle() {
		return lifeCycle;
	}

	public void setLifeCycle(Integer lifeCycle) {
		this.lifeCycle = lifeCycle;
	}

	public List<DcColumn> getPartitionColumns() {
		return partitionColumns;
	}

	public void setPartitionColumns(List<DcColumn> partitionColumns) {
		this.partitionColumns = partitionColumns;
	}

	public List<DcColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DcColumn> columns) {
		this.columns = columns;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getFileFormate() {
		return fileFormate;
	}

	public void setFileFormate(String fileFormate) {
		this.fileFormate = fileFormate;
	}

	public Boolean getIfNotExists() {
		return ifNotExists;
	}

	public void setIfNotExists(Boolean ifNotExists) {
		this.ifNotExists = ifNotExists;
	}

	public Boolean getIfExists() {
		return ifExists;
	}

	public void setIfExists(Boolean ifExists) {
		this.ifExists = ifExists;
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

	public Boolean getLocation() {
		return location;
	}

	public void setLocation(Boolean location) {
		this.location = location;
	}

	public String getQuerySql() {
		return querySql;
	}

	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}

	public TableData getTableData() {
		return tableData;
	}

	public void setTableData(TableData tableData) {
		this.tableData = tableData;
	}
}

/**

data class DcTable(
        val databaseName: String?,
        val tableName: String,
        val comment: String?,
        var lifeCycle: Int?,
        var partitionColumns: List<DcColumn>?,
        var columns: List<DcColumn>?,
        var properties: Map<String, String>?,
        var fileFormate: String?,
        var ifNotExists: Boolean = false, //是否存在 if not exists 关键字
        var ifExists: Boolean = false,
        var external: Boolean = false,
        var temporary: Boolean = false,
        var location: Boolean = false,
        var querySql: String? = null,
        var tableData: TableData? = null) : Statement() { //是否存在 if exists 关键字

    constructor(databaseName: String?,
                tableName: String,
                comment: String?,
                lifeCycle: Int?,
                partitionColumns: List<DcColumn>?,
                columns: List<DcColumn>?): this(databaseName,
            tableName, comment, lifeCycle, partitionColumns, columns, null, null, false, false, false)

    constructor(databaseName: String?,
                tableName: String): this(databaseName,
            tableName, null, null, null, null, null, null, false, false, false)
}
**/