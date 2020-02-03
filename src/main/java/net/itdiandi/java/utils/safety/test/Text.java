package net.itdiandi.java.utils.safety.test;

import net.itdiandi.java.utils.safety.MD5Util;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.safety.test
* @ClassName Text
* @Description TODO
* @Author 刘吉超
* @Date 2016-05-11 17:01:06
*/
public class Text {
	public static void main(String[] args) throws Exception {
		String str = "76d80224611fc919a5d54f0ff9fba446";
		System.out.println(MD5Util.encodeMd5(str));
	}
}
