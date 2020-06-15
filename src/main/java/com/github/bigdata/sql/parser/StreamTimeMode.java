package com.github.bigdata.sql.parser;

import java.io.Serializable;

public enum StreamTimeMode implements Serializable{
	PROCTIME, 
	ROWTIME
}