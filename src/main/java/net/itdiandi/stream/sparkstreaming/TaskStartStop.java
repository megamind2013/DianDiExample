package net.itdiandi.stream.sparkstreaming;

import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQuery;

public class TaskStartStop {
	public static void main(String[] args) {
		int checkInterval=100;
		SparkSession ssc = SparkSession.builder().appName("JavaStructuredNetworkWordCount").getOrCreate();
		
		// 通过flag文件来判断
		new Thread(new Runnable(){
			public void run(){
				while(true){
					try{
						boolean stopFlag = FileSystem.get(ssc.sparkContext().hadoopConfiguration()).exists(new Path("/tmp/aaa_flag"));
						if(stopFlag){
//							log.warn("/tmp/aaa_flag exists,program will stop soon");
							ssc.stop();
							break;
						}
					}catch(IOException e){
						e.printStackTrace();
					}
					try{
						Thread.sleep(checkInterval);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			}
		});
	}
	
}