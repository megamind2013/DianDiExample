package net.itdiandi.java.utils;

import java.io.File;

/** 
* @ProjectName SSOSeaBox
* @PackageName com.seabox.loginsys.login.utils
* @ClassName PathUtils
* @Description TODO
* @Author 刘吉超
* @Date 2017-02-08 10:13:43
*/
public class PathUtils {
	/*
	 * 使用代码new File(Thread.currentThread().getContextClassLoader().getResource("").toString()).getParentFile().getParentFile().toString()
	 * 获取项目路径如下：
	 * 	Linux：file:/opt/server/apache-tomcat-7.0.70/webapps/SSOSeaBox
	 * 	window：file:\D:\ProgramFiles\apache-tomcat-7.0.70\webapps\SSOSeaBox
	 */
	public static String getProjectPath(){
		String path = new File(Thread.currentThread().getContextClassLoader().getResource("").toString()).getParentFile().getParentFile().toString();
		if(path.startsWith("file:\\")){ // windows下获取项目路径
			path = path.substring(6);
		}else if(path.startsWith("file:/")){
			path = path.substring(5); // linux下获取项目路径 
		}
		return path;
	}
    
    public static void main(String[] args){
        // 获取项目所在路径
        System.out.println(System.getProperty("user.dir")); // 如loadDataClient项目的地址是：E:\Workspace\300\loadDataClient，获取到的值就是E:\Workspace\300\loadDataClient
    }
}
