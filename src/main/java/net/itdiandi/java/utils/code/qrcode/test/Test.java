package net.itdiandi.java.utils.code.qrcode.test;

import net.itdiandi.java.utils.code.qrcode.QRCodeUtil;
import net.itdiandi.java.utils.code.qrcode.ZxingUtil;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.code.qrcode.test
* @ClassName Test
* @Description TODO
* @author 刘吉超
* @date 2016-03-08 10:39:50
*/
public class Test {
	public static void main(String[] args) {
		/*
		 * 通过谷歌的Zxing操作二维码
		 */
		// 将文字转换为二维码
		ZxingUtil.encoder("北京航空航天大学-软件学院",300,300,"D:/二维码文件.gif");
		// 解析二维码
		System.out.println(ZxingUtil.decoder("D:/二维码文件.gif"));
		
		/*
		 * 通过日本的QRCode操作二维码
		 */
		// 将文字转换为二维码
		QRCodeUtil.encoder("北京航空航天大学-软件学院",140,140,"D:/二维码文件1.gif");
	}
}
