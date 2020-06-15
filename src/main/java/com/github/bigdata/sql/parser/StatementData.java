package com.github.bigdata.sql.parser;

import java.io.Serializable;

public class StatementData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StatementType type;
	private Statement statement;

	public StatementData() {}
	
	public StatementData(StatementType type,Statement statement) {
		this.type = type;
		this.statement = statement;
	}
	
	public StatementData(StatementType type) {
		this.type = type;
		this.statement = null;
	}

	public StatementType getType() {
		return type;
	}

	public void setType(StatementType type) {
		this.type = type;
	}

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}


/**
data class StatementData(val , val statement: Statement?): Serializable {
    constructor(type: StatementType): this(type, null)
}
**/