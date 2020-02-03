package net.itdiandi.java.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @ProjectName common
* @PackageName cn.ce.bigdata.util
* @ClassName DynamicVOUtil
* @Description vo动态编译
* @Author 刘吉超 
* @Date 2018年1月5日 下午3:38:37
*/
@SuppressWarnings("unused")
public class DynamicVOUtil {
	private static final Logger logger = LoggerFactory.getLogger(DynamicVOUtil.class);
	private String separator = System.getProperty("line.separator");
	private String classNamePrefix = "MIS"; 

	public DynamicVOUtil() {
	}
	
	public DynamicVO buildVOTypeDefaultList(List<String> propertyList){
		Map<String,String> voMap = new HashMap<String,String>();
		
		for(String property : propertyList){
			voMap.put(property, "java.util.List<String>");
		}
		// 默认类名
		buildVO(voMap,null);
		
		return null;
	}
	
	public DynamicVO buildVO(Map<String,String> propertyMap,String className){
//		DynamicVO dynamicVO = new DynamicVO();
		// 默认类名yyyyMMddHHmmss
		className = StringUtils.isBlank(className) ? classNamePrefix + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) : className;
		
		StringBuilder dynamicVOString = generateVO(propertyMap,className);
		
		logger.info(separator + dynamicVOString.toString());
		
		String javaFilePath = writeJavaFile(className,dynamicVOString.toString());
		
		logger.info(separator + javaFilePath);
		
		compile(javaFilePath);
		
		return null;
	}
	
	
	
	public StringBuilder generateVO(Map<String,String> propertyMap,String className){
		StringBuilder dynamicVOString = new StringBuilder();
		Set<String> importClassSet = new HashSet<String>();
		
		dynamicVOString.append("public class "+ className + " {" + separator);
		
		for(Entry<String,String> entry : propertyMap.entrySet()){
			if(StringUtils.isNotBlank(entry.getValue()) && entry.getValue().split("\\.").length > 1){
				importClassSet.add(entry.getValue().replaceAll("<(.*)>", ""));
			}
			dynamicVOString.append(generateProperty(entry.getKey(),entry.getValue().substring(entry.getValue().lastIndexOf(".") + 1)));
		}
		
		dynamicVOString.append("}");
		
		for(String importClass : importClassSet){
			dynamicVOString.insert(0, "import " + importClass + ";" + separator);	
		}
		
		return dynamicVOString;
	}
	
	private StringBuilder generateProperty(String propertyName,String propertyType){
		StringBuilder sb = new StringBuilder();
		if(StringUtils.isBlank(propertyName) || StringUtils.isBlank(propertyType)){
			return sb;
		}
		
		sb.append("  private " + propertyType + " " + propertyName + ";");
		
		sb.append(separator);
		
		sb.append("  public "+ propertyType + " get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1) + "() {" + separator);
		sb.append("    return " + propertyName + ";" + separator);
		sb.append("  }");
		
		sb.append(separator);
		
		sb.append("  public void set"+ propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1)+"(" + propertyType + " " + propertyName + ") {" + separator);
		sb.append("    this." + propertyName + "="+propertyName+";" + separator);
		sb.append("  }");
		
		sb.append(separator);
		
		return sb;
	}
	
	private String writeJavaFile(String classPath,String codeContent){
		String[] split = classPath.split("\\.");
		String className = split[split.length-1];
		StringBuilder packagePath = new StringBuilder();
		
		for(int i=0; i < split.length-1;i++){  
			packagePath.append(split[i]+File.separator);  
		}  
	          
		String filePath = packagePath.insert(0, PathUtils.getProjectPath() + File.separator).toString();    
		File file = new File(filePath);    
		if(!file.exists())  
			file.mkdirs();  
	          
		String classFullPath = filePath + className + ".java";  
		file = new File(classFullPath);  
		try (FileWriter fw = new FileWriter(file);){  
			fw.write(codeContent);  
			fw.flush();  
		} catch (IOException e) {  
			logger.error("",e);
		}
		return classFullPath;
	}

	/**
	 * Compile a Java source file in memory.
	 * 
	 * @param fileName
	 *            Java file name, e.g. "Test.java"
	 * @param source
	 *            The source code as String.
	 * @return The compiled results as Map that contains class name as key,
	 *         class binary as value.
	 * @throws IOException
	 *             If compile error.
	 */
	public Map<String, byte[]> compile(String javaFilePath){
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();    
        StandardJavaFileManager fileMgr = null;  
        try{  
            fileMgr = compiler.getStandardFileManager(null, null, null);    
            final Iterable<? extends JavaFileObject> javaFileObjects = fileMgr.getJavaFileObjects(javaFilePath);    
            //编译  
            compiler.getTask(null, fileMgr, null, null, null, javaFileObjects).call();    
        }catch (Exception e) {  
            logger.error("",e);
        }finally{  
            if(fileMgr!=null)  
                try {  
                    fileMgr.close();  
                } catch (IOException e) {  
                    logger.error("",e);
                }  
        }  
		return null;
	}

	/**
	 * Load class from compiled classes.
	 */

	private static Class<?> load(String name){
        Class<?> cls = null;
        ClassLoader classLoader = null;
        try{
            classLoader = DynamicVOUtil.class.getClassLoader();
            cls = classLoader.loadClass(name);
            if(logger.isInfoEnabled()){
                logger.debug("Load Class["+name+"] by "+classLoader);
            }
        }catch(Exception e){
            logger.error("",e);
        }
        return cls;
    }

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("id");
		list.add("name");
//		DynamicVOUtil dynamicVOUtil = new DynamicVOUtil();
//		dynamicVOUtil.buildVOTypeDefaultList(list).formatVOToJson()
//		DynamicVO dynamicVO = ;
		
	}
}

class DynamicVO{
//	private StringBuilder dynamicVO = new StringBuilder();
	
	public void setProperty(String key,String value){
		
	}
	
	public String formatVOToJson(){
		return "";
	}
}