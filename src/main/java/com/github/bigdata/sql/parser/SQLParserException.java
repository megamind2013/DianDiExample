package com.github.bigdata.sql.parser;

public class SQLParserException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public SQLParserException(String message) {
        super(message);
    }
	
	public SQLParserException(final String message,Exception exception) {
        super(message, exception);
    }
}