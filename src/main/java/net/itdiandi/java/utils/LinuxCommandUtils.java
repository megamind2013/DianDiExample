package net.itdiandi.java.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LinuxCommandUtils {
	public static String doCmd(String cmd){
		 StringBuffer result=new StringBuffer();  
		 System.out.println("ccccccccccc");
		Runtime run = Runtime.getRuntime();//返回与当前 Java 应用程序相关的运行时对象  
		System.out.println("222222222");
        try {  
            Process p = run.exec(cmd);// 启动另一个进程来执行命令  
            System.out.println("3333333333");
            BufferedInputStream in = new BufferedInputStream(p.getInputStream());  
            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));  
            String lineStr="";  
            if ((lineStr = inBr.readLine()) != null)  {
                //获得命令执行后在控制台的输出信息  
            result.append(lineStr);
            result.append("\n"); 
            }
            System.out.println("11111111111");
            //检查命令是否执行失败。  
            if (p.waitFor() != 0) {  
                if (p.exitValue() == 1)//p.exitValue()==0表示正常结束，1：非正常结束  
                    System.err.println("命令执行失败!");  
                		return null;
            }  
            inBr.close();  
            in.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return result.toString();
		
	}
	public static void main(String[] args) {
		
		System.out.println(doCmd("ls"));
	}
}
