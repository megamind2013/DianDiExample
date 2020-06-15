package com.github.bigdata.sql.parser;

import java.util.List;
import java.util.Map;

public class StreamSourceTable extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tableName;
    private List<StreamColumn> columns;
    private Map<String, String> properties;
    private StreamTimeMode timeMode;
    private String timeAttrName;
    private StreamWaterMark waterMarkType;
    private int intervalTime;

    public StreamSourceTable() {}
    
    public StreamSourceTable(String tableName,List<StreamColumn> columns,Map<String, String> properties,StreamTimeMode timeMode,String timeAttrName,StreamWaterMark waterMarkType,int intervalTime) {
    	this.tableName = tableName;
    	this.columns = columns;
    	this.properties = properties;
    	this.timeMode = timeMode;
    	this.timeAttrName = timeAttrName;
    	this.waterMarkType = waterMarkType;
    	this.intervalTime = intervalTime;
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

	public StreamTimeMode getTimeMode() {
		return timeMode;
	}

	public void setTimeMode(StreamTimeMode timeMode) {
		this.timeMode = timeMode;
	}

	public String getTimeAttrName() {
		return timeAttrName;
	}

	public void setTimeAttrName(String timeAttrName) {
		this.timeAttrName = timeAttrName;
	}

	public StreamWaterMark getWaterMarkType() {
		return waterMarkType;
	}

	public void setWaterMarkType(StreamWaterMark waterMarkType) {
		this.waterMarkType = waterMarkType;
	}

	public int getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
	}

	
	
}
