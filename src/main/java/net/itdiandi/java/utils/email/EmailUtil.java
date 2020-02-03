package net.itdiandi.java.utils.email;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.common.resources.Resources;
import com.sun.mail.util.MailSSLSocketFactory;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.email
* @ClassName EmailUtil
* @Description email工具类
* @author 刘吉超
* @date 2016-03-03 22:10:49
*/
public class EmailUtil {
	// 日志
	private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);
	// 编码
	private static final String EMAIL_CHARSET = "UTF-8"; // 电子邮件编码
	// VO
	private static EmailUtil emailUtil = new EmailUtil();
	
	/**
	 * email属性
	 */
	private String host; // 服务器
	private int port = -1; // 服务器端口
	private String user; // 登陆用户名
	private String pwd; // 登陆密码

	private boolean enableSSL = false; // 是否启用 SSL
	private boolean enableTLS = false; // 是否启用TLS
	
	private String from; // 发件人
	private List<String> tos = new ArrayList<>(); // 可以发送给多个收件人

	private boolean debug = false;

	private EmailUtil() {
	}
	
	static 
    {
       try 
       {
    	   Properties properties = new Properties();
    	   properties.load(Resources.getResourceAsReader("com/buaa/utils/email/email.properties"));
    	   
    	   // 服务器
    	   emailUtil.host = (String)properties.get("host");
    	   // 端口
    	   int port = Integer.parseInt((String)properties.get("port"));
   		   if (port > 0) {
   			   emailUtil.port = port;
   		   }else{
   			   emailUtil.port = 25;
   		   }
    	   // 登陆用户名
    	   emailUtil.user = (String)properties.get("user");
    	   // 登陆密码
    	   emailUtil.pwd = (String)properties.get("pwd");
    	   // 是否启用SSL,true启用，false不启用
    	   emailUtil.enableSSL = Boolean.parseBoolean((String)properties.get("enableSSL"));
    	   // 是否启用TLS,true启用,false不启用
    	   emailUtil.enableTLS = Boolean.parseBoolean((String)properties.get("enableTLS"));
    	   // 发件人
    	   emailUtil.from = (String)properties.get("from");
    	   // 收件人
    	   String to = (String)properties.get("to");
    	   if(StringUtils.isNotBlank(to)){
    		   String[] ts = to.split(";");
    		   emailUtil.tos.addAll(Arrays.asList(ts));
    	   }
       } catch (IOException e) 
       {
           logger.error("读取配置文件出错", e);
       }
    }

	/**
	 * 返回EmailUtil实例
	 * 
	 * @return EmailUtil
	 */
	public static EmailUtil getInstance() {
		if (logger.isDebugEnabled()) {
			emailUtil.setDebug(true);
		}

		return emailUtil;
	}
	
	public boolean send(String subject,String body) {
		boolean sendSuccess = true;

		SimpleEmail email = new SimpleEmail();
		email.setHostName(host);
		email.setAuthentication(user, pwd);
		email.setCharset(EMAIL_CHARSET);
		email.setDebug(debug);

		if (port > 0) {
			if (enableSSL) {
				email.setSslSmtpPort(String.valueOf(port));
			} else {
				email.setSmtpPort(port);
			}
		}

		if (enableSSL || enableTLS) {
			if (enableTLS) {
				email.setStartTLSEnabled(enableTLS);
			}

			if (enableSSL) {
				email.setSSLOnConnect(enableSSL);
			}

			try {
				MailSSLSocketFactory sf = new MailSSLSocketFactory();
				sf.setTrustAllHosts(true);
				email.getMailSession().getProperties().put("mail.smtp.ssl.socketFactory", sf);
			} catch (final GeneralSecurityException | EmailException e) {
				logger.error("", e);
			}
		}

		try {
			email.setFrom(from);
			for (String to : tos) {
				email.addTo(to);
			}
			email.setSubject(subject);
			email.setMsg(body);
			email.send();
		} catch (EmailException e) {
			logger.error("email send error:", e);
			sendSuccess = false;
		}

		return sendSuccess;
	}

	/**
	 * 添加收件人地址
	 * 
	 * @param to
	 */
	public void addTo(String to) {
		this.tos.add(to);
	}

	/**
	 * 添加多个收件人地址
	 * 
	 * @param tos
	 */
	public void addTo(List<String> tos) {
		this.tos.addAll(tos);
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public void setEnableSSL(boolean enableSSL) {
		this.enableSSL = enableSSL;
	}

	public void setEnableTLS(boolean enableTLS) {
		this.enableTLS = enableTLS;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}