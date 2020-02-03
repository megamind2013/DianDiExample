package net.itdiandi.java.utils.file.excel.poi;

import java.util.List;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;

/** 
* @ProjectName Utils
* @PackageName net.itdiandis.utils.file.excel.poi
* @ClassName CheckExcel
* @Description TODO
* @author 刘吉超
* @date 2016-03-15 16:17:21
*/
public interface CheckExcel {
	/**
	 * 设置excel版本（2003、2007）,默认2003
	 * xls：2003；xlsx：2007
	 * @return int
	*/
	public String getExcelVersion();
	
	/**
	 * 是否将数据显示在一个sheet中
	 * true：表示将数据显示在一个sheet中
	 * false：表示将数据显示在若干个sheet中，sheet名称可在getSheetNames设置
	 * 
	 * @return boolean
	 */
	public boolean isShowSingleSheet();
	
	/**
	 * 每个sheet显示多少条记录
	 * 必须大于0，否则不起作用
	 * 
	 * @return int
	 */
	public int recordNumPerSheet();
	
	/**
	 * 获取sheet名称
	 * 返回值如果是null或长度为0，则有两种情况
	 * 	设isShowSingleSheet的返回值为true，sheet名称为“sheet1”
	 * 	设isShowSingleSheet的返回值为false，sheet名称为“sheet1”,“sheet2”,“sheet3”,“sheet4”...
	 * 返回值不为null且长度大于0，则有两种情况
	 * 	设isShowSingleSheet的返回值为true，将getSheetNames返回值的第一个作为sheet名称
	 * 	设isShowSingleSheet的返回值为false，将getSheetNames返回值作为sheet名称
	 * 
	 * @return String[]
	*/
	public String[] getSheetNames();
	
	/**
	 * 是否显示head
	 * 
	 * @return boolean
	 */
	public boolean showSheetHead();
	
	/**
	 * 设置head样式
	 * null：表示使用默认设置，否则自行设置
	 * 
	 * @param cellStyle
	 * @param font
	 * @param format
	 * @return CellStyle
	 */
	public CellStyle headCellStyle(CellStyle cellStyle,Font font,DataFormat format);
	
	/**
	 * 设置head样式
	 * null：表示使用默认设置，否则自行设置
	 * 
	 * @param cellStyle
	 * @param font
	 * @param format
	 * @return CellStyle
	 */
	public CellStyle bodyCellStyle(CellStyle cellStyle,Font font,DataFormat format);
	
	/**
	 * excel列与实体属性对应关系
	 * null或长度为0表示默认，显示所有属性，列名col1,col2,col3,col4...
	 * 不为null且长度大于0，则
	 * 	Entry
	 * 		key：属性名
	 * 		value：列名
	 * 
	 * @return
	 */
	public List<Entry<String,String>> colPropertyCorrespondence(String[] propertyName);
}
