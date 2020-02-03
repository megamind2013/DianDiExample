package net.itdiandi.java.utils.safety;

import java.security.MessageDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.safety
* @ClassName MD5Util
* @Description md5工具
* @author 刘吉超
* @date 2016-02-24 19:43:39
*/
public class MD5Util
{
   private static final Logger logger = LoggerFactory.getLogger(MD5Util.class);
   private static final String ENCRYPT_TYPE = "MD5";
   
   /**
    * 解密操作
    * 
    * @param plainText
    * @throws Exception
    * @return String
    */
   public static String encodeMd5(String plainText)throws Exception
   {
       String str = "";
       try
       {
           MessageDigest md = MessageDigest.getInstance(ENCRYPT_TYPE);
           md.update(plainText.getBytes());
           byte b[] = md.digest();

           int i;

           StringBuffer buf = new StringBuffer("");
           for (int offset = 0; offset < b.length; offset++)
           {
               i = b[offset];
               if (i < 0)
                   i += 256;
               if (i < 16)
                   buf.append("0");
               buf.append(Integer.toHexString(i));
           }
           str = buf.toString();
       }
       catch (Exception e) 
       {
           logger.error("", e);
           return "";
       }
       return str;
   }
}