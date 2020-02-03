package net.itdiandi.java.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import net.itdiandi.java.utils.file.FileUtil;

/**
 * @ProjectName SPMFront
 * @PackageName com.dfjx.utils
 * @ClassName MessageUtil
 * @Description TODO
 * @Author
 * @Date 2017-01-10 10:15:35
 */
public class MessageUtil {
	private static Logger logger = Logger.getLogger(MessageUtil.class);

	public static void print(String sting) {
		System.out.println(sting);
	}

	public static void print(StringBuilder stringBuilder) {
		print(stringBuilder.toString());
	}

	public static void print(InputStream inputStream) {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				inputStream));
		try {
			StringBuilder context = new StringBuilder();
			String line = null;

			while ((line = in.readLine()) != null) {
				if (StringUtils.isNotBlank(line)) {
					context.append(line);
				}
			}
			print(context);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
		}
	}

	public static void print(HttpServletRequest request) {

		String head = request.getMethod() + " " + request.getRequestURL() + " " + request.getProtocol() + "\r\n";

		FileUtil.append("C:\\Users\\k\\Desktop\\ambariapi\\url.txt", head);
		System.out.print(head);

		StringBuilder headerResult = new StringBuilder();

		headerResult.append("      Header    \r\n");
		Enumeration<String> e = request.getHeaderNames(); //
		// 通过枚举类型获取请求文件的头部信息集
		// 遍历头部信息集
		while (e.hasMoreElements()) {
			// 取出信息名
			String name = (String) e.nextElement();
			// 取出信息值
			String value = request.getHeader(name);
			headerResult.append("            " + name + "=" + value + "\r\n");
		}

		FileUtil.append("C:\\Users\\k\\Desktop\\ambariapi\\url.txt", headerResult.toString());
		System.out.print(headerResult);

		StringBuilder paramResult = new StringBuilder();
		paramResult.append("      Param    \r\n");

		Enumeration<String> paramsEnumeration = request.getParameterNames();
		StringBuilder params = new StringBuilder();

		// 遍历头部信息集
		while (paramsEnumeration.hasMoreElements()) {
			// 取出信息名
			String name = (String) paramsEnumeration.nextElement();
			// 取出信息值
			String[] values = request.getParameterValues(name);

			for (String p : values) {
				params.append(p + " ");
			}
			paramResult.append("            " + name + "=" + params + "\r\n");
			// 清空数据
			params.setLength(0);
		}

		FileUtil.append("C:\\Users\\k\\Desktop\\ambariapi\\url.txt",
				paramResult.toString());
		System.out.print(paramResult);
	}

	public static void print(HttpServletRequest request, HttpServletResponse response,String responseBody) {
		String head = "Request\r\n    " + request.getMethod() + " " + request.getRequestURL() + " " + request.getProtocol() + "\r\n";

		FileUtil.append("C:\\Users\\k\\Desktop\\ambariapi\\url.txt", head);
		System.out.print(head);

		StringBuilder headerResult = new StringBuilder();
		headerResult.append("          Header    \r\n");
		Enumeration<String> e = request.getHeaderNames(); //
		// 通过枚举类型获取请求文件的头部信息集
		// 遍历头部信息集
		while (e.hasMoreElements()) {
			// 取出信息名
			String name = (String) e.nextElement();
			// 取出信息值
			String value = request.getHeader(name);
			headerResult.append("            " + name + "=" + value + "\r\n");
		}

		FileUtil.append("C:\\Users\\k\\Desktop\\ambariapi\\url.txt", headerResult.toString());
		System.out.print(headerResult);

		StringBuilder paramResult = new StringBuilder();
		paramResult.append("          Param    \r\n");

		Enumeration<String> paramsEnumeration = request.getParameterNames();
		StringBuilder params = new StringBuilder();

		// 遍历头部信息集
		while (paramsEnumeration.hasMoreElements()) {
			// 取出信息名
			String name = (String) paramsEnumeration.nextElement();
			// 取出信息值
			String[] values = request.getParameterValues(name);

			for (String p : values) {
				params.append(p + " ");
			}
			paramResult.append("            " + name + "=" + params + "\r\n");
			// 清空数据
			params.setLength(0);
		}

		FileUtil.append("C:\\Users\\k\\Desktop\\ambariapi\\url.txt", paramResult.toString());
		System.out.print(paramResult);
		
		
		
		StringBuilder responseHeaderResult = new StringBuilder();
		responseHeaderResult.append("Response\r\n    " + response + "\r\n          Header    \r\n");
		Enumeration<String> e1 = request.getHeaderNames(); //
		// 通过枚举类型获取请求文件的头部信息集
		// 遍历头部信息集
		while (e1.hasMoreElements()) {
			// 取出信息名
			String name = (String) e1.nextElement();
			// 取出信息值
			String value = request.getHeader(name);
			responseHeaderResult.append("            " + name + "=" + value + "\r\n");
		}

		FileUtil.append("C:\\Users\\k\\Desktop\\ambariapi\\url.txt", responseHeaderResult.toString());
		System.out.print(responseHeaderResult);
		
		
		FileUtil.append("C:\\Users\\k\\Desktop\\ambariapi\\url.txt", "          Body    \r\n            " + responseBody);
		System.out.print("          Body    \r\n            " + responseBody);
	}
}
