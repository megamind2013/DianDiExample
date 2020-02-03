package net.itdiandi.java.utils.file.excel.jxl.test;

import java.util.Date;

/** 
* @ProjectName Utils
* @PackageName net.itdiandis.utils.file.excel.jxl
* @ClassName StudentVO
* @Description TODO
* @author 刘吉超
* @date 2016-03-05 10:48:23
*/
public class StudentVO{
	// 姓名
	private String name;
	// 性别
	private String sex;
	// 年龄
	private int age;
	// 日期
	private Date date;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}