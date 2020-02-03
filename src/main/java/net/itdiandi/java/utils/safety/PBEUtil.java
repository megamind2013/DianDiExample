package net.itdiandi.java.utils.safety;

import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.safety
* @ClassName PBEUtil
* @Description 基于口令加密工具
* @author 刘吉超
* @date 2016-02-24 18:29:09
*/
public class PBEUtil {
	private static final Logger logger = LoggerFactory.getLogger(PBEUtil.class);
	// 盐
	private static final String SALT = "buaabuaa";
	// 口令
	private static final String PASSWORD = "ljc";
	// PBEWithMD5AndDES算法
	private static final String ALGORITHM = "PBEWithMD5AndDES";
	// 编码
	private static final String ENCODING = "UTF-8";
	
	// 标识
	private static boolean INIT_SUCCESS = Boolean.FALSE;
	// 实例对象
	private static final PBEUtil INSTANCE = new PBEUtil();
	
	private Cipher encrypter;
	private Cipher decrypter;
	
	/**
	 * 私有构造方法，单例模式
	 */
	private PBEUtil() {
		char[] chPass = PBEUtil.PASSWORD.toCharArray();
		
		try {
			SecretKey key = SecretKeyFactory.getInstance(PBEUtil.ALGORITHM).generateSecret(new PBEKeySpec(chPass));
			PBEParameterSpec pSpec = new PBEParameterSpec(PBEUtil.SALT.getBytes(PBEUtil.ENCODING), 20);
			
			encrypter = Cipher.getInstance(PBEUtil.ALGORITHM);
			decrypter = Cipher.getInstance(PBEUtil.ALGORITHM);
			
			encrypter.init(Cipher.ENCRYPT_MODE, key, pSpec);
			decrypter.init(Cipher.DECRYPT_MODE, key, pSpec);

			PBEUtil.INIT_SUCCESS = Boolean.TRUE;
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 加密操作
	 * 
	 * @param plaintText
	 * @return String
	*/
	private String innerEncrypt(String plaintText) {
		if (!PBEUtil.INIT_SUCCESS) {
			logger.error("INIT_SUCCESS = false");
			return "";
		} else {
			try {
				byte[] output = plaintText.getBytes(PBEUtil.ENCODING);
				
				byte[] enc = encrypter.doFinal(output);
				
				// PBE加密后，再进行Base64加密
				String strCipherText = this.encodeForBase64(enc);

				return strCipherText;
			} catch (Exception e) {
				logger.error("", e);
				return "";
			}
		}
	}
	
	/**
	 * 解密操作
	 * 
	 * @param ciphertext
	 * @return String
	*/
	private String innerDecrypt(String ciphertext) {
		if (!PBEUtil.INIT_SUCCESS) {
			logger.error("INIT_SUCCESS = false");
			return "";
		} else {
			try {
				byte[] dec = this.decodeFromBase64(ciphertext);
				byte[] output = decrypter.doFinal(dec);
				
				String strDecrytText = new String(output,PBEUtil.ENCODING);

				return strDecrytText;
			} catch (Exception e) {
				logger.error("", e);
				return "";
			}
		}
	}

	/**
	 * base64解密
	 * 
	 * @param input
	 * @throws IOException
	 * @return byte[]
	*/
	private byte[] decodeFromBase64(String input) throws IOException {
		return Base64.decodeBase64(input);
	}

	/**
	 * base64加密
	 * 
	 * @param input
	 * @return String
	*/
	private String encodeForBase64(byte[] input) {
		return Base64.encodeBase64String(input);
	}

	/**
	 * 加密操作，暴漏给外部使用
	 * 
	 * @param text
	 * @return String
	*/
	public static String encrypt(String text) {
		if (StringUtils.isEmpty(text)) {
			return text;
		}

		return PBEUtil.INSTANCE.innerEncrypt(text);
	}
	
	/**
	 * 解密操作，暴漏给外部使用
	 * 
	 * @param text
	 * @return String
	*/
	public static String decrypt(String text) {
		if (StringUtils.isEmpty(text)) {
			return text;
		}

		return PBEUtil.INSTANCE.innerDecrypt(text);
	}
	
	public static void main(String[] args) {
		System.out.println(PBEUtil.encrypt("PEEUtil@ljc"));
		System.out.println(PBEUtil.encrypt("123"));
		System.out.println(PBEUtil.decrypt("IDwlcvqfEqJeUyPL0kUO1w=="));
	}
}