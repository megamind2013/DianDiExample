package com.github.bigdata.sql.parser;

public class UnSetData extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String key;

    public UnSetData() {}
    
    public UnSetData(String key) {
    	this.key = key;
    }

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
    
    
    
/**
 data class UnSetData(val key: String) : Statement()

 */
}
