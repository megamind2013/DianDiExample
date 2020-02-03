package net.itdiandi.java.utils.hadoop;

import java.util.Arrays;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.hadoop
* @ClassName HFDFSTest
* @Description hdfs测试
* @Author 刘吉超
* @Date 2016-04-13 21:33:38
*/
@SuppressWarnings("unused")
public class HFDFSTest {
	public static void main(String[] args) {
		// 判断/buaa目录是否存在，如果不存在，则创建
//		boolean flag = HDFSUtil.existDir("/buaa",true);
//		System.out.println(flag);
		
		// 在/buaa下创建text目录
//		boolean flag1 = HDFSUtil.mkdir("/buaa/ljc");
//		System.out.println(flag1);
		
		// 拷贝D:\test.txt（这个文件已存在）文件到HDFS中
//		boolean flag2 = HDFSUtil.copyFileToHDFS("D:\\test.txt", "/buaa/ljc");
//		System.out.println(flag2);
		
		// 查看/buaa/ljc目录内容
//		String[] strArr = HDFSUtil.listFile("/buaa/ljc");
//		System.out.println("/buaa/ljc目录中内容：" + Arrays.toString(strArr));
		
		// 将在/buaa/ljc目录下的test.txt文件改名为ljc.txt
//		boolean flag3 = HDFSUtil.rename("/buaa/ljc/test.txt", "/buaa/ljc/ljc.txt");
//		System.out.println(flag3);
		
		// 将在/buaa/ljc目录下的ljc.txt下载到D:\ljc（已存在）下
//		HDFSUtil.downFile("/buaa/ljc/ljc.txt", "D:\\ljc");
		
		// 将/buaa/ljc目录删除
//		boolean flag4 = HDFSUtil.rmdir("/buaa/ljc");
//		System.out.println(flag4);
	}
}
