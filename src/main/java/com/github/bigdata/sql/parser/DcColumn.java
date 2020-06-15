package com.github.bigdata.sql.parser;

public class DcColumn extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
    private String type;
    private String comment;
    private String oldName;

    public DcColumn() {}
    
    public DcColumn(String name,String type,String comment,String oldName) {
    	this.name = name;
    	this.type = type;
    	this.comment = comment;
    	this.oldName = oldName;
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

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
}

/**

data class DcColumn(
        val name: String,
        val type: String? = null,
        val comment: String? = null,
        val oldName: String? = null) : Statement()
**/
