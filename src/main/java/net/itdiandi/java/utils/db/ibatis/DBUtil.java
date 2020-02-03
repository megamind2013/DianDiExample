package net.itdiandi.java.utils.db.ibatis;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

import net.itdiandi.java.utils.safety.PBEUtil;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils
* @ClassName DBUtil
* @Description 数据库工具
* @author 刘吉超
* @date 2016-02-24 16:35:26
*/
public class DBUtil {
   private static SqlMapClient sqlMapClient = null;
   // 日志
   private static Logger logger = LoggerFactory.getLogger(DBUtil.class);
   
   static 
   {
       Reader reader = null;
       try 
       {
           Properties props = new Properties();
           props.load(Resources.getResourceAsReader("com/buaa/utils/db/ibatis/jdbc.properties"));
           
           // 解密
           String passwd = props.getProperty("password");
           passwd = PBEUtil.decrypt(passwd);
           props.setProperty("password", passwd);
           
           reader = Resources.getResourceAsReader("com/buaa/utils/db/ibatis/sqlmap-config.xml");
           sqlMapClient = SqlMapClientBuilder.buildSqlMapClient(reader, props);
       } catch (IOException e) 
       {
           logger.error("读取配置文件出错", e);
       }finally
       {
           IOUtils.closeQuietly(reader);
       }
   }
   
   /**
    * 获取SqlMapClient
    */
   public static SqlMapClient getSqlMapClient()
   {
       return sqlMapClient;
   }

   
   /**
    * 获取序列号(UUID)
    */
   public static String getUUID()
   {
       return UUID.randomUUID().toString();
   }
}