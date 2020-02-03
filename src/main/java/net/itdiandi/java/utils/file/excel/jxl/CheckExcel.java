package net.itdiandi.java.utils.file.excel.jxl;

import java.util.Map;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils.file.excel.jxl
* @ClassName CheckExcel
* @Description 检查excel中每一行的数据是否合法
* @author 刘吉超
* @date 2016-03-05 09:11:39
*/
public interface CheckExcel {
    /**
     * 返回true合法
     *
     * @param data      excel中每一行的数据
     * @return
     */
    public boolean check(Map<String, Object> data);
}
