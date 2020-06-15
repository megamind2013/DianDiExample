package com.github.bigdata.sql.parser;

import java.util.ArrayList;

public class TableData {
	private ArrayList<TableSource> inputTables = new ArrayList<>();
	private ArrayList<TableSource> outpuTables = new ArrayList<>();
	private Integer limit;
	public ArrayList<TableSource> getInputTables() {
		return inputTables;
	}
	public void setInputTables(ArrayList<TableSource> inputTables) {
		this.inputTables = inputTables;
	}
	public ArrayList<TableSource> getOutpuTables() {
		return outpuTables;
	}
	public void setOutpuTables(ArrayList<TableSource> outpuTables) {
		this.outpuTables = outpuTables;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	
	
}

/**
data class TableData(
        var inputTables: java.util.ArrayList<TableSource> = ArrayList(),
        var outpuTables: java.util.ArrayList<TableSource> = ArrayList(),
        var limit: Int? = null
): Statement()
**/