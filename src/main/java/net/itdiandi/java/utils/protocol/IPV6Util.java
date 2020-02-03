package net.itdiandi.java.utils.protocol;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.net.util.IPAddressUtil;

/** 
* @ProjectName ProjectSummary
* @PackageName com.java.utils
* @ClassName IPV6Util
* @Description ipv6工具
* @Author 刘吉超
* @Date 2015-07-20 11:38:16
*/
public class IPV6Util {
	private static Logger logger = LoggerFactory.getLogger(IPV6Util.class);// 日志类
	
	private static final String HEX_STRING = "0123456789ABCDEF"; // 16进制字串
	private static final String IPV6_FIELD_SEPARATOR = ":"; // IPv6分割符
	private static final String IPV4_TO_IPV6_PREFIX = "00000000000000000000FFFF"; // ipv4映像地址转ipv6数字串前缀
	private static final String IPV4_TO_IPV6_NORMAILIZATION_PREFIX = "0000:0000:0000:0000:0000:FFFF:"; // ipv4映像地址转ipv6数字串前缀

	/**
	 * <pre>
	 * 判断是不是IPv6
	 * 合法的格式：
	 * 	:: -> "0:0:0:0:0:0:0"
	 * 	1::1 -> "1:0:0:0:0:0:1"
	 * 	::FFFF:192.1.1.1 -> "0:0:0:0:0:FFFF:C001:0101" IPv4映像地址
	 * 	性能：CPU:3.2GHZ 4.5s/1000W次
	 * </pre>
	 * 
	 * @param ip 要判断的IP
	 * @return true:是合法的IPv6 false:不是合法的IPv6
	 */
	public static boolean isIPv6(String ip) {
		return IPAddressUtil.isIPv6LiteralAddress(StringUtils.trimToEmpty(ip));
	}

	/**
	 * <pre>
	 * 比较两个IPv6地址的大小
	 * 支持IPv4映像地址
	 * 性能：CPU:3.2GHZ 15.3s/1000W次
	 * </pre>
	 * 
	 * @param ip1 要比较的IPv6 1
	 * @param ip2 要比较的IPv6 1
	 * @return 1 : ip1 > ip2<br>
	 *         0 : ip1 = ip2<br>
	 *         -1: ip1 < ip2
	 * @throws IllegalArgumentException 如果ip不是合法的IPv6,抛出异常
	 */
	public static int ipv6Compare(String ip1, String ip2) {
		String NumericIp1 = ipv6ToNumeric(ip1);
		String NumericIp2 = ipv6ToNumeric(ip2);
		if (NumericIp1 == null || NumericIp2 == null) {
			logger.error("IPv6 format error: IP1-: " + ip1 + "  ip2-: " + ip2);
		}

		int result = NumericIp1.compareTo(NumericIp2);

		if (result > 0) {
			return 1;
		} else {
			if (result == 0) {
				return 0;
			}
			return -1;
		}
	}

	/**
	 * <pre>
	 * IPv6转换成32+7位16进制格式
	 *  "::"    -> "0000:0000:0000:0000:0000:0000:0000:0000"
	 *  "ffff:1:1:1:1:1:1:1"    -> "FFFF:0001:0001:0001:0001:0001:0001:0001"
	 *  "::ffff:192.1.1.1"    -> "0000:0000:0000:0000:0000:FFFF:C001:0101" IPv4映像地址
	 *  性能：CPU:3.2GHZ 9.6s/1000W次
	 * @param ip 要转换的IPv6 ip
	 * @return 转换后的字串,或者null 如果字串不合法
	 */
	public static String ipv6Normalize(String ip) {
		byte[] byteIp = IPAddressUtil.textToNumericFormatV6(StringUtils .trimToEmpty(ip));

		if (byteIp == null) {
			return null;
		}

		int length = byteIp.length;

		StringBuffer sb = new StringBuffer();

		// 处理IPv4映像地址
		if (length == 4) {
			sb.append(IPV4_TO_IPV6_NORMAILIZATION_PREFIX);
		} else if (length != 16) {
			return null;
		}

		int maxIndex = length - 1;
		for (int i = 0; i < length; i++) {
			sb.append(getHexChar((byteIp[i] >> 4) & 0x0F));
			sb.append(getHexChar(byteIp[i] & 0x0F));
			i++;
			sb.append(getHexChar((byteIp[i] >> 4) & 0x0F));
			sb.append(getHexChar(byteIp[i] & 0x0F));

			if (i != maxIndex) {
				sb.append(IPV6_FIELD_SEPARATOR);
			}
		}

		return sb.toString();
	}

	/**
	 * <pre>
	 * IPv6转换成数字串
	 *  "::"    -> "00000000000000000000000000000000"
	 *  "ffff:1:1:1:1:1:1:1"    -> "FFFF0001000100010001000100010001"
	 *  "::ffff:192.1.1.1"    -> "00000000000000000000FFFFC0010101" IPv4映像地址
	 *  性能：CPU:3.2GHZ 8.3s/1000W次
	 * </pre>
	 * 
	 * @param ip 要转换的IPv6 ip
	 * @return 转换后的字串,或者null 如果字串不合法
	 */
	public static String ipv6ToNumeric(String ip) {
		byte[] byteIp = IPAddressUtil.textToNumericFormatV6(StringUtils.trimToEmpty(ip));

		if (byteIp == null) {
			return null;
		}

		int length = byteIp.length;

		StringBuffer sb = new StringBuffer();

		// 处理IPv4映像地址
		if (length == 4) {
			sb.append(IPV4_TO_IPV6_PREFIX);
		} else if (length != 16) {
			return null;
		}

		for (byte b : byteIp) {
			sb.append(getHexChar((b >> 4) & 0x0F));
			sb.append(getHexChar(b & 0x0F));
		}

		return sb.toString();
	}

	/**
	 * IPv6转换成一对long<br>
	 * 性能：CPU:3.2GHZ 5s/1000W次
	 * 
	 * @param ip
	 *            要转换的IP
	 * @return 转换成的一对Pair&lt;IP高位, IP低位&gt;<br>
	 *         转换失败返回null
	 */
	public static Pair<Long, Long> ipv6ToLongPair(String ip) {
		long[] longArray = ipv6ToLongArray(ip);

		if (longArray == null) {
			return null;
		}
		return Pair.of(longArray[0], longArray[1]);
	}

	/**
	 * IPv6转换成long数组<br>
	 * 性能：CPU:3.2GHZ 4.9s/1000W次
	 * 
	 * @param ip
	 *            要转换的IP
	 * @return 转换成的long数组，<br>
	 *         ipArray[0]:IP高位,ipArray[1]:IP低位<br>
	 *         转换失败返回null
	 */
	public static long[] ipv6ToLongArray(String ip) {
		byte[] byteIp = IPAddressUtil.textToNumericFormatV6(StringUtils.trimToEmpty(ip));

		if (byteIp == null) {
			return null;
		}

		long[] ipArray = { 0L, 0L };// 返回long数组，第0为：IPv6高位;第1为：IPv6低位
		int length = byteIp.length;

		// 处理IPv4映像地址
		if (length == 4) {
			ipArray[1] = 0xFFFF;
			for (int i = 0; i < 4; i++) {
				ipArray[1] = (ipArray[1] << 8) | (byteIp[i] & 0xFF);
			}
		} else if (length == 16) {
			for (int i = 0; i < 8; i++) {
				ipArray[0] = (ipArray[0] << 8) | (byteIp[i] & 0xFF);
				ipArray[1] = (ipArray[1] << 8) | (byteIp[i + 8] & 0xFF);
			}
		} else {
			return null;
		}

		return ipArray;
	}

	/**
	 * 一对Pair&lt;Long, Long&gt;转换成对应的IPv6 IP<br>
	 * 全部，不省略0： "FFFF:0001:0001:0001:0001:0001:0001:0001"<br>
	 * 性能：CPU:3.2GHZ 6.2s/1000W次
	 * 
	 * @param pair
	 *            要转换的long对，Pair&lt;IP高位, IP低位&gt;
	 * @return 转换后的IP
	 */
	public static String longPairToIPv6(Pair<Long, Long> pair) {
		return longPairToIPv6(pair.getLeft(), pair.getRight());
	}

	/**
	 * 一对Long转换成对应的IPv6 IP<br>
	 * 全部，不省略0： "FFFF:0001:0001:0001:0001:0001:0001:0001"<br>
	 * 性能：CPU:3.2GHZ 6.1s/1000W次
	 * 
	 * @param ipv6H
	 *            IP高位
	 * @param ipv6L
	 *            IP低位
	 * @return 转换后的IP
	 */
	public static String longPairToIPv6(long ipv6H, long ipv6L) {
		StringBuffer sb = new StringBuffer();

		int halfLong;// 临时变量

		halfLong = (int) (ipv6H >> 32);
		appendIPv6Filed(sb, halfLong);

		halfLong = (int) ipv6H;
		sb.append(IPV6_FIELD_SEPARATOR);
		appendIPv6Filed(sb, halfLong);

		halfLong = (int) (ipv6L >> 32);
		sb.append(IPV6_FIELD_SEPARATOR);
		appendIPv6Filed(sb, halfLong);

		halfLong = (int) ipv6L;
		sb.append(IPV6_FIELD_SEPARATOR);
		appendIPv6Filed(sb, halfLong);

		return sb.toString();
	}

	/**
	 * 向IPv6串上拼接字段(仅供内部使用)
	 * 
	 * @param sb IPv6字串
	 * @param halfLong 两段IP的int之
	 */
	private static void appendIPv6Filed(StringBuffer sb, int halfLong) {
		sb.append(getHexChar((halfLong >> 28) & 0x0F)).append(getHexChar((halfLong >> 24) & 0x0F));
		sb.append(getHexChar((halfLong >> 20) & 0x0F)).append(getHexChar((halfLong >> 16) & 0x0F));
		sb.append(IPV6_FIELD_SEPARATOR);
		sb.append(getHexChar((halfLong >> 12) & 0x0F)).append(getHexChar((halfLong >> 8) & 0x0F));
		sb.append(getHexChar((halfLong >> 4) & 0x0F)).append(getHexChar((halfLong) & 0x0F));
	}

	/** 返回long中49-64bit位对应的int */
//	 private final static short longBitRange49To64(long x) {
//		 return (short) ((x >> 48) & 0xFFFF);
//	 }

	/** 返回long中33-48bit位对应的int */
//	 private final static short longBitRange33To48(long x) {
//		 return (short) ((x >> 32) & 0xFFFF);
//	 }

	/** 返回long中17-32bit位对应的int */
//	 private final static short longBitRange17To32(long x) {
//		 return (short) ((x >> 16) & 0xFFFF);
//	 }

	/** 返回long中1-16bit位对应的int */
//	 private final static short longBitRange1To16(long x) {
//		 return (short) (x & 0xFFFF);
//	 }

	/** 返回index(0-15)值对应的16进制字符 */
	private static char getHexChar(int index) {
		return HEX_STRING.charAt(index);
	}
}