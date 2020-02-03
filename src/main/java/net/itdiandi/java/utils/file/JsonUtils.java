package net.itdiandi.java.utils.file;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils {

	public static String bean2json(Object bean) {
		Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")//时间转化为特定格式  
				.setPrettyPrinting() //对json结果格式化
				.create();
		//registerTypeAdapter(java.sql.Date.class,
		//		new SQLDateSerializer()).registerTypeAdapter(
		//		java.util.Date.class, new UtilDateSerializer()).setDateFormat(
		//		DateFormat.LONG).setPrettyPrinting().create();
		return gson.toJson(bean);
	}

	public static Object json2bean(String json, Type type) {
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(json, type);
	}
	public static void main(String[] args) {
		String json = "{name:\"zhangsan\",age:25,hight:1.72,sex:true}"; 
		
//		JSONObject jsonObject = JSONObject.fromObject(json);
		System.out.println(json2bean(json,Map.class));
				
	}
}
