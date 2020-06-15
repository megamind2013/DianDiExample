package com.github.bigdata.sql.parser.util;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateUtils {
	 public static String getCurrentDateTime() {
		 try {
			 LocalDateTime ldt = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
			 DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
	            return ldt.format(format);
	        } catch (DateTimeException ex) {
	            throw new RuntimeException(ex.getMessage(), ex);
	        }
	}
}
