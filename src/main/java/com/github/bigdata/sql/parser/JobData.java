package com.github.bigdata.sql.parser;

import java.util.List;

public class JobData extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String resourceName;
    private String className;
    private List<String> params;

    public JobData() {}
    
    public JobData(String resourceName,String className,List<String> params) {
    	this.resourceName = resourceName;
    	this.className = className;
    	this.params = params;
    }

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}
    
	
/**
data class JobData(val resourceName: String,
                   val className: String,
                   val params: List<String>?) : Statement()
 */
	
}
