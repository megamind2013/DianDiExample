package net.itdiandi.java.utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils
* @ClassName ByteUtil
* @Description Byte工具类
* @author 刘吉超
* @date 2016-02-24 10:40:17
*/
public class ByteUtil {
	private static Logger logger = LoggerFactory.getLogger(ByteUtil.class);
	// 默认编码
	private static final String ENCODING = "UTF-8";

	/**
	 * 二进制转换为长整型
	 * 
	 * @param value
	 * @return long类型
	 */
	public static long byteToLong(byte[] value) {
		long ret = 0;
		try {
			ret = byteToLong(value,0);
		} catch (Exception e) {
			logger.error("byte to long error:", e);
		}
		return ret;
	}
	
	/**
	 * 二进制转换为长整型
	 * 
	 * @param value
	 * @param index
	 * @return long类型
	 */
	public static long byteToLong(byte[] value, int index) {
		long ret = 0;
		try {
			ret = byteToLong(value,index,value.length);
		} catch (Exception e) {
			logger.error("byte to long error:", e);
		}
		return ret;
	}
	
	/**
	 * 二进制转换为长整型
	 * 
	 * @param value
	 * @param index
	 * @param length
	 * @return long类型
	 */
	public static long byteToLong(byte[] value, int index, int length) {
		long ret = 0;
		try {
			for (int i = index; i < value.length && i < index + length; i++) {
				ret = (ret << 8) | (value[i] & 0xff);
			}
		} catch (Exception e) {
			logger.error("byte to long error:", e);
		}

		return ret;
	}

	/**
	 * 二进制转换为Ip格式 
	 * 
	 * @param value
	 * @param index
	 * @return String类型
	 */
	public static String byteToIP(byte[] value, int index) {
		String ret = null;
		try {
			if ((value != null) && (value.length >= 4 + index)) {
				ret = new StringBuilder(value[index] & 0xFF).append('.')
						.append(value[index + 1] & 0xFF).append('.')
						.append(value[index + 2] & 0xFF).append('.')
						.append(value[index + 3] & 0xFF).toString();
			}
		} catch (Exception e) {
			logger.error("byte to ip error:" + e);
		}
		return ret;
	}

	/**
	 * 二进制转换为String
	 * 
	 * @param value
	 * @param index
	 * @param length
	 * @return String类型
	 */
	public static String byteToString(byte[] value, int index, int length) {
		String returnStr = "";
		try {
			returnStr =  new String(value, index, length, ByteUtil.ENCODING);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		return returnStr;
	}

	/**
	 * byte数组转换为16进制字符
	 * 
	 * @param src
	 * @return String类型
	 */
	public static String bytesToHexString(byte[] src) {
		if (src == null || src.length <= 0) {
			return "";
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i = 0; i < src.length; i++) {
			String hv = Integer.toHexString(src[i] & 0xFF);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 16进制转化为byte数组
	 * 
	 * @param hexString
	 * @return byte数组
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (StringUtils.isBlank(hexString)) {
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

	/**
	 * 字符转换为byte
	 * 
	 * @param c
	 * @return byte
	 */
	public static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	
	public static void main(String[] args) {
		System.out.println(Arrays.toString(hexStringToBytes("23")));
	}
}