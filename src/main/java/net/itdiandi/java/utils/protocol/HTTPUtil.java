package net.itdiandi.java.utils.protocol;

import org.apache.commons.lang.StringUtils;

/** 
* @ProjectName OnlineShop
* @PackageName net.itdiandi.common.utils
* @ClassName UrlUtil
* @Description url工具
* @author 刘吉超
* @date 2016-01-11 21:02:40
*/
public class HTTPUtil {
	// context root,这里根据需要修改
	public static String CONTEXT_ROOT = "/";
	
	// http协议
	public static final String HTTP_PROTOCOL_HEAD = "http://";
	
	// url分隔符
	public static final String URL_SEPARATE = "/";
	
	/**
	 * 得到URL的域名部分，包括协议名称 例如：http://www.sina.com.cn/id=xxx得到的是http://www.sina.com.cn
	 * 
	 * @param url
	 * @return String
	 */
	public static String getDomainWithPrefix(String url) {
		if (StringUtils.isBlank(url)) {
			return null;
		}

		if (url.indexOf("/") == 0) {
			int index = url.indexOf("/", 1);
			if (index != -1) {
				return url.substring(0, index);
			} else {
				return url;
			}
		}

		int index = url.indexOf("//");
		if (index < 0) {
			index = 0;
		} else {
			index = index + 2;
		}

		if (url.length() > index) {
			index = url.indexOf("/", index);
			if (index != -1) {
				return url.substring(0, index);
			}
		}

		return url;
	}
	
	/**
	 * 完善url
	 * 
	 * @param url
	 * @return
	 */
	public static String perfectURLIgnoreBlank(String url) {
		if(StringUtils.isNotBlank(url)){
			url = url.trim();
			
			// 如果是绝对地址，直接返回
			if(url.startsWith(HTTP_PROTOCOL_HEAD)){
				return url;
			}
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(CONTEXT_ROOT);
			if(!url.startsWith(URL_SEPARATE)){
				sb.append(URL_SEPARATE);
			}
			sb.append(url);
			
			return sb.toString();
		}
		
		return url;
	}
	
	/**
	 * 完善url
	 * 
	 * @param url
	 * @return
	 */
	public static String perfectURLIgnoreEmpty(String url) {
		if(StringUtils.isNotEmpty(url)){
			url = url.trim();
			
			// 如果是绝对地址，直接返回
			if(url.startsWith(HTTP_PROTOCOL_HEAD)){
				return url;
			}
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(CONTEXT_ROOT);
			if(!url.startsWith(URL_SEPARATE)){
				sb.append(URL_SEPARATE);
			}
			sb.append(url);
			
			return sb.toString();
		}
		
		return url;
	}
	
	/**
	 * 完善url
	 * 
	 * @param url
	 * @return
	 */
	public static String perfectURL(String url) {
		if(StringUtils.isNotEmpty(url)){
			url = url.trim();
			
			// 如果是绝对地址，直接返回
			if(url.startsWith(HTTP_PROTOCOL_HEAD)){
				return url;
			}
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(CONTEXT_ROOT);
			if(!url.startsWith(URL_SEPARATE)){
				sb.append(URL_SEPARATE);
			}
			sb.append(url);
			
			return sb.toString();
		}
		// 如果是空，直接返回context root
		return CONTEXT_ROOT;
	}
}