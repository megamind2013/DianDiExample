package com.github.bigdata.sql.parser;

import java.util.List;
import java.util.Map;

public class TidbCreateTable extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
