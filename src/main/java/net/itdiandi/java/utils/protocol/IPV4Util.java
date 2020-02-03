package net.itdiandi.java.utils.protocol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
* @ProjectName ProjectSummary
* @PackageName com.java.utils
* @ClassName IPV4Util
* @Description ipv4工具
* @Author 刘吉超
* @Date 2015-07-20 11:38:01
*/
public class IPV4Util {

	private static final Pattern IPRegex = Pattern
			.compile("\\b(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]?)"
					+ "\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]?|0)"
					+ "\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]?|0)"
					+ "\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]?|0)\\b");
	private static final Pattern PortRegex = Pattern
			.compile("^[0-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}"
					+ "|65[0-4][0-9]{2}|655[0-2][0-9]{1}|6553[0-5]$");

	public static boolean isIP(String str) {
		if (StringUtils.isBlank(str)) {
			return false;
		}
		return IPRegex.matcher(str).matches();
	}

	public static String byteToIPString(byte b1, byte b2, byte b3, byte b4) {
		return (b1 & 0xff) + "." + (b2 & 0xff) + "." + (b3 & 0xff) + "."
				+ (b4 & 0xff);
	}

	public static String byteToIPString(byte b[]) {
		return IPV4Util.byteToIPString(b[0], b[1], b[2], b[3]);
	}

	public static int byteToIPInt(byte[] ips) {
		if (ips == null || ips.length != 4) {
			return 0;
		}
		return (int) ((((ips[3] & 0xff) << 0) | ((ips[2] & 0xff) << 8)
				| ((ips[1] & 0xff) << 16) | ((ips[0] & 0xff) << 24)));
	}

	public static String byteToPortString(byte b1, byte b2) {
		return byteToPortInt(b1, b2) + "";
	}

	public static int byteToPortInt(byte b1, byte b2) {
		return (int) (((b1 & 0xff) << 8) | (b2 & 0xff));
	}

	private static byte int3(int x) {
		return (byte) (x >> 0);
	}

	private static byte int2(int x) {
		return (byte) (x >> 8);
	}

	private static byte int1(int x) {
		return (byte) (x >> 16);
	}

	private static byte int0(int x) {
		return (byte) (x >> 24);
	}

	public static byte[] ipIntToByte(int ip) {
		byte ips[] = new byte[4];
		ips[0] = int0(ip);
		ips[1] = int1(ip);
		ips[2] = int2(ip);
		ips[3] = int3(ip);
		return ips;
	}

	public static int ipStringToInt(String ip) {
		Matcher ipMatcher = IPRegex.matcher(ip);
		if (!ipMatcher.find()) {
			throw new IllegalArgumentException("IP format error:" + ip);
		}
		byte ips[] = new byte[4];
		ips[0] = int3(Integer.valueOf(ipMatcher.group(1)));
		ips[1] = int3(Integer.valueOf(ipMatcher.group(2)));
		ips[2] = int3(Integer.valueOf(ipMatcher.group(3)));
		ips[3] = int3(Integer.valueOf(ipMatcher.group(4)));

		return byteToIPInt(ips);
	}

	/**
	 * 判断字符串是否是port
	 * 
	 * @param port
	 * @return boolean
	 */
	public static boolean isValidatePort(String port) {
		Matcher ipMatcher = PortRegex.matcher(port);
		return ipMatcher.matches();
	}

	public static String ipIntToString(int ip) {
		byte ips[] = ipIntToByte(ip);
		return ipByteToString(ips);
	}

	public static String ipByteToString(byte ips[]) {
		return (ips[0] & 0xff) + "." + (ips[1] & 0xff) + "." + (ips[2] & 0xff) + "." + (ips[3] & 0xff);
	}

	/**
	 * <pre>
	 * IPV4转换成数字串
	 *  "1.1.1.1"    -> "001001001001"
	 *  " 1.1.1.1 "  -> "001001001001"
	 *  "1.01.001.1" -> "001001001001"
	 *  (性能)自己机器测试，千万级转换在2.5秒内
	 * </pre>
	 * 
	 * @param ip 要转换的IPV4 ip
	 * @return 转换后的字串
	 * @throws IllegalArgumentException IP格式不正确，抛出异常，附带转换IP
	 */
	public static String ipV4ToNumeric(String ip) throws IllegalArgumentException {

		String ipTrim = StringUtils.trimToNull(ip);
		if (ipTrim == null) {
			throw new IllegalArgumentException("IP format error:" + ip);
		}

		char[] numericIp = new char[12];// 存放返回的IP串,length 12

		char[] ipInChar = ipTrim.toCharArray();

		char[] ipOneSeg = new char[3];// for循环中的临时变量，一个段的三位，{'0','0','1'}、{'2','5','5'}
		String tmpIpSeg;// for循环中的临时变量
		int tmpCount;// for循环中的临时变量
		char tempChar;// for循环中的临时变量
		int seg = 3;// 段的下标，倒着处理

		int i = ipInChar.length - 1;
		int j = i;

		// 倒着处理IP中的每一个字符，方便不足位补0
		for (; i >= 0; i--) {
			tempChar = ipInChar[i];

			if (tempChar != '.') {
				tmpCount = j - i;

				// 当前处理字段中的字符下标应该是[2,1,0],当前字符应该是0-9之间的数字
				if (tmpCount < 0 || tmpCount > 2 || tempChar < '0' || tempChar > '9') {
					throw new IllegalArgumentException("IP format error:" + ip);
				}

				// 倒着处理，需要反转顺序存放
				ipOneSeg[2 - tmpCount] = tempChar;
			}

			// 最前面没有'.'分割，将j调整，方便后面统一处理
			if (i == 0) {
				j++;
			}

			// 一个字段处理完成，校验并保存，准备处理下个字段
			if (tempChar == '.' || i == 0) {
				tmpCount = j - i;

				// 每个IP段只能有 1、2或3个字符
				if (tmpCount < 1 || tmpCount > 3) {
					throw new IllegalArgumentException("IP format error:" + ip);
				}
				// 当前段只有一个字符
				if (tmpCount == 1) {
					ipOneSeg[0] = '0';// 第一位需要补0
					ipOneSeg[1] = '0';// 第二位需要补0
				} else if (tmpCount == 2) {
					ipOneSeg[0] = '0';// 当前段有2个字符，第一位需要补0
				}

				// 处理当前段
				tmpIpSeg = new String(ipOneSeg);
				if (tmpIpSeg.compareTo("000") < 0 || tmpIpSeg.compareTo("255") > 0) {
					throw new IllegalArgumentException("IP format error:" + ip);
				}

				// 将转换后的段复制到对应位
				tmpCount = seg * 3;
				numericIp[tmpCount] = ipOneSeg[0];
				numericIp[tmpCount + 1] = ipOneSeg[1];
				numericIp[tmpCount + 2] = ipOneSeg[2];

				// 准备处理下个段，超过4个段说明ip格式有问题
				seg--;
				if (seg < -1) {
					throw new IllegalArgumentException("IP format error:" + ip);
				}

				j = i - 1;// 调整标志位
			}
		}

		// 不是4个段说明ip格式有问题
		if (seg != -1) {
			throw new IllegalArgumentException("IP format error:" + ip);
		}

		return new String(numericIp);
	}

	public static String getFullIpString(String ip) {
		StringBuilder sb = new StringBuilder(16);
		String[] array = StringUtils.split(ip, ".");
		if (array != null && array.length == 4) {
			for (int i = 0; i < 3; i++) {
				if (array[i].length() == 1) {
					sb.append("00").append(array[i]).append(".");
				} else if (array[i].length() == 2) {
					sb.append("0").append(array[i]).append(".");
				} else {
					sb.append(array[i]).append(".");
				}
			}

			if (array[3].length() == 1) {
				sb.append("00").append(array[3]);
			} else if (array[3].length() == 2) {
				sb.append("0").append(array[3]);
			} else {
				sb.append(array[3]);
			}
			return sb.toString();
		} else {
			return ip;
		}
	}
}