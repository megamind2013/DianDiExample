package net.itdiandi.java.utils.safety;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import net.itdiandi.java.utils.ByteUtil;

import java.io.UnsupportedEncodingException;

/** 
* @ProjectName Utils
* @PackageName net.itdiandis.utils.safety
* @ClassName CryptTool
* @Description TODO
* @author 刘吉超
* @date 2016-02-24 20:21:19
*/
public class CryptTool
{
    public static final int ENCODE_HEX = 16;
    
    public static final int ENCODE_BASE64 = 64;
    
    public static final int ALGORITHM_AES_ECB = 0;
    
    public static final int ALGORITHM_AES_CBC = 1;
    
    public static final int ALGORITHM_BLOWFISH_ECB = 2;
    
    public static final int ALGORITHM_BLOWFISH_CBC = 3;
    
    public static final int ALGORITHM_3DES = 5;
    
    protected static final int MIN_ALGORITHM_TYPE = 0;
    
    protected static final int MAX_ALGORITHM_TYPE = 5;
    
    public static final int DEFAULT_AESKEY_SIZE = 128;
    
    public static final int DEFAULT_RSAKEY_SIZE = 1024;
	
	    // 用于加密密钥的密钥
    private static final String KEY_ENCRYPT_KEY = "1wrTF@e$%S8(4D%O";
    
    // 加密模式
    public static final String[] CIPHER_TYPE = {"AES/ECB/PKCS5Padding", "AES/CBC/PKCS5Padding",
            "Blowfish/ECB/PKCS5Padding", "Blowfish/CBC/PKCS5Padding", "RSA", "DESede/CBC/NoPadding"};
    
    // 填充模式
    public static final String[] PADDING_TYPE = {"PKCS5Padding", "NoPadding", "ZeroPadding"};
    
    // 密钥分组长度
    public static final int STR_KEY_LENGTH = 16;
    
    // 密钥长度
    public static final int[] KEY_SIZE = {128, 192, 256, 512, 1024, 2048};
    
    // 密钥类型
    public static final String[] KEY_TYPE = {"AES", "Blowfish", "RSA", "DESede"};
    
    // RSA密钥字符串公钥、密钥分隔符
    public static final String KEY_SEPERATOR = ";";
    
    protected static final BouncyCastleProvider provider = new BouncyCastleProvider();    
    private static final String CHARSET_UTF_8 = "UTF-8";    
	/**
	 * 生成默认长度密钥 生成一个指定AES/128密钥
	 * 
	 * @throws Exception
	 * @return SecretKey
	 */
	public static SecretKey generateKey() throws Exception
    {
        return generateKey(0, DEFAULT_AESKEY_SIZE);
    }
    
    /**
     * 生成默认长度密钥 生成一个指定加密算法的128位密钥，keyType必须是KEY_TYPE常量数组中的一个索引。 KEY_TYPE中目前有AES、Blowfish
     * 
     * @param keyType
     * @throws Exception
     * @return SecretKey
    */
    public static SecretKey generateKey(int keyType) throws Exception
    {
        return generateKey(keyType, DEFAULT_AESKEY_SIZE);
    }
    
    /**
     * 生成默认长度密钥 生成一个指定加密算法的128位密钥，keyType必须是KEY_TYPE常量数组中的一个值。 KEY_TYPE中目前有AES、Blowfish
     * 
     * @param keyType
     * @throws Exception
     * @return SecretKey
    */
    public static SecretKey generateKey(String keyType) throws Exception
    {
        return generateKey(keyType, DEFAULT_AESKEY_SIZE);
    }
	
    /**
     * 生成指定长度密钥 生成一个指定加密算法的keySize位密钥，keyType必须是KEY_TYPE常量数组中的一个索引。 KEY_TYPE中目前有AES、Blowfish；
     * keySize默认只允许128，更大长度（192/256）需要修改JRE设置
     * 
     * @param keyType
     * @param keySize
     * @throws Exception
     * @return SecretKey
    */
    public static SecretKey generateKey(int keyType, int keySize) throws Exception
    {
        return generateKey(KEY_TYPE[keyType], keySize);
    }
    
    /**
     * 生成指定长度密钥 生成一个指定加密算法的keySize位密钥，keyType必须是KEY_TYPE常量数组中的一个值。 KEY_TYPE中目前有AES、Blowfish；
     * keySize默认只允许128，更大长度（192/256）需要修改JRE设置
     * 
     * @param keyType
     * @param keySize
     * @throws Exception
     * @return SecretKey
    */
    public static SecretKey generateKey(String keyType, int keySize) throws Exception
    {
		boolean isValid = false;
        for (int i = 0; i < KEY_TYPE.length; i++)
        {
            if (KEY_TYPE[i].equals(keyType))
            {
                isValid = true;
                break;
            }
        }
        
        if (isValid)
        {
            for (int i = 0; i < KEY_SIZE.length; i++)
            {
                if (KEY_SIZE[i] == keySize)
                {
                    isValid = true;
                    break;
                }
            }
        }
        
        if (isValid)
        {
            /*
            KeyGenerator keyGen = KeyGenerator.getInstance(keyType, provider);
            final int KEY_SIZE = keySize;
            keyGen.init(KEY_SIZE, new SecureRandom());
            SecretKey key = keyGen.generateKey();
            */
            String rnd = generateRndStr(16);
            System.out.println("Key111: "+rnd);
            
            SecretKey key = new SecretKeySpec(rnd.getBytes(CHARSET_UTF_8), keyType);
            
            return key;
        }
        else
        {
            throw new Exception("Invalid KeyType: " + keyType + " or KeySize: " + keySize);
        }
    }
    
    /**
     * 生成CBC加密模式的初始化向量 生成一个16位长度的随机数组
     * 
     * @return byte[]
    */
    public static byte[] generateIV()
    {
        String rnd = generateRndStr(16);
        /*System.out.println("IV111: "+rnd);*/
        try 
        {
            return rnd.getBytes(CHARSET_UTF_8);
        }
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 生成初始化向量字符串 生成一个用于CBC加密模式的随机初始化向量Base64编码字符串形式
     * 
     * @return String
    */
    public static String generateIVStr()
    {
        return generateIVStr(false, ENCODE_BASE64);
    }
    
    public static String generateIVStr16()
    {
        return generateIVStr(false, ENCODE_HEX);
    }
    
    public static String generateIVStr(int encode)
    {
        return generateIVStr(false, encode);
    }
    
    /**
     * 生成初始化向量字符串 生成一个用于CBC加密模式的随机初始化向量Base64编码字符串形式
     * 
     * @param encypted
     * @return String
    */
    public static String generateIVStr(boolean encypted)
    {
          return generateIVStr(encypted, ENCODE_BASE64);
    }
    
    public static String generateIVStr16(boolean encypted)
    {
          return generateIVStr(encypted, ENCODE_HEX);
    }
    
    public static String generateIVStr(boolean encypted, int encode)
    {
        byte[] bytes = generateIV();
        
        if (encypted)
        {
            try
            {
                return encryptKey(bytes, encode);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            return encodeString(bytes, encode);
        }
    }
        
	/**
	 * 生成默认长度密钥字符串 生成一个AES/128位密钥的Base64字符串形式
	 * 
	 * @throws Exception
	 * @return String
	*/
	public static String generateKeyStr() throws Exception
    {
        return generateKeyStr(0, DEFAULT_AESKEY_SIZE);
    }
    
    /**
     * 生成默认长度密钥字符串 生成一个AES/128位密钥的16进制字符串形式
     * 
     * @throws Exception
     * @return String
    */
    public static String generateKeyStr16() throws Exception
    {
        return generateKeyStr(0, DEFAULT_AESKEY_SIZE, ENCODE_HEX);
    }
    
    /**
     * 生成默认长度密钥字符串 生成一个AES/128位密钥的Base64字符串形式
     * 
     * @param encypted
     * @throws Exception
     * @return String
    */
    public static String generateKeyStr(boolean encypted) throws Exception
    {
        byte[] bytes = generateKeyBytes(0, DEFAULT_AESKEY_SIZE);
        if (encypted)
        {
            try
            {
                return encryptKey(bytes);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }else
        {
            return encodeBase64(bytes);
        }
    }
    
    /**
     * 生成默认长度密钥字符串 生成一个AES/128位密钥的16进制字符串形式
     * 
     * @param encypted
     * @throws Exception
     * @return String
    */
    public static String generateKeyStr16(boolean encypted) throws Exception
    {
        byte[] bytes = generateKeyBytes(0, DEFAULT_AESKEY_SIZE);
        if (encypted)
        {
            try
            {
                return encryptKey(bytes, ENCODE_HEX);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
               return null;
            }
        }else
        {
            return byte2HexString(bytes);
        }        
    }
    
    /**
     * 生成默认长度密钥字符串 生成一个指定加密算法的128位密钥的Base64字符串形式，keyType必须是KEY_TYPE常量数组中的一个索引。 KEY_TYPE中目前有AES、Blowfish
     * 
     * @param keyType
     * @throws Exception
     * @return String
    */
    public static String generateKeyStr(int keyType) throws Exception
    {
        return generateKeyStr(keyType, DEFAULT_AESKEY_SIZE);
    }
    
    /**
     * 生成默认长度密钥字符串 生成一个指定加密算法的128位密钥的16进制字符串形式，keyType必须是KEY_TYPE常量数组中的一个索引。 KEY_TYPE中目前有AES、Blowfish
     * 
     * @param keyType
     * @throws Exception
     * @return String
    */
    public static String generateKeyStr16(int keyType) throws Exception
    {
        return generateKeyStr(keyType, DEFAULT_AESKEY_SIZE, ENCODE_HEX);
    }
   
    /**
     * 生成默认长度密钥字符串 生成一个指定加密算法的128位密钥的Base64字符串形式，keyType必须是KEY_TYPE常量数组中的一个索引。 KEY_TYPE中目前有AES、Blowfish
     * 
     * @param keyType
     * @param encypted
     * @throws Exception
     * @return String
    */
    public static String generateKeyStr(int keyType, boolean encypted) throws Exception
    {
        byte[] bytes = generateKeyBytes(keyType, DEFAULT_AESKEY_SIZE);
        
        if (encypted)
        {
            try
            {
                return encryptKey(bytes);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }else
        {
            return encodeBase64(bytes);
        }
    }

    /**
     * 生成默认长度密钥字符串 生成一个指定加密算法的128位密钥的16进制字符串形式，keyType必须是KEY_TYPE常量数组中的一个索引。 KEY_TYPE中目前有AES、Blowfish
     * 
     * @param keyType
     * @param encypted
     * @throws Exception
     * @return String
    */
    public static String generateKeyStr16(int keyType, boolean encypted) throws Exception
    {
        byte[] bytes = generateKeyBytes(keyType, DEFAULT_AESKEY_SIZE);
        if (encypted)
        {
            try
            {
                return encryptKey(bytes, ENCODE_HEX);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }else
        {
            return byte2HexString(bytes);
        }
    }
    
    /**
     * 生成默认长度密钥字符串 生成一个指定加密算法的128位密钥的Base64字符串形式，keyType必须是KEY_TYPE常量数组中的一个值。 KEY_TYPE中目前有AES、Blowfish
     * 
     * @param keyType
     * @throws Exception
     * @return String
    */
    public static String generateKeyStr(String keyType) throws Exception
    {
        return generateKeyStr(keyType, DEFAULT_AESKEY_SIZE);
    }
	
    /**
     * 生成默认长度密钥字符串 生成一个指定加密算法的128位密钥的16进制字符串形式，keyType必须是KEY_TYPE常量数组中的一个值。 KEY_TYPE中目前有AES、Blowfish
     * 
     * @param keyType
     * @throws Exception
     * @return String
    */
    public static String generateKeyStr16(String keyType) throws Exception
    {
        return generateKeyStr16(keyType, DEFAULT_AESKEY_SIZE);
    }
    
    /**
     * 生成指定长度密钥字符串 生成一个指定加密算法的128位密钥的Base64编码字符串形式，keyType必须是KEY_TYPE常量数组中的一个索引。
     * KEY_TYPE中目前有AES、Blowfish；keySize默认只允许128，更大长度（192/256）需要修改JRE设置
     * 
     * @param keyType
     * @param keySize
     * @throws Exception
     * @return String
    */
    public static String generateKeyStr(int keyType, int keySize) throws Exception
    {
        return generateKeyStr(keyType, keySize, ENCODE_BASE64);
    }
    
    /**
     * 生成指定长度密钥字符串 生成一个指定加密算法的128位密钥的16进制形式，keyType必须是KEY_TYPE常量数组中的一个索引。
     * KEY_TYPE中目前有AES、Blowfish；keySize默认只允许128，更大长度（192/256）需要修改JRE设置
     * 
     * @param keyType
     * @param keySize
     * @throws Exception
     * @return String
    */
    public static String generateKeyStr16(int keyType, int keySize) throws Exception
    {
        return generateKeyStr(keyType, keySize, ENCODE_HEX);
    }
	    /**
     * 生成指定长度密钥字符串 生成一个指定加密算法的128位密钥的Base64编码字符串形式，keyType必须是KEY_TYPE常量数组中的一个索引。
     * KEY_TYPE中目前有AES、Blowfish；keySize默认只允许128，更大长度（192/256）需要修改JRE设置
     * 
     * @param keyType
     * @param keySize
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static byte[] generateKeyBytes(int keyType, int keySize) throws Exception
    {
        SecretKey key = generateKey(keyType, keySize);
        if (null != key)
        {
            return key.getEncoded();
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 生成指定长度密钥字符串 生成一个指定加密算法的128位密钥的Base64编码字符串形式，keyType必须是KEY_TYPE常量数组中的一个索引。
     * KEY_TYPE中目前有AES、Blowfish；keySize默认只允许128，更大长度（192/256）需要修改JRE设置
     * 
     * @param keyType
     * @param keySize
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static String generateKeyStr(int keyType, int keySize, int encode) throws Exception
    {
        return encodeString(generateKeyBytes(keyType, keySize), encode);
    } 
	    /**
     * 生成指定长度密钥字符串 生成一个指定加密算法的128位密钥的Base64编码字符串形式，keyType必须是KEY_TYPE常量数组中的一个索引。
     * KEY_TYPE中目前有AES、Blowfish；keySize默认只允许128，更大长度（192/256）需要修改JRE设置
     * 
     * @param keyType
     * @param keySize
     * @param encypted 是否使用加密
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static String generateKeyStr(int keyType, int keySize, boolean encypted) throws Exception
    {
        byte[] bytes =  generateKeyBytes(keyType, keySize);
        if (encypted)
        {
            try
            {
                return encryptKey(bytes);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }else
        {
            return encodeBase64(bytes);
        }
    }
    
    /**
     * 生成指定长度密钥字符串 生成一个指定加密算法的128位密钥的16进制字符串形式，keyType必须是KEY_TYPE常量数组中的一个索引。
     * KEY_TYPE中目前有AES、Blowfish；keySize默认只允许128，更大长度（192/256）需要修改JRE设置
     * 
     * @param keyType
     * @param keySize
     * @param encypted 是否使用加密
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static String generateKeyStr16(int keyType, int keySize, boolean encypted) throws Exception
    {
        byte[] bytes =  generateKeyBytes(keyType, keySize);
        if (encypted)
        {
            try
            {
                return encryptKey(bytes, ENCODE_HEX);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }else
        {
            return byte2HexString(bytes);
        }
    }
	    /**
     * 生成指定长度密钥字符串 生成一个指定加密算法的128位密钥的Base64编码字符串形式，keyType必须是KEY_TYPE常量数组中的一个值。
     * KEY_TYPE中目前有AES、Blowfish；keySize默认只允许128，更大长度（192/256）需要修改JRE设置
     * 
     * @param keyType
     * @param keySize
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static String generateKeyStr(String keyType, int keySize) throws Exception
    {
        SecretKey key = generateKey(keyType, keySize);
        if (null != key)
        {
            return encodeBase64(key.getEncoded());
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 生成指定长度密钥字符串 生成一个指定加密算法的128位密钥的16进制字符串形式，keyType必须是KEY_TYPE常量数组中的一个值。
     * KEY_TYPE中目前有AES、Blowfish；keySize默认只允许128，更大长度（192/256）需要修改JRE设置
     * 
     * @param keyType
     * @param keySize
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static String generateKeyStr16(String keyType, int keySize) throws Exception
    {
        SecretKey key = generateKey(keyType, keySize);
        if (null != key)
        {
            return byte2HexString(key.getEncoded());
        }
        else
        {
            return null;
        }
    }
	    /**
     * * 生成密钥对 *
     * 
     * @param keySize 密钥长度 *
     * @return KeyPair *
     * @throws EncryptException
     */
    public static KeyPair generateRSAKeyPair() throws Exception
    {
        return generateRSAKeyPair(DEFAULT_RSAKEY_SIZE);
    }
    
    /**
     * * 生成密钥对 *
     * 
     * @param keySize 密钥长度 *
     * @return KeyPair *
     * @throws EncryptException
     */
    public static KeyPair generateRSAKeyPair(int keySize) throws Exception
    {
        try
        {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA", provider);
            final int KEY_SIZE = keySize;
            keyPairGen.initialize(KEY_SIZE, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();
            
            return keyPair;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
	    /**
     * * 生成密钥对16进制形式 *
     * 
     * @param keySize 密钥长度 *
     * @return modulusStr;privateKeyStr;publicKeyStr
     * @throws EncryptException
     */
    public static String generateRSAKeyPairStr16() throws Exception
    {
        return generateRSAKeyPairStr16(DEFAULT_RSAKEY_SIZE, false);
    }
    
    /**
     * * 生成密钥对16进制形式 *
     * 
     * @param keySize 密钥长度 *
      * @param encypted 是否使用加密
     * @return modulusStr;privateKeyStr;publicKeyStr
     * @throws EncryptException
     */
    public static String generateRSAKeyPairStr16(boolean encypted) throws Exception
    {
        return generateRSAKeyPairStr16(DEFAULT_RSAKEY_SIZE, encypted);
    }
    
    /**
     * * 生成密钥对16进制形式 *
     * 
     * @param keySize 密钥长度 *
     * @return modulusStr;privateKeyStr;publicKeyStr
     * @throws EncryptException
     */
    public static String generateRSAKeyPairStr16(int keySize) throws Exception
    {
        return generateRSAKeyPairStr16(keySize, false);
    }
	    /**
     * * 生成密钥对16进制形式 *
     * 
     * @param keySize 密钥长度 *
     * @param encypted 是否使用加密
     * @return modulusStr;privateKeyStr;publicKeyStr
     * @throws EncryptException
     */
    public static String generateRSAKeyPairStr16(int keySize, boolean encypted) throws Exception
    {
        return generateRSAKeyPairStr16(keySize, encypted, ENCODE_BASE64);
    }
    
    /**
     * * 生成密钥对16进制形式 *
     * 
     * @param keySize 密钥长度 *
     * @param encypted 是否使用加密
     * @return modulusStr;privateKeyStr;publicKeyStr
     * @throws EncryptException
     */
    public static String generateRSAKeyPairStr16(int keySize, boolean encypted, int encode) throws Exception
    {
        try
        {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA", provider);
            final int KEY_SIZE = keySize;
            keyPairGen.initialize(KEY_SIZE, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();
            
            RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
            
            String mod = bytesToHexString(publicKey.getModulus().toByteArray());
            String prv = bytesToHexString(privateKey.getPrivateExponent().toByteArray());
            String pub = bytesToHexString(publicKey.getPublicExponent().toByteArray());
            
            if(encypted)
            {
                mod = encryptKey(mod, encode);
                prv = encryptKey(prv, encode);
                pub = encryptKey(pub, encode);
            }
			            StringBuffer sb = new StringBuffer(mod).append(KEY_SEPERATOR);
            sb.append(prv).append(KEY_SEPERATOR);
            sb.append(pub);
            
            return sb.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
    
    /**
     * * 生成密钥对Base64形式 *
     * 
     * @param keySize 密钥长度 *
     * @return modulusStr;privateKeyStr;publicKeyStr
     * @throws EncryptException
     */
    public static String generateRSAKeyPairStr64() throws Exception
    {
        return generateRSAKeyPairStr64(DEFAULT_RSAKEY_SIZE, false);
    }
    
    /**
     * * 生成密钥对Base64形式 *
     * 
     * @param keySize 密钥长度 *
      * @param encypted 是否使用加密
     * @return modulusStr;privateKeyStr;publicKeyStr
     * @throws EncryptException
     */
    public static String generateRSAKeyPairStr64(boolean encypted) throws Exception
    {
        return generateRSAKeyPairStr64(DEFAULT_RSAKEY_SIZE, encypted);
    }
	    /**
     * * 生成密钥对Base64形式 *
     * 
     * @param keySize 密钥长度 *
     * @return modulusStr;privateKeyStr;publicKeyStr
     * @throws EncryptException
     */
    public static String generateRSAKeyPairStr64(int keySize) throws Exception
    {
        return generateRSAKeyPairStr64(keySize, false);
    }
    
    /**
     * * 生成密钥对Base64形式 *
     * 
     * @param keySize 密钥长度 *
     * @param encypted 是否使用加密
     * @return modulusStr;privateKeyStr;publicKeyStr
     * @throws EncryptException
     */
    public static String generateRSAKeyPairStr64(int keySize, boolean encypted) throws Exception
    {
        return generateRSAKeyPairStr64(keySize, encypted, ENCODE_BASE64);
    }
    
    /**
     * * 生成密钥对Base64形式 *
     * 
     * @param keySize 密钥长度 *
     * @param encypted 是否使用加密
     * @return modulusStr;privateKeyStr;publicKeyStr
     * @throws EncryptException
     */
    public static String generateRSAKeyPairStr64(int keySize, boolean encypted, int encode) throws Exception
    {
        try
        {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA", provider);
            final int KEY_SIZE = keySize;
            keyPairGen.initialize(KEY_SIZE, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();
			            RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
            
            String mod = encodeBase64(publicKey.getModulus().toByteArray());
            String prv = encodeBase64(privateKey.getPrivateExponent().toByteArray());
            String pub = encodeBase64(publicKey.getPublicExponent().toByteArray());
            
            if(encypted)
            {
                mod = encryptKey(mod, encode);
                prv = encryptKey(prv, encode);
                pub = encryptKey(pub, encode);
            }
            
            StringBuffer sb = new StringBuffer(mod).append(KEY_SEPERATOR);
            sb.append(prv).append(KEY_SEPERATOR);
            sb.append(pub);
            
            return sb.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
    
    /**
     * 以指定密钥对文本进行AES/ECB模式加密
     * 
     * @param keyStr Base64格式字符串密钥
     * @param clearStr 要加密的文本
     * @return Base64格式的密文
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static String encryptKey(byte[] keyByte) throws Exception
    {        
        return encryptKey(keyByte, ENCODE_BASE64);
    }
	    public static String encryptKey(byte[] keyByte, int encode) throws Exception
    {
        SecretKeySpec key = new SecretKeySpec(KEY_ENCRYPT_KEY.getBytes(CHARSET_UTF_8), "AES");
        Cipher in = Cipher.getInstance("AES/ECB/PKCS5Padding", provider);
        
        in.init(Cipher.ENCRYPT_MODE, key);
        
        byte[] enc = in.doFinal(keyByte);
        
        return encodeString(enc, encode);
    }
    
    /**
     * 以指定密钥对文本进行AES/ECB模式加密
     * 
     * @param keyStr Base64格式字符串密钥
     * @param clearStr 要加密的文本
     * @return Base64格式的密文
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static String encryptKey(String clearStr) throws Exception
    {       
        return encryptKey(clearStr.getBytes(CHARSET_UTF_8));
    }
    
    public static String encryptKey(String clearStr, int encode) throws Exception
    {       
        return encryptKey(clearStr.getBytes(CHARSET_UTF_8), encode);
    }
	
	/**
     * 以指定密钥对密文进行AES/ECB模式解密
     * 
     * @param keyStr Base64格式字符串密钥
     * @param encStr Base64格式的密文
     * @return 明文
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static String decryptKey(String encStr) throws Exception
    {        
        return decryptKey(encStr, ENCODE_BASE64);
    }
    
    /**
     * 以指定密钥对密文进行AES/ECB模式解密
     * 
     * @param keyStr Base64格式字符串密钥
     * @param encStr Base64格式的密文
     * @return 明文
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static String decryptKey(String encStr, int encode) throws Exception
    {
        SecretKeySpec key = new SecretKeySpec(KEY_ENCRYPT_KEY.getBytes(CHARSET_UTF_8), "AES");
        Cipher in = Cipher.getInstance("AES/ECB/PKCS5Padding", provider);
        
        in.init(Cipher.DECRYPT_MODE, key);
        
        byte[] data = decodeString(encStr, encode);
                     
        data = in.doFinal(data);
        
        return new String(data, CHARSET_UTF_8);
    }
	    /**
     * 以指定密钥对密文进行AES/ECB模式解密
     * 
     * @param keyStr Base64格式字符串密钥
     * @param encStr Base64格式的密文
     * @return 明文
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static byte[] decryptKey(byte[] encStr)
    {
        try
        {
            SecretKeySpec key = new SecretKeySpec(KEY_ENCRYPT_KEY.getBytes(CHARSET_UTF_8), "AES");
            Cipher in;
            in = Cipher.getInstance("AES/ECB/PKCS5Padding", provider);
            in.init(Cipher.DECRYPT_MODE, key);
            
            byte[] data = in.doFinal(encStr);
            
            return data;
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }                     
    }    
    
    public static byte[] decodeBase64(String encodedStr)
    {
        try
        {
            return Base64.decode(encodedStr.getBytes(CHARSET_UTF_8));
        }
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
            return null;
        }
    }
	
	    public static byte[] encodeBase64(String unencodestr)
    {
        try
        {
            return Base64.encode(unencodestr.getBytes(CHARSET_UTF_8));
        }
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String decodeBase64(byte[] encodedBytes)
    {
        try
        {
            return new String(Base64.decode(encodedBytes), CHARSET_UTF_8);
        }
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String encodeBase64(byte[] unencodeBytes)
    {
        try
        {
            return new String(Base64.encode(unencodeBytes), CHARSET_UTF_8);
        }
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
            return null;
        }
    }
	
	    public static String encodeString(byte[] bytes, int encode)
    {
        if (null != bytes)
        {
            if(encode==ENCODE_HEX)
            {
                return byte2HexString(bytes);
            }else
            {
                return encodeBase64(bytes);
            }
        }
        else
        {
            return null;
        }
    }
    
    public static byte[] decodeString(String str, int encode)
    {
        if (null != str)
        {
            if(encode==ENCODE_HEX)
            {
                return ByteUtil.hexStringToBytes(str);
            }else
            {
                return decodeBase64(str);
            }
        }
        else
        {
            return null;
        }
    }
    
    protected static byte[] hex2Bin(String src)
    {
        if (src.length() < 1)
            return null;
        byte[] encrypted = new byte[src.length() / 2];
        for (int i = 0; i < src.length() / 2; i++)
        {
            int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);
            
            encrypted[i] = (byte)(high * 16 + low);
        }
        return encrypted;
    }
	
	    public static String byte2HexString(byte buf[])
    {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;
        
        for (i = 0; i < buf.length; i++)
        {
            if (((int)buf[i] & 0xff) < 0x10)
                strbuf.append("0");
            
            strbuf.append(Long.toString((int)buf[i] & 0xff, 16));
        }
        
        return strbuf.toString();
    }
    
    public static String bytesToHexString(byte[] src)
    {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0)
        {
            return null;
        }
        for (int i = 0; i < src.length; i++)
        {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2)
            {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
	
	//    public static byte[] hexStringToBytes(String hexString)
//    {
//        if (hexString == null || hexString.equals(""))
//        {
//            return null;
//        }
//        hexString = hexString.toUpperCase();
//        int length = hexString.length() / 2;
//        char[] hexChars = hexString.toCharArray();
//        byte[] d = new byte[length];
//        for (int i = 0; i < length; i++)
//        {
//            int pos = i * 2;
//            d[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
//        }
//        return d;
//    }
    
    public static byte charToByte(char c)
    {
        return (byte)"0123456789ABCDEF".indexOf(c);
    }
    
    public static String generateRndStr(int len)
    {
        SecureRandom random = new SecureRandom();
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < 16; i++)
        {
            int c = random.nextInt(128);
            if (c < 33)
            {
                c = 33 + c;
            }
            sb.append((char)c);
        }
        String s = sb.toString();
        return s;
    }
	
	   protected static void showHelp()
    {
        System.out.println("param list: ");
        System.out.println("   [-t] keyTpye(0:AES/ECB,1:AES/CBC,2:Blowfish/ECB,3:Blowfish/CBC,4:RSA, default is 0)");
        System.out.println("   [-s] keySize(128,192,256,512,1024 default is 128)");
        System.out.println("   [-e] encrypted(0,1/true,false default is false)");
        System.out.println("examplse: ");
        System.out.println("   1.generate clear AES/ECB/128 key");
        System.out.println("      CryptUtil");
        System.out.println("   2.generate encrypted AES/CBC/128 key");
        System.out.println("      CryptUtil -t 1 -e 1");
        System.out.println("   3.generate clear Blowfish/ECB/256 key");
        System.out.println("      CryptUtil -t 2 -s 256");
        System.out.println("   4.generate clear RSA/1024 key");
        System.out.println("      CryptUtil -t 4");
    }
    
    /**
     * 密钥生成界面
     * 
     * @param args
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected static void generateKeyByCmd(String[] args)
    {
        try
        {
            int count = args.length;
            
            if (count == 0)
            {
                System.out.println("clear Key: " + generateKeyStr());
            }
            else
            {
                int keyType = 0;
                int keySize = 128;
                
                HashMap paraMap = new HashMap(count / 2);
                
                for (int i = 0; i < count; i = i + 2)
                {
                    paraMap.put(args[i].trim(), args[i + 1].trim());
                }
				                Iterator it = paraMap.entrySet().iterator();
                String key;
                String value;
                boolean encrypted = false;
                Entry entry;
                while (it.hasNext())
                {
                    entry = (Entry)it.next();
                    key = (String)entry.getKey();
                    value = (String)entry.getValue();
                    
                    if (key.startsWith("-t"))
                    {
                        keyType = Integer.parseInt(value);
                    }
                    else if (key.startsWith("-s"))
                    {
                        keySize = Integer.parseInt(value);
                    }
                    else if (key.startsWith("-e"))
                    {
                        if("1".equals(value) || "true".equals(value.toLowerCase()))
                        {
                            encrypted = true;
                        }
                    }
                    else
                    {
                        System.out.println("Invalid Parameters, help is following:");
                        showHelp();
                        return;
                    }
                }
                
                String pre = encrypted ? "encrypted" : "clear";
                
                switch (keyType)
                {
                    case 0:
                    case 2:
                        System.out.println(pre + " Key: " + generateKeyStr(keyType, keySize, encrypted));
                        break;
                    case 1:
                    case 3:
                        System.out.println(pre + " IV: " + generateIVStr(encrypted));
                        System.out.println(pre + " Key: " + generateKeyStr(keyType, keySize, encrypted));
                        break;
                    case 4:
                        if (keySize < 1024)
                        {
                            keySize = 1024;
                        }
                        String keys = generateRSAKeyPairStr16(keySize, encrypted);
                        String[] sa = keys.split(KEY_SEPERATOR);
                        
                        System.out.println(pre + " Modulus: " + sa[0]);
                        System.out.println(pre + " PrivateKey: " + sa[1]);
                        System.out.println(pre + " PublicKey: " + sa[2]);
                        break;
                    default:
                        System.out.println("Invalid Parameters, help is following:");
                        showHelp();
				}
                
            }
        }
        catch (Exception e)
        {
            System.out.println("Invalid Parameters, help is following:");
            showHelp();
        }
    }
    
/*    public static void main(String[] args)
    {
        try
        {
            //generateKeyByCmd(args);
            // String k = new String(encodeBase64(KEY_ENCRYPT_KEY));
           // String k = generateKeyStr();
            //System.out.println(k);
           // System.out.println(new String(decodeBase64(k)));
            
            String key = encryptAesECB(KEY_ENCRYPT_KEY, "一二三四五，上山打老虎。");
            System.out.println(key);
            byte[] data = decodeBase64(key);
            key = decryptAesECB(KEY_ENCRYPT_KEY, key);
            System.out.println(key);
            key = decryptAesECB(KEY_ENCRYPT_KEY, data);
            System.out.println(key);       
                 
            for(int i=0; i<10; i++)
            {
                System.out.println(generateRndStr(16));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/
}