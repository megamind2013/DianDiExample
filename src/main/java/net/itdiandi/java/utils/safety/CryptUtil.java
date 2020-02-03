package net.itdiandi.java.utils.safety;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;

import net.itdiandi.java.utils.ByteUtil;

/**
* bcprov-jdk15on-1.49
* use bouncycastle to AES encrypt
*/
public class CryptUtil extends CryptTool
{        
   private static final int ALGORITHM_INVALID = -1;
       
   private int algorithmType = 0;    
   private int keyType = 0;
   private SecretKey key = null;    
   private IvParameterSpec iv = null;
   private static final String CHARSET_UTF_8 = "UTF-8";

   /**
    * 构造一个默认支持AES/ECB/PKCS5Padding加密模式的实例，使用的加密密钥字节形式为keyBytes
    */
   public CryptUtil(byte[] keyBytes)
   {
       this(0, null, keyBytes);
   }
   
   /**
    * 构造一个默认支持AES/ECB/PKCS5Padding加密模式的实例，使用的加密密钥为Base64编码字符串keyStr
    */
   public CryptUtil(String base64KeyStr)
   {
       this(0, null, decodeBase64(base64KeyStr));
   }
   
   /**
    * 构造一个默认支持AES/ECB/PKCS5Padding加密模式的实例，使用的加密密钥为Base64编码字符串keyStr
    */
   public CryptUtil(String keyStr, int encode)
   {
       byte[] keyBytes = decodeString(keyStr, encode);
          
       if(null!=keyBytes)
       {    
           this.algorithmType = ALGORITHM_AES_ECB;
           this.keyType = 0;                  
           
           this.key = new SecretKeySpec(keyBytes, KEY_TYPE[keyType]);
       }else{
           this.algorithmType = ALGORITHM_INVALID;
       }      
       keyBytes = null;
   }

/**
    * 构造一个默认支持AES/CBC/PKCS5Padding加密模式的实例，使用的加密初始向量字节形式为ivBytes，加密密钥字节形式为keyBytes
    */
   public CryptUtil(byte[] ivBytes, byte[] keyBytes)
   {
       this(1, ivBytes, keyBytes);
   }
   
   /**
    * 构造一个默认支持AES/CBC/PKCS5Padding加密模式的实例，使用的加密初始向量Base64编码字符串为ivStr，加密密钥为Base64编码字符串keyStr
    */
   public CryptUtil(String base64IvStr, String base64KeyStr)
   {
       this(1, decodeBase64(base64IvStr), decodeBase64(base64KeyStr));
   }
   
   /**
    * 构造一个默认支持AES/CBC/PKCS5Padding加密模式的实例，使用的加密初始向量Base64编码字符串为ivStr，加密密钥为Base64编码字符串keyStr
    */
   public CryptUtil(String ivStr, String keyStr, int encode)
   {
       byte[] keyBytes = null;
       byte[] ivBytes = null;
       if(encode==ENCODE_HEX)
       {
           ivBytes = ByteUtil.hexStringToBytes(ivStr);
           keyBytes = ByteUtil.hexStringToBytes(keyStr);
       }else
       {
           ivBytes = decodeBase64(ivStr);
           keyBytes = decodeBase64(keyStr);
       }      
      
       if(null!=keyBytes)
       {
           this.algorithmType =  ALGORITHM_AES_CBC;
           keyType = 0;     
           
           key = new SecretKeySpec(keyBytes, KEY_TYPE[keyType]);            
           iv = new IvParameterSpec(ivBytes);
       }else{
           algorithmType = ALGORITHM_INVALID;
       }       
   }

/**
    * 构造一个指定加密模式的实例，使用的加密初始向量字节形式为ivBytes，加密密钥字节形式为keyBytes
    */
   public CryptUtil(int algorithmType, byte[] ivBytes, byte[] keyBytes)
   {
       if(algorithmType>=MIN_ALGORITHM_TYPE && algorithmType<=MAX_ALGORITHM_TYPE)
       {        
           this.algorithmType = algorithmType;
           
           switch(algorithmType)
           {
               case ALGORITHM_AES_ECB:
               case ALGORITHM_AES_CBC:               
                   keyType = 0;
                   break;
               case ALGORITHM_BLOWFISH_ECB:
               case ALGORITHM_BLOWFISH_CBC:
                   keyType = 1;
                   break;
               case ALGORITHM_3DES:
                   keyType = 3;
                   break;
               default:
                   algorithmType = ALGORITHM_INVALID;
           }        
           
           key = new SecretKeySpec(keyBytes, KEY_TYPE[keyType]);
           
           if((algorithmType==ALGORITHM_BLOWFISH_CBC || algorithmType==ALGORITHM_AES_CBC || algorithmType==ALGORITHM_3DES) && null!=ivBytes)
           {
               iv = new IvParameterSpec(ivBytes);
           }
       }else{
           algorithmType = ALGORITHM_INVALID;
       }       
   }   
   
   /**
    * 加密数据并编码为Base64形式
    * 使用当前实例对clearStr进行加密，加密后的密文转换为Base64编码的字符串
    * @param clearStr
    * @return
    * @throws Exception
    * @see [类、类#方法、类#成员]
    */
   public String encrypt(String clearStr) throws Exception
   {
       return encrypt(clearStr, ENCODE_BASE64);
   }

/**
    * 加密数据并编码为16进制形式
    * 使用当前实例对clearStr进行加密，加密后的密文转换为Base64编码的字符串
    * @param clearStr
    * @return
    * @throws Exception
    * @see [类、类#方法、类#成员]
    */
   public String encrypt16(String clearStr) throws Exception
   {
       return encrypt(clearStr, ENCODE_HEX);
   }
   
   /**
    * 加密数据并以指定形式编码
    * 使用当前实例对clearStr进行加密，
    * 加密后的密文转换为Base64编码的字符串（outputEncoder为64时）或16进制字符串（outputEncoder为16时）
    * @param clearStr
    * @param outputEncoder
    * @return
    * @throws Exception
    * @see [类、类#方法、类#成员]
    */
   public String encrypt(String clearStr, int outputEncoder) throws Exception
   {        
       if(algorithmType==ALGORITHM_INVALID)
       {
           throw new Exception("invalid algorithm type, must between " + MIN_ALGORITHM_TYPE + " and " + MAX_ALGORITHM_TYPE);
       }
       
       if(outputEncoder != ENCODE_HEX && outputEncoder !=ENCODE_BASE64)
       {
           throw new Exception("invalid outputEncoder, must be " + ENCODE_HEX + " or " + ENCODE_BASE64);
       }
       
           System.out.println("CIPHER_TYPE[algorithmType]：" + CIPHER_TYPE[algorithmType]);
       
           Cipher in = Cipher.getInstance(CIPHER_TYPE[algorithmType], provider);            
               
           if((algorithmType==ALGORITHM_BLOWFISH_CBC || algorithmType==ALGORITHM_AES_CBC|| algorithmType==ALGORITHM_3DES) && null!=iv)
           {             
               in.init(Cipher.ENCRYPT_MODE, key, iv);     
           }else
           {
               in.init(Cipher.ENCRYPT_MODE, key);
           }
                       
           byte[] enc = in.doFinal(clearStr.getBytes(CHARSET_UTF_8));
       
           return encodeString(enc, outputEncoder);      
   }


/**
    * 对Base64编码后的加密串进行解密
    * 使用当前实例对Base64编码的字符串encStr进行解密，输出为普通明文
    * @param encStr
    * @return
    * @throws Exception
    * @see [类、类#方法、类#成员]
    */
   public String decrypt(String encStr) throws Exception
   {
       return decrypt(encStr, ENCODE_BASE64);
   }
   
   /**
    * 对16进制编码后的加密串进行解密
    * 使用当前实例对Base64编码的字符串encStr进行解密，输出为普通明文
    * @param encStr
    * @return
    * @throws Exception
    * @see [类、类#方法、类#成员]
    */
   public String decrypt16(String encStr) throws Exception
   {
       return decrypt(encStr, ENCODE_HEX);
   }
   
   /**
    * 指定加密串的编码进行解密
    * 使用当前实例对encStr进行解密，输出为普通明文。
    * encStr可能是Base64编码的字符串（inputEncoder为64时）或16进制字符串（inputEncoder为16时）
    * @param encStr
    * @param inputEncoder
    * @return
    * @throws Exception
    * @see [类、类#方法、类#成员]
    */
   public String decrypt(String encStr, int inputEncoder) throws Exception
   {
       if(algorithmType==ALGORITHM_INVALID)
       {
           throw new Exception("invalid algorithm type, must between " + MIN_ALGORITHM_TYPE + " and " + MAX_ALGORITHM_TYPE);
       }
       
       if(inputEncoder != ENCODE_HEX && inputEncoder !=ENCODE_BASE64)
       {
           throw new Exception("invalid inputEncoder, must be " + ENCODE_HEX + " or " + ENCODE_BASE64);
       }
       
           Cipher in = Cipher.getInstance(CIPHER_TYPE[algorithmType], provider);  
               
           if(algorithmType==ALGORITHM_BLOWFISH_CBC || algorithmType==ALGORITHM_AES_CBC || algorithmType==ALGORITHM_3DES && null!=iv)
           {              
               in.init(Cipher.DECRYPT_MODE, key, iv);     
           }else{
               in.init(Cipher.DECRYPT_MODE, key);
           }  
           
           byte[] data = null;
           
           if(inputEncoder == ENCODE_BASE64)
           {
               data = Base64.decode(encStr.getBytes(CHARSET_UTF_8));
           }else
           {
               data = hex2Bin(encStr);
           }
               
           byte[] dec = in.doFinal(data);
           
           return new String(dec, CHARSET_UTF_8);               
   }   


/**
    * SHA-1消息摘要算法16进制形式
    */
   public static String encodeSHAHex(byte[] data) throws Exception
   {
       // 初始化MessageDigest,SHA即SHA-1的简称
       MessageDigest md = MessageDigest.getInstance("SHA");
       // 执行摘要方法
       byte[] digest = md.digest(data);
       return byte2HexString(digest);
   }
   
   /**
    * SHA-1消息摘要算法Base64编码形式
    */
   public static String encodeSHA(byte[] data) throws Exception
   {
       // 初始化MessageDigest,SHA即SHA-1的简称
       MessageDigest md = MessageDigest.getInstance("SHA");
       // 执行摘要方法
       byte[] digest = md.digest(data);
       return encodeBase64(digest);
   }
   
   /**
    * SHA-256消息摘要算法16进制形式
    */
   public static String encodeSHA256Hex(byte[] data) throws Exception
   {
       // 初始化MessageDigest,SHA即SHA-1的简称
       MessageDigest md = MessageDigest.getInstance("SHA-256");
       // 执行摘要方法
       byte[] digest = md.digest(data);
       return byte2HexString(digest);
   }


/**
    * SHA-256消息摘要算法Base64编码形式
    */
   public static String encodeSHA256(byte[] data) throws Exception
   {
       // 初始化MessageDigest,SHA即SHA-1的简称
       MessageDigest md = MessageDigest.getInstance("SHA-256");
       // 执行摘要方法
       byte[] digest = md.digest(data);
       return encodeBase64(digest);
   }

   /**
    * SHA-384消息摘要算法16进制形式
    */
   public static String encodeSHA384Hex(byte[] data) throws Exception
   {
       // 初始化MessageDigest,SHA即SHA-1的简称
       MessageDigest md = MessageDigest.getInstance("SHA-384");
       // 执行摘要方法
       byte[] digest = md.digest(data);
       return byte2HexString(digest);
   }
   
   /**
    * SHA-384消息摘要算法Base64编码形式
    */
   public static String encodeSHA384(byte[] data) throws Exception
   {
       // 初始化MessageDigest,SHA即SHA-1的简称
       MessageDigest md = MessageDigest.getInstance("SHA-384");
       // 执行摘要方法
       byte[] digest = md.digest(data);
       return encodeBase64(digest);
   }
  
   /**
    * SHA-512消息摘要算法16进制形式
    */
   public static String encodeSHA512Hex(byte[] data) throws Exception
   {
       // 初始化MessageDigest,SHA即SHA-1的简称
       MessageDigest md = MessageDigest.getInstance("SHA-512");
       // 执行摘要方法
       byte[] digest = md.digest(data);
       return byte2HexString(digest);
   }
   
   /**
    * SHA-512消息摘要算法Base64编码形式
    */
   public static String encodeSHA512(byte[] data) throws Exception
   {
       // 初始化MessageDigest,SHA即SHA-1的简称
       MessageDigest md = MessageDigest.getInstance("SHA-512");
       // 执行摘要方法
       byte[] digest = md.digest(data);
       return encodeBase64(digest);
   }


/**
    * 对Base64编码后的加密串进行解密 使用当前实例对Base64编码的字符串encStr进行解密，输出为普通明文
    * 
    * @param encStr
    * @return
    * @throws Exception
    * @see [类、类#方法、类#成员]
    */
   public String decrypt(byte[] data) throws Exception
   {
       return new String(decryptByte(data), CHARSET_UTF_8);
   }
   
   /**
    * 对Base64编码后的加密串进行解密 使用当前实例对Base64编码的字符串encStr进行解密，输出为普通明文
    * 
    * @param encStr
    * @return
    * @throws Exception
    * @see [类、类#方法、类#成员]
    */
   public byte[] decryptByte(byte[] data) throws Exception
   {
       if (algorithmType == ALGORITHM_INVALID)
       {
           throw new Exception("invalid algorithm type, must between " + MIN_ALGORITHM_TYPE + " and "
                   + MAX_ALGORITHM_TYPE);
       }
       
       Cipher in = Cipher.getInstance(CIPHER_TYPE[algorithmType], provider);
       
       if (algorithmType == ALGORITHM_BLOWFISH_CBC || algorithmType == ALGORITHM_AES_CBC && null != iv)
       {
           in.init(Cipher.DECRYPT_MODE, key, iv);
       }
       else
       {
           in.init(Cipher.DECRYPT_MODE, key);
       }
       
       byte[] dec = in.doFinal(data);
       
       return dec;
   }
   
/*    private static String formatClearText(String text)
   {
       StringBuffer sb = new StringBuffer(text);
       int len = text.length();
       int plen = len % 16;
       if (plen > 0)
       {
           int count = 16 - plen;
           for (int i = 0; i < count; i++)
           {
               sb.append("\0");
           }
       }
       return sb.toString();
   }
   
   private static String formatDesText(String text)
   {
       return text.trim();
   }


private static String test3DESC(String ivStr, String keyStr, String text) throws Exception
   {
       System.out.println("formatText Result: " + text);
       
       CryptUtil util = new CryptUtil(CryptTool.ALGORITHM_3DES, ivStr.getBytes("UTF-8"), keyStr.getBytes("UTF-8"));
       String r = util.encrypt(text, 16);
       System.out.println("En3DES Result: " + r);
       
       r = util.decrypt(r, 16);
       System.out.println("De3DES Result: " + r);
       
       return r;
   }
   
   private static String testEnCBC(String ivStr, String keyStr, String text) throws Exception
   {
       text = formatClearText(text);
       System.out.println("formatText Result: " + text);
       
       CryptUtil util = new CryptUtil(ivStr.getBytes(CHARSET_UTF_8), keyStr.getBytes(CHARSET_UTF_8));
       String r = util.encrypt(text, 16);
       System.out.println("EnCBC Result: " + r);
       return r;
   }
   
   private static String testEnECB(String keyStr, String text) throws Exception
   {
       text = formatClearText(text);
       System.out.println("formatText Result: " + text);
       
       CryptUtil util = new CryptUtil(keyStr.getBytes(CHARSET_UTF_8));
       String r = util.encrypt(text, 16);
       System.out.println("EnECB Result: " + r);
       return r;
   }
   
   private static String testDeCBC(String ivStr, String keyStr, String text) throws Exception
   {
       CryptUtil util = new CryptUtil(ivStr.getBytes(CHARSET_UTF_8), keyStr.getBytes(CHARSET_UTF_8));
       String r = formatDesText(util.decrypt(text, 16));
       System.out.println("DeCBC Result: " + r);
       return r;
   }
   
   private static String testDeECB(String keyStr, String text) throws Exception
   {
       CryptUtil util = new CryptUtil(keyStr.getBytes(CHARSET_UTF_8));
       String r = formatDesText(util.decrypt(text, 16));
       System.out.println("DeECB Result: " + r);
       return r;
   }
   
   private static void testSHA256(String text) throws Exception
   {
       System.out.println(encodeSHA256(text.getBytes(CHARSET_UTF_8)));
       
   }

public static void main(String[] args)
   {
       try
       {
           //String keyStr = "0123456789abcdef";
           //String ivStr = "fedcba9876543210";
           
           // String text = "中国人民银行很好很强大！Abc1234%";
           //String text = "01234567890123456789";
           
            * String rCBC = testEnCBC(ivStr, keyStr, text); String rECB = testEnECB(keyStr, text);
            * 
            * String eCBC = testDeCBC(ivStr, keyStr,
            * "3df57c773a8e1ddd1a18be38a9bb786f8e0db535fc24960f84c1d6ae94a147a43950929ebad10280455ec717bcb47f80");
            * String eECB = testDeECB(keyStr,
            * "c9977035db95ba1a3963c8759a0d778a370148a203585083a7e420a9985090cf377222e061a924c591cd9c27ea163ed4");
            

           //testSHA256(text);
         //String keyStr = generateKeyStr16(false);
         //String ivStr = generateIVStr16(false);
         
         //System.out.println("encode IV: "+ivStr);
         //System.out.println("encode Key: "+keyStr);
         
         //byte[] keyBytes = decryptKey(keyStr, 16).getBytes();
         //byte[] ivBytes = decryptKey(ivStr, 16).getBytes();
         
         byte[] keyBytes = decodeString(keyStr, 16);
         byte[] ivBytes = decodeString(ivStr, 16);
        
        // String text = "中国人民银行很好很强大！Abc1234%";
         String text1 = "中国人民银行很好很强大";
         String text2 = "中国人民";
         
         CryptUtil utilEcb = new CryptUtil(keyBytes);          
         String ecbEnc64 = utilEcb.encrypt(text1);
         System.out.println("ecbEnc64: "+ecbEnc64);
         String ecbDec64 = utilEcb.decrypt(ecbEnc64);
         System.out.println("ecbDec64: "+ecbDec64);
         
         ecbEnc64 = utilEcb.encrypt(text2);
         System.out.println("ecbEnc64: "+ecbEnc64);
         ecbDec64 = utilEcb.decrypt(ecbEnc64);
         System.out.println("ecbDec64: "+ecbDec64);
         
         test3DESC("12345678", "123456781234567812345678", "Abc1234%");
         //7293DEC3486B3615FEB959B7D4642FCB
         //7293DEC3486B3615FEB959B7D4642FCB
         //7BA6C7843CD9E4D2B5504C9B8C0C2660
         //7BA6C7843CD9E4D2
//          CryptUtil utilCbc = new CryptUtil(ivBytes, keyBytes);          
//          String cbcEnc16 = utilCbc.encrypt16(text);
//          System.out.println("cbcEnc16: "+cbcEnc16);
//          String cbcDec16 = utilCbc.decrypt16(cbcEnc16);
//          System.out.println("cbcDec16: "+cbcDec16);          
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
   }*/

}