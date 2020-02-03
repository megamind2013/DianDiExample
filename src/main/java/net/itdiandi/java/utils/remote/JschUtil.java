package net.itdiandi.java.utils.remote;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/** 
* @ProjectName ProjectSummary
* @PackageName com.java.utils
* @ClassName JschUtil
* @Description 连接sshd服务器工具
* @Author 刘吉超
* @Date 2015-07-20 11:38:47
*/
public class JschUtil {
	private static Logger logger = LoggerFactory.getLogger(JschUtil.class);
	public static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";

	private Session session;

	/**
	 * 连接远端主机 <功能详细描述>
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean connectSession(String ip, int port, String user,
			String passwd) {
		int connectTimeOut = 1000; // 根据需要设置
		boolean login = false;
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(user, ip, port);
			session.setConfig(STRICT_HOST_KEY_CHECKING, "no");
			session.setPassword(passwd);
			session.connect(connectTimeOut);
			if (session.isConnected()) {
				login = true;
				logger.debug("have connected to server {}", ip);
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (!login) {
				logger.error("faild connect to server {}", ip);
			}
		}
		logger.debug("jsch connect end");
		return login;
	}

	/**
	 * Jsch执行命令
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String executor(String command) {
		logger.info("executor begin....");
		// reult的格式为:命令是否执行成功|命令返回结果
		StringBuffer result = new StringBuffer();
		ChannelExec openChannel = null;

		try {
			openChannel = (ChannelExec) session.openChannel("exec");
			openChannel.setCommand(command);

			((ChannelExec) openChannel).setErrStream(System.err);
			openChannel.connect();

			InputStream in = openChannel.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, "UTF-8"));

			int outTime = 30000; // 根据需要设置
			int sleepTime = 500; // 根据需要设置
			int count = 0;
			int times = outTime / sleepTime;

			while (true) {
				if (openChannel.isClosed()) {
					result.append(openChannel.getExitStatus()).append("|"); // 根据需要设置

					// 命令执行结果返回值
					String buf = "";
					while ((buf = reader.readLine()) != null) {
						result.append(buf);
					}

					break;
				}

				count++;
				if (count >= times) {
					// 出现异常设置默认值为失败，
					result.append("fail").append("|").append("fialure");
					logger.error("executor outTime....");
					break;
				}
				Thread.sleep(sleepTime);
			}

			// 关闭流
			reader.close();
		} catch (Exception e) {
			// 出现异常设置默认值为失败，
			result.append("fail").append("|").append("fialure");

			logger.error("", e);
		} finally {
			if (openChannel != null && !openChannel.isClosed()) {
				openChannel.disconnect();
			}
		}

		logger.info("executor end....");

		return result.toString();
	}

	/**
	 * Jsch切换到root用户执行命令 
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String switchRootExec(String command, String rootPswd) {
		logger.info("switchRootExec begin....");
		// reult的格式为:命令是否执行成功|命令返回结果
		StringBuffer result = new StringBuffer();

		ChannelExec openChannel = null;

		try {
			openChannel = (ChannelExec) session.openChannel("exec");
			openChannel.setCommand(command);
			OutputStream out = openChannel.getOutputStream();
			openChannel.connect();

			out.write((rootPswd + "\n").getBytes("UTF-8"));
			out.flush();

			InputStream in = openChannel.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, "UTF-8"));

			int outTime = 30000;
			int sleepTime = 500;
			int count = 0;
			int times = outTime / sleepTime;
			while (true) {

				if (openChannel.isClosed()) {
					// 命令是否执行成功，0表示成功,其他值表示失败。
					result.append(openChannel.getExitStatus()).append("|");

					// 命令执行结果返回值
					String buf = "";
					while ((buf = reader.readLine()) != null) {
						result.append(buf);
					}

					break;
				}

				count++;
				if (count >= times) {
					// 出现异常设置默认值为失败，
					result.append("fail").append("|").append("fialure");
					logger.error("switchRootExec timeOut....");
					break;
				}

				Thread.sleep(500);
			}
			// 关闭流
			reader.close();
		} catch (Exception e) {
			// 出现异常设置默认值为失败，
			result.append("fail").append("|").append("fialure");

			logger.error("" + e);
		} finally {
			if (openChannel != null && !openChannel.isClosed()) {
				openChannel.disconnect();
			}

		}
		logger.info("switchRootExec end....");
		return result.toString();
	}

	/**
	 * @param session
	 */
	public void closeSession() {
		if (session != null && session.isConnected()) {
			session.disconnect();
		}
	}

	public static void main(String[] args) {
		List<String> tmp1 = new ArrayList<String>();
		List<String> tmp2 = new ArrayList<String>();
		String aaa = "73-10.163.235.125,10-10.163.235.125,37-10.163.235.128,97-172.16.144.208,10-10.163.235.125";
		
		String[] bb = aaa.split(",");
		tmp1 = Arrays.asList(bb);
		
		for(String s : bb)
		{
			tmp2.add(s.split("-")[1]);
		}
		
		tmp2 = new ArrayList<>(new HashSet<>(tmp2));
		System.out.println(tmp1);
		System.out.println(tmp2);
		
		System.out.println("AAA".toLowerCase());
	}
}