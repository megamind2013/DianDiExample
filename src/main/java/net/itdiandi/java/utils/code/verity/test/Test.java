package net.itdiandi.java.utils.code.verity.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.itdiandi.java.utils.code.verity.VerityCodeUtil;

/** 
* @ProjectName Utils
* @PackageName net.itdiandis.utils.code.verity.test
* @ClassName Test
* @Description TODO
* @author 刘吉超
* @date 2016-03-08 11:33:17
*/
public class Test {
	public static void main(String[] args) throws IOException {
		OutputStream outputStream = new FileOutputStream("D://as.jpg");
		// 创建验证码
		VerityCodeUtil.build(null, outputStream, 4);
		// 关闭流
		outputStream.close();
	}
}
