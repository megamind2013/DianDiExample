package net.itdiandi.java.utils.db;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

import net.itdiandi.java.utils.safety.PBEUtil;

/** 
* @ProjectName MIS
* @PackageName cn.ce.bigdata.utils
* @ClassName MySQLUtil
* @Description TODO
* @Author 刘吉超
* @Date 2017年12月25日 下午5:21:52
*/
public class DBUtil {
	   // 日志
	   private static Logger logger = LoggerFactory.getLogger(DBUtil.class);
	   
	   /**
	    * 获取SqlMapClient
	    */
	   public static SqlMapClient getSqlMapClient(String proFile,String xmlFile)
	   {
		   SqlMapClient sqlMapClient = null;
		   Reader reader = null;
	       try 
	       {
	           Properties props = new Properties();
	           props.load(DBUtil.class.getResourceAsStream(proFile));
	           
	           // 解密
	           String passwd = props.getProperty("password");
	           passwd = PBEUtil.decrypt(passwd);
	           props.setProperty("password", passwd);
	           
	           reader = Resources.getResourceAsReader(xmlFile);
	           sqlMapClient = SqlMapClientBuilder.buildSqlMapClient(reader, props);
	       } catch (IOException e) 
	       {
	           logger.error("读取配置文件出错", e);
	       }finally
	       {
	           IOUtils.closeQuietly(reader);
	       }
	       return sqlMapClient;
	   }
}
