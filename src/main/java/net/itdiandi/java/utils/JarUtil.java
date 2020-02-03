package net.itdiandi.java.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

/** 
* @ProjectName ProjectSummary
* @PackageName com.java.utils
* @ClassName JarUtil
* @Description jar工具
* @Author 刘吉超
* @Date 2015-07-20 11:37:38
*/
public class JarUtil {

	public static void makeJar(String jarPath, String path) throws Exception {
		try {
			JarOutputStream jos = new JarOutputStream(new FileOutputStream(path));
			File jarFile = new File(jarPath);
			JarFile jf = new JarFile(jarFile);
			Enumeration<JarEntry> je = jf.entries();
			while (je.hasMoreElements()) {
				JarEntry jar = je.nextElement();
				// System.out.println(jar.getName());
				ZipEntry z = new ZipEntry(jar.getName());
				InputStream in = jf.getInputStream(jar);
				jos.putNextEntry(z);

				if ("config.properties".equals(jar.getName())) {
					jos.write(IOUtil.toString(in, "UTF-8").append("测试").toString().getBytes("UTF-8"));
				}
				jos.closeEntry();
			}
			jos.close();
			jf.close();
		} catch (Exception e) {
			throw e;
		}
	}

	static final Pattern reUnicode = Pattern.compile("\\\\u([0-9a-zA-Z]{4})");

	public static String decode1(String s) {
		Matcher m = reUnicode.matcher(s);
		StringBuffer sb = new StringBuffer(s.length());
		while (m.find()) {
			m.appendReplacement(sb,
					Character.toString((char) Integer.parseInt(m.group(1), 16)));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static String encode(String s) {
		StringBuilder sb = new StringBuilder(s.length() * 3);
		for (char c : s.toCharArray()) {
			if (c < 256) {
				sb.append(c);
			} else {
				sb.append("\\u");
				sb.append(Character.forDigit((c >>> 12) & 0xf, 16));
				sb.append(Character.forDigit((c >>> 8) & 0xf, 16));
				sb.append(Character.forDigit((c >>> 4) & 0xf, 16));
				sb.append(Character.forDigit((c) & 0xf, 16));
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		try {
			makeJar("C:\\Users\\lWX204389\\Desktop\\test\\Test.jar", "C:\\Users\\lWX204389\\Desktop\\test\\Test1.jar");
		} catch (Exception e) {
			e.printStackTrace();
		}
//		 System.out.println(getUnicode("测试"));
//		 System.out.println(unicodeToString("\ufeff\u6d4b\u8bd5"));
//		 System.out.println("11".toCharArray().length);
	}

	public static String unicodeToString(String str) {

		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(1), ch + "");
		}
		return str;
	}

	static String getUnicode(String s) {
		try {
			StringBuffer out = new StringBuffer("");
			byte[] bytes = s.getBytes("unicode");
			for (int i = 0; i < bytes.length - 1; i += 2) {
				out.append("\\u");
				String str = Integer.toHexString(bytes[i + 1] & 0xff);
				for (int j = str.length(); j < 2; j++) {
					out.append("0");
				}
				String str1 = Integer.toHexString(bytes[i] & 0xff);
				out.append(str1);
				out.append(str);

			}
			return out.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

}

class IOUtil {
	public static StringBuffer toString(InputStream in, String encode) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, encode));

			String str = null;
			while ((str = reader.readLine()) != null) {
				sb.append(str).append("\n");
				System.out.println(unicodeToString(str));
			}
		} catch (IOException e) {

		}

		return sb;
	}

	public static String unicodeToString(String str) {

		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(1), ch + "");
		}
		return str;
	}

	public static StringBuffer clearStringBuffer(StringBuffer sb) {
		return new StringBuffer();
	}
}