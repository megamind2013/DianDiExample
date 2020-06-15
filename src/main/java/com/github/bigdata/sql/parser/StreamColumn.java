package com.github.bigdata.sql.parser;

public class StreamColumn extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
    private String type;
    private String comment;
    private String jsonPath;

    public StreamColumn() {}
    
    public StreamColumn(String name,String type,String comment,String jsonPath) {
    	this.name = name;
    	this.type = type;
    	this.comment = comment;
    	this.jsonPath = jsonPath;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getJsonPath() {
		return jsonPath;
	}

	public void setJsonPath(String jsonPath) {
		this.jsonPath = jsonPath;
	}
    
    
   
}
