package net.itdiandi.java.utils.file.excel.jxl.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.itdiandi.java.utils.file.excel.jxl.ExcelUtil;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.file.excel.jxl
* @ClassName Test
* @Description TODO
* @author 刘吉超
* @date 2016-03-05 09:13:34
*/
public class Test {
	public static void main(String[] args) throws FileNotFoundException {
//		List<String> excelColumnNames = new ArrayList<String>();
//		excelColumnNames.add("姓名");
//		excelColumnNames.add("性别");
//		excelColumnNames.add("年龄");
//		excelColumnNames.add("出生日期");
		
		InputStream is = new FileInputStream("D:\\workspace\\Utils\\src\\com\\buaa\\utils\\file\\excel\\jxl\\test\\测试.xls");
		
		List<Map<String, String>> list = ExcelUtil.getImportData(is);
		
		// 输出结果
		boolean titleFlag = true;
		for(Map<String, String> maps: list){
			StringBuilder title = new StringBuilder();
			StringBuilder row = new StringBuilder();
			
			for(Entry<String,String> entry : maps.entrySet()){
				if(titleFlag){
					title.append(entry.getKey()+"，");
				}
				row.append(entry.getValue()+"，");
			}
			
			if(titleFlag){
				System.out.println(title.substring(0,title.length()-1));
				titleFlag = false;
			}
			System.out.println(row.substring(0,row.length()-1));
		}
		
		
		
		
		List<StudentVO> studentList = ExcelUtil.makeData(list, StudentVO.class, new CheckExcelService());
		for(StudentVO vo : studentList){
			System.out.println(vo.getName()+"，"+vo.getAge()+"，"+vo.getSex()+"，"+vo.getDate());
		}
	}
	
	/*
	     错误一：
	  jxl.read.biff.BiffException: Unable to recognize OLE stream
		at jxl.read.biff.CompoundFile.<init>(CompoundFile.java:116)
		at jxl.read.biff.File.<init>(File.java:127)
		at jxl.Workbook.getWorkbook(Workbook.java:268)
		at jxl.Workbook.getWorkbook(Workbook.java:253)
		at net.itdiandis.utils.file.excel.jxl.ExcelUtil.getImportData(ExcelUtil.java:53)
		at net.itdiandi.utils.file.excel.jxl.Test.main(Test.java:27)
	 报以上错误，说明你操作的是2007版的
	
	
	*/
}
