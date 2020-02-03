package net.itdiandi.java.utils.email.test;

import net.itdiandi.java.utils.email.EmailUtil;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.email.test
* @ClassName Test
* @Description TODO
* @author 刘吉超
* @date 2016-03-05 10:55:43
*/
public class Test {
	public static void main(String args[]) {
		EmailUtil e = EmailUtil.getInstance();
		e.send("subject：邮件需求，发送测试","body：发送测试");
	}
}
