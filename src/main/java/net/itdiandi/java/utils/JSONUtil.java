package net.itdiandi.java.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.ApplicationContext;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils
* @ClassName JSONUtil
* @Description TODO
* @author 刘吉超
* @date 2016-02-24 19:48:25
*/
public class JSONUtil {
    
    /**
    * 支持搜索Object、Array中的key, 
    *     key如果在Object中 返回值（key对应的值）
    *     key如果在Array中 返回 [value,value,value]
    * 支持父子key检索  如exp_list.express 先搜索exp_list,搜到后，基于检索到value，再检索express
    */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object getValue(Object json,String key) {
		String[] keyArr = key.split("\\.");
		key = keyArr[0];
		Object returnobj = null;
		if(json instanceof JSONObject){
			JSONObject jsonObj = (JSONObject)json;
			if(jsonObj.containsKey(key)){
				returnobj =  jsonObj.get(key);
			}else{
				Iterator it = jsonObj.keys();
				while (it.hasNext()) {
		        	Object obj = jsonObj.get((String)it.next());
		        	if(obj instanceof JSONObject || obj instanceof JSONArray){
		        		returnobj = getValue(obj,key);
		        	}
				}
			}
		}else if(json instanceof JSONArray){
			JSONArray jsonarray = (JSONArray)json;
			List jsonList = new ArrayList();
			for(int i=0;i<jsonarray.size();i++){
				jsonList.add(getValue(jsonarray.get(i),key));
			}
			return jsonList;	
		}
		for(int i=1;i<keyArr.length;i++){
			returnobj = getValue(returnobj,keyArr[i]);
		}
		return returnobj;  
	}
    
    /*
    "{"app_type":"redis","contact_members": {"zhangsan": {"sms": "13611388521","tel": "13611388521","email": "test@300.cn"}},"field":["total_user","total_system"],"cycle":"1m","exp_list": [{"oper_type": "add","express":"totalMemory","operation": ">=","value":"0.8","level":"Red"},{"oper_type": "delete","express":"totalMemory","operation": ">=","value":"0.8","level":"Red"}]}";
    =>
    app_type----redis
    contact_members.zhangsan.sms----13611388521
    contact_members.zhangsan.tel----13611388521
    contact_members.zhangsan.email----test@300.cn
    field.0----total_user
    field.1----total_system
    cycle----1m
    exp_list.0.oper_type----add
    exp_list.0.express----totalMemory
    exp_list.0.operation---->=
    exp_list.0.value----0.8
    exp_list.0.level----Red
    exp_list.1.oper_type----delete
    exp_list.1.express----totalMemory
    exp_list.1.operation---->=
    exp_list.1.value----0.8
    exp_list.1.level----Red
    */
    @SuppressWarnings({ "rawtypes" })
	private static Map<String,String> iterateJSON(Object json) {
		Map<String,String> result = new LinkedHashMap<>();
		if(json instanceof JSONObject){
			JSONObject jsonObj = (JSONObject)json;
			Iterator iterator = jsonObj.keys();
			while (iterator.hasNext()) {
				String key = (String)iterator.next();
	        	Object obj = jsonObj.get(key);
	        	if(obj instanceof String){
	        		result.put(key, (String)obj);
	        	}else {
	        			Map<String,String> tempMap = iterateJSON(obj);
		        		for(Entry<String,String> entry : tempMap.entrySet()){
		        			result.put(key + "." + entry.getKey(), entry.getValue());
		        		}
	        	}
	        }
		}else if(json instanceof JSONArray){
			int strIndex = 0;
    		int arrIndex = 0;
			Iterator iterator = ((JSONArray)json).iterator();
			while (iterator.hasNext()) {
    			Object temp = iterator.next();
    			if(temp instanceof String){
    				result.put(""+strIndex++, (String)temp);
    			}else{
    				for(Entry<String,String> entry : iterateJSON(temp).entrySet()){
        				result.put(arrIndex + "." + entry.getKey(), entry.getValue());
    				}
    				arrIndex++;
    			}
    		}
		}
		return result;  
	}
    
    public static void main(String[] args) {
        String str = "{\"app_type\": \"redis\",\"contact_members\": {\"zhangsan\": {\"sms\": \"13611388521\", \"tel\": \"13611388521\", \"email\": \"test@300.cn\"}},\"field\":[\"total_user\",\"total_system\"],\"cycle\":\"1m\",\"exp_list\": [{\"oper_type\": \"add\",\"express\":\"totalMemory\",\"operation\": \">=\",\"value\":\"0.8\",\"level\":\"Red\"},{\"oper_type\": \"add\",\"express\":\"useMemory\",\"operation\": \">=\",\"value\":\"0.8\",\"level\":\"Red\"}]}";
//        String str = "{\"app_type\": \"redis\",\"contact_members\": {\"zhangsan\": {\"sms\": \"13611388521\", \"tel\": \"13611388521\", \"email\": \"test@300.cn\"}},\"field\":[\"total_user\",\"total_system\"],\"cycle\":\"1m\",\"exp_list\": [{\"oper_type\": \"add\",  \"express\":\"totalMemory\",  \"operation\": \">=\", \"value\":\"0.8\", \"level\":\"Red\"}]}";
//        String str = "{\"app_type\": \"redis\",\"cycle\":\"1m\"}";
//        System.out.println(Arrays.toString("exp_list.express".split("\\.")));
        JSONObject jsonObject = JSONObject.fromObject(str);
        System.out.println(getValue(jsonObject,"app_type"));
        System.out.println(getValue(jsonObject,"exp_list.express"));
        System.out.println(getValue(jsonObject,"exp_list.value"));
        System.out.println(getValue(jsonObject,"contact_members"));
        System.out.println(getValue(jsonObject,"contact_members.zhangsan.tel"));
	}
}