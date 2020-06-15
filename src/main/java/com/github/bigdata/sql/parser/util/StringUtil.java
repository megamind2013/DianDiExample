package com.github.bigdata.sql.parser.util;

import org.apache.commons.lang.StringUtils;

public class StringUtil {
	 public static String cleanSingleQuote(String value) {
		 if(StringUtils.startsWith(value, "'") && StringUtils.endsWith(value, "'")) {
			 return StringUtils.substringBetween(value, "'", "'");
		 } else {
			 return value;
		 }
	}

	public static String cleanDoubleQuote(String value) {
		if(StringUtils.startsWith(value, "\"") && StringUtils.endsWith(value, "\"")) {
			return StringUtils.substringBetween(value, "\"", "\"");
		} else {
			return value;
		}
	}

	public static String cleanQuote(String value) {
		if(value == null)
			return "";
		if(StringUtils.startsWith(value, "`") && StringUtils.endsWith(value, "`")) {
			return StringUtils.substringBetween(value, "`", "`");
		} else {
			return value;
		}
	}

	public static String parseDataType(String type) {
		String value = type;
		if(StringUtils.startsWith(type, "TOK_")) 
			value = StringUtils.substringAfter(type, "TOK_");
	        
		return StringUtils.lowerCase(value);
	}
}
