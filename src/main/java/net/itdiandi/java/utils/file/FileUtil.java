package net.itdiandi.java.utils.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @ProjectName ProjectSummary
* @PackageName com.java.utils
* @ClassName FileUtil
* @Description File工具类
* @Author 刘吉超
* @Date 2015-07-20 10:43:28
*/
public class FileUtil {
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	public static final String ENCODING_UTF8 = "UTF-8";
	private static final String GZIP_SUFFIX = ".gz";
	private static int bufferSize = 4096;

	public static InputStream getFileInputStream(File file) {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			logger.error("getFileInputStream error: file not found", e);
		}
		return null;
	}

	public static InputStream getGZIPInputStream(File file) {
		try {
			return new GZIPInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("getGZIPInputStream error: file not found", e);
		} catch (IOException e) {
			logger.error("getGZIPInputStream error:", e);
		}
		return null;
	}

	public static OutputStream getFileOutputStream(File file) {
		try {
			return new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			logger.error("getFileOutputStream error: file not found", e);
		}
		return null;
	}

	public static OutputStream getGZIPOutputStream(File file) {
		try {
			return new GZIPOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("getGZIPOutputStream error: file not found", e);
		} catch (IOException e) {
			logger.error("getGZIPOutputStream error:", e);
		}
		return null;
	}

	/**
	 * 获取用utf-8格式读取的流
	 * 
	 * @param file
	 * @return BufferedReader
	 */
	public static BufferedReader getBufferedReader(File file) {
		if (file == null) {
			logger.error("getBufferedReader error: file is null");
			return null;
		}
		String filename = file.getName();
		InputStream is;
		if (StringUtils.endsWithIgnoreCase(filename, GZIP_SUFFIX)) {
			is = getGZIPInputStream(file);
		} else {
			is = getFileInputStream(file);
		}

		if (is == null) {
			logger.error("getBufferedReader return null");
			return null;
		}
		try {
			return new BufferedReader(new InputStreamReader(is, ENCODING_UTF8));
		} catch (UnsupportedEncodingException e) {
			logger.error("getBufferedReader error", e);
			IOUtils.closeQuietly(is);
		}
		return null;
	}

	/**
	 * 获取用utf-8写出的流
	 * 
	 * @param file
	 * @return PrintWriter
	 */
	public static PrintWriter getBufferedWriter(File file) {
		if (file == null) {
			logger.error("getBufferedWriter error: file is null");
			return null;
		}
		String filename = file.getName();
		OutputStream os;
		if (StringUtils.endsWithIgnoreCase(filename, GZIP_SUFFIX)) {
			os = getGZIPOutputStream(file);
		} else {
			os = getFileOutputStream(file);
		}

		if (os == null) {
			logger.error("getBufferedWriter return null");
			return null;
		}

		try {
			return new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					os, ENCODING_UTF8)));
		} catch (UnsupportedEncodingException e) {
			logger.error("getBufferedWriter error", e);
			IOUtils.closeQuietly(os);
		}
		return null;
	}

	/**
	 * 使用GZIP压缩文件
	 * 
	 * @param file要压缩的文件
	 * @return boolean
	 */
	public static boolean compressGZFile(String file) {
		String destFile = file + GZIP_SUFFIX;
		return compressGZFile(file, destFile);
	}

	/**
	 * 使用GZIP压缩文件
	 * 
	 * @param srcFile 要压缩的文件
	 * @param destFile 生成的文件
	 * @return boolean
	 */
	public static boolean compressGZFile(String srcFile, String destFile) {
		InputStream in = null;
		OutputStream out = null;
		try {
			logger.debug("compress file " + srcFile + " begin.");

			in = new FileInputStream(srcFile);
			out = new GZIPOutputStream(new FileOutputStream(destFile));
			byte[] buf = new byte[bufferSize];
			int read = 0;
			while ((read = in.read(buf)) >= 0) {
				out.write(buf, 0, read);
			}
		} catch (IOException e) {
			logger.error("compress File " + srcFile + " occurs error", e);
			File dest = new File(destFile);
			if (dest.exists()) {
				FileUtils.deleteQuietly(dest);
			}
			return false;
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		logger.debug("compress file " + srcFile + " end.");
		return true;
	}
	
	/**
	 * 使用GZIP解压缩文件
	 * 
	 * @param file
	 * @return boolean
	*/
	public static boolean decompressGZFile(String file) {
		if (StringUtils.isEmpty(file)) {
			return false;
		}
		int index = file.indexOf(GZIP_SUFFIX);
		if (index == -1) {
			logger.error("the file name " + file + " is not a correct GZIP file.");
			return false;
		}
		String destFile = file.substring(0, index);

		return decompressGZFile(file, destFile);
	}
	
	/**
	 * 解压缩文件
	 * 
	 * @param srcFile
	 * @param destFile
	 * @return boolean
	*/
	public static boolean decompressGZFile(String srcFile, String destFile) {
		InputStream in = null;
		OutputStream out = null;

		try {
			logger.debug("decompress file " + srcFile + " begin.");
			in = new GZIPInputStream(new FileInputStream(srcFile));
			out = new FileOutputStream(destFile);
			byte buf[] = new byte[bufferSize];
			int read = 0;
			while ((read = in.read(buf)) >= 0) {
				out.write(buf, 0, read);
			}
		} catch (IOException e) {
			logger.error("decompress File " + srcFile + " occurs error", e);
			File dest = new File(destFile);
			if (dest.exists()) {
				FileUtils.deleteQuietly(dest);
			}

			return false;
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		logger.debug("decompress file " + srcFile + " end.");
		return true;
	}
	
	/**
	 * 删除目录下文件
	 * 
	 * @param path
	 * @param fileName
	 * @return void
	*/
	public static void deleteFile(String path, String fileName) {
		if (StringUtils.isNotEmpty(path)) {
			// 删除备份文件
			FileUtils.deleteQuietly(new File(path, fileName));
		}
	}
	
    /**
     * 复制文件到指定目录 
     * 
     * @param file
     * @param dest
     * @return
     * @return boolean
    */
    public static boolean copyFile(File file, File dest) 
    { 
        logger.debug("copy file " + file.getName() + " to " + dest.getParent() + " begin."); 
        try 
        { 
            FileUtils.copyFile(file, dest); 
        } catch (IOException e) { 
            logger.error("copy file " + file.getName() + " to " + dest.getParent() + " error.", e); 
            return false; 
        } 
        logger.debug("copy file " + file.getName() + " to " + dest.getParent() + " end."); 
        return true; 
    } 
    
    /**
     * 创建文件 
     * 
     * @param name
     * @return boolean
    */
    public static boolean createFile(String name) 
    { 
        logger.debug("tring to create file " + name); 
        try 
        { 
            FileUtils.touch(new File(name)); 
        } catch (IOException e) { 
            logger.error("create file " + name + "occurs error ", e); 
            return false; 
        } 
        return true; 
    } 
	
	public static void append(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/** 
	  * 递归删除目录下的所有文件及子目录下的所有文件 
	  * 
	  * @param file 将要删除的文件目录 
	  * @return 如果所有文件都删除成功则返回true, 有一个文件删除失败就停止删除并返回false 
	  */  
	public static boolean deleteFile(File file) {  
		boolean success = false;  
//		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();  
				for (File file1 : files) {//递归删除文件或目录
					deleteFile(file1);
				}
				FileUtils.deleteQuietly(file);
			} else {
				FileUtils.deleteQuietly(file);
			}  
//		} else {  
//			success = false;  
//		}  
		return success;  
	}
	public static void main(String[] args) {
		System.out.println(deleteFile(new File("E:\\Contribution\\Github\\kylin")));
//		FileUtils.deleteQuietly(new File("E:\\Contribution\\Github"));
	}
}