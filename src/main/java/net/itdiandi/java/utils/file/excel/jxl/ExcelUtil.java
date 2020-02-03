package net.itdiandi.java.utils.file.excel.jxl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @ProjectName Util
* @PackageName net.itdiandi.utils.file.excel.jxl
* @ClassName ExcelUtils
* @Description Excel工具
* @author 刘吉超
* @date 2016-03-04 21:55:21
*/
public class ExcelUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
    //成功
    public static final Integer STATUS_OK = Integer.valueOf(1);
    //失败
    public static final Integer STATUS_NO = Integer.valueOf(0);
    /**
     * 私有化构造器
     */
    private ExcelUtil(){

    }

    /**
     * 获取excel中的数据
     *
     * @param is excel
     * @return excel每行是list一条记录，map是对应的"字段名-->值"
     */
    public static List<Map<String, String>> getImportData(InputStream is){
    	return getImportData(is,null);
    }
    
    /**
     * 获取excel中的数据
     *
     * @param is excel
     * @param excelColumnNames excel字段名
     * @return excel每行是list一条记录，map是对应的"字段名-->值"
     */
    public static List<Map<String, String>> getImportData(InputStream is, List<String> excelColumnNames) {
        logger.debug("InputStream:{}", is);
        if (is == null) {
            return Collections.emptyList();
        }

        Workbook workbook = null;
        try {
            //拿到excel
            workbook = Workbook.getWorkbook(is);
        } catch (BiffException e) {
            logger.error(e.getMessage(), e);
            return Collections.emptyList();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return Collections.emptyList();
        }
        logger.debug("workbook:{}", workbook);

        if (workbook == null) {
            return Collections.emptyList();
        }
        
        //第一个sheet
        Sheet sheet = workbook.getSheet(0);
        //行数
        int rowCounts = sheet.getRows() - 1;
        logger.debug("rowCounts:{}", rowCounts);
        
        // 列名
        if(excelColumnNames == null){
        	excelColumnNames = new ArrayList<String>();
        }
        
        if(excelColumnNames.isEmpty()){
        	Cell[] cells = sheet.getRow(0);
        	for(Cell cell : cells){
        		excelColumnNames.add(cell.getContents());
        	}
        }
        
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        //双重for循环取出数据
        for(int i = 1; i < rowCounts; i++){
            Map<String, String> params = new HashMap<String, String>();
            //i,j i:行 j:列
            for(int j = 0; j < excelColumnNames.size(); j++){
                Cell cell = sheet.getCell(j, i);
                params.put(excelColumnNames.get(j), cell.getContents());
            }
            
            list.add(params);
        }

        return list;
    }
    
    /**
     * 获取导入数据为对象的List
     *
     * @param data
     * @param clazz
     * @param excelColumnNames
     * @param checkExcel
     * @param <T>
     * @return List<T>
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	public static <T> List<T> makeData(Object data, Class<T> clazz, CheckExcel checkExcel) {
        if(data == null || clazz == null || checkExcel == null) {
            return Collections.emptyList();
        }
        
        if(data instanceof List<?>){
        	List<Object> dataList = (List<Object>)data;
        	
        	List<T> result = new ArrayList<T>(dataList.size());
        	
        	for(Object object : dataList) {
        		// 现只支持列用map表示的
            	if(object instanceof Map<?, ?>){
            		Map<Object,Object> tempMap = (Map<Object,Object>)object;
            		
            		// key是String,value是object类型的
            		Map<String,Object> keyStringMap =  new HashMap<String,Object>();
            		for(Entry<Object,Object> entry : tempMap.entrySet()){
            			if(entry.getKey() instanceof String){
            				keyStringMap.put((String)entry.getKey(), entry.getValue());
            			}
            		}
            		
            		// 处理key是String类型,value是object类型的
            		if(!keyStringMap.isEmpty()){
            			if(!checkExcel.check(keyStringMap)) {
                            continue;
                        }
                        
                        T entity = null;
            			try {
            				entity = clazz.newInstance();
            				for(Entry<String,Object> entry : keyStringMap.entrySet()){
            					BeanUtils.setProperty(entity, entry.getKey(), entry.getValue());
            				}
            			} catch (Exception e) {
            				logger.error(e.getMessage(), e);
            			}
            			result.add(entity);
            		}
            	}
            }
        	
        	return result;
        }
        
        // 现只支持行用list表示的
        return null;
    }
}
