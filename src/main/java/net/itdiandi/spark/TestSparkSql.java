package net.itdiandi.spark;

import org.apache.log4j.Logger;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SparkSession.Builder;

public class TestSparkSql {
    public static void main(String[] args) {
        Logger log = Logger.getLogger(TestSparkSql.class);
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.parsers.SAXParserFactory",
                "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
        
        Builder builder = SparkSession.builder().appName("dse load application in Java");

        
//        if (!sparkJarAddress.isEmpty() && !sparkMaster.contains("local")) {
//            sparkConf.set("spark.executor.memory", sparkExecutorMemory); // 16g
//            sparkConf.set("spark.scheduler.mode", "FAIR");
//            sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
//            sparkConf.set("spark.kryo.registrator", "com.dahua.dse3.driver.dataset.DseKryoRegistrator");
//            sparkConf.set("spark.cores.max", sparkCoresMax);
//            sparkConf.set("spark.akka.threads", "12");
//            sparkConf.set("spark.local.dir", sparkLocalDir);
//            sparkConf.set("spark.shuffle.manager", "SORT");
//            sparkConf.set("spark.network.timeout", "120");
//            sparkConf.set("spark.rpc.lookupTimeout", "120");
//            sparkConf.set("spark.executor.extraClassPath", "/usr/dahua/spark/executelib/hbase-protocol-0.98.3-hadoop2.jar");
//            sparkConf.set("spark.executor.extraJavaOptions", "-verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps");
//            sparkConf.set("spark.sql.codegen", "TRUE");
//            //sparkConf.set("spark.sql.parquet.filterPushdown","true");
//        }
//        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
//        if (!sparkJarAddress.isEmpty() && !sparkMaster.contains("local")) {
//            jsc.addJar(sparkJarAddress);
//        }
//        String hdfsPath = "hdfs://mycluster/wl/parquet/test/2016-06-21";
//        String source = "test";
//        SQLContext sqlContext = new SQLContext(jsc);
//        DataFrame dataFrame = sqlContext.parquetFile(hdfsPath);
//        dataFrame.registerTempTable(source);
//        String sql = "SELECT id,dev_chnid,dev_chnname,car_num,car_speed,car_direct from test";
//        DataFrame result = sqlContext.sql(sql);
//        log.info("Result:"+result.count());
    }
}
