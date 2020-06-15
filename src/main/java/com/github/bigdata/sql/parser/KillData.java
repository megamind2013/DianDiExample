package com.github.bigdata.sql.parser;

public class KillData extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String jobGroupId;

    public KillData() {}
    
    public KillData(String jobGroupId) {
    	this.jobGroupId = jobGroupId;
    }

	public String getJobGroupId() {
		return jobGroupId;
	}

	public void setJobGroupId(String jobGroupId) {
		this.jobGroupId = jobGroupId;
	}
    
    
    
/**
data class (val : String) : Statement()

 * 
 */
	
}
