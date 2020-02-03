package net.itdiandi.java.utils.file.excel.poi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** 
* @ProjectName ProjectSummary
* @PackageName com.java.utils
* @ClassName ExcelExportUtil
* @Description excel导出工具
* @Author 刘吉超
* @Date 2015-07-20 10:43:12
*/
public class ExcelUtil {
	/**
	 * 创建工作簿
	 * 
	 * @param resultList
	 * @param columnNames
	 * @param excelVO
	 * @return Workbook
	*/
	public Workbook createExcelWorkbook(List<?> resultList, CheckExcel checkExcel) {
		// 创建Workbook
		Workbook workbook = getWorkbookInstance(checkExcel);
		// excelVO
		ExcelVO excelVO = initExcelVO(workbook,resultList,checkExcel);
		// 创建sheet
		Sheet[] sheets = createExcelSheet(workbook, excelVO);
		// 创建excel头
		for (Sheet sheet : sheets) {
			createExcelHead(sheet, excelVO);
		}

		// 创建excel body
		for (int i = 0; i < sheets.length; i++) {
			List<?> temp = getSheetData(resultList, i, excelVO);
			for (int j = 0; j < temp.size(); j++) {
				createExcelRow(temp.get(j), sheets[i], j, excelVO);
			}
		}

		return workbook;
	}
	
	/**
	 * 设置excel head单元格格式 根据需要，可以被重写
	 * 
	 * @param workbook
	 * @return CellStyle
	*/
	protected CellStyle headCellStyle(CellStyle cellStyle,Font font,DataFormat format) {
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
		cellStyle.setFillBackgroundColor((short) 10);
		font.setFontName("宋体"); // 设置字体格式
		font.setFontHeightInPoints((short) 8); // 设置字体大小
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 设置粗体
		cellStyle.setFont(font);
		
		cellStyle.setDataFormat(format.getFormat("@")); // 设置单元格格式为文本

		return cellStyle;
	}
	
	/**
	 * 设置excel body单元格格式
	 * 
	 * @param cellStyle
	 * @param font
	 * @param format
	 * @return CellStyle
	 */
	protected CellStyle bodyCellStyle(CellStyle cellStyle,Font font,DataFormat format) {
		cellStyle.setDataFormat(format.getFormat("@")); // 设置单元格格式为文本
		
		return cellStyle;
	}
	
	/**
	 * 获取Workbook实例
	 * 
	 * @param excelVO
	 * @return Workbook
	*/
	private Workbook getWorkbookInstance(CheckExcel checkExcel) {
		if(checkExcel != null && ExcelVO.EXCEL2007.equalsIgnoreCase(checkExcel.getExcelVersion())){
			return new XSSFWorkbook();
		}
		return new HSSFWorkbook();
	}
	
	/**
	 * 初始化ExcelVO
	 * 
	 * @param workbook
	 * @param resultList
	 * @param checkExcel
	 * @return ExcelVO
	 */
	private ExcelVO initExcelVO(Workbook workbook,List<?> resultList,CheckExcel checkExcel){
		ExcelVO excelVO = new ExcelVO();
		
		if(checkExcel != null){
			// 初始化sheet
			initSheetName(resultList,checkExcel,excelVO);
			// head样式
			CellStyle headCellStyle = checkExcel.headCellStyle(workbook.createCellStyle(),workbook.createFont(),workbook.createDataFormat());
			if(headCellStyle == null){
				headCellStyle = headCellStyle(workbook.createCellStyle(),workbook.createFont(),workbook.createDataFormat());
			}
			excelVO.setHeadCellStyle(headCellStyle);
			
			// body样式
			CellStyle bodyCellStyle = checkExcel.bodyCellStyle(workbook.createCellStyle(),workbook.createFont(),workbook.createDataFormat());
			if(bodyCellStyle == null){
				bodyCellStyle = bodyCellStyle(workbook.createCellStyle(),workbook.createFont(),workbook.createDataFormat());
			}
			excelVO.setBodyCellStyle(bodyCellStyle);
			
			// 获取属性名
			initEntryPropertyName(excelVO,resultList);
			// 属性名与列名
			List<Entry<String,String>> tempList = checkExcel.colPropertyCorrespondence(excelVO.getPropertyName());
			if(tempList != null && tempList.size() > 0){
				String[] tempPropertyName = new String[tempList.size()];
				String[] tempColName = new String[tempList.size()];
				
				for(int i = 0;i < tempList.size();i++){
					Entry<String,String> entry = tempList.get(i);
					tempPropertyName[i] = entry.getKey();
					tempColName[i] = entry.getValue();
				}
				
				excelVO.setPropertyName(tempPropertyName);
				excelVO.setColName(tempColName);
			}else{
				String[] tempPropertyName = excelVO.getPropertyName();
				String[] tempColName = new String[tempPropertyName.length];
				
				for(int i=0;i<tempPropertyName.length;i++){
					tempColName[i]="col"+(i+1);
				}
				excelVO.setColName(tempColName);
			}
			
			
		}
		
		return excelVO;
	}

	
	/**
	 * 获取属性名
	 * 
	 * @param excelVO
	 * @param resultList
	 * @return ExcelVO
	*/
	private ExcelVO initEntryPropertyName(ExcelVO excelVO,List<?> resultList){ 
		// 如果结果集是空集合，直接返回 
		if(resultList == null || resultList.size() == 0){ 
			return excelVO; 
		} 
		
		// reslutAttributeNameSet为空，将其初始化 
		Class<? extends Object> classType = resultList.get(0).getClass(); 
		Field[] fields = classType.getDeclaredFields(); 
		
		String[] tempName = new String[fields.length]; 
		for(int i=0;i<fields.length;i++){ 
			tempName[i] = fields[i].getName(); 
		}
		
		excelVO.setPropertyName(tempName); 

		return excelVO; 
	} 
	
	/**
	 * initSheetName
	 * 
	 * @param excelVO
	 * @param resultList
	 * @return ExcelVO
	*/
	private void initSheetName(List<?> resultList,CheckExcel checkExcel,ExcelVO excelVO) {
		// 是否将数据显示在一个sheet中
		excelVO.setShowSingleSheet(checkExcel.isShowSingleSheet());
		// 每个sheet显示多少条记录
		excelVO.setRecordNumPerSheet(checkExcel.recordNumPerSheet());
		
		String[] sheetName = checkExcel.getSheetNames();
		
		if (excelVO.isShowSingleSheet()) {
			String[] temp = new String[1];
			if (sheetName == null || sheetName.length == 0) {
				temp[0] = "sheet1";
			} else {
				temp[0] = sheetName[0];
			}
			sheetName = temp;
		} else if (resultList != null && resultList.size() > 0) {
			int numPerSheet = excelVO.getRecordNumPerSheet();
			int resultSize = resultList.size();
			int sheetNum = resultSize % numPerSheet == 0 ? resultSize / numPerSheet : resultSize / numPerSheet + 1;
			
			if(sheetName == null || sheetName.length == 0){
				sheetName = new String[sheetNum];

				for (int i = 0; i < sheetNum; i++) {
					sheetName[i] = "sheet" + (i + 1);
				}
			}else if(sheetName.length < sheetNum){
				for(int i = sheetName.length;i < sheetNum;i++){
					sheetName[i] = "sheet" + (i + 1);
				}
			}
		}
		// sheet名称
		excelVO.setSheetName(sheetName);
	}
	
	/**
	 * initReslutAttributeNameSet
	 * 
	 * @param workbook
	 * @param resultList
	 * @param excelVO
	 * @return Sheet[]
	*/
	private Sheet[] createExcelSheet(Workbook workbook, ExcelVO excelVO) {
		String[] sheetName = excelVO.getSheetName();
		
		Sheet[] sheet = new Sheet[sheetName.length];
		for (int i = 0; i < sheetName.length; i++) {
			sheet[i] = workbook.getSheet(sheetName[i]);
			if (sheet[i] == null) {
				sheet[i] = workbook.createSheet(sheetName[i]);// 创建sheet
			}
		}

		return sheet;
	}
	
	/**
	 * 获得sheet数据
	 * 
	 * @param resultList
	 * @param i
	 * @param sheetLength
	 * @param excelVO
	 * @return List<?>
	*/
	private List<?> getSheetData(List<?> resultList, int i, ExcelVO excelVO) {
		int sheetNumber = excelVO.getSheetName().length;
		int fromIndex = i * sheetNumber;

		if (resultList == null) {
			return new ArrayList<Object>();
		} else if (excelVO.isShowSingleSheet()) {
			return resultList;
		} else if (i == sheetNumber - 1) {
			return resultList.subList(fromIndex, resultList.size());
		} else {
			int toIndex = fromIndex + sheetNumber;
			toIndex = toIndex >= resultList.size() ? resultList.size() : toIndex;
			return resultList.subList(fromIndex, toIndex);
		}
	}
	
	/**
	 * 创建excel头部
	 * 
	 * @param sheet
	 * @param columns
	 * @param cellStyle
	 * @param excelVO
	 * @return void
	*/
	private void createExcelHead(Sheet sheet, ExcelVO excelVO) {
		Row contenRow = sheet.getRow(excelVO.getHeadTopMargin());
		if (contenRow == null) {
			contenRow = sheet.createRow(excelVO.getHeadTopMargin());
		}

		String[] columns = excelVO.getColName();
		
		// 输出head
		for (int i = 0; i < columns.length; i++) {
			Cell cell = contenRow.getCell(i);
			if (cell == null) {
				cell = contenRow.createCell(i);
			}
			// 编码
			cell.setCellValue(columns[i]);
		}

		// 设置样式
		for (int i = 0; i < contenRow.getPhysicalNumberOfCells(); i++) {
			contenRow.getCell(i).setCellStyle(excelVO.getHeadCellStyle());
		}
	}
	
	/**
	 * 创建excel body
	 * 
	 * @param bean
	 * @param sheet
	 * @param index
	 * @param cellStyle
	 * @param excelVO
	 * @return void
	*/
	private void createExcelRow(Object bean, Sheet sheet, int index, ExcelVO excelVO) {
		// 实体vo中的属性名
		String[] propertyName = excelVO.getPropertyName();

		Row contenRow = sheet.getRow(index + excelVO.getBodyTopMargin());
		if (contenRow == null) {
			contenRow = sheet.createRow(index + excelVO.getBodyTopMargin());
		}

		// 创建
		Cell[] cells = new Cell[propertyName.length];

		for (int i = 0; i < cells.length; i++) {
			cells[i] = contenRow.getCell(i);
			if (cells[i] == null) {
				cells[i] = contenRow.createCell(i);
			}

			cells[i].setCellStyle(excelVO.getBodyCellStyle());
		}

		String[] reslutAttributeValue = getBeanPropertyValue(bean,propertyName, index);

		for (int i = 0; i < reslutAttributeValue.length; i++) {
			cells[i].setCellValue(reslutAttributeValue[i]);
		}
	}
	
	/**
	 * 获取值
	 * 
	 * @param bean
	 * @param reslutAttributeNameSet
	 * @param index
	 * @return String[]
	*/
	private String[] getBeanPropertyValue(Object bean, String[] reslutAttributeNameSet, int index) {
		String[] reslutAttributeValue = new String[reslutAttributeNameSet.length];

		Class<? extends Object> classType = bean.getClass();

		for (int i = 0; i < reslutAttributeNameSet.length; i++) {
			String stringLetter = reslutAttributeNameSet[i].substring(0, 1).toUpperCase();
			// 获得相应属性的getXXX方法名称
			String getName = "get" + stringLetter + reslutAttributeNameSet[i].substring(1);
			// 获取相应的方法
			try {
				Method getMethod = classType.getMethod(getName, new Class[] {});
				reslutAttributeValue[i] = getMethod.invoke(bean, new Object[] {}).toString();
			} catch (Exception e) {
			}
		}

		return reslutAttributeValue;
	}

	/*
	 * 私有构造方法
	 */
	private ExcelUtil() {
	}
}

class ExcelVO {
	public static final String EXCEL2003 = "xls"; // 2003版表格后缀
	public static final String EXCEL2007 = "xlsx"; // 2007版表格后缀
	
	// excel默认版本
	private String excelVersion = EXCEL2003;
	// 实体vo中的属性名
	private String[] propertyName = null;
	// excel文件中列名
	private String[] colName = null;
	// 默认值创建一个sheet
	private boolean showSingleSheet = true;
	// 默认一个sheet显示100条数据
	private int recordNumPerSheet = 100;
	// 默认为空，随机生成 sheet1,sheet2,sheet2,sheet2...
	private String[] sheetName = null;
	// head样式
	private CellStyle headCellStyle;
	// head样式
	private CellStyle headColName;
	// body样式
	private CellStyle bodyCellStyle;
	// head上边距
	private int headTopMargin = 0;
	// body上边距
	private int bodyTopMargin = 1;
	// head是否显示 默认显示
	private boolean showSheetHead = false;
	// 列宽度 负数表示默认
	private int colWidth = -1;

	public String getExcelVersion() {
		return excelVersion;
	}

	public void setExcelVersion(String excelVersion) {
		if (EXCEL2003.equalsIgnoreCase(excelVersion) || EXCEL2007.equalsIgnoreCase(excelVersion)) {
			this.excelVersion = excelVersion;
		}
	}
	
	public String[] getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String[] propertyName) {
		this.propertyName = propertyName;
	}

	public void setSheetName(String[] sheetName) {
		this.sheetName = sheetName;
	}

	public void setShowSingleSheet(boolean showSingleSheet) {
		this.showSingleSheet = showSingleSheet;
	}

	public int getRecordNumPerSheet() {
		return recordNumPerSheet;
	}

	public void setRecordNumPerSheet(int recordNumPerSheet) {
		if(recordNumPerSheet > 0){
			this.recordNumPerSheet = recordNumPerSheet;
		}
	}

	public String[] getSheetName() {
		return sheetName;
	}

	public boolean isShowSingleSheet() {
		return showSingleSheet;
	}
	
	public CellStyle getHeadCellStyle() {
		return headCellStyle;
	}

	public void setHeadCellStyle(CellStyle headCellStyle) {
		this.headCellStyle = headCellStyle;
	}

	public CellStyle getBodyCellStyle() {
		return bodyCellStyle;
	}

	public void setBodyCellStyle(CellStyle bodyCellStyle) {
		this.bodyCellStyle = bodyCellStyle;
	}

	public boolean isShowSheetHead() {
		return showSheetHead;
	}

	public void setShowSheetHead(boolean showSheetHead) {
		if(showSheetHead){
			this.setHeadTopMargin(0);
			this.setBodyTopMargin(1);
		}else{
			this.setHeadTopMargin(0);
			this.setBodyTopMargin(0);
		}
		this.showSheetHead = showSheetHead;
	}

	public int getColWidth() {
		return colWidth;
	}

	public void setColWidth(int colWidth) {
		this.colWidth = colWidth;
	}

	public int getHeadTopMargin() {
		return headTopMargin;
	}

	public void setHeadTopMargin(int headTopMargin) {
		this.headTopMargin = headTopMargin;
	}

	public int getBodyTopMargin() {
		return bodyTopMargin;
	}

	public void setBodyTopMargin(int bodyTopMargin) {
		this.bodyTopMargin = bodyTopMargin;
	}

	public CellStyle getHeadColName() {
		return headColName;
	}

	public void setHeadColName(CellStyle headColName) {
		this.headColName = headColName;
	}

	public String[] getColName() {
		return colName;
	}

	public void setColName(String[] colName) {
		this.colName = colName;
	}
}