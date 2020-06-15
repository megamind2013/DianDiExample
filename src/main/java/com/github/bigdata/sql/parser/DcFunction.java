package com.github.bigdata.sql.parser;

public class DcFunction extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
    private String className;
    private String file;

    public DcFunction() {}
    
    public DcFunction(String name,String className,String file) {
    	this.name = name;
    	this.className = className;
    	this.file = file;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
    
    
    
    /**
data class DcFunction(
        val : String,
        val : String?,
        val : String?) : Statement() {
    constructor(name: String): this(name, null, null)
}
     */
	
}
