package net.itdiandi.stream.sparkstreaming;

public class TaskStartStop{

	// 通过flag文件来判断
	new Tread(new Runnable(){

		@Override
		public void run(){

			while(true){
				try{
					boolean stopFlag = FileSystem.get(ssc.sparkContext().hadoopConfiguration()).exists(new Path("/tmp/aaa_flag"))
					if(stopFlag){
						log.warn("/tmp/aaa_flag exists,program will stop soon")
						ssc.stop()
						break;
					}
				}catch(IOException e){
					e.printStackTrace();
				}

				try{
					Thread.sleep(checkInterval)
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	})
}