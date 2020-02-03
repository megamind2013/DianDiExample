package net.itdiandi.java.utils.safety;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.servlet.http.HttpSession;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.safety
* @ClassName RSAUtil
* @Description RSA工具
* @author 刘吉超
* @date 2016-02-24 19:37:36
*/
public class RSAUtil {    
   public static final String PRIVATE_KEY = "RSA_PRIVATE_KEY";
   public static final String PUBLIC_KEY = "RSA_PUBLIC_KEY";
   public static final String PUBLIC_STRING = "RSA_PUBLIC_STRING";
   public static final String PRIVATE_STRING = "RSA_PRIVATE_STRING";
   
   public static final String MODULUS_STRING = "RSA_MODULUS_STRING";
   
   private static String DEFAULT_KEYSTORE = System.getProperty("user.home")+System.getProperty("file.separator")+"RSAKey.txt";
   private static KeyPair keyPairInStore = null;    
   
   private static BouncyCastleProvider provider = new BouncyCastleProvider();
   private static final String CHARSET_UTF_8 = "UTF-8";
   /**
    * * 生成密钥对 *
    * @param session
    *            http会话 *
    * @param keySize
    *            密钥长度 *
    * @throws EncryptException
    */
   public static void generateKeyPair(HttpSession session) throws Exception {
       generateKeyPair(session, CryptTool.DEFAULT_RSAKEY_SIZE);
   }

   public static void generateKeyPair(HttpSession session, int keySize) throws Exception {
       try {            
           if(null==session.getAttribute(PRIVATE_KEY))
           {            
               KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA",
                       provider);
               final int KEY_SIZE = keySize;
               keyPairGen.initialize(KEY_SIZE, new SecureRandom());
               KeyPair keyPair = keyPairGen.generateKeyPair();
               
               RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
               RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
               
               session.setAttribute(PRIVATE_KEY, privateKey);
               session.setAttribute(PUBLIC_KEY, publicKey);
               session.setAttribute(PUBLIC_STRING, bytesToHexString(publicKey.getPublicExponent().toByteArray()));
               session.setAttribute(PRIVATE_STRING, bytesToHexString(privateKey.getPrivateExponent().toByteArray()));
               session.setAttribute(MODULUS_STRING, bytesToHexString(publicKey.getModulus().toByteArray()));
           }
       } catch (Exception e) {
           e.printStackTrace();
           throw new Exception(e.getMessage());
       }
   }
       
    /**
    * * 生成密钥对 *
    * @param keySize
    *            密钥长度 *
    * @return KeyPair *
    * @throws EncryptException
    */
   public static KeyPair generateKeyPairToStore(int keySize, String storePath) throws Exception {
       try {          
           KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA",
                   provider);
           final int KEY_SIZE = keySize;
           keyPairGen.initialize(KEY_SIZE, new SecureRandom());
           keyPairInStore = keyPairGen.generateKeyPair();
           
           saveKeyPair(keyPairInStore, storePath);
           
           return keyPairInStore;
       } catch (Exception e) {
           e.printStackTrace();
           throw new Exception(e.getMessage());
       }
   }

   public static KeyPair getKeyPairFromStore(String storePath) throws Exception {
       if(null==storePath)
       {
           storePath = DEFAULT_KEYSTORE;
       }
       
       FileInputStream fis = new FileInputStream(storePath);
       ObjectInputStream oos = new ObjectInputStream(fis);
       keyPairInStore = (KeyPair) oos.readObject();
       oos.close();
       fis.close();
       
       return keyPairInStore;
   }
   
   public static void saveKeyPair(KeyPair kp, String storePath) throws Exception {

       if(null==storePath)
       {
           storePath = DEFAULT_KEYSTORE;
       }
       FileOutputStream fos = new FileOutputStream(storePath);
       ObjectOutputStream oos = new ObjectOutputStream(fos);
       // 生成密钥
       oos.writeObject(kp);
       oos.close();
       fos.close();
   }

   /**
    * * 生成密钥对 *
    * @param keySize
    *            密钥长度 *
    * @return KeyPair *
    * @throws EncryptException
    */
   public static KeyPair generateRSAKeyPair() throws Exception {
       return CryptTool.generateRSAKeyPair();
   }

   /**
    * * 生成密钥对 *
    * @param keySize
    *            密钥长度 *
    * @return KeyPair *
    * @throws EncryptException
    */
   public static KeyPair generateRSAKeyPair(int keySize) throws Exception {
       return CryptTool.generateRSAKeyPair(keySize);
   }
   
   /**
    * * 生成密钥对16进制形式 *
    * @param keySize 密钥长度 *
    * @return modulusStr;privateKeyStr;publicKeyStr
    * @throws EncryptException
    */
   public static String generateRSAKeyPairStr16() throws Exception {
       return CryptTool.generateRSAKeyPairStr16();
   }   
   
   /**
    * * 生成密钥对16进制形式 *
    * @param keySize 密钥长度 *
    * @return modulusStr;privateKeyStr;publicKeyStr
    * @throws EncryptException
    */
   public static String generateRSAKeyPairStr16(int keySize) throws Exception {
       return CryptTool.generateRSAKeyPairStr16(keySize);
   }
   
   /**
    * * 生成密钥对16进制形式 *
    * @param keySize 密钥长度 *
    * @return modulusStr;privateKeyStr;publicKeyStr
    * @throws EncryptException
    */
   public static String generateRSAKeyPairStr64(int keySize) throws Exception {
       return CryptTool.generateRSAKeyPairStr64(keySize);
   }

   /**
    * * 生成密钥对16进制形式 *
    * @param keySize 密钥长度 *
    * @return modulusStr;privateKeyStr;publicKeyStr
    * @throws EncryptException
    */
   public static String generateRSAKeyPairStr64() throws Exception {
       return CryptTool.generateRSAKeyPairStr64();
   }
   
   /**
    * * 加密 *
    * 
    * @param key
    *            加密的密钥 *
    * @param data
    *            待加密的明文数据 *
    * @return 加密后的数据 *
    * @throws Exception
    */
   public static byte[] encrypt(Key pk, byte[] data) throws Exception {
       try {
           Cipher cipher = Cipher.getInstance("RSA",
                   provider);
           cipher.init(Cipher.ENCRYPT_MODE, pk);
           int blockSize = cipher.getBlockSize();// 获得加密块大小，如：加密前数据为128个byte，而key_size=1024
           // 加密块大小为127
           // byte,加密后为128个byte;因此共有2个加密块，第一个127
           // byte第二个为1个byte
           int outputSize = cipher.getOutputSize(data.length);// 获得加密块加密后块大小
           int leavedSize = data.length % blockSize;
           int blocksSize = leavedSize != 0 ? data.length / blockSize + 1
                   : data.length / blockSize;
           byte[] raw = new byte[outputSize * blocksSize];
           int i = 0;
           while (data.length - i * blockSize > 0) {
               if (data.length - i * blockSize > blockSize)
               {
                   cipher.doFinal(data, i * blockSize, blockSize, raw, i
                           * outputSize);
               }
               else
               {
                   cipher.doFinal(data, i * blockSize, data.length - i
                           * blockSize, raw, i * outputSize);
               }
               i++;
           }
           return raw;
       } catch (Exception e) {
           throw new Exception(e.getMessage());
       }
   }

       /**
    * * 加密 *
    * 
     @param key
    *            加密的密钥 *
    * @param data
    *            待加密的明文数据 *
    * @return 加密后的密文 *
    * @throws Exception
    */
   public static String encrypt(Key pk, String data)
   {        
       String encString = "";
       try{
           byte[] en_result = encrypt(pk, data.getBytes(CHARSET_UTF_8));
           
           encString = bytesToHexString(en_result);
       }catch(Exception e)
       {
           e.printStackTrace();
       }
       
       return encString;
   }
   
   /**
    * * 加密 *
    * 
    * @param session
    *            http会话 *
    * @param data
    *            待加密的明文数据 *
    * @return 加密后的密文 *
    * @throws Exception
    */
   public static String encrypt(HttpSession session, String data)
   {        
       String encString = "";
       try{
           RSAPublicKey pk = (RSAPublicKey)session.getAttribute(PUBLIC_KEY);
           if(null==pk)
           {
               return null;
           }
           byte[] en_result = encrypt(pk, data.getBytes(CHARSET_UTF_8));
           
           encString = bytesToHexString(en_result);
       }catch(Exception e)
       {
           e.printStackTrace();
       }
       
       return encString;
   }


   /**
    * * 加密 *
    * @param data
    *            待加密的明文数据 *
    * @return 加密后的密文 *
    * @throws Exception
    */
   public static String encrypt(String data)
   {        
       String encString = "";
       try{
           if(null==keyPairInStore)
           {
               return null;
           }
           RSAPublicKey pk = (RSAPublicKey)keyPairInStore.getPublic();
           byte[] en_result = encrypt(pk, data.getBytes(CHARSET_UTF_8));
           
           encString = bytesToHexString(en_result);
       }catch(Exception e)
       {
           e.printStackTrace();
       }
       
       return encString;
   } 
  /**
   * 公钥加密
   * @param pubKeyStr
   * @param moduluSTr
   * @param data
   * @param keyEncoder
   * @param dataEncoder
   * @return
   */
   public static String encryptWithPubKey(String pubKeyStr, String moduluStr, String data)
   {        
       String encString = null;
       
       if(null==data)
       {
           return null;
       }
       
       try{
           byte[] pubBytes = hexStringToBytes(pubKeyStr);                          
           byte[] modBytes = hexStringToBytes(moduluStr);
           
           BigInteger intMod = new BigInteger(modBytes);        
           BigInteger intPub = new BigInteger(pubBytes);       
           
           RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(intMod, intPub);
           
           KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      
           RSAPublicKey pubKey = (RSAPublicKey)keyFactory.generatePublic(pubSpec);
           
           encString = encrypt(pubKey, data);
       }catch(Exception e)
       {
           e.printStackTrace();
       }
       
       return encString;
   }


   /**
    * 私钥加密
    * @param prvKeyStr
    * @param moduluSTr
    * @param data
    * @param keyEncoder
    * @param dataEncoder
    * @return
    */
    public static String encryptWithPrvKey(String prvKeyStr, String moduluStr, String data)
    {        
        String encString = null;
        
        if(null==data)
        {
           return null;
        }
        
        try{
            byte[] prvBytes = hexStringToBytes(prvKeyStr);                          
            byte[] modBytes = hexStringToBytes(moduluStr);
            
            BigInteger intMod = new BigInteger(modBytes);        
            BigInteger intPrv = new BigInteger(prvBytes);       
            
            RSAPrivateKeySpec prvSpec = new RSAPrivateKeySpec(intMod, intPrv);
            
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
       
            RSAPrivateKey prvKey = (RSAPrivateKey)keyFactory.generatePrivate(prvSpec);
            
            encString = encrypt(prvKey, data);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return encString;
    }
   
   /**
    * 私钥解密
    * @param prvKeyStr
    * @param moduluSTr
    * @param data
    * @return
    */
   public static String decryptWithPubKey(String pubKeyStr, String moduluStr, String data)
   {        
       String encString = "";
       
       if(null==data)
       {
           return null;
       }
       
       try{
           byte[] pubBytes = hexStringToBytes(pubKeyStr);                          
           byte[] modBytes = hexStringToBytes(moduluStr);
           
           BigInteger intMod = new BigInteger(modBytes);        
           BigInteger intPub = new BigInteger(pubBytes);       
           
           RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(intMod, intPub);
           
           KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      
           RSAPublicKey pubKey = (RSAPublicKey)keyFactory.generatePublic(pubSpec);
           
           encString = decrypt(pubKey, data);
       }catch(Exception e)
       {
           e.printStackTrace();
       }
       
       return encString;
   }
   
   /**
    * 私钥解密
    * @param prvKeyStr
    * @param moduluSTr
    * @param data
    * @return
    */
   public static String decryptWithPubKeyJS(String pubKeyStr, String moduluStr, String data)
   {        
        String s = decryptWithPubKey(pubKeyStr, moduluStr, data);
        String deString = null;
        if(null!=s)
        {
          StringBuffer sb = new StringBuffer(s);
            deString = sb.reverse().toString();
            try {
           deString = URLDecoder.decode(deString, "UTF-8");
       } catch (UnsupportedEncodingException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
        }
        
        return deString;
   }


   /**
    * 私钥解密
    * @param prvKeyStr
    * @param moduluSTr
    * @param data
    * @return
    */
   public static String decryptWithPrvKey(String prvKeyStr, String moduluStr, String data)
   {        
       String encString = "";
       
       if(null==data)
       {
           return null;
       }
       
       try{
           byte[] prvBytes = hexStringToBytes(prvKeyStr);                              
           byte[] modBytes = hexStringToBytes(moduluStr);
                   
           BigInteger intMod = new BigInteger(modBytes);        
           BigInteger intPrv = new BigInteger(prvBytes);    
           
           RSAPrivateKeySpec prvSpec = new RSAPrivateKeySpec(intMod, intPrv);
           
           KeyFactory keyFactory = KeyFactory.getInstance("RSA");
           RSAPrivateKey prvKey = (RSAPrivateKey)keyFactory.generatePrivate(prvSpec);
           
           encString = decrypt(prvKey, data);
       }catch(Exception e)
       {
           e.printStackTrace();
       }
       
       return encString;
   }
   
   /**
    * 私钥解密
    * @param prvKeyStr
    * @param moduluSTr
    * @param data
    * @return
    */
   public static String decryptWithPrvKeyJS(String prvKeyStr, String moduluStr, String data)
   {        
      String s = decryptWithPrvKey(prvKeyStr, moduluStr, data);
      String deString = null;
      if(null!=s)
      {
          StringBuffer sb = new StringBuffer(s);
          deString = sb.reverse().toString();
          try {
           deString = URLDecoder.decode(deString, "UTF-8");
       } catch (UnsupportedEncodingException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
      }
      
      return deString;
   }
   
   /**
    * * 解密 *
    * 
    * @param key
    *            解密的密钥 *
    * @param raw
    *            已经加密的数据 *
    * @return 解密后的明文 *
    * @throws Exception
    */
   public static byte[] decrypt(Key pk, byte[] raw) throws Exception {
       try {
           Cipher cipher = Cipher.getInstance("RSA",
                   provider);
           cipher.init(Cipher.DECRYPT_MODE, pk);
           int blockSize = cipher.getBlockSize();
           
           ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
           int j = 0;

          while (raw.length - j * blockSize > 0) {
               if (raw.length - j * blockSize > blockSize)
               {
                   bout.write(cipher.doFinal(raw, j * blockSize, blockSize));
               }                   
               else
               {
                   bout.write(cipher.doFinal(raw, j * blockSize, raw.length - j * blockSize));
               }                           
               j++;
           }
           return bout.toByteArray();
       } catch (Exception e) {
           throw new Exception(e.getMessage());
       }
   }


   /**
    * * 解密 *
    * 
    * @param key
    *            解密的密钥 *
    * @param encString
    *            已经加密的数据 *
    * @return 解密后的明文 *
    * @throws Exception
    */
   public static String decrypt(Key pk, String encString)
   {        
       String deString = "";
       try{            
           byte[] en_result = hexStringToBytes(encString);
           
           byte[] de_result = decrypt(pk, en_result);
   
           deString = new String(de_result, CHARSET_UTF_8);
       }catch(Exception e)
       {
           e.printStackTrace();
       }
       
       return deString;
   }
   
   /**
    * * 解密 *
    * 
    * @param session
    *            http会话 *
    * @param encString
    *            已经加密的数据 *
    * @return 解密后的明文 *
    * @throws Exception
    */
   public static String decrypt(HttpSession session, String encString)
   {   
       String deString = "";
       try{            
           
           RSAPrivateKey pk = (RSAPrivateKey)session.getAttribute(PRIVATE_KEY);
           if(null==pk)
           {
               return null;
           }
           
           byte[] en_result = hexStringToBytes(encString);
           
           byte[] de_result = decrypt(pk, en_result);
           
           StringBuffer sb = new StringBuffer(new String(de_result, CHARSET_UTF_8));
           deString = sb.reverse().toString();
   
           deString = URLDecoder.decode(deString, "UTF-8");
       }catch(Exception e)
       {
           e.printStackTrace();
       }
       /*finally
       {
           clearInSession(session);
       }
       */
       return deString;
   }
   
   /**
    * 清除会话中的密钥信息
    * <功能详细描述>
    * @param session
    * @see [类、类#方法、类#成员]
    */
   public static void clearInSession(HttpSession session)
   {
       session.removeAttribute(PRIVATE_KEY);
       session.removeAttribute(PUBLIC_KEY);
       session.removeAttribute(PUBLIC_STRING);
       session.removeAttribute(MODULUS_STRING);
   }
   
    /**
    * * 解密 *
    * @param encString
    *            已经加密的数据 *
    * @return 解密后的明文 *
    * @throws Exception
    */
   public static String decrypt(String encString)
   {        
       String deString = "";
       try{           
           if(null==keyPairInStore)
           {
               return null;
           }
           RSAPrivateKey pk = (RSAPrivateKey)keyPairInStore.getPrivate();
           
           byte[] en_result = hexStringToBytes(encString);
           
           byte[] de_result = decrypt(pk, en_result);
               
           deString = new String(de_result, CHARSET_UTF_8);
       }catch(Exception e)
       {
           e.printStackTrace();
       }
       
       return deString;
   }   

   public static String bytesToHexString(byte[] src){  
       StringBuilder stringBuilder = new StringBuilder("");  
       if (src == null || src.length <= 0) {  
           return null;  
       }  
       for (int i = 0; i < src.length; i++) {  
           int v = src[i] & 0xFF;  
           String hv = Integer.toHexString(v);  
           if (hv.length() < 2) {  
               stringBuilder.append(0);  
           }  
           stringBuilder.append(hv);  
       }  
       return stringBuilder.toString();  
   }  

   public static byte[] hexStringToBytes(String hexString) {  
       if (hexString == null || hexString.equals("")) {  
           return null;  
       }  
       hexString = hexString.toUpperCase();  
       int length = hexString.length() / 2;  
       char[] hexChars = hexString.toCharArray();  
       byte[] d = new byte[length];  
       for (int i = 0; i < length; i++) {  
           int pos = i * 2;  
           d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
       }  
       return d;  
   }  


   public static byte charToByte(char c) {  
       return (byte) "0123456789ABCDEF".indexOf(c);  
   }

/*    public static void test()
   {
       try
       {
           String data = "一二三四五，上山打老虎。";
           String keys = generateRSAKeyPairStr16();
           String[] sa = keys.split(";");
           
           String moduluStr = sa[0];
           String prvKeyStr = sa[1];
           String pubKeyStr = sa[2];   
           
           System.out.println("modulus:" + moduluStr);
           System.out.println("privateKey:" + prvKeyStr);
           System.out.println("publicKey:" + pubKeyStr);
           
           String enString = RSAUtil.encryptWithPubKey(pubKeyStr, moduluStr, data);
           System.out.println("encryptWithPubKey:" + enString);
           
           String deString = RSAUtil.decryptWithPrvKey(prvKeyStr, moduluStr, enString);
           System.out.println("decryptWithPrvKey:" + deString);
           
           String enString2 = RSAUtil.encryptWithPrvKey(prvKeyStr, moduluStr, data);
           System.out.println("encryptWithPrvKey:" + enString2);
           String deString2 = RSAUtil.decryptWithPubKey(pubKeyStr, moduluStr, enString2);
           System.out.println("decryptWithPubKey:" + deString2);
       }
       catch (Exception e)
       {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
   }

   *//**
    * * *
    * 
    * @param args *
    * @throws Exception
    *//*
   public static void main(String[] args) throws Exception {
       test();
   }*/
}