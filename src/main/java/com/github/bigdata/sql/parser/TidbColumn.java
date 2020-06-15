package com.github.bigdata.sql.parser;

import java.util.List;

public class TidbColumn extends Statement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String indexName;
	private String indexType;
	private List<String> columns;
	
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	public String getIndexType() {
		return indexType;
	}
	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
}
/**
data class TidbColumn(
        val indexName: String?,
        val indexType: String?,
        var columns: List<String>
) : Statement() {}
**/