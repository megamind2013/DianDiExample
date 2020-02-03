package net.itdiandi.java.utils.remote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.remote
* @ClassName ScpClient
* @Description SCP协议封装类 实现SCP协议常用方法
* @author 刘吉超
* @date 2016-02-24 20:13:47
*/
public class ScpClient {
	/**
	 * 日志处理类
	 */
	private static Log logger = LogFactory.getLog(ScpClient.class);

	public static final String RM_COMMAND = "rm -f ";
	public static final String TOUCH_COMMAND = "touch ";

	private static final byte LINE_FEED = 0x0a;
	private static final int BUFFER_SIZE = 1024;
	private static final String END = "END";
	public static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
	public static final String USERAUTH_GSSAPI = "userauth.gssapi-with-mic";
	private static String charEncoding = "UTF-8";

	private Session session;

	private String host;
	private int port;
	private String user;
	private String passwd;
	private String pipeName;
	
	private int dirSusscee = 200; // 成功
	private int dirNotExist = 100; // 目录不存在
	private int dirPermissionErr = 300; // 目录权限不正确
	private int dirException = 400; // 目录异常
	
	
	private int connectRetryCnt;// 重试次数
	private int connectTimeout; // 重试间隔
	private int transferTimeout; // 传输时间

	public ScpClient(String pipeName, String host, int port, String user,
			String passwd) {
		this.pipeName = pipeName;
		this.host = host;
		this.port = port;
		this.user = user;
		this.passwd = passwd;
	}
	
	/**
	 * 建立连接
	 * 
	 * @return boolean
	*/
	public boolean connect() {
		logger.debug("pipeName:" + pipeName + " scp connect begin");

		// 重试次数
		int retryCnt = 0;
		boolean login = false;
		while (!login && (retryCnt < connectRetryCnt)) {
			try {
				retryCnt++;
				JSch jsch = new JSch();
				session = jsch.getSession(user, host, port);
				session.setConfig(STRICT_HOST_KEY_CHECKING, "no");
				session.setConfig(USERAUTH_GSSAPI, "no");
				session.setPassword(passwd);
				session.connect(connectTimeout);
				if (session.isConnected()) {
					login = true;
					session.setTimeout(transferTimeout);
					logger.debug("have connected to server " + host);
				}
			} catch (Exception e) {
				logger.error(e);
			} finally {
				if (!login) {
					if (session != null) {
						session.disconnect();
					}
					logger.error("pipeName:" + pipeName
							+ " faild connect to server " + host
							+ " retryCnt = " + retryCnt);
				}
			}
		}
		logger.debug("pipeName:" + pipeName + " scp connect end");
		return login;
	}
	
	/**
	 * 关闭连接
	 * 
	 * @return void
	*/
	public void disconnect() {
		logger.debug("pipeName:" + pipeName + " scp disconnet from server "
				+ host + " begin");
		if (session != null) {
			session.disconnect();
		}
		logger.debug("pipeName:" + pipeName + " scp disconnet from server "
				+ host + " end");
	}
	
	/**
	 * 获取要下载的文件列表
	 * 
	 * @param regex
	 * @param remoteDir
	 * @param batchMax
	 * @param ctrlFileExt
	 * @return List<String>
	*/
	public List<String> getRemoteFileList(String regex, String remoteDir, int batchMax, String ctrlFileExt) {
		logger.debug("pipeName:" + pipeName
				+ " ScpClient getRemoteFileList begin");
		List<String> files = new ArrayList<String>();
		String command = "scp -f " + remoteDir + "*" + ctrlFileExt;
		ChannelExec channel = null;
		try {
			channel = openExecChannel(command);

			// get I/O stream from scp server
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			channel.connect();
			// 发送'\0'
			sendAck(out);
			while (true) {
				// C0644 filesize filename - header for a regular file
				String header = getResponse(in);
				if (header.equals(ScpClient.END)) {
					break;
				}
				if (header.charAt(0) != 'C') {
					sendAck(out);
					continue;
				} else {
					int start = header.indexOf(" ");
					int end = header.indexOf(" ", start + 1);
					long filesize = Long.parseLong(header.substring(start + 1,
							end));
					String filename = header.substring(end + 1);
					String name = filename.substring(0,
							filename.indexOf(ctrlFileExt));
					if (StringUtils.isEmpty(regex)) {
						files.add(name);
					} else {
						if (filename.matches(regex)) {
							files.add(name);
						}
					}

					if (files.size() >= batchMax) {
						break;
					}

					sendAck(out);

					// 下面这一段只是为了读完ok文件内容，然后接收下一个文件名，读取的内容没有任何意义 start
					int foo = 0;
					byte[] buf = new byte[BUFFER_SIZE];
					while (true) {
						if (buf.length < filesize) {
							foo = buf.length;
						} else {
							foo = (int) filesize;
						}
						foo = in.read(buf, 0, foo);

						filesize -= foo;
						if (filesize <= 0) {
							break;
						}
					}
					// 只是为了读完ok文件内容，然后接收下一个文件名，读取的内容没有任何意义 end

					checkAck(in);
					sendAck(out);
				}
			}
		} catch (JSchException e) {
			logger.error("pipeName:" + pipeName
					+ " ScpClient getRemoteFiles error:", e);
		} catch (IOException e) {
			logger.error("pipeName:" + pipeName
					+ " ScpClient getRemoteFiles error:", e);
		} finally {
			if (channel != null && channel.isConnected()) {
				channel.disconnect();
			}
		}
		logger.debug("pipeName:" + pipeName
				+ " ScpClient getRemoteFileList end");
		return files;
	}
	
	/**
	 * 下载文件
	 * 
	 * @param localDir
	 * @param remoteFile
	 * @return boolean
	*/
	public boolean getRemoteFile(String localDir, String remoteFile) {
		logger.debug("pipeName:" + pipeName + " ScpClient download file "
				+ remoteFile + " to " + localDir + " begin");
		String command = "scp -f " + remoteFile;
		ChannelExec channel = null;
		try {
			channel = openExecChannel(command);

			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			channel.connect();
			sendAck(out);
			while (true) {
				// C0644 filesize filename - header for a regular file
				// D 0 directory - this is the header for a directory.

				// 读取响应头
				String header = getResponse(in);
				if (header.equals(ScpClient.END)) {
					break;
				}

				// 响应头为文件
				if (header.charAt(0) == 'C') {
					fetchFile(in, out, header, localDir);
					checkAck(in);
					sendAck(out);
				}

				// 响应头为目录
				else if (header.charAt(0) == 'D') {
					int start = header.indexOf(" ");
					int end = header.indexOf(" ", start + 1);
					String dirname = header.substring(end + 1);
					logger.error("file " + dirname + " is directory");
					return false;
				} else if (header.charAt(0) == 'E') {
					sendAck(out);
				} else if (header.charAt(0) == '\01'
						|| header.charAt(0) == '\02') {
					logger.error(header.substring(1));
					return false;
				}
			}
		}

		catch (JSchException e) {
			logger.error("pipeName:" + pipeName + " ScpClient download file "
					+ remoteFile + " error:", e);
			return false;
		} catch (IOException e) {
			logger.error("pipeName:" + pipeName + " ScpClient download file "
					+ remoteFile + " error:", e);
			return false;
		} finally {
			if (channel != null && channel.isConnected()) {
				channel.disconnect();
			}
		}
		logger.debug("pipeName:" + pipeName + " ScpClient download file "
				+ remoteFile + " to " + localDir + " end");
		return true;
	}
	
	/**
	 * 上传文件
	 * 
	 * @param realFile
	 * @param remoteDir
	 * @return boolean
	*/
	public boolean putRemoteFile(String realFile, String remoteDir) {
		logger.debug("pipeName:" + pipeName + " ScpClient upload file "
				+ realFile + " to " + remoteDir + " begin");
		String command = "scp -t " + remoteDir;
		ChannelExec channel = null;
		try {
			channel = openExecChannel(command);

			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			channel.connect();
			sendFile(in, out, realFile);
		} catch (JSchException e) {
			logger.error("pipeName:" + pipeName + " ScpClient upload file "
					+ realFile + " error:", e);
			return false;
		} catch (IOException e) {
			logger.error("pipeName:" + pipeName + " ScpClient upload file "
					+ realFile + " error:", e);
			return false;
		} finally {
			if (channel != null && channel.isConnected()) {
				channel.disconnect();
			}
		}
		logger.debug("pipeName:" + pipeName + " ScpClient upload file "
				+ realFile + " to " + remoteDir + " end");
		return true;
	}
	
	/**
	 * 上传文件
	 * 
	 * @param in
	 * @param out
	 * @param localFile
	 * @throws IOException
	 * @return void
	*/
	private void sendFile(InputStream in, OutputStream out, String localFile)
			throws IOException {
		File local = new File(localFile);

		String command = "C0644 " + local.length() + " ";
		command += local.getName();
		command += "\n";

		out.write(command.getBytes());
		out.flush();

		checkAck(in);

		InputStream fin = null;
		try {
			fin = new BufferedInputStream(new FileInputStream(localFile));
			byte[] buf = new byte[BUFFER_SIZE];
			while (true) {
				int read = fin.read(buf, 0, buf.length);
				if (read <= 0) {
					break;
				}
				out.write(buf, 0, read);
			}

			out.flush();
			sendAck(out);
			checkAck(in);
		} finally {
			IOUtils.closeQuietly(fin);
		}
	}
	
	/**
	 * 下载文件
	 * 
	 * @param in
	 * @param out
	 * @param response
	 * @param local
	 * @throws IOException
	 * @return void
	*/
	private void fetchFile(InputStream in, OutputStream out, String response,
			String local) throws IOException {
		int start = response.indexOf(" ");
		int end = response.indexOf(" ", start + 1);
		long filesize = Long.parseLong(response.substring(start + 1, end));
		String filename = response.substring(end + 1);

		File destFile = new File(local, filename);

		// 开始下载文件
		byte[] buf = new byte[BUFFER_SIZE];
		sendAck(out);
		OutputStream fos = null;
		try {
			fos = new BufferedOutputStream(new FileOutputStream(destFile));
			int foo = 0;
			while (true) {
				if (buf.length < filesize) {
					foo = buf.length;
				} else {
					foo = (int) filesize;
				}
				foo = in.read(buf, 0, foo);
				if (foo < 0) {
					throw new IOException("unexpect close");
				}
				fos.write(buf, 0, foo);
				filesize -= foo;
				if (filesize == 0) {
					break;
				}
			}
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}
	
	/**
	 * 校验目录及权限
	 * 
	 * @param dir
	 * @return int
	*/
	public int checkDirectory(String dir) {
		logger.debug("pipeName:" + pipeName + " ScpClient check dir " + dir + " exist or not begin");
		int returnCode = dirSusscee;
		String command = "scp -f -r " + dir;
		ChannelExec channel = null;
		try {
			channel = openExecChannel(command);

			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			channel.connect();
			sendAck(out);
			String header = getResponse(in);
			if (!header.startsWith("D")) {
				returnCode = dirNotExist;
				logger.error("pipeName:" + pipeName + " directory " + dir + " not exist");
			} else if (!header.startsWith("D07")) {
				returnCode = dirPermissionErr;
				logger.error("pipeName:" + pipeName + " directory " + dir + " permission error " + header);
			}
		} catch (JSchException e) {
			returnCode = dirException;
			logger.error("pipeName:" + pipeName
					+ " ScpClient checkDirectory error:", e);
		} catch (IOException e) {
			returnCode = dirException;
			logger.error("pipeName:" + pipeName + " ScpClient checkDirectory error:", e);
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
		}
		logger.debug("pipeName:" + pipeName + " ScpClient check dir " + dir + " exist or not end");
		return returnCode;
	}
	
	/**
	 * 发送'\0'
	 * 
	 * @param out
	 * @throws IOException
	 * @return void
	*/
	public void sendAck(OutputStream out) throws IOException {
		logger.debug("pipeName:" + pipeName + " ScpClient sendAck begin");
		byte[] buf = new byte[1];
		buf[0] = 0;
		out.write(buf);
		out.flush();
		logger.debug("pipeName:" + pipeName + " ScpClient sendAck end");
	}
	
	/**
	 * 校验流 may be: -1 for scp server terminated,0 for success,1 for error,2 for
	 * fatal error
	 * 
	 * @param in
	 * @throws IOException
	 * @return void
	*/
	public void checkAck(InputStream in) throws IOException {
		logger.debug("pipeName:" + pipeName + " ScpClient checkAck begin");
		int b = in.read();
		if (b == 0) {
			return;
		}
		if (b == -1) {
			throw new IOException("pipeName:" + pipeName
					+ " ScpClient Remote scp terminated unexpectedly");
		}
		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				throw new IOException("pipeName:" + pipeName
						+ " ScpClient Remote scp terminated with error:"
						+ sb.toString());
			}
			if (b == 2) { // fatal error
				throw new IOException("pipeName:" + pipeName
						+ " ScpClient Remote scp terminated with fatal error:"
						+ sb.toString());
			}
		} else {
			throw new IOException("pipeName:" + pipeName
					+ " ScpClient Remote scp sent illegal error code.");
		}
		logger.debug("pipeName:" + pipeName + " ScpClient checkAck end");
	}
	
	/**
	 * 获取响应头信息
	 * 
	 * @param in
	 * @throws IOException
	 * @return String
	*/
	private String getResponse(InputStream in) throws IOException {
		logger.debug("pipeName:" + pipeName
				+ " ScpClient get response header from server begin");

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		while (true) {
			int read = in.read();
			if (read < 0) {
				return END;
			}
			if ((byte) read == LINE_FEED) {
				break;
			}
			stream.write(read);
		}

		String response = stream.toString(charEncoding);
		logger.debug("pipeName:" + pipeName
				+ " ScpClient get response header from server end");
		return response;
	}
	
	/**
	 * 执行shell命令
	 * 
	 * @param fileName
	 * @param commond
	 * @return boolean
	*/
	public boolean execCommond(String fileName, String commond) {
		logger.debug("pipeName:" + pipeName + " ScpClient " + commond + " "
				+ fileName + " begin");
		boolean flag = true;
		String command = commond + fileName;
		ChannelExec channel = null;
		try {
			session.setTimeout(connectTimeout);
			channel = openExecChannel(command);
			channel.connect();
			final ChannelExec tmpChannel = channel;
			final Thread thread = new Thread() {
				public void run() {
					int i = 0;
					while (!tmpChannel.isClosed() && i < 150) {
						sleepMilliSeconds(100);
						i++;
					}
				}
			};
			try {
				thread.start();
				thread.join(connectTimeout);
			} catch (InterruptedException e) {
				logger.error(e);
			}

			int status = channel.getExitStatus();
			if (status != 0) {
				flag = false;
				logger.error(command + " error");
			}

		} catch (JSchException e) {
			logger.error("pipeName:" + pipeName + " open shell Channel error：",
					e);
			flag = false;
		} finally {
			if (channel != null && channel.isConnected()) {
				channel.disconnect();
			}
		}
		logger.debug("pipeName:" + pipeName + " ScpClient " + commond + " "
				+ fileName + " end");
		return flag;
	}

	private ChannelExec openExecChannel(String command) throws JSchException {
		ChannelExec channel = (ChannelExec) session.openChannel("exec");
		channel.setCommand(command);
		return channel;
	}

	private void sleepMilliSeconds(int time) {
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}
}