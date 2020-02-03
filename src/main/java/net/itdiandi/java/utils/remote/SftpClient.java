package net.itdiandi.java.utils.remote;

import java.io.File;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.remote
* @ClassName SftpClient
* @Description SFTP协议封装类 实现SFTP协议常用方法
* @author 刘吉超
* @date 2016-02-24 20:17:13
*/
public class SftpClient {
	/**
	 * 日志处理类
	 */
	private static Logger logger = LoggerFactory.getLogger(SftpClient.class);
	public static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";

	private Session session;
	private ChannelSftp channel;

	int maxRetryCnt = 3; // 重试次数
	int retryInterval = 30; // 重试间隔
	int connectTimeOut = 1000; // 连接时间
	
	/**
	 * 建立连接
	 * 
	 * @param ftpIp
	 * @param ftpPort
	 * @param ftpUser
	 * @param ftpPasswd
	 * @return boolean
	*/
	public boolean connect(String ftpIp, int ftpPort, String ftpUser, String ftpPasswd) {
		JSch jsch = new JSch();

		boolean connected = false;

		// 重试次数
		int retryCnt = 0;
		boolean login = false;
		while (!login && (retryCnt < maxRetryCnt)) {
			try {
				retryCnt++;
				session = jsch.getSession(ftpUser, ftpIp, ftpPort);
				session.setConfig(STRICT_HOST_KEY_CHECKING, "no");
				session.setPassword(ftpPasswd);
				session.connect(connectTimeOut);
				if (session.isConnected()) {
					login = true;
					logger.debug("have connected to server {}", ftpIp);
				}
			} catch (Exception e) {
				logger.error("", e);
			} finally {
				if (!login) {
					logger.error("faild connect to server {}, retryCnt ={} ", ftpIp, retryCnt);
					sleepSeconds(retryInterval);
				}
			}
		}
		if (login) {
			connected = openChannel(session);
		}
		logger.debug("sftp connect end");
		return connected;
	}

	private boolean openChannel(Session session) {
		boolean succeed = true;
		if (session.isConnected()) {
			try {
				Channel tmpChannel = session.openChannel("sftp");
				tmpChannel.connect();
				channel = (ChannelSftp) tmpChannel;
				logger.debug("channel has been established.");
			} catch (JSchException e) {
				logger.error("SftpClient open channel error:", e);
				succeed = false;
			}
		}

		return succeed;
	}

	private void sleepSeconds(int second) {
		try {
			TimeUnit.SECONDS.sleep(second);
		} catch (InterruptedException e) {
			logger.error("", e);
		}
	}
	
	/**
	 * 关闭连接
	 * 
	 * @return void
	*/
	public void disconnect() {
		logger.debug("sftp disconnet from server");
		if (channel != null) {
			channel.exit();
		}
		if (session != null) {
			session.disconnect();
		}
	}
	
	/**
	 * 更改当前工作目录为path
	 * 
	 * @param path
	 * @return boolean
	*/
	public boolean changeWorkDirectory(String path) {
		logger.debug("change sftp server work directory to {}", path);
		try {
			channel.cd(path);
		} catch (SftpException e) {
			logger.error("change work directory error:", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 上传文件到server
	 * 
	 * @param localFile
	 * @param destName
	 * @return boolean
	*/
	public boolean putRemoteFile(String localFile, String destName) {
		logger.debug("SftpClient upload {} to {}", localFile, destName);

		try {
			// 传输数据文件
			channel.put(localFile, destName);
		} catch (SftpException e) {
			logger.error("put file {} error:", localFile, e);
			return false;
		}

		return true;
	}
	
	/**
	 * 下载文件
	 * 
	 * @param localDir
	 * @param remoteFile
	 * @return boolean
	*/
	public boolean getRemoteFile(String localDir, String remoteFile) {
		logger.debug("SftpClient download {} begin", remoteFile);
		boolean successed = true;
		if (!localDir.endsWith(File.separator)) {
			localDir += File.separator;
		}
		try {
			SftpATTRS attrs = channel.lstat(remoteFile);
			if (!attrs.isDir()) {
				channel.get(remoteFile, localDir);
			}
		} catch (SftpException e) {
			logger.error("download {} error:", remoteFile, e);
			successed = false;
		}

		logger.debug("SftpClient download {} end", remoteFile);
		return successed;
	}
	
	/**
	 * 新建目录
	 * 
	 * @param dir
	 * @return boolean
	*/
	public boolean createRemoteDir(String dir) {
		logger.info("tring to mkdir " + dir);
		try {
			channel.mkdir(dir);
		} catch (SftpException e) {
			logger.error("could not create directory " + dir, e);
			return false;
		}
		return true;
	}
	
	/**
	 * 通过sftp删除文件
	 * 
	 * @param file
	 * @return boolean
	*/
	public boolean deleteRemoteFile(String file) {
		logger.debug("SftpClient delete remote file {}", file);
		try {
			SftpATTRS attr = channel.stat(file);
			if (!attr.isDir()) {
				channel.rm(file);
			}
		} catch (SftpException e) {
			logger.error("SftpClient delete remote file {} error:", file, e);
			return false;
		}
		return true;
	}
	
	/**
	 * 文件重命名
	 * 
	 * @param oldName
	 * @param newName
	 * @return boolean
	*/
	public boolean renameFile(String oldName, String newName) {
		logger.debug("SftpClient rename {} to  {}", oldName, newName);
		try {
			channel.rename(oldName, newName);
		} catch (SftpException e) {
			logger.error("SftpClient rename {} to {} error:", oldName, newName,
					e);
			return false;
		}

		return true;
	}
	
	/**
	 * 校验目录及权限
	 * 
	 * @param dir
	 * @return boolean
	*/
	public boolean checkDirectory(String dir) {
		boolean succeed = true;
		try {
			SftpATTRS attr = channel.stat(dir);
			String permission = attr.getPermissionsString();
			if (!permission.startsWith("d")) {
				logger.error(" directory {} is not exist", dir);
				succeed = false;
			} else if (!permission.startsWith("drwx")) {
				logger.error("directory {} permission error ", dir);
				succeed = false;
			}
		} catch (SftpException e) {
			succeed = false;
			logger.error("", e);
		}

		return succeed;
	}

	public boolean exists(String file) {
		try {
			channel.stat(file);
		} catch (SftpException e) {
			return false;
		}

		return true;
	}
	
	/**
	 * 通过sftp创建控制文件
	 * 
	 * @param fileName
	 * @return boolean
	*/
	public boolean createRemoteCtrlFile(String fileName) {
		logger.debug("SftpClient create remote file {}", fileName);
		OutputStream out = null;
		boolean flag = true;
		try {
			// 生成控制文件
			out = channel.put(fileName);
		} catch (SftpException e) {
			logger.error("SftpClient create remote file {} error:", fileName, e);
			flag = false;
		} finally {
			IOUtils.closeQuietly(out);
		}

		return flag;
	}
	
	/**
	 * 是否已建立连接
	 * 
	 * @return boolean
	*/
	public boolean isConnected() {
		return session.isConnected();
	}
}