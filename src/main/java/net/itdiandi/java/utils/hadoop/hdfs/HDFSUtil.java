package net.itdiandi.java.utils.hadoop.hdfs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @ProjectName HDFSTest
* @PackageName net.itdiandi
* @ClassName Test
* @Description java api访问hdfs
* @Author 刘吉超
* @Date 2016-04-13 09:46:30
*/
public class HDFSUtil {
	// 日志
	private static Logger logger = LoggerFactory.getLogger(HDFSUtil.class);
	// hdfs的uri,hdfsUri为空表示本地运行（打成jar,在hadoop集群中运行），否则，远程运行（在windows中的eclipse下运行）；
	public static String HDFSUri= "hdfs://ljc:9000";
	
	/**
	 * 判断路径是否存在
	 * 
	 * @param filePath 路径
	 * @return boolean
	 */
	public static boolean existDir(String filePath){
		return existDir(filePath,false);
	}
	
	/**
	 * 判断路径是否存在
	 * 
	 * @param filePath 路径
	 * @param create 如果不存在，是否创建，true：创建；false：不创建
	 * @return boolean
	 */
	public static boolean existDir(String filePath, boolean create){
		boolean flag = false;
		
		if (StringUtils.isEmpty(filePath)){
			return flag;
		}
		
		try{
			Path path = new Path(filePath);
			// FileSystem对象
			FileSystem fs = getFileSystem();
			
			if (create){
				if (!fs.exists(path)){
					fs.mkdirs(path);
				}
			}
			
			if (fs.isDirectory(path)){
				flag = true;
			}
		}catch (Exception e){
			logger.error("", e);
		}
		
		return flag;
	}
	
	/**
	 * 文件重命名
	 * 
	 * @param srcPath 源路径
	 * @param dstPath 目标路径
	 * @return boolean
	 */
	public static boolean rename(String srcPath, String dstPath){
		boolean flag = false;
		
		String hdfsUri = HDFSUri;
		if(StringUtils.isNotBlank(hdfsUri)){
			srcPath = hdfsUri + srcPath;
			dstPath = hdfsUri + dstPath;
		}
		
		try	{
			// 返回FileSystem对象
			FileSystem fs = getFileSystem();
			
			flag = fs.rename(new Path(srcPath), new Path(dstPath));
		} catch (IOException e) {
			logger.error("{} rename to {} error.", srcPath, dstPath);
		}
		
		return flag;
	}
	
	/**
	 * 查找某个文件在 HDFS集群的位置
	 * 
	 * @param filePath
	 * @return BlockLocation[]
	 */
	public static BlockLocation[] getFileBlockLocations(String filePath) {
		// 文件路径
		String hdfsUri = HDFSUri;
		if(StringUtils.isNotBlank(hdfsUri)){
			filePath = hdfsUri + filePath;
		}
		Path path = new Path(filePath);
		
		// 文件块位置列表
		BlockLocation[] blkLocations = new BlockLocation[0];
		try {
			// 返回FileSystem对象
			FileSystem fs = getFileSystem();
			// 获取文件目录 
			FileStatus filestatus = fs.getFileStatus(path);
			//获取文件块位置列表
			blkLocations = fs.getFileBlockLocations(filestatus, 0, filestatus.getLen());
			
			// 释放资源
			fs.close();
		} catch (IOException e) {
			logger.error("", e);
		}
		return blkLocations;
	}
	
	/**
	 * 获取 HDFS集群节点信息
	 * 
	 * @return DatanodeInfo[]
	 */
	public static DatanodeInfo[] getHDFSNodes() {
		// 获取所有节点
		DatanodeInfo[] dataNodeStats = new DatanodeInfo[0];
		
		try {
			// 返回FileSystem对象
			FileSystem fs = getFileSystem();
			
			// 获取分布式文件系统
			DistributedFileSystem hdfs = (DistributedFileSystem)fs;
			
			dataNodeStats = hdfs.getDataNodeStats();
			
			// 释放资源
			fs.close();
		} catch (IOException e) {
			logger.error("", e);
		}
		return dataNodeStats;
	}
	
	/**
	 * 从 HDFS下载文件
	 * 
	 * @param srcFile
	 * @param destPath
	 */
	public static boolean downFile(String srcFile,String destPath) {
		boolean flag = false;
		
		// 源文件路径
		String hdfsUri = HDFSUri;
		if(StringUtils.isNotBlank(hdfsUri)){
			srcFile = hdfsUri + srcFile;
		}
		Path srcPath = new Path(srcFile);
		
		// 目的路径是Linux下的路径，如果在 windows 下测试，需要改写为Windows下的路径，比如D:\hadoop\djt
		Path dstPath = new Path(destPath);
		
		try {
			// 获取FileSystem对象
			FileSystem fs = getFileSystem();
			// 下载hdfs上的文件
			fs.copyToLocalFile(srcPath, dstPath);
			
			// 释放资源
			fs.close();
			
			flag = true;
		} catch (IOException e) {
			logger.error("", e);
		}
		return flag;
	}
	
	/**
	 * 文件上传至 HDFS
	 * 
	 * @param srcFile
	 * @param destPath
	 */
	public static boolean copyFileToHDFS(String srcFile,String destPath) {
		return copyFileToHDFS(false,srcFile, destPath);
	}
	
	/**
	 * 文件上传至 HDFS
	 * 
	 * @param delSrc 如果存在，是否删除源文件
	 * @param srcFile 源文件
	 * @param destPath 目标路径
	 */
	public static boolean copyFileToHDFS(boolean delSrc,String srcFile,String destPath) {
		return copyFileToHDFS(delSrc,true,srcFile, destPath);
	}
	
	/**
	 * 文件上传至 HDFS
	 * 
	 * @param delSrc
	 * @param overwrite
	 * @param srcFile
	 * @param destPath
	 */
	public static boolean copyFileToHDFS(boolean delSrc, boolean overwrite,String srcFile,String destPath) {
		boolean flag = false;
		
		// 源文件路径是Linux下的路径，如果在 windows 下测试，需要改写为Windows下的路径，比如D://hadoop/djt/weibo.txt
		Path srcPath = new Path(srcFile);
		
		// 目的路径
		String hdfsUri = HDFSUri;
		if(StringUtils.isNotBlank(hdfsUri)){
			destPath = hdfsUri + destPath;
		}
		Path dstPath = new Path(destPath);
		
		// 实现文件上传
		try {
			// 获取FileSystem对象
			FileSystem fs = getFileSystem();
			
			fs.copyFromLocalFile(delSrc,overwrite,srcPath, dstPath);
			// 释放资源
			fs.close();
			
			flag = true;
		} catch (IOException e) {
			logger.error("", e);
		}
		return flag;
	}
	
	/**
	 * 获取目录下的文件
	 * 
	 * @param path
	 * @return String[]
	 */
	public static String[] listFile(String path) {
		return listFile(path,null);
	}
	
	/**
	 * 根据filter获取目录下的文件
	 * 
	 * @param path
	 * @param pathFilter
	 * @return String[]
	 */
	public static String[] listFile(String path,PathFilter pathFilter) {
		String[] files = new String[0];
		
		String hdfsUri = HDFSUri;
		if(StringUtils.isNotBlank(hdfsUri)){
			path = hdfsUri + path;
		}
		
		try {
			// 返回FileSystem对象
			FileSystem fs = getFileSystem();
			
			FileStatus[] status;
			if(pathFilter != null){
				// 根据filter列出目录内容
				status = fs.listStatus(new Path(path),pathFilter);
			}else{
				// 列出目录内容
				status = fs.listStatus(new Path(path));
			}
			
			// 获取目录下的所有文件路径
			Path[] listedPaths = FileUtil.stat2Paths(status);
			// 转换String[]
			if (listedPaths != null && listedPaths.length > 0){
				files = new String[listedPaths.length];
				for (int i = 0; i < files.length; i++){
					files[i] = listedPaths[i].toString();
				}
			}
			// 释放资源
			fs.close();
		} catch (IllegalArgumentException | IOException e) {
			logger.error("", e);
		}
		
		return files;
	}
	
	/**
	 * 删除文件或者文件目录
	 * 
	 * @param path
	 */
	public static boolean rmdir(String path) {
		boolean flag = false;
		
		String hdfsUri = HDFSUri;
		if(StringUtils.isNotBlank(hdfsUri)){
			path = hdfsUri + path;
		}
		
		try {
			// 返回FileSystem对象
			FileSystem fs = getFileSystem();
			
			// 删除文件或者文件目录  delete(Path f) 此方法已经弃用
			flag = fs.delete(new Path(path),true);
			
			// 释放资源
			fs.close();
		} catch (IllegalArgumentException | IOException e) {
			logger.error("", e);
		}
		return flag;
	}
	
	/**
	 * 创建目录
	 * 
	 * @param path 路径
	 */
	public static boolean mkdir(String path) {
		boolean flag = false;
		
		String hdfsUri = HDFSUri;
		if(StringUtils.isNotBlank(hdfsUri)){
			path = hdfsUri + path;
		}
		
		try {
			// 获取文件系统
			FileSystem fs = getFileSystem();
			
			// 创建目录
			flag = fs.mkdirs(new Path(path));
			
			//释放资源
			fs.close();
		} catch (IllegalArgumentException | IOException e) {
			logger.error("", e);
		}
		return flag;
	}
	
	/**
	 * 获取文件系统
	 * 
	 * @return FileSystem
	 */
	public static FileSystem getFileSystem() {
		//读取配置文件
		Configuration conf = new Configuration();
		// 文件系统
		FileSystem fs = null;
		
		String hdfsUri = HDFSUri;
		if(StringUtils.isBlank(hdfsUri)){
			// 返回默认文件系统  如果在 Hadoop集群下运行，使用此种方法可直接获取默认文件系统
			try {
				fs = FileSystem.get(conf);
			} catch (IOException e) {
				logger.error("", e);
			}
		}else{
			// 返回指定的文件系统,如果在本地测试，需要使用此种方法获取文件系统
			try {
				URI uri = new URI(hdfsUri.trim());
				fs = FileSystem.get(uri,conf);
			} catch (URISyntaxException | IOException e) {
				logger.error("", e);
			}
		}
		
		return fs;
	}
}
