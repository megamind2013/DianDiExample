package net.itdiandi.java.utils.file.property;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.file.property
* @ClassName PropertyUtil
* @Description Property工具
* @Author 刘吉超
* @Date 2016-04-13 20:43:04
*/
public class PropertyUtil {
	// 日志
	private static Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
	// 属性
	private static Properties property = new Properties();
	
	// 加载conf.properties文件
	static {
		InputStream inStream = PropertyUtil.class.getClassLoader().getResourceAsStream("conf.properties");
		try {
			property.load(inStream);
		} catch (IOException e) {
			logger.error("", e);
		}
	}
	
	/**
	 * 根据key获取value
	 * 
	 * @return String
	 */
	public static String getValue(String key){
		return (String)property.get(key);
	}
}
