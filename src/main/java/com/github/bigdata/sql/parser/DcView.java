package com.github.bigdata.sql.parser;

public class DcView {
	private String databaseName;

	private String tableName;
	
	private String comment;
	
	private boolean ifNotExists;//是否存在 if not exists 关键字

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

	public boolean isIfNotExists() {
		return ifNotExists;
	}

	public void setIfNotExists(boolean ifNotExists) {
		this.ifNotExists = ifNotExists;
	}

	public boolean isIfExists() {
		return ifExists;
	}

	public void setIfExists(boolean ifExists) {
		this.ifExists = ifExists;
	}

	private boolean ifExists = false;//是否存在 if exists 关键字
}

/**
data class DcView(
        val databaseName: String?,
        val tableName: String,
        val comment: String? = null,
        var ifNotExists: Boolean = false, //是否存在 if not exists 关键字
        var ifExists: Boolean = false) : Statement() { //是否存在 if exists 关键字
}

**/
