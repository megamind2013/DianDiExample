package net.itdiandi.java.utils.file.excel.jxl.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import net.itdiandi.java.utils.file.excel.jxl.CheckExcel;

public class CheckExcelService implements CheckExcel{
	
	@Override
	public boolean check(Map<String, Object> data) {
		data.put("name", data.get("姓名"));
		data.put("sex", data.get("性别"));
		data.put("age", data.get("年龄"));
		data.put("date", parseDate(data.get("出生日期")));
		return true;
	}
	
	private Date parseDate(Object object){
		Date date = null;
		if(object instanceof String){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try{
				date = sdf.parse((String)object);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return date;
	}
}
